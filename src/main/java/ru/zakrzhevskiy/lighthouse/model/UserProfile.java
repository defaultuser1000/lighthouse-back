package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

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
    private Set<Role> roles;

}