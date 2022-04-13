package com.haust.minitalk.mapper;

import com.haust.minitalk.vo.FriendsRequestVO;
import com.haust.minitalk.vo.MyFriendsVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自定义UserMapper 扩展
 */
@Repository
public interface UserMapperCustom {
    // 获取好友请求列表
    List<FriendsRequestVO> queryFriendRequestList(String acceptUserId);

    // 我的好友列表
    List<MyFriendsVO> queryMyFriends(String userId);

    // 批处理更新消息为已签收
    void batchUpdateMsgSigned(List<String> msgIdList);
}
