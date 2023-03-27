package com.controller;

import com.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Lenovo
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${takeaway.path}")
    private String path;

    /**
     * 文件上传到服务端
     * @param file 需要与前端的参数名相同
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //file 是一个临时文件，需要转存到指定位置，否则本次请求完后临时文件会被删除
        log.info(file.toString());

        //原始文件名，不建议在使用其来当转存后的文件名，容易出现重名，造成文件覆盖
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止因为文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString()+suffix;

        //判断目录是否存在，不存在则创建一个
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }

        //将文件转存到指定位置
        file.transferTo(new File(path+fileName));

        return R.success(fileName);
    }

    /**
     * 文件下载到浏览器
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(path + name));
        //输出流，通过输出流将文件写回浏览器，从而可以在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();

        //设置响应回去的文件类型为图片
        response.setContentType("image/jpeg");

        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();

    }

}
