package ru.zakrzhevskiy.lighthouse.model.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;

import javax.persistence.*;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PRICE_LIST")
public class PriceList extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("priceListId")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_type_id", referencedColumnName = "id", nullable = false)
    private OrderType orderType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scanner_id", referencedColumnName = "id", nullable = false)
    private Scanner scanner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "process_id", referencedColumnName = "id", nullable = false)
    private Process process;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "film_format_id", referencedColumnName = "id", nullable = false)
    private FilmFormat filmFormat;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scan_size_id", referencedColumnName = "id", nullable = false)
    private ScanSize scanSize;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "price_list_id")
    private Set<Price> prices;

}
