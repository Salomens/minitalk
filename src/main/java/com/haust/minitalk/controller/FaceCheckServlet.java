package com.haust.minitalk.controller;

import com.alibaba.fastjson.JSONObject;
import com.haust.minitalk.utils.face_check.FaceUtil;
import com.haust.minitalk.utils.face_check.ImageUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: csp1999
 * @Date: 2020/09/19/10:26
 * @Description:
 */
@WebServlet("/login")
public class FaceCheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonObject = new JSONObject();

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
                System.out.println("类型："+type);
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
            jsonObject.put("msg", result);
            PrintWriter printWriter = response.getWriter();
            printWriter.write(String.valueOf(jsonObject));
            printWriter.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
