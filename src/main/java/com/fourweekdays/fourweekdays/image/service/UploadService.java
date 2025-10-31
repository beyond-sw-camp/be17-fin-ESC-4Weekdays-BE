package com.fourweekdays.fourweekdays.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

public interface UploadService {
    String upload(MultipartFile file) throws IOException;
}
