package com.haust.minitalk.utils;

import java.util.UUID;

/**
 * @Auther: csp1999
 * @Date: 2020/09/02/13:05
 * @Description:
 */
public class UUIDUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "")// 去掉默认自带的 - 分隔符
                .substring(0, 32).toUpperCase();// 截取 32 位
    }
}
