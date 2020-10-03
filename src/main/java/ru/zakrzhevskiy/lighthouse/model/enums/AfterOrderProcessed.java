package ru.zakrzhevskiy.lighthouse.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AfterOrderProcessed {

    @JsonProperty("utilize")
    RECOVER("utilize"),

    @JsonProperty("save-self-return")
    SAVE("save-self-return"),

    @JsonProperty("send-after-scan")
    SEND_BY_MAIL("send-after-scan");

    AfterOrderProcessed(String name) { }

}
