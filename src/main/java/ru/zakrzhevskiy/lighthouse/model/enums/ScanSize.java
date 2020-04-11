package ru.zakrzhevskiy.lighthouse.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ScanSize {

    @JsonProperty("L")
    L,
    @JsonProperty("XL")
    XL,
    @JsonProperty("TIFF")
    TIFF

}
