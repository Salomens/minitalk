package com.haust.minitalk.config;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Auther: csp1999
 * @Date: 2020/09/12/13:31
 * @Description: FastDFS 客户端
 */
@Component
public class FastDFSClient {

    @Autowired
    private FastFileStorageClient storageClient;

//	@Autowired
//	private AppConfig appConfig; // 项目参数配置

    /*
     * 上传文件
     * @param: file 文件对象
     * @return: java.lang.String 返回文件访问地址
     * @create: 2020/9/28 21:46
     * @author: csp1999
     */
    public String uploadFile(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);

        return storePath.getPath();
    }

    /*
     * 上传头像并生成缩略图
     * @param: file
     * @return: java.lang.String
     * @create: 2020/9/28 21:48
     * @author: csp1999
     */
    public String uploadFile2(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);

        return storePath.getPath();
    }

    /*
     * 上传用户二维码
     * @param: file
     * @return: java.lang.String
     * @create: 2020/9/28 21:49
     * @author: csp1999
     */
    public String uploadQRCode(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                "png", null);

        return storePath.getPath();
    }

    public String uploadFace(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(),
                "png", null);

        return storePath.getPath();
    }

    /*
     * base64 文件上传
     * @param: file
     * @return: java.lang.String
     * @create: 2020/9/28 21:51
     * @author: csp1999
     */
    public String uploadBase64(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(),
                "png", null);

        return storePath.getPath();
    }

    /*
     * 将一段字符串生成一个文件上传
     * @param: content 文件内容
     * @param: fileExtension
     * @return: java.lang.String
     * @create: 2020/9/28 21:51
     * @author: csp1999
     */
    public String uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(Charset.forName("UTF-8"));
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtension, null);
        return storePath.getPath();
    }

    /**
     * 封装图片完整URL地址
     * private String getResAccessUrl(StorePath storePath) {
     * 		String fileUrl = AppConstants.HTTP_PRODOCOL + appConfig.getResHost() + ":" + appConfig.getFdfsStoragePort()
     * 				+ "/" + storePath.getFullPath();
     * 		return fileUrl;
     * }
     */

    /*
     * 删除文件
     * @param: fileUrl 图片访问地址
     * @return: void
     * @create: 2020/9/28 21:50
     * @author: csp1999
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            e.getMessage();
        }
    }
}

