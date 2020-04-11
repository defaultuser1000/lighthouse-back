package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonView
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue
    @JsonProperty("userId")
    @Column(updatable = false, insertable = false)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    private String secondName;

    @Column(nullable = false)
    private String lastName;

    @Column(columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date birthDay;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String eMail;

    @Column(unique = true)
    @ElementCollection
    private List<String> instagram;

    @OneToMany(mappedBy="orderOwner")
    private Set<Order> ownedOrders;

    @OneToMany(mappedBy="orderCreator")
    private Set<Order> createdOrders;

    public String getFIO() {
        String fio = this.firstName + " " + this.lastName;
        return secondName == null ? fio : fio.replaceAll("\\s", " " + this.secondName + " ");
    }
}
