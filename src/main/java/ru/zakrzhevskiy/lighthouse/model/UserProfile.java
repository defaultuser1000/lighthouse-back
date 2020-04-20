package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @JsonProperty("userId")
    private Long id;
    private String username;
    private String fio;
    private byte[] avatar;

}