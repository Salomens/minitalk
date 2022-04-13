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
 * User 扩展实体类
 */
public class UserVo {
    private String id;// 用户id
    private String username;// 用户名
    private String faceImage;// 用户头像缩略图
    private String faceImageBig;// 用户头像大图
    private String nickname;// 用户昵称
    private String qrcode;// 用户二维码
}