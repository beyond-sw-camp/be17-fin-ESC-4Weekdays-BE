package com.fourweekdays.fourweekdays.image.service;

import com.fourweekdays.fourweekdays.image.utils.FileUploadUtil;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//@Service
@RequiredArgsConstructor
public class S3UploadService implements UploadService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Operations s3Operations;

    @Override
    public String upload(MultipartFile file) throws IOException {
        // 날짜별 경로 생성 (예: 2025/10/20/)
        String dirPath = FileUploadUtil.makeUploadPath();

        // S3 업로드 수행
        S3Resource resource = s3Operations.upload(
                bucketName,
                dirPath + file.getOriginalFilename(),
                file.getInputStream()
        );

        // 업로드된 파일의 전체 S3 URL 반환
        return resource.getURL().toString();
    }
}
