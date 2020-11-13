package ru.zakrzhevskiy.lighthouse.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.dto.StorageItemDto;

import java.util.List;

@Service
public class StorageService {

    private final Logger log = LoggerFactory.getLogger(StorageService.class);

    @Autowired
    private StorageStrategy storageStrategy;

    public List<StorageItemDto> uploadFile(Order order, String additionalPath, MultipartFile... files) {
        return this.storageStrategy.uploadFiles(order, additionalPath, files);
    }

    public void deleteFiles(Order order, String... itemsUris) {
        boolean success = this.storageStrategy.deleteFiles(order, itemsUris);
        if (!success) {
            log.error("Failed to delete some files of order: {}", order);
        }
    }

    public ResponseEntity<Object> downloadFile(String fileUrl) throws Exception {
        return this.storageStrategy.archiveAndDownloadFiles(fileUrl);
    }

    public List<StorageItemDto> listDirContent(Order order, CharSequence additionalPath) {

        String rootDir = String.join(
                "/",
                "ready",
                order.getOrderOwner().getUsername(),
                order.getOrderNumber().toString()
        );

        if (additionalPath != null && additionalPath.length() > 0) {
            rootDir += "/" + StringUtils.chop(additionalPath.toString());
        }

        return this.storageStrategy.listDirContent(rootDir);
    }
}
