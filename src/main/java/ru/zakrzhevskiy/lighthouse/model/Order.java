package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;
import ru.zakrzhevskiy.lighthouse.model.enums.*;

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
public class Order extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("orderId")
    @Column(updatable = false, insertable = false)
    private Long id;
    private Integer orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name="user_owner_id", nullable=false)
    private Long orderOwner;

    @Column(name="user_creator_id", nullable=false)
    private Long orderCreator;

    private String scanner;

    @Enumerated(EnumType.STRING)
    private ScanType scanType;

    @Enumerated(EnumType.STRING)
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

    @Lob
    private String orderForm;
}
