package com.haust.minitalk.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haust.minitalk.SpringUtil;
import com.haust.minitalk.enums.MsgActionEnum;
import com.haust.minitalk.service.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: csp1999
 * @Date: 2020/09/23/10:49
 * @Description: 用于处理消息的 handler
 * 由于传输数据的载体是 frame 这个 frame 在 netty 中 是用于为
 * websocket专门处理文本对象的，frame 是消息的载体
 * 这个类名为：TextWebSocketFrame
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 用于记录和管理所有客户端的管道 channel
    public static ChannelGroup userClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端所传输的消息
        String content = msg.text();
        // System.out.println("服务器接收到的消息内容：" + content);
        // System.out.println("ip是：" + ctx.channel().remoteAddress());

        //1. 获取客户端发来的消息
        // 方式一：
        DataContent dataContent = JSON.parseObject(content, DataContent.class);
        // 方式二：DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();//消息行为
        Channel channel = ctx.channel();// 客户端请求过来之后生成channel通道，通过ctx 上下文对象获取该通道

        //2. 判断消息类型(行为)，根据不同的类型来处理不同的业务
        if (action == MsgActionEnum.CONNECT.type) {// 第一次(或重连)初始化连接
            // 2.1 当websocket 第一次open的时候 初始化channel，把channel 和发送者的userid 关联起来
            String senderId = dataContent.getUserChatMsg().getSenderId();
            UserChannelRelation.put(senderId, channel);// hashMap

            // 测试
            userClients.forEach(userChannel -> {
                System.out.println(userChannel.id().asLongText());
            });
            UserChannelRelation.output();// 测试

        } else if (action == MsgActionEnum.CHAT.type) {// 聊天消息
            // 2.2 收到聊天类型的消息 把聊天记录保存到数据库 同时标记消息的签收状态(未签收)
            UserChatMsg userChatMsg = dataContent.getUserChatMsg();
            String msgContent = userChatMsg.getMsg();
            String senderId = userChatMsg.getSenderId();
            String receiverId = userChatMsg.getReceiverId();
            // 调用userService 保存消息到数据库 并且消息标记为未签收
            // 注意：chatHandler 并未交由spring容器接管 因此无法使用注入方式 获取userService
            // 因此：这里采用SpringUtils 工具类手动注入
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(userChatMsg);// 聊天消息保存数据库
            userChatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setUserChatMsg(userChatMsg);// 将用户聊天消息内容保存到完整消息内容实体类中

            // 发送消息
            // 从全局用户的channel关系中获取接收方的channel
            Channel receiveChannel = UserChannelRelation.get(receiverId);
            if (receiveChannel == null) {
                // 离线用户
            } else {
                // 当 receiveChannel 不为空的时候 从channelGroup 中查找对应的channel是否存在
                Channel findChannel = userClients.find(receiveChannel.id());
                if (findChannel != null) {
                    // 用户在线
                    // 方式一：// 将消息内容写入通道并刷新
                    JSONObject jsonObject = new JSONObject();
                    String jsonString = jsonObject.toJSONString(dataContentMsg);
                    receiveChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    jsonString
                                    //方式二：JsonUtils.objectToJson(dataContent)
                            )
                    );
                } else {
                    // 离线用户
                }
            }

        } else if (action == MsgActionEnum.SIGNED.type) {// 消息签收
            // 2.3 签收消息类型，针对具体的消息进行签收 修改数据库中对应消息的签收状态(已签收)
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            // 扩展字段在 signed 类型消息中 代表需要去签收的消息id 逗号间隔
            String msgIdStr = dataContent.getExtend();
            String[] msgsId = msgIdStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String msgId : msgsId) {
                if (!StringUtils.isEmpty(msgId)) {
                    msgIdList.add(msgId);
                }
            }
            // 控制台打印输出查看
            msgIdList.forEach(msgId -> {
                System.out.println(msgId);
            });

            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                // 消息批量签收，更新标记
                userService.updateMsgSigned(msgIdList);
            }
        } else if (action == MsgActionEnum.KEEPALIVE.type) {// 客户端保持心跳
            // 2.4 收到心跳类型的消息
            System.out.println("收到来自channel 为【" + channel + "】的心跳包");
        }

        /*
        // 将数据刷新到客户端上面
        userClients.writeAndFlush(
                new TextWebSocketFrame(
                        "[服务器在: ]" + LocalDateTime.now()
                                + "接收到消息，消息内容为：" + content
                )
        );*/
    }

    /*
     * (客户端创建完成就会触发该方法)
     * 客户端与服务器端建立连接的时候就在clients 中添加 该channel 通道
     * @param: ctx
     * @return: void
     * @create: 2020/9/23 11:01
     * @author: csp1999
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 获取channel
        Channel channel = ctx.channel();
        // 在通道管理组中添加 该channel
        userClients.add(channel);// 将存在心跳链接的channel 放入userClients统一录和管理
    }

    /*
     * (浏览器关闭(用户离开客户端)就会触发该方法)
     * 客户端与服务器端断开连接的时候就在clients 中移除 该channel 通道
     * @param: ctx
     * @return: void
     * @create: 2020/9/23 11:01
     * @author: csp1999
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 获取channelId
        String chanelId = ctx.channel().id().asShortText();
        System.out.println("客户端被移除：channel id 为：" + chanelId);

        userClients.remove(ctx.channel());// 从userClients移出channel
        /**
         * 在通道管理组中移除 该channel
         * 如果用户把相应的浏览器关闭 也会自动移除该channel通道，
         * 所以即使不调用remove方法 channel仍被移除,
         * 所以可以不写 clients.remove(channel); 这行代码

         System.out.println("客户端断开，channel 对应的长id为：" + ctx.channel().id().asLongText());
         System.out.println("客户端断开，channel 对应的短id为：" + ctx.channel().id().asShortText());*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生了异常后关闭连接，同时从channelgroup 移除
        ctx.channel().close();
        userClients.remove(ctx.channel());
    }
}
