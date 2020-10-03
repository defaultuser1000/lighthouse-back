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
@Table(name = "SCAN_SIZES")
public class ScanSize extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("scanSizeId")
    @Column(updatable = false, insertable = false)
    @JsonView(View.Full.class)
    private Long id;

    @Column(unique = true)
    @JsonView({View.Short.class, View.Full.class, View.OrderUser.class})
    private String size;

    @JsonView({View.Short.class, View.Full.class})
    private String description;

}
