package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zakrzhevskiy.lighthouse.model.enums.OrderStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ORDERS")
public class Order {

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
    private String scanType;
    private String scanSize;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private Set<Film> orderFilms = new HashSet<>();

    private String special;

    private String colorTones;
    private String contrast;
    private String density;
    private Boolean frame;
    private String pack;
    private String express;

    private Date creationDate;
    private Date modificationDate;

    @Lob
    private String orderForm;
}
