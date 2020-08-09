package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;
import ru.zakrzhevskiy.lighthouse.model.views.View;

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
    @JsonView({View.Short.class, View.Full.class})
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonView({View.Short.class, View.Full.class})
    private String username;

    @Column(unique = true, nullable = false)
    @JsonView(View.Full.class)
    private String eMail;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @JsonView(View.Full.class)
    private Boolean enabled;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", referencedColumnName = "id")
    @Builder.Default
    @JsonView({View.Short.class, View.Full.class})
    private MyUserDetails myUserDetails = new MyUserDetails();

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonView({View.Short.class, View.Full.class})
    private Set<Role> roles;

    @OneToMany(mappedBy="orderOwner")
    @JsonView(View.Full.class)
    private Set<Order> ownedOrders;

    @OneToMany(mappedBy="orderCreator")
    @JsonView(View.Full.class)
    private Set<Order> createdOrders;

    @Builder.Default
    @JsonView({View.Short.class, View.Full.class})
    private Boolean termsOfConditionsAccepted = false;

}
