package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IFileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileStorageService implements IFileStorageService {

    private final Path fileStorageLocation;

    public LocalFileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Không thể tạo thư mục lưu trữ file: uploads");
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (originalFileName.contains("..")) {
                throw new BusinessException("Tên file không hợp lệ " + originalFileName);
            }

            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf(".");
            if(dotIndex >= 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Construct the URL to access this file via HTTP (Assuming running on localhost:8080)
            return "http://localhost:8080/uploads/" + newFileName;
        } catch (IOException ex) {
            throw new BusinessException("Không thể lưu file " + originalFileName + ". Thử lại sau!");
        }
    }

    @Override
    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String url = storeFile(file);
                if (url != null) {
                    fileUrls.add(url);
                }
            }
        }
        return fileUrls;
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.contains("/uploads/")) {
            try {
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.deleteIfExists(targetLocation);
            } catch (IOException ex) {
                // Log the exception, but we might not want to throw an error
                System.err.println("Could not delete file: " + fileUrl);
            }
        }
    }
}
