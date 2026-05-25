package com.zpark.sb.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OssStorageService {

    @Value("${aliyun.oss.endpoint:}")
    private String endpoint;
    @Value("${aliyun.oss.bucket:}")
    private String bucket;
    @Value("${aliyun.oss.access-key-id:}")
    private String accessKeyId;
    @Value("${aliyun.oss.access-key-secret:}")
    private String accessKeySecret;
    @Value("${aliyun.oss.public-url-prefix:}")
    private String publicUrlPrefix;
    @Value("${aliyun.oss.avatar-dir:avatar}")
    private String avatarDir;

    public Map<String, String> uploadAvatar(String employeeNumber, MultipartFile file) {
        validateOssConfig();
        String ext = getExtension(file);
        String key = buildObjectKey(employeeNumber, ext);
        OSS client = null;
        try (InputStream inputStream = file.getInputStream()) {
            client = buildClient();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            client.putObject(bucket, key, inputStream, metadata);
            Map<String, String> data = new HashMap<>();
            data.put("key", key);
            data.put("url", resolveAvatarUrl(key));
            return data;
        } catch (Exception ex) {
            throw new IllegalStateException("头像上传失败: " + ex.getMessage(), ex);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    public void deleteAvatarQuietly(String key) {
        if (isBlank(key)) {
            return;
        }
        validateOssConfig();
        OSS client = null;
        try {
            client = buildClient();
            client.deleteObject(bucket, key.trim());
        } catch (Exception ignore) {
            // Ignore delete failures to avoid blocking profile update.
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    public String resolveAvatarUrl(String avatarKey) {
        if (isBlank(avatarKey)) {
            return null;
        }
        String key = avatarKey.trim();
        if (key.startsWith("http://") || key.startsWith("https://")) {
            return key;
        }
        String prefix = buildPublicPrefix();
        return prefix.endsWith("/") ? prefix + key : prefix + "/" + key;
    }

    public boolean isAvatarKey(String value) {
        if (isBlank(value)) {
            return false;
        }
        String text = value.trim();
        return !text.startsWith("http://") && !text.startsWith("https://") && !text.startsWith("data:");
    }

    private OSS buildClient() {
        return new OSSClientBuilder().build(normalizeEndpoint(endpoint), accessKeyId, accessKeySecret);
    }

    private String buildObjectKey(String employeeNumber, String ext) {
        String dir = trimToDefault(avatarDir, "avatar");
        String number = trimToDefault(employeeNumber, "unknown");
        String suffix = isBlank(ext) ? ".jpg" : ext;
        return dir + "/" + number + "/" + UUID.randomUUID() + suffix;
    }

    private String getExtension(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return "";
        }
        String filename = file.getOriginalFilename();
        int index = filename.lastIndexOf('.');
        if (index < 0 || index >= filename.length() - 1) {
            return "";
        }
        return filename.substring(index).toLowerCase();
    }

    private String buildPublicPrefix() {
        if (!isBlank(publicUrlPrefix)) {
            return publicUrlPrefix.trim();
        }
        String normalizedEndpoint = normalizeEndpoint(endpoint);
        if (normalizedEndpoint.startsWith("http://") || normalizedEndpoint.startsWith("https://")) {
            String endpointHost = normalizedEndpoint.replaceFirst("^https?://", "");
            return "https://" + bucket + "." + endpointHost;
        }
        return "https://" + bucket + "." + normalizedEndpoint;
    }

    private String normalizeEndpoint(String value) {
        if (isBlank(value)) {
            return "";
        }
        String endpointText = value.trim();
        if (endpointText.startsWith("http://") || endpointText.startsWith("https://")) {
            return endpointText;
        }
        return "https://" + endpointText;
    }

    private void validateOssConfig() {
        if (isBlank(endpoint) || isBlank(bucket) || isBlank(accessKeyId) || isBlank(accessKeySecret)) {
            throw new IllegalStateException("OSS配置不完整，请设置 endpoint/bucket/access-key-id/access-key-secret");
        }
    }

    private String trimToDefault(String value, String defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

