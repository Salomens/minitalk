package com.haust.minitalk.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Auther: csp1999
 * @Date: 2020/09/12/13:07
 * @Description: 用户头像扩展类
 */
@Data
@Accessors(chain = true)
@ToString
public class ImageVo {
    private String userId;// 用户id
    private String imgData;// 前端传来的由图片转成的base64 字符串
}
