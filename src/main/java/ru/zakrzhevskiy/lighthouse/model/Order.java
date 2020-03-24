package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    private Long id;
    private Integer orderNumber;
    private String orderStatus;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User orderOwner;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User orderCreator;
    private String scanner;
    private String skinTones;
    private String contrast;
    private String bwContrast;
    private String express;
    private String special;
    private Date creationDate;
    private Date modificationDate;
    private String orderForm;
}
