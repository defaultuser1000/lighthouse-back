package ru.zakrzhevskiy.lighthouse.model.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;
import ru.zakrzhevskiy.lighthouse.model.views.View;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SCANNERS")
public class Scanner extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("scannerId")
    private Long id;

    @Column(unique = true)
    @JsonView(View.OrderUser.class)
    private String name;

    private String description;

    private String yearOfManufacture;

}
