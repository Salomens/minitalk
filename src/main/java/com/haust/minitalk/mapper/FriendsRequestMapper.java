package com.haust.minitalk.mapper;

import com.haust.minitalk.entity.FriendsRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsRequestMapper {
    int deleteByPrimaryKey(String id);

    int insert(FriendsRequest record);

    int insertSelective(FriendsRequest record);

    FriendsRequest selectByPrimaryKey(String id);

    FriendsRequest selectBySendUserIdAndAcceptUserId(@Param("sendUserId") String sendUserId,@Param("acceptUserId") String acceptUserId);

    int updateByPrimaryKeySelective(FriendsRequest record);

    int updateByPrimaryKey(FriendsRequest record);

    //根据好友请求对象进行删除操作
    void deleteByFriendRequest(FriendsRequest friendsRequest);
}