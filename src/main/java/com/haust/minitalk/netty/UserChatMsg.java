package com.haust.minitalk.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Auther: csp1999
 * @Date: 2020/09/25/12:10
 * @Description: 用户聊天内容实体类
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserChatMsg implements Serializable {
    private String senderId;// 发送者Id
    private String receiverId;// 接收者Id
    private String msg;// 聊天内容
    private String msgId;// 聊天Id 用于消息的签收
}
