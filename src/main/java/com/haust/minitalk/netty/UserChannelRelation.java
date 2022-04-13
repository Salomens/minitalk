package com.haust.minitalk.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: csp1999
 * @Date: 2020/09/25/12:30
 * @Description: 用户管道关系实体类(用于将客户端获取的管道和userid 关联)
 */
public class UserChannelRelation {
    private static HashMap<String, Channel> manage = new HashMap<>();

    public static void put(String senderId, Channel channel) {
        manage.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return manage.get(senderId);
    }

    // 用于测试
    public static void output() {
        for (Map.Entry<String, Channel> entry : manage.entrySet()) {
            System.out.println("UserId: " + entry.getKey()
                    + ",ChannelId: " + entry.getValue().id().asLongText());
        }
    }
}
