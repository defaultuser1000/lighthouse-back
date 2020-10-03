package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TRANSPORT_COMPANIES")
public class TransportCompany {

    @Id
    @GeneratedValue
    @JsonProperty("orderId")
    @Column(updatable = false, insertable = false)
    private Long id;

    private String code;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private List<TransportCompanyName> companyNames;

}
