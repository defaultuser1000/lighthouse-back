package ru.zakrzhevskiy.lighthouse.service;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.groovy.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.FirebaseCredential;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.dto.StorageItemDto;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class FirebaseStorageStrategyImpl implements StorageStrategy {

    @Value("${temp.catalog.path}")
    private String tempPath;

    private static int recursionDepth = 0;
    public static final String THUMBNAILS_DIR_NAME = "thumbnails";
    private final Logger logger = LoggerFactory.getLogger(FirebaseStorageStrategyImpl.class);

    private final String ADMINSDK_JSON = "lighthouse-fl-photo-firebase-storage.json";
    private final FirebaseCredential firebaseCredential;

    private StorageOptions storageOptions;
    private String bucketName = "lighthouse-fl-photo.appspot.com";

    FirebaseStorageStrategyImpl() throws IOException {
        Gson gson = new Gson();

        File credPath = new ClassPathResource(this.ADMINSDK_JSON).getFile();
        JsonReader reader = new JsonReader(new FileReader(credPath));
        this.firebaseCredential = gson.fromJson(reader, FirebaseCredential.class);
    }

    @PostConstruct
    private void initializeFirebase() throws Exception {
        byte[] credBytes = new Gson().toJson(this.firebaseCredential).getBytes(StandardCharsets.UTF_8);
        InputStream serviceAccountStream = new ByteArrayInputStream(credBytes);

        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(this.firebaseCredential.getProjectId())
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();
    }

    @Override
    public List<StorageItemDto> uploadFiles(Order order, String additionalPath, MultipartFile... multipartFiles) {
        logger.debug("bucket name====" + bucketName);
        Map<String, Map<String, Object>> files = convertMultiPartToFilesMap(multipartFiles);

        Storage storage = storageOptions.getService();

        String clientName = order.getOrderOwner().getUsername();
        String orderNumber = String.valueOf(order.getOrderNumber());

        List<StorageItemDto> savedFiles = new ArrayList<>();

        files.forEach((key, value) -> {
            String additionalPathString = additionalPath != null && !additionalPath.isEmpty() ? additionalPath + "/" : "";

            String originalPathToSave = String.join(
                    "/",
                    "ready", clientName, orderNumber, (additionalPathString + key)
            );

            String thumbnailPathToSave = String.join(
                    "/",
                    "ready", clientName, orderNumber, (additionalPathString + THUMBNAILS_DIR_NAME), key
            );

            File file = (File) value.get("file");

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

            String contentType = (String) value.get("contentType");

            BlobId originBlobId = BlobId.of(bucketName, originalPathToSave);
            BlobInfo originBlobInfo = BlobInfo.newBuilder(originBlobId)
                    .setContentType(contentType)
                    .setMetadata(Maps.of(
                            "firebaseStorageDownloadTokens", UUID.randomUUID().toString()
                    )).build();

            BlobId thumbnailBlobId = BlobId.of(bucketName, thumbnailPathToSave);
            BlobInfo thumbnailBlobInfo = BlobInfo.newBuilder(thumbnailBlobId)
                    .setContentType(contentType)
                    .setMetadata(Collections.singletonMap("firebaseStorageDownloadTokens", UUID.randomUUID().toString()))
                    .build();
            try {
                Blob originBlob = storage.create(originBlobInfo, Files.readAllBytes(file.toPath()));
                logger.info("File " + file.toPath() + " uploaded to bucket " + bucketName + " as " + originBlob.getName());

                Blob thumbnailBlob = storage.create(thumbnailBlobInfo, bufferedImageToByteArray(thumbnail));
                logger.info("Thumbnail for " + file.toPath() + " uploaded to bucket " + bucketName + " as " + thumbnailBlob.getName());

                savedFiles.add(createStorageItem(thumbnailBlob, originBlob));
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
        Storage storage = storageOptions.getService();

        String clientName = order.getOrderOwner().getUsername();
        String orderNumber = String.valueOf(order.getOrderNumber());

        String basePath = String.join(
                "/",
                "ready", clientName, orderNumber, "");

        List<BlobId> blobIds = new ArrayList<>();

        for (String uri : storageItemUris) {
            blobIds.addAll(
                    Arrays.asList(
                            BlobId.of(bucketName, basePath + uri),
                            BlobId.of(bucketName, String.join(
                                    "/",
                                    basePath + THUMBNAILS_DIR_NAME, uri)
                            )
                    )
            );
        }

        return storage.delete(blobIds).stream().noneMatch(result -> result.equals(false));
    }


    private byte[] bufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
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

    @Override
    public ResponseEntity<Object> archiveAndDownloadFiles(String basePath) throws Exception {

        List<String> pathParts = Arrays.stream(basePath.split("/")).filter(x -> !x.isEmpty()).collect(toList());
        String archiveName = pathParts.get(pathParts.size() - 1);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);

        zipFiles(basePath, "", zos);
        zos.finish();

        byte[] arrayBytes = bos.toByteArray();

        final ByteArrayResource byteArrayResource = new ByteArrayResource(arrayBytes);

        return ResponseEntity
                .ok()
                .contentLength(arrayBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archiveName + ".zip\"")
                .body(byteArrayResource);
    }

    private void zipFiles(String path, String subFoldersPath, ZipOutputStream zipOut) throws IOException {
        Storage storage = storageOptions.getService();
        Page<Blob> blobs = storage.list(bucketName,
                Storage.BlobListOption.currentDirectory(),
                Storage.BlobListOption.prefix(path));

        for (Blob blob : blobs.iterateAll()) {
            String[] filePathParts = blob.getName().split("/");
            String fileName = filePathParts[filePathParts.length - 1];

            if (fileName.equals(THUMBNAILS_DIR_NAME)) {
                continue;
            }

            if (blob.getContentType() == null) {

                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    zipOut.closeEntry();
                }
                recursionDepth++;
                zipFiles(path + fileName + "/", subFoldersPath + fileName + "/", zipOut);
                recursionDepth--;

                return;
            }
            ReadChannel reader = blob.reader();
            InputStream inputStream = Channels.newInputStream(reader);

            ZipEntry zipEntry = new ZipEntry(subFoldersPath + fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            inputStream.close();
        }
    }

    @Override
    public List<StorageItemDto> listDirContent(String rootDir) {
        Storage storage = storageOptions.getService();

        Page<Blob> blobs = storage.list(bucketName,
                Storage.BlobListOption.currentDirectory(),
                Storage.BlobListOption.prefix(rootDir + "/"));

        List<StorageItemDto> result = new ArrayList<>();

        blobs.iterateAll().forEach(blob -> result.add(createStorageItem(storage, blob)));

        return result;
    }

    private StorageItemDto createStorageItem(Storage storage, Blob blob) {
        String[] pathParts = blob.getName().split("/");
        Blob thumbBlob = null;

        if (blob.getContentType() != null) {
            thumbBlob = getThumbnailBlob(storage, pathParts);
        }

        return createStorageItem(thumbBlob, blob);
    }

    private StorageItemDto createStorageItem(Blob thumbnail, Blob origin) {
        String[] pathParts = origin.getName().split("/");
        String objectName = pathParts[pathParts.length - 1];

        return new StorageItemDto(
                objectName,
                origin.getContentType() != null
                        ? origin.signUrl(2, TimeUnit.HOURS).toString()
                        : "",
                thumbnail != null ?
                        thumbnail.signUrl(2, TimeUnit.HOURS).toString()
                        : "",
                origin.getContentType() != null
                        ? origin.getContentType()
                        : "folder",
                origin.getSize());
    }

    private Blob getThumbnailBlob(Storage storage, String[] pathParts) {
        String[] thumbnailPathParts = new String[pathParts.length + 1];
        for (int i = 0; i < thumbnailPathParts.length; i++) {
            if (i < pathParts.length - 1) {
                thumbnailPathParts[i] = pathParts[i];
            } else {
                if (i < thumbnailPathParts.length - 1) {
                    thumbnailPathParts[i] = THUMBNAILS_DIR_NAME;
                } else {
                    thumbnailPathParts[i] = pathParts[i - 1];
                }
            }
        }
        String thumbnailPath = String.join("/", thumbnailPathParts);
        BlobId thumbBlobId = BlobId.of(bucketName, thumbnailPath);
        return storage.get(thumbBlobId);
    }

    private Map<String, Map<String, Object>> convertMultiPartToFilesMap(MultipartFile... files) {
        return Arrays.stream(files).collect(toMap(this::generateFileName, value -> {
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

    private String generateFileName(MultipartFile multiPart) {
        return Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    public InputStream getFirebaseCredentials() throws Exception {
        return IOUtils.toInputStream(new Gson().toJson(this.firebaseCredential), StandardCharsets.UTF_8);
    }

}
