package ru.zakrzhevskiy.lighthouse.model;

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
@Table(name = "ORDER_STATUSES")
public class OrderStatus extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("orderStatusId")
    @Column(updatable = false, insertable = false)
    private Long id;

    @Column(unique = true)
    private String displayName;

    private Long nextStatusId;

}
