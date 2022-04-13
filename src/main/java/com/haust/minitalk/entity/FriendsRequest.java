package com.haust.minitalk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
/**
 * 好友请求实体类
 */
public class FriendsRequest {
  private String id;
  private String sendUserId;// 发送者Id
  private String acceptUserId;// 接收者Id
  private Date requestDateTime;
}
