package com.haust.minitalk;

import com.haust.minitalk.entity.FriendsRequest;
import com.haust.minitalk.entity.MyFriends;
import com.haust.minitalk.entity.Users;
import com.haust.minitalk.mapper.FriendsRequestMapper;
import com.haust.minitalk.mapper.MyFriendsMapper;
import com.haust.minitalk.mapper.UserMapper;
import com.haust.minitalk.service.UserService;
import com.haust.minitalk.utils.UUIDUtils;
import com.haust.minitalk.utils.face_check.FaceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Date;

@SpringBootTest
class MinitalkApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    void test01() {
        Users user = userMapper.selectByPrimaryKey("xp01");
        System.out.println(user);
    }

    @Test
    void test02() {
        Users user = new Users();
        user.setUsername("admin123");
        user.setId(UUIDUtils.getUUID());
        user.setNickname("admin123");
        user.setQrcode("");
        user.setPassword("123456");
        user.setFaceImage("");
        user.setFaceImageBig("");
        user.setCid("");
        userMapper.insert(user);
        System.out.println("数据插入成功");
    }

    @Test
    void test03() {
        Users user = userService.queryUserNameIsExit("admin123");
        System.out.println(user);
    }

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Test
    void test04(){
        FriendsRequest friendsRequest = new FriendsRequest();
        String friendsRequestId = UUIDUtils.getUUID();
        friendsRequest.setId(friendsRequestId).setSendUserId("C877CB8FA92E4595A11BAFD8D706C45D")
                .setAcceptUserId("C79BEED8253B4BE886890E6FDDEACB2F").setRequestDateTime(new Date());

        friendsRequestMapper.insert(friendsRequest);
    }
    @Test
    void test05(){
        Users user = userMapper.queryUserNameIsExit("admin");
        System.out.println("user-----"+user);
        MyFriends myFriends = new MyFriends();
        myFriends.setMyFriendUserId(user.getId()).setMyUserId("C877CB8FA92E4595A11BAFD8D706C45D");
        MyFriends myFriendsResult = myFriendsMapper.selectOneByExample(myFriends);

        System.out.println("myFriendsResult-----"+myFriendsResult);
    }

    @Test
    void test06(){
        FriendsRequest friendsRequest = friendsRequestMapper.selectBySendUserIdAndAcceptUserId("C877CB8FA92E4595A11BAFD8D706C45D", "C79BEED8253B4BE886890E6FDDEACB2F");
        System.out.println(friendsRequest);
    }

    @Test
    void test07(){
        // 获取到要检测的图片
//        File file = new File("E:\\本机图片\\Saved Pictures\\dc.jpg");
        File file = new File("E:\\本机图片\\Saved Pictures\\dc2.png");
        try {
            String faceToken = FaceUtil.detect(file);//人脸检测
            System.out.println("face_token:"+faceToken);
            // 添加一个人脸到集合中
//            boolean addResult = FaceUtil.addFace(faceToken);
//            System.out.println("人脸添加结果："+addResult);
            boolean searchResult = FaceUtil.search(faceToken);// 人脸搜索
            System.out.println("人脸集合搜索结果："+searchResult);
            // 获取人脸集合
            searchResult = FaceUtil.getDetail();
            if (!searchResult){
                // 如果没有该集合 就去创建
                searchResult = FaceUtil.createFaceSet();
                System.out.println("如果没有指定的人脸集合则创建新的："+searchResult);
            }
            System.out.println(searchResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test08(){
        System.out.println(7&1);
    }
}
