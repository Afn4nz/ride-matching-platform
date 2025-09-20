package com.ridematch.driver.service;

import com.ridematch.driver.configuration.MinioConfig;
import com.ridematch.driver.dto.DriverDetailsResponse;
import com.ridematch.driver.dto.FileDTO;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private static final String PREFIX_LICENSE = "license/";
    private static final String PREFIX_REGISTRATION = "registration/";
    private static final String PREFIX_IMAGES = "images/";

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public void put(String objectKey, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            long size = file.getSize() > 0 ? file.getSize() : -1;
            PutObjectArgs args =
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectKey)
                            .contentType(file.getContentType())
                            .stream(is, size, 10 * 1024 * 1024)
                            .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to MinIO: " + objectKey, e);
        }
    }

    public DriverDetailsResponse populateDriverFiles(Long id, DriverDetailsResponse dto) {
        String base = driverPrefix(id);
        FileDTO license = null;
        FileDTO registration = null;
        List<FileDTO> images = new ArrayList<>();

        try {
            Iterable<Result<Item>> results =
                    minioClient.listObjects(
                            ListObjectsArgs.builder()
                                    .bucket(minioConfig.getBucket())
                                    .prefix(base)
                                    .recursive(true)
                                    .build());

            for (Result<Item> r : results) {
                Item it = r.get();
                String fullKey = it.objectName();
                String rel = fullKey.substring(base.length());

                if (rel.isEmpty() || rel.endsWith("/")) continue;

                String url = presign(fullKey, Duration.ofMinutes(30));
                FileDTO file =
                        new FileDTO(
                                rel,
                                it.lastModified() == null
                                        ? null
                                        : it.lastModified().toInstant().toString(),
                                url);

                if (rel.startsWith(PREFIX_LICENSE)) {
                    license = file;
                } else if (rel.startsWith(PREFIX_REGISTRATION)) {
                    registration = file;
                } else if (rel.startsWith(PREFIX_IMAGES)) {
                    images.add(file);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list/presign driver files for id=" + id, e);
        }

        dto.setLicense(license);
        dto.setVehicleRegistration(registration);
        dto.setImages(images);
        return dto;
    }

    private String driverPrefix(Long id) {
        return "drivers/" + id + "/";
    }

    private String presign(String objectKey, Duration ttl) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucket())
                            .object(objectKey)
                            .expiry((int) ttl.toSeconds())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to presign: " + objectKey, e);
        }
    }
}
