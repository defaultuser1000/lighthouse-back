package ru.zakrzhevskiy.lighthouse.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Contrast {

    @JsonProperty("Low")
    LOW,
    @JsonProperty("Neutral")
    NEUTRAL,
    @JsonProperty("High")
    HIGH
}
