package com.example.startlight.infra.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;

    public String uploadPetImg(MultipartFile file, String petId) throws IOException {
        // 사용자 ID와 고정 파일 이름을 결합하여 고유한 경로 생성
        String key = "petImgs/" + petId + ".jpg";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        String uploadFileUrl = "";

        try (InputStream inputStream = file.getInputStream()) {
            // 파일 업로드
            amazonS3Client.putObject(bucketName, key, inputStream, objectMetadata);
            uploadFileUrl = amazonS3Client.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        return uploadFileUrl;
    }

    public void deletePetImg(Long petId) {
        // 업로드할 때 사용한 파일 키와 동일한 형식으로 경로 생성
        String key = "petImgs/" + petId.toString() + ".jpg";

        try {
            // S3에서 파일 삭제
            amazonS3Client.deleteObject(bucketName, key);
            System.out.println("Image deleted successfully from S3: " + key);
        } catch (AmazonServiceException e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }


    public String uploadMemoryImg(MultipartFile file, Long memoryId) throws IOException {
        // 사용자 ID와 고정 파일 이름을 결합하여 고유한 경로 생성
        String key = "memoryImgs/" + memoryId.toString() + ".jpg";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        String uploadFileUrl = "";

        try (InputStream inputStream = file.getInputStream()) {
            // 파일 업로드
            amazonS3Client.putObject(bucketName, key, inputStream, objectMetadata);
            uploadFileUrl = amazonS3Client.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        return uploadFileUrl;
    }

    public void deleteMemoryImg(Long memoryId) {
        // 업로드할 때 사용한 파일 키와 동일한 형식으로 경로 생성
        String key = "memoryImgs/" + memoryId.toString() + ".jpg";

        try {
            // S3에서 파일 삭제
            amazonS3Client.deleteObject(bucketName, key);
            System.out.println("Image deleted successfully from S3: " + key);
        } catch (AmazonServiceException e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public String uploadPostImg(MultipartFile file, Long postId) throws IOException {
        // 사용자 ID와 고정 파일 이름을 결합하여 고유한 경로 생성
        String key = "postImgs/" + postId.toString() + ".jpg";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        String uploadFileUrl = "";

        try (InputStream inputStream = file.getInputStream()) {
            // 파일 업로드
            amazonS3Client.putObject(bucketName, key, inputStream, objectMetadata);
            uploadFileUrl = amazonS3Client.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        return uploadFileUrl;
    }

    public void deletePostImg(Long postId) {
        // 업로드할 때 사용한 파일 키와 동일한 형식으로 경로 생성
        String key = "postImgs/" + postId.toString() + ".jpg";

        try {
            // S3에서 파일 삭제
            amazonS3Client.deleteObject(bucketName, key);
            System.out.println("Image deleted successfully from S3: " + key);
        } catch (AmazonServiceException e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    private String getUrlFromKey(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }
}
