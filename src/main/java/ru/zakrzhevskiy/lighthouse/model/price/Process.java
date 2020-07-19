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
@Table(name = "PROCESSES")
public class Process extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("processId")
    private Long id;

    @Column(unique = true)
    private String name;

}
