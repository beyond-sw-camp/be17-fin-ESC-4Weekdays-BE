package com.fourweekdays.fourweekdays.image.service;

import com.fourweekdays.fourweekdays.image.entity.Image;
import com.fourweekdays.fourweekdays.image.repository.ImageRepository;
import com.fourweekdays.fourweekdays.product.exception.ProductException;
import com.fourweekdays.fourweekdays.product.exception.ProductExceptionType;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final UploadService uploadService;

    /*
     * 1. 파일을 S3에 업로드 한다.
     * 2. 업로드 하고 S3에 저장된 파일 URL을 DB에 저장한다.
     * */
    public void upload(Product product, List<MultipartFile> files) {
        try {
            for (MultipartFile file : files) {

                // 파일을 서버에 저장
                String fileurl = uploadService.upload(file);    // 실제 파일 업로드(S3)

                Image image = Image.builder()
                        .fileName(fileurl)  // 어떤 상품의 이미지인지 명시
                        .product(product)
                        .build();

                imageRepository.save(image);    // 이미지 메타데이터 DB저장
            }
        } catch (Exception e) {
            throw new ProductException(ProductExceptionType.FILE_UPLOAD_FAILED);
        }
    }
}
