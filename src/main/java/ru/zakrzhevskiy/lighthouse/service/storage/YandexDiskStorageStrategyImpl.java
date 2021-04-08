package ru.zakrzhevskiy.lighthouse.service.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.dto.StorageItemDto;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ru.zakrzhevskiy.lighthouse.service.storage.StorageConstants.READY;
import static ru.zakrzhevskiy.lighthouse.service.storage.StorageConstants.THUMBNAILS_DIR_NAME;
import static ru.zakrzhevskiy.lighthouse.service.storage.StorageHelper.*;

@Service("Yandex")
public class YandexDiskStorageStrategyImpl implements StorageStrategy {


    private final Logger logger = LoggerFactory.getLogger(YandexDiskStorageStrategyImpl.class);

    @Value("${temp.catalog.path}")
    private String tempPath;

    private AmazonS3 s3Storage;
    private String bucketName = "lighthouse-film-lab";

    @PostConstruct
    private void initYCStorage() {
        AWSCredentials credentials = new ProfileCredentialsProvider(
                ".aws/credentials",
                "default"
        ).getCredentials();

        s3Storage = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net",
                                "ru-central1"
                        )
                )
                .build();
        if (s3Storage.listBuckets().stream().noneMatch(bucket -> bucketName.equals(bucket.getName()))) {
            s3Storage.createBucket(bucketName);
        }
    }

    @Override
    public void createFolder(Order order, String additionalPath) {
        String clientName = order.getOrderOwner().getUsername();
        String orderNumber = String.valueOf(order.getOrderNumber());

        List<String> newFolderPathParts = new ArrayList<>();
        newFolderPathParts.add(READY);
        newFolderPathParts.add(clientName);
        newFolderPathParts.add(orderNumber);
        newFolderPathParts.addAll(Arrays.asList(additionalPath.split("/")));
        String newFolderPath = String.join("/", newFolderPathParts);

        PutObjectRequest request = new PutObjectRequest(bucketName, newFolderPath + "/", "");
        try {
            s3Storage.putObject(request);
            logger.info("Successfully created folder {}", newFolderPath);
        } catch (Exception e) {
            logger.error("Failed to create folder {}", newFolderPath);
        }
    }

    @Override
    public List<StorageItemDto> uploadFiles(Order order, String additionalPath, MultipartFile... multipartFiles) {
        logger.debug("bucket name====" + bucketName);
        Map<String, Map<String, Object>> files = convertMultiPartToFilesMap(multipartFiles);

        String clientName = order.getOrderOwner().getUsername();
        String orderNumber = String.valueOf(order.getOrderNumber());

        List<StorageItemDto> savedFiles = new ArrayList<>();

        files.forEach((key, value) -> {
            String additionalPathString = additionalPath != null && !additionalPath.isEmpty()
                    ? additionalPath + "/" : "";

            String originalPathToSave = String.join(
                    "/",
                    READY, clientName, orderNumber, (additionalPathString + key)
            );

            String thumbnailPathToSave = String.join(
                    "/",
                    READY, clientName, orderNumber, (additionalPathString + THUMBNAILS_DIR_NAME), key
            );

            File file = (File) value.get("file");
            BufferedImage thumbnail = createThumbnailImage(file);
            String contentType = (String) value.get("contentType");

            try {
                InputStream thumbnailIS = bufferedImageToInputStream(thumbnail);
                ObjectMetadata thumbnailMetadata = new ObjectMetadata();
                thumbnailMetadata.setContentType(contentType);

                PutObjectRequest originFilePutRequest = new PutObjectRequest(bucketName, originalPathToSave, file);
                PutObjectRequest thumbnailFilePutRequest = new PutObjectRequest(bucketName, thumbnailPathToSave, thumbnailIS, thumbnailMetadata);

                PutObjectResult originFile = s3Storage.putObject(originFilePutRequest);
                logger.info("File " + file.toPath() + " uploaded to bucket " + bucketName + " as " + originFile.getMetadata());

                PutObjectResult thumbnailFile = s3Storage.putObject(thumbnailFilePutRequest);
                logger.info("Thumbnail for " + file.toPath() + " uploaded to bucket " + bucketName + " as " + thumbnailFile.getMetadata());

                savedFiles.add(createStorageItem(thumbnailPathToSave, originalPathToSave));
            } catch (IOException e) {
                logger.error("Failed to read bytes of file", e);
            }

            if (!file.delete())
                logger.warn("Failed to delete file {}", file.getAbsolutePath());
        });

        return savedFiles;
    }

    @Override
    public boolean deleteFiles(Order order, String... storageItemUris) {
        List<Boolean> deleteResult = new ArrayList<>();

        String clientName = order.getOrderOwner().getUsername();
        String orderNumber = String.valueOf(order.getOrderNumber());

        String basePath = String.join(
                "/",
                READY, clientName, orderNumber, "");

        for (String uri : storageItemUris) {
            String thumbnailPath = String.join("/", basePath + THUMBNAILS_DIR_NAME, uri);
            String originPath = basePath + uri;

            try {
                s3Storage.deleteObject(new DeleteObjectRequest(bucketName, thumbnailPath));
                deleteResult.add(true);
            } catch (SdkClientException e) {
                logger.error("Failed to delete thumbnail '{}'", thumbnailPath);
                deleteResult.add(false);
            }
            try {
                s3Storage.deleteObject(new DeleteObjectRequest(bucketName, originPath));
                deleteResult.add(true);
            } catch (SdkClientException e) {
                logger.error("Failed to delete origin file '{}'", originPath);
                deleteResult.add(false);
            }
        }

        return deleteResult.contains(false);
    }

    @Override
    public ResponseEntity<Object> archiveAndDownloadFiles(String basePath) throws Exception {
        List<String> pathParts = Arrays.stream(basePath.split("/")).filter(x -> !x.isEmpty()).collect(toList());
        String archiveName = pathParts.get(pathParts.size() - 1);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);

        zipFiles(basePath, zos);
        zos.finish();

        byte[] arrayBytes = bos.toByteArray();

        final ByteArrayResource byteArrayResource = new ByteArrayResource(arrayBytes);

        return ResponseEntity
                .ok()
                .contentLength(arrayBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archiveName + ".zip\"")
                .body(byteArrayResource);
    }

    private void zipFiles(String path, ZipOutputStream zipOut) throws IOException {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName, path, path, null, null);
        ObjectListing objectListing = s3Storage.listObjects(listObjectsRequest);

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String fileName = objectSummary.getKey().replace(path, "");

            if (fileName.contains(THUMBNAILS_DIR_NAME + "/")) {
                continue;
            }

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectSummary.getKey());
            S3Object object = s3Storage.getObject(getObjectRequest);

            if (object.getObjectMetadata().getContentType().matches("image/.*")) {
                InputStream inputStream = object.getObjectContent();

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                inputStream.close();
            }
        }
    }

    @Override
    public List<StorageItemDto> listDirContent(String rootDir) {
        ObjectListing objectListing = s3Storage.listObjects(bucketName, rootDir + "/");
        List<StorageItemDto> result = new ArrayList<>();

        Set<String> keys = objectListing.getObjectSummaries()
                .stream()
                .map(object -> rootDir + "/" + object.getKey().replace(rootDir + "/", "").split("/")[0])
                .collect(toSet());

        keys.forEach(key -> {
            if (key.matches("^.*\\.(jpg|JPG|jpeg|JPEG|png|PNG)$")) {
                result.add(createStorageItem(key));
            } else {
                String[] parts = key.split("/");
                String folderName = parts[parts.length - 1];
                result.add(createStorageFolder(folderName));
            }
        });

        return result;
    }

    private StorageItemDto createStorageItem(String thumbnailPath, String originalPath) {
        GetObjectRequest originGetRequest = new GetObjectRequest(bucketName, originalPath);

        S3Object origin = s3Storage.getObject(originGetRequest);

        String[] pathParts = originalPath.split("/");

        String name = pathParts[pathParts.length - 1].split("\\.")[0];
        String url = getItemUrl(originalPath);
        String thumbnailUrl = getItemUrl(thumbnailPath);
        String contentType = origin.getObjectMetadata().getContentType();
        long contentLength = origin.getObjectMetadata().getContentLength();

        return new StorageItemDto(name, url, thumbnailUrl, contentType, contentLength);
    }

    private StorageItemDto createStorageItem(String itemPath) {
        return createStorageItem(generateThumbnailPath(itemPath), itemPath);
    }

    private StorageItemDto createStorageFolder(String name) {
        return new StorageItemDto(name, "", "", "folder", 0);
    }

    private String generateThumbnailPath(String itemPath) {
        ArrayList<String> pathParts = new ArrayList<>(Arrays.asList(itemPath.split("/")));

        pathParts.add(pathParts.size() - 1, THUMBNAILS_DIR_NAME);

        return String.join("/", pathParts);
    }

    private String getItemUrl(String itemPath) {
        // Set the presigned URL to expire after one hour.
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        logger.debug("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, itemPath)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        return s3Storage.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}
