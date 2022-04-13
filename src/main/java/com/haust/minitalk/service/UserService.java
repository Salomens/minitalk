package com.haust.minitalk.service;

import com.haust.minitalk.entity.ChatMsg;
import com.haust.minitalk.entity.FriendsRequest;
import com.haust.minitalk.entity.Users;
import com.haust.minitalk.netty.UserChatMsg;
import com.haust.minitalk.vo.FriendsRequestVO;
import com.haust.minitalk.vo.MyFriendsVO;

import java.util.List;

/**
 * @Auther: csp1999
 * @Date: 2020/09/01/20:18
 * @Description: 用户接口
 */
public interface UserService {

    Users getUserById(String id);

    //根据用户名查找指定用户对象
    Users queryUserNameIsExit(String username);

    //保存
    Users insert(Users user);

    //修改用户
    Users updateUserInfo(Users user);

    //搜索好友的前置条件接口,返回状态 1,2,3
    Integer preconditionSearchFriends(String myUserId, String friendUserName);

    //发送好友请求
    String sendFriendRequest(String myUserId, String friendUserName);

    //好友请求列表查询
    List<FriendsRequestVO> queryFriendRequestList(String acceptUserId);

    //处理好友请求——忽略好友请求
    void deleteFriendRequest(FriendsRequest friendsRequest);

    //处理好友请求——通过好友请求
    void passFriendRequest(String sendUserId, String acceptUserId);

    //好友列表查询
    List<MyFriendsVO> queryMyFriends(String userId);

    //保存用户聊天消息
    String saveMsg(UserChatMsg userChatMsg);

    // 批量更新消息状态标记
    void updateMsgSigned(List<String> msgIdList);

    //获取未签收的消息列表
    List<ChatMsg> getUnReadMsgList(String acceptUserId);

    //更新登录设备信息
    void updateCidByUserId(String id, String cid);
}
