package com.haust.minitalk.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Auther: csp1999
 * @Date: 2020/09/25/12:07
 * @Description: 完整消息内容实体类
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DataContent implements Serializable {
    private Integer action;// 动作类型
    private UserChatMsg userChatMsg;// 用户聊天内容
    private String extend;// 扩展字段
}
