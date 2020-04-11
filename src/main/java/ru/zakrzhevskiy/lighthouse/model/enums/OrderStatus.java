package ru.zakrzhevskiy.lighthouse.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {

    @JsonProperty("New")
    NEW,
    @JsonProperty("Processed")
    PROCESSED,
    @JsonProperty("Ready")
    READY

}
