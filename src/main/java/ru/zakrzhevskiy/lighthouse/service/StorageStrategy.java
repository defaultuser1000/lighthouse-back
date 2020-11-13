package ru.zakrzhevskiy.lighthouse.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.dto.StorageItemDto;

import java.util.List;

public interface StorageStrategy {

    List<StorageItemDto> uploadFiles(Order order, String additionalPath, MultipartFile... multipartFile);

    boolean deleteFiles(Order order, String... storageItemUris);

    ResponseEntity<Object> archiveAndDownloadFiles(String fileUrl) throws Exception;

    List<StorageItemDto> listDirContent(String rootDir);

}
