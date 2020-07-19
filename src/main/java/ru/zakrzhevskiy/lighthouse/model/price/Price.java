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
@Table(name = "PRICES")
public class Price extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("priceId")
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    private Double value;

}
