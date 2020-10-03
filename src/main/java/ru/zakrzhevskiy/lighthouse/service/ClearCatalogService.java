package ru.zakrzhevskiy.lighthouse.service;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ClearCatalogService {

    @Value("${temp.catalog.path:/home/tmp}")
    private String tmpCatalogPath;

    @SneakyThrows
    public void clearCatalogRecursively() {
        Path rootPath = Paths.get(tmpCatalogPath);

        try {
            FileUtils.cleanDirectory(rootPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to clear directory '%s' recursively.", rootPath), e);
        }
    }

}
