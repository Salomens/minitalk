package com.haust.minitalk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
/**
 * 用户实体类
 */
public class Users {

  private String id;
  private String username;
  private String password;
  private String faceImage;
  private String faceImageBig;
  private String nickname;
  private String qrcode;
  private String cid;
}
