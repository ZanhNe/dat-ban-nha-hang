package com.ou.nhahang.dat_ban_nha_hang.service.port;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IFileStorageService {
    String storeFile(MultipartFile file);
    List<String> storeFiles(List<MultipartFile> files);
    void deleteFile(String fileUrl);
}
