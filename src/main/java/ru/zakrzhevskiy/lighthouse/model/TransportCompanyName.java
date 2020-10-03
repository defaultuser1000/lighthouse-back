package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TRANSPORT_COMPANY_NAMES")
public class TransportCompanyName {

    @Id
    @GeneratedValue
    @JsonProperty("orderId")
    @Column(updatable = false, insertable = false)
    private Long id;
    private String name;
    private Locale locale;

}
