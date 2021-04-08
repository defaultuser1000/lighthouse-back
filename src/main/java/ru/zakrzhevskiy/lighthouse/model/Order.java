package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;
import ru.zakrzhevskiy.lighthouse.model.enums.*;
import ru.zakrzhevskiy.lighthouse.model.price.OrderType;
import ru.zakrzhevskiy.lighthouse.model.price.ScanSize;
import ru.zakrzhevskiy.lighthouse.model.price.Scanner;
import ru.zakrzhevskiy.lighthouse.model.views.View;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ORDERS")
@JsonView(View.OrderUser.class)
public class Order extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("orderId")
    @Column(updatable = false, insertable = false)
    private Long id;
    private Integer orderNumber;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="order_status_id", nullable=false)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name="user_owner_id", nullable=false)
    private User orderOwner;

    @ManyToOne
    @JoinColumn(name="user_creator_id", nullable=false)
    private User orderCreator;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="scanner_id")
    private Scanner scanner;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="order_type_id", nullable=false)
    private OrderType orderType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="scan_size_id")
    private ScanSize scanSize;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private final Set<Film> orderFilms = new HashSet<>();

    private String special;

    @Enumerated(EnumType.STRING)
    private ColorTones colorTones;

    @Enumerated(EnumType.STRING)
    private Contrast contrast;

    @Enumerated(EnumType.STRING)
    private Density density;
    private Boolean frame;

    @Enumerated(EnumType.STRING)
    private Pack pack;
    private Boolean express;

    @Enumerated(EnumType.STRING)
    private AfterOrderProcessed afterOrderProcessed;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="transport_company_id")
    private TransportCompany transportCompany;

    private String orderDiskDestination;

    public String toString() {
        return "Order[" + this.orderNumber + "]";
    }
}
