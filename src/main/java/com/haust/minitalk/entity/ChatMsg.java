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
 * 聊天信息实体类
 */
public class ChatMsg {

  private String id;
  private String sendUserId;
  private String acceptUserId;
  private String msg;
  private Integer signFlag;
  private Date createTime;
}
