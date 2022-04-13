package com.haust.minitalk.service.impl;

import com.alibaba.fastjson.JSON;
import com.haust.minitalk.config.FastDFSClient;
import com.haust.minitalk.entity.ChatMsg;
import com.haust.minitalk.entity.FriendsRequest;
import com.haust.minitalk.entity.MyFriends;
import com.haust.minitalk.entity.Users;
import com.haust.minitalk.enums.MsgActionEnum;
import com.haust.minitalk.enums.MsgSignFlagEnum;
import com.haust.minitalk.enums.SearchFriendsStatusEnum;
import com.haust.minitalk.mapper.*;
import com.haust.minitalk.netty.DataContent;
import com.haust.minitalk.netty.UserChannelRelation;
import com.haust.minitalk.netty.UserChatMsg;
import com.haust.minitalk.service.UserService;
import com.haust.minitalk.utils.FileUtils;
import com.haust.minitalk.utils.QRCodeUtils;
import com.haust.minitalk.utils.UUIDUtils;
import com.haust.minitalk.vo.FriendsRequestVO;
import com.haust.minitalk.vo.MyFriendsVO;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Auther: csp1999
 * @Date: 2020/09/01/20:19
 * @Description: 用户service 接口实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMapperCustom userMapperCustom;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Override
    public Users getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public Users queryUserNameIsExit(String username) {
        Users user = userMapper.queryUserNameIsExit(username);
        return user;
    }


    @Override
    public Users insert(Users user) {
        user.setId(UUIDUtils.getUUID());// 设置用户主键

        // 使用谷歌二维码生成插件zxing 为每一个注册用户生成一个唯一的二维码
        String qrCodePath = "E://本机图片//Other Img//user//user" + user.getId() + "qrcode.png";// 二维码生成地址
        // 创建二维码对象信息，minitalk_qrcode 盐值
        qrCodeUtils.createQRCode(qrCodePath, "minitalk_qrcode:" + user.getUsername());
        // 获取 MultipartFile 对象
        MultipartFile qrCodeMultipartFile = FileUtils.fileToMultipart(qrCodePath);
        // 上传二维码到 fastDFS,并返回图片地址 qrCodeUrl
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeMultipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 添加为用户设置二维码url
        user.setQrcode(qrCodeUrl);
        // 保存数据库
        userMapper.insert(user);
        return user;
    }

    @Override
    public Users updateUserInfo(Users user) {
        // 更新用户昵称
        userMapper.updateByPrimaryKeySelective(user);
        // 更新完成后把用户信息返回给controller
        Users userResult = userMapper.selectByPrimaryKey(user.getId());
        return userResult;
    }

    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUserName) {
        Users user = userMapper.queryUserNameIsExit(friendUserName);
        // 1.搜索的用户如果不存在，则返回【无此用户】
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        // 2.搜索的账号如果是你自己，则返回【不能添加自己】
        if (user.getId().equalsIgnoreCase(myUserId)) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        MyFriends myFriends = new MyFriends();
        myFriends.setMyFriendUserId(user.getId()).setMyUserId(myUserId);
        // 到my_friends 表中查找是否存在该用户对应的好友，并返回
        MyFriends myFriendsResult = myFriendsMapper.selectOneByExample(myFriends);
        // 3.搜索的朋友已经是你好友，返回【该用户已经是你的好友】
        if (myFriendsResult != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        // 可以添加该好友
        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Override
    public String sendFriendRequest(String myUserId, String friendUserName) {
        // 根据用户名查询该用户信息
        Users user = userMapper.queryUserNameIsExit(friendUserName);
        MyFriends myFriends = new MyFriends();
        myFriends.setMyFriendUserId(user.getId()).setMyUserId(myUserId);
        MyFriends myFriendsResult = myFriendsMapper.selectOneByExample(myFriends);
        // 搜索的朋友不是你好友
        if (myFriendsResult == null) {
            // 实例化好友添加请求对象
            FriendsRequest friendsRequest = new FriendsRequest();
            String friendsRequestId = UUIDUtils.getUUID();
            friendsRequest.setId(friendsRequestId).setSendUserId(myUserId)
                    .setAcceptUserId(user.getId()).setRequestDateTime(new Date());

            // 查询好友添加请求是否已经存在
            FriendsRequest result = friendsRequestMapper.selectBySendUserIdAndAcceptUserId(
                    friendsRequest.getSendUserId(), friendsRequest.getAcceptUserId());

            System.out.println("好友添加请求查询结果：" + result);
            if (result == null) {
                // 好友添加请求发送并存数据库
                friendsRequestMapper.insert(friendsRequest);
                return "添加请求发送成功！";
            } else {
                return "您已经发送过添加请求，请勿重复操作！";
            }
        }
        return "添加请求发送失败！";
    }

    @Override
    public List<FriendsRequestVO> queryFriendRequestList(String acceptUserId) {

        return userMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Override
    public void deleteFriendRequest(FriendsRequest friendsRequest) {
        friendsRequestMapper.deleteByFriendRequest(friendsRequest);
    }

    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        // 给请求接受者 和 请求发送者 都进行好友列表添加
        saveFriends(sendUserId, acceptUserId);
        saveFriends(acceptUserId, sendUserId);

        // 删除好友请求信息
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setSendUserId(sendUserId).setAcceptUserId(acceptUserId);
        deleteFriendRequest(friendsRequest);

        Channel sendChannel = UserChannelRelation.get(sendUserId);
        if (sendChannel != null) {
            // 被请求添加者 使用 websocket主动推送消息到 请求添加发起者， 更新请求添加发起者的通讯录
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);// 拉取好友

            // 消息的推送
            sendChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));
        }
    }

    // 通过好友请求并保存数据到my_friends 表中
    private void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(sendUserId).setMyFriendUserId(acceptUserId).setId(UUIDUtils.getUUID());
        myFriendsMapper.insert(myFriends);
    }

    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        return userMapperCustom.queryMyFriends(userId);
    }

    @Override
    public String saveMsg(UserChatMsg userChatMsg) {
        // UserChatMsg netty 包中的聊天消息实体类
        ChatMsg chatMsg = new ChatMsg();// ChatMsg entity 包下的聊天消息内容实体类
        String msgId = UUIDUtils.getUUID();

        chatMsg.setId(msgId).setAcceptUserId(userChatMsg.getReceiverId())
                .setSendUserId(userChatMsg.getSenderId()).setCreateTime(new Date())
                .setSignFlag(MsgSignFlagEnum.unsign.type).setMsg(userChatMsg.getMsg());

        chatMsgMapper.insert(chatMsg);
        return msgId;
    }

    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        userMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Override
    public List<ChatMsg> getUnReadMsgList(String acceptUserId) {
        List<ChatMsg> resultList = chatMsgMapper.getUnReadMsgListByAcceptUid(acceptUserId);
        return resultList;
    }

    @Override
    public void updateCidByUserId(String id, String cid) {
        userMapper.updateCidByUserId(id, cid);
    }
}
