package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.OssStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private OssStorageService ossStorageService;

    @PostMapping(value = "/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadAvatar(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        String currentUserNumber = authContextService.getCurrentUserNumber(request);
        if (currentUserNumber == null) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }
        if (file == null || file.isEmpty()) {
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        if (!isSupportedImage(file.getContentType())) {
            return Result.failure(ResultCode.PARAM_IS_INVALID);
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            Result result = Result.failure(ResultCode.PARAM_IS_INVALID);
            result.setMessage("头像文件不能超过2MB");
            return result;
        }
        try {
            Map<String, String> data = ossStorageService.uploadAvatar(currentUserNumber, file);
            return Result.success(data);
        } catch (Exception ex) {
            Result result = Result.failure(ResultCode.SYSTEM_INNER_ERROR);
            result.setMessage(ex.getMessage());
            return result;
        }
    }

    private boolean isSupportedImage(String contentType) {
        if (contentType == null) {
            return false;
        }
        String type = contentType.toLowerCase();
        return type.contains("jpeg") || type.contains("jpg") || type.contains("png") || type.contains("webp");
    }
}

