package org.y.notepad.web.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.y.notepad.model.entity.UploadInfo;
import org.y.notepad.service.UploadInfoService;
import org.y.notepad.util.FileUtil;
import org.y.notepad.web.util.WebUtil;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${upload.upload-dir}")
    private String uploadDir;

    private final UploadInfoService uploadInfoService;

    public UploadController(UploadInfoService uploadInfoService) {
        this.uploadInfoService = uploadInfoService;
    }

    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return 上传成功返回文件信息ID(> 0)
     */
    @RequestMapping("/up")
    public JSONObject upload(@RequestParam("editormd-image-file") MultipartFile file) {
        JSONObject jsonObject = new JSONObject();

        UploadInfo info = null;
        try {
            FileUtil.FileWrapper fileWrapper = FileUtil.upload(file, uploadDir);
            info = new UploadInfo();
            if (null != fileWrapper) {
                info.setPath(fileWrapper.PATH);
                info.setSize(fileWrapper.FILE.length());
                info.setType(fileWrapper.FILE_TYPE);
                info.setName(fileWrapper.ORIGIN_FILE_NAME);
                info.setRelativePath(fileWrapper.RELATIVE_PATH);
                uploadInfoService.upload(info);
            }
        } catch (Exception e) {
            log.warn("上传失败, " + e.getMessage());
        }

        boolean isOk = (null != info);
        if (isOk) {
            jsonObject.put("success", 1);
            jsonObject.put("message", "上传成功");
            jsonObject.put("url", "/upload/img/" + info.getId());
        } else {
            jsonObject.put("success", 0);
            jsonObject.put("message", "上传失败");
        }

        return jsonObject;
    }

    /**
     * 页面显示图片
     *
     * @param id 上传信息ID
     */
    @RequestMapping("/img/{id}")
    public void image(@PathVariable int id) {
        UploadInfo info = uploadInfoService.getById(id);
        if (null != info)
            WebUtil.writeImage(new File(info.getPath()));
    }

}
