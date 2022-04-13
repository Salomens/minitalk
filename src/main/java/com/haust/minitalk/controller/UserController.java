package com.haust.minitalk.controller;

import com.alibaba.fastjson.JSONObject;
import com.haust.minitalk.config.FastDFSClient;
import com.haust.minitalk.entity.ChatMsg;
import com.haust.minitalk.entity.FriendsRequest;
import com.haust.minitalk.entity.Users;
import com.haust.minitalk.enums.OperatorFriendRequestTypeEnum;
import com.haust.minitalk.enums.SearchFriendsStatusEnum;
import com.haust.minitalk.service.UserService;
import com.haust.minitalk.utils.FileUtils;
import com.haust.minitalk.utils.MD5Utils;
import com.haust.minitalk.vo.FriendsRequestVO;
import com.haust.minitalk.vo.ImageVo;
import com.haust.minitalk.vo.MyFriendsVO;
import com.haust.minitalk.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csp1999
 * @Date: 2020/09/01/20:22
 * @Description: 用户 Controller
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    /*
     * 用户登录
     * @param: user
     * @return: com.haust.minitalk.utils.IWdzlJSONResult
     * @create: 2020/9/2 13:07
     * @author: csp1999
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping("/login")
    public JSONObject login(@RequestBody Map<String, String> params) {
        // System.out.println(params);
        JSONObject jsonObject = new JSONObject();
        // 根据 username 查询数据库中是否有这个用户信息
        Users userResult = userService.queryUserNameIsExit(params.get("username"));

        if (userResult != null) {// 数据存在该用户信息
            if (userResult.getPassword().equals(MD5Utils.getPwd(params.get("password")))) {
                // 如果用户更换登录设备则更新登录设备cid
                if (!params.get("cid").equals(userResult.getCid())) {
                    userService.updateCidByUserId(userResult.getId(), params.get("cid"));
                }
                userResult.setCid(params.get("cid"));

                System.out.println("----------登录成功------------");
                jsonObject.put("status", "200");
                jsonObject.put("msg", "登录成功");
                jsonObject.put("user", userResult);
                return jsonObject;
            } else {
                // 密码错误
                System.out.println("----------密码错误------------");
                jsonObject.put("status", "500");
                jsonObject.put("msg", "密码错误,请重新输入");
                jsonObject.put("user", null);
                return jsonObject;
            }
        } else {
            // 数据库不存在该用户信息，需要注册
            System.out.println("----------用户名未注册------------");
            jsonObject.put("status", "404");
            jsonObject.put("msg", "用户名未注册");
            jsonObject.put("user", null);
            return jsonObject;
        }
    }

    /*
     * 用户注册
     * @param: user
     * @return: java.lang.String
     * @create: 2020/9/2 13:07
     * @author: csp1999
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping("/register")
    public JSONObject register(@RequestBody Map<String, String> params) {
        // System.out.println(params);
        JSONObject jsonObject = new JSONObject();
        Users user = new Users();

        // 根据 username 查询数据库中是否有这个用户信息
        Users findUser = userService.queryUserNameIsExit(params.get("username"));
        if (findUser == null) {// 不存在这个用户信息则可以注册
            user.setNickname(params.get("username"));
            user.setQrcode("");// 用户二维码暂时置空，在service实现类中添加
            user.setPassword(MD5Utils.getPwd(params.get("password")));
            user.setFaceImage("M00/00/00/wKhYhF9fP26APvWXAAgB_NmKHcI542_150x150.png");// 默认缩略头像
            user.setFaceImageBig("M00/00/00/wKhYhF9fP26APvWXAAgB_NmKHcI542.png");// 默认大头像
            user.setUsername(params.get("username"));
            user.setCid(params.get("cid"));// 设备id(可不加，在登录操作会添加或者更新)

            Users userResult = userService.insert(user);// 注册成功！
            UserVo userVo = new UserVo();// 使用扩展类的目的是为了不把用户密码等敏感信息返回到前端本地缓存(不安全)
            BeanUtils.copyProperties(userResult, userVo);

            jsonObject.put("status", "200");
            jsonObject.put("msg", "用户注册成功");
            jsonObject.put("user", userVo);
            System.out.println(jsonObject);
        } else {
            System.out.println("该用户名已被注册！");
            jsonObject.put("status", "500");
            jsonObject.put("msg", "该用户名已被注册");
            jsonObject.put("user", null);
        }
        return jsonObject;// 把注册成功的user结果信息响应到前端
    }

    /*
     * 更新用户昵称
     * @param: params
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/3 21:26
     * @author: csp1999
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping("/setNickname")
    public JSONObject updateNickname(@RequestBody Map<String, String> params) {
        JSONObject jsonObject = new JSONObject();
        Users user = new Users();
        user.setNickname(params.get("nickname")).setId(params.get("id"));
        // System.out.println(user);
        // 更新用户名
        Users userResult = userService.updateUserInfo(user);
        userResult.setPassword("");// 将密码置空，避免敏感数据返回到前端本地缓存
        // System.out.println(userResult);

        jsonObject.put("status", "200");
        jsonObject.put("msg", "用户昵称修改成功");
        jsonObject.put("user", userResult);
        return jsonObject;
    }

    /*
     * 用户头像上传
     * @param: imageVo
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/12 13:11
     * @author: csp1999
     */
    @RequestMapping("/uploadFaceBase64")
    @ResponseBody
    @CrossOrigin
    public JSONObject uploadFaceBase64(@RequestBody Map<String, String> params) throws Exception {
        JSONObject jsonObject = new JSONObject();
        // 图片扩展类
        ImageVo imageVo = new ImageVo();
        imageVo.setUserId(params.get("userId")).setImgData(params.get("imgData"));

        // System.out.println(imageVo);
        // 获取前端传递过来的base64 字符串，然后转为文件对象在进行上传
        String base64Data = imageVo.getImgData();
        // 这里我的服务器测试用的是本机，所以图片路径从E盘开始，如果部署服务器后，图片路径地址应该是如下：
        // String userImgPath = "/usr/local/face/"+imageVo.getUserId()+"userImgBase64.png";
        String userImgPath = "E:\\本机图片\\Other Img\\" + imageVo.getUserId() + "userImgBase64.png";

        // 使用FileUtils 工具类 通过图片路径和base64字符串在服务器主机生成图片
        FileUtils.base64ToFile(userImgPath, base64Data);

        // 获得MultipartFile(获得服务器本地图片对象)
        MultipartFile multipartFile = FileUtils.fileToMultipart(userImgPath);

        // 获取fastDFS上传图片后返回的路径
        String imgUrl = fastDFSClient.uploadBase64(multipartFile);// 返回图片url
        // System.out.println(imgUrl);

        String thump = "_150x150.";// 图片缩略图尺寸
        String[] arr = imgUrl.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1]; // 图片缩略图url

        // 更新用户头像
        Users user = new Users();
        user.setId(imageVo.getUserId());
        user.setFaceImageBig(imgUrl);
        user.setFaceImage(thumpImgUrl);
        Users result = userService.updateUserInfo(user);
        result.setPassword("");// 将密码置空，避免敏感数据返回到前端本地缓存
        // System.out.println(result);

        jsonObject.put("status", "200");
        jsonObject.put("msg", "用户头像修改成功");
        jsonObject.put("user", result);
        return jsonObject;
    }

    /*
     * 搜索好友操作
     * @param: username 好友的用户名
     * @param: userId 我的用户id
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/13 16:05
     * @author: csp1999
     */
    @RequestMapping("/searchFriend")
    @ResponseBody
    @CrossOrigin
    public JSONObject searchFriend(@RequestParam("username") String username,
                                   @RequestParam("userId") String userId) throws Exception {
        JSONObject jsonObject = new JSONObject();
        /**
         * 前置条件：
         * 1.搜索的用户如果不存在，则返回【无此用户】
         * 2.搜索的账号如果是你自己，则返回【不能添加自己】
         * 3.搜索的朋友已经是你好友，返回【该用户已经是你的好友】
         */
        Integer status = userService.preconditionSearchFriends(userId, username);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            Users user = userService.queryUserNameIsExit(username);// 根据username查询该好友信息
            // System.out.println(user);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            jsonObject.put("status", "200");
            jsonObject.put("msg", "用户添加成功...");
            jsonObject.put("user", userVo);
            return jsonObject;
        }else {
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            jsonObject.put("status", "500");
            jsonObject.put("msg", msg);
            jsonObject.put("user", null);
            return jsonObject;
        }
    }

    /*
     * 好友添加操作
     * @param: myUserId 我的用户id
     * @param: friendUserName 好友用户名
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/13 20:56
     * @author: csp1999
     */
    @RequestMapping("/addFriend")
    @ResponseBody
    @CrossOrigin
    public JSONObject addFriend(@RequestParam("myUserId") String myUserId,
                                @RequestParam("friendUserName") String friendUserName) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if(myUserId==""||myUserId==null){
            jsonObject.put("status", "500");
            jsonObject.put("msg", "myUserId为获取失败！");
            jsonObject.put("user", null);
            return jsonObject;
        }

        Integer status = userService.preconditionSearchFriends(myUserId, friendUserName);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            // 发送好友添加请求，并存入数据库
            String msg = userService.sendFriendRequest(myUserId, friendUserName);
            jsonObject.put("status", "200");
            jsonObject.put("msg", msg);
            jsonObject.put("user", null);
            return jsonObject;
        }else {
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            jsonObject.put("status", "500");
            jsonObject.put("msg", msg);
            jsonObject.put("user", null);
            return jsonObject;
        }
    }

    /*
     * 获取好友请求列表
     * @param: userId
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/14 16:43
     * @author: csp1999
     */
    @RequestMapping("/queryFriendRequest")
    @ResponseBody
    @CrossOrigin
    public JSONObject queryFriendRequest(@RequestParam("userId") String userId) throws Exception {
        JSONObject jsonObject = new JSONObject();

        // 根据用户id 获取好友列表
        List<FriendsRequestVO> friendRequestList = userService.queryFriendRequestList(userId);
        jsonObject.put("status", "200");
        jsonObject.put("msg", "获取好友请求列表成功！");
        jsonObject.put("friendRequestList", friendRequestList);
        return jsonObject;
    }

    /*
     * 好友请求相关操作
     * @param: userId 请求接收者id
     * @param: sendUserId 发送请求者id
     * @param: operationType 好友请求操作(同意/忽略)
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/14 19:01
     * @author: csp1999
     */
    @RequestMapping("/operFriendRequest")
    @ResponseBody
    @CrossOrigin
    public JSONObject operFriendRequest(@RequestParam("userId") String userId,
                                        @RequestParam("sendUserId") String sendUserId,
                                        @RequestParam("operationType") Integer operationType){

        JSONObject jsonObject = new JSONObject();
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setAcceptUserId(userId).setSendUserId(sendUserId);

        if (operationType == OperatorFriendRequestTypeEnum.IGNORE.type){// 忽略好友请求
            // 满足这个条件将删除好友请求表中的相关记录
            userService.deleteFriendRequest(friendsRequest);
        }else if (operationType == OperatorFriendRequestTypeEnum.PASS.type){// 通过好友请求
            // 满足这个条件将为好友表添加一条记录，并删除好友请求表中的相关记录
            userService.passFriendRequest(sendUserId,userId);
        }
        // 查询该用户好友列表中的数据
        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);

        jsonObject.put("status", "200");
        jsonObject.put("msg", "获取好友请求列表成功！");
        jsonObject.put("friendList", myFriends);
        return jsonObject;
    }

    /*
     * 我的好友列表
     * @param: userId
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/27 12:59
     * @author: csp1999
     */
    @RequestMapping("/myfriendsList")
    @ResponseBody
    @CrossOrigin
    public JSONObject myfriendsList(@RequestParam("userId") String userId){
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isEmpty(userId)){
            jsonObject.put("status", "404");
            jsonObject.put("msg", "用户id为null！");
            jsonObject.put("friendList", null);
        }
        // 数据库查询好友列表
        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);
        jsonObject.put("status", "200");
        jsonObject.put("msg", "获取用户列表成功！");
        jsonObject.put("friendList", myFriends);
        return jsonObject;
    }

    /*
     * 获取未读取消息列表
     * @param: acceptUserId
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/9/27 13:02
     * @author: csp1999
     */
    @RequestMapping("/getUnReadMsgList")
    @ResponseBody
    @CrossOrigin
    public JSONObject getUnReadMsgList(@RequestParam("acceptUserId") String acceptUserId){
        JSONObject jsonObject = new JSONObject();

        System.out.println("接收者ID:"+acceptUserId);
        if (StringUtils.isEmpty(acceptUserId)){
            jsonObject.put("status", "500");
            jsonObject.put("msg", "接受者ID不能为空！");
            return jsonObject;
        }

        // 根据接收ID 查找未签收的消息列表
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        System.out.println("未签收的消息列表"+unReadMsgList);
        jsonObject.put("status", "200");
        jsonObject.put("msg", "获取未签收消息列表成功！");
        jsonObject.put("unReadMsgList", unReadMsgList);
        return jsonObject;
    }
}
