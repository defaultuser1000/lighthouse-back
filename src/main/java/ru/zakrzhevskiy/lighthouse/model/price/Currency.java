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
@Table(name = "CURRENCIES")
public class Currency extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("currencyId")
    private Long id;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String symbol;

}
