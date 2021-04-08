package ru.zakrzhevskiy.lighthouse.model.dto;

public class StorageItemDto {

    private String itemName;
    private String url;
    private String thumbnailUrl;
    private String contentType;
    private long size;

    public StorageItemDto() {

    }

    public StorageItemDto(String itemName, String url, String thumbnailUrl, String contentType, long size) {
        this.itemName = itemName;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.contentType = contentType;
        this.size = size;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
