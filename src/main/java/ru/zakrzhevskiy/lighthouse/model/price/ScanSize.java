package ru.zakrzhevskiy.lighthouse.model.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SCAN_SIZES")
public class ScanSize extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("scanSizeId")
    @Column(updatable = false, insertable = false)
    private Long id;

    @Column(unique = true)
    private String size;

    private String description;

}
