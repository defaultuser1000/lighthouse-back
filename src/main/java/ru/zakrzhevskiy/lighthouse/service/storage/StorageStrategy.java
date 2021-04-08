package ru.zakrzhevskiy.lighthouse.service.storage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.dto.StorageItemDto;

import java.util.List;

public interface StorageStrategy {

    void createFolder(Order order, String additionalPath);

    List<StorageItemDto> uploadFiles(Order order, String additionalPath, MultipartFile... multipartFiles);

    boolean deleteFiles(Order order, String... storageItemUris);

    ResponseEntity<Object> archiveAndDownloadFiles(String basePath) throws Exception;

    List<StorageItemDto> listDirContent(String rootDir);

}
