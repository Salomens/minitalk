package com.haust.minitalk.controller;

import com.alibaba.fastjson.JSONObject;
import com.haust.minitalk.utils.face_check.FaceUtil;
import com.haust.minitalk.utils.face_check.ImageUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: csp1999
 * @Date: 2020/09/01/19:20
 * @Description: 测试
 */
@Controller
@RequestMapping("/test")
//@PropertySource(value = "classpath:application.properties")// 从类路径下的application.properties 读取配置
public class TestController {
//    @Value("${API_KEY}")
//    private String api_key;


    @ResponseBody
    @GetMapping("/test")
    public String test() {
        // 获取到要检测的图片
        File file = new File("E:\\本机图片\\Saved Pictures\\dc.jpg");
        String faceToken = null;
        try {
            faceToken = FaceUtil.detect(file);
            System.out.println("face_token" + faceToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return faceToken;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = new JSONObject();
        System.out.println(request.getParameter("imgData"));
        //1. 获取客户上传的图片 上传到指定文件夹(上传到fastDFS)
        File file = ImageUtils.uploadImg(request, "imgData", "upimg");
        //2. 判断是否含人脸信息，detect
        boolean result = false;
        boolean delFalg = true;
        try {
            //2. 判断是否含人脸信息，detect
            String faceToken = FaceUtil.detect(file);
            if (faceToken != null) {// 包含：
                // 查找在faceSet 中是否有相似度高的人脸信息，search
                result = FaceUtil.search(faceToken);// 有，登录成功/无，登录失败
                // 判断用户请求的类型
                String type = request.getParameter("type");
                System.out.println("类型：" + type);
                if ("register".equals(type)) {
                    if (result) {// 已经注册
                        result = false;
                    } else {// 没有注册,添加faceToken 到faceSet 中去
                        FaceUtil.addFace(faceToken);
                        delFalg = false;// 不删除照片
                    }
                }
            } else {// 不包含，登录失败:

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 不论登录成功或失败都要删除照片
            if (delFalg) {
                file.delete();
            }
            // 返回数据给客户端
            jsonObject.put("status", result);
            PrintWriter printWriter = response.getWriter();
            printWriter.write(String.valueOf(jsonObject));
            printWriter.close();
        }
    }
}
