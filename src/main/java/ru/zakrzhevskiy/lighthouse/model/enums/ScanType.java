package ru.zakrzhevskiy.lighthouse.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ScanType {

    @JsonProperty("Basic")
    BASIC,
    @JsonProperty("Premium")
    PREMIUM

}
