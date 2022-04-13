package com.haust.minitalk.mapper;

import com.haust.minitalk.entity.Users;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    int deleteByPrimaryKey(String id);

    //根据用户名查找指定用户对象
    Users queryUserNameIsExit(String username);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    //更新登录设备信息
    void updateCidByUserId(@Param("id") String id, @Param("cid") String cid);
}