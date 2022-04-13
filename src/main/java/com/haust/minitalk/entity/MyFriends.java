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
 * 好友实体类
 */
public class MyFriends {

  private String id;
  private String myUserId;
  private String myFriendUserId;
}
