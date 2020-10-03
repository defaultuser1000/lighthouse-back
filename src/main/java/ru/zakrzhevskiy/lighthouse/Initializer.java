package ru.zakrzhevskiy.lighthouse;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Initializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Value("${temp.catalog.path:/tmp/order_forms_generation}")
    private String tmpPath;

    @Override
    @SneakyThrows
    public void run(String... strings) {
        Path tmpRoot = Paths.get(tmpPath);

        if (!Files.exists(tmpRoot)) {
            logger.debug("Attempt to create app temp directory '{}'.", tmpRoot.toFile().getAbsolutePath());
            Files.createDirectory(tmpRoot);
            logger.debug("Temp directory '{}' successfully created.", tmpRoot.toFile().getAbsolutePath());
        }
    }
}
