package ru.zakrzhevskiy.lighthouse.model.reference_gallery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "REFERENCE_PHOTOS")
public class ReferencePhoto extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("photoId")
    @Column(updatable = false, insertable = false)
    @JsonView({View.Short.class, View.Full.class})
    private Long id;

    private Long userId;

    @Lob
    @JsonView(View.Full.class)
    private byte[] photo;

    @Lob
    @JsonView(View.Short.class)
    private byte[] thumbnail;

    @JsonView(View.Full.class)
    private String description;

}
