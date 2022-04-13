package com.haust.minitalk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
/**
 * 好友请求实体类
 */
public class FriendsRequestVO {

    private String sendUserId;// 发送者id
    private String sendUsername;// 发送者username
    private String sendFaceImage;// 发送者头像
    private String sendNickname;// 发送者昵称
}