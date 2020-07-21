package ru.zakrzhevskiy.lighthouse.model;

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
@Table(name = "USERS")
public class User extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("userId")
    @Column(updatable = false, insertable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String eMail;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean enabled;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", referencedColumnName = "id")
    @Builder.Default
    private MyUserDetails myUserDetails = new MyUserDetails();

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy="orderOwner")
    private Set<Order> ownedOrders;

    @OneToMany(mappedBy="orderCreator")
    private Set<Order> createdOrders;

    @Builder.Default
    private Boolean termsOfConditionsAccepted = false;

}
