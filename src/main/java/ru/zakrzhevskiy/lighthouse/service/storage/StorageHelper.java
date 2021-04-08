package ru.zakrzhevskiy.lighthouse.service.storage;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.groovy.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

public class StorageHelper {

    private static final Logger logger = LoggerFactory.getLogger(StorageHelper.class);

    public static Map<String, Map<String, Object>> convertMultiPartToFilesMap(MultipartFile... files) {
        return Arrays.stream(files).collect(toMap(StorageHelper::generateFileName, value -> {
            File convertedFile = new File(Objects.requireNonNull(value.getOriginalFilename()));
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(convertedFile);
                fos.write(value.getBytes());
                fos.close();
            } catch (IOException e) {
                logger.error("Failed to convert MultipartFile to File", e);
            }
            return Maps.of("contentType", value.getContentType(), "file", convertedFile);
        }));
    }

    public static BufferedImage createThumbnailImage(File file) {
        BufferedImage originalImage;
        BufferedImage thumbnail = null;
        try {
            originalImage = ImageIO.read(file);
            int targetWidth = 250;
            int targetHeight = originalImage.getHeight() / (originalImage.getWidth() / 250);
            thumbnail = resizeImage(originalImage, targetWidth, targetHeight);
        } catch (Exception e) {
            logger.error("Failed to create thumbnail", e);
        }
        return thumbnail;
    }

    private static String generateFileName(MultipartFile multiPart) {
        return Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(targetWidth, targetHeight)
                .outputFormat("JPEG")
                .outputQuality(1)
                .toOutputStream(outputStream);
        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return ImageIO.read(inputStream);
    }

    public static byte[] bufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    public static InputStream bufferedImageToInputStream(BufferedImage image) throws IOException {
        return new ByteArrayInputStream(bufferedImageToByteArray(image));
    }


}
