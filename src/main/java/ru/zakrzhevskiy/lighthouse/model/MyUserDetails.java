package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.views.View;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USERS_DETAILS")
public class MyUserDetails {

    @Id
    @GeneratedValue
    @JsonProperty("userDetailId")
    @Column(updatable = false, insertable = false)
    private Long id;

    @Lob
    @JsonView({View.Short.class, View.Full.class})
    private byte[] avatar;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String firstName;

    @JsonView(View.Full.class)
    private String secondName;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String lastName;

    @Column(columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    @JsonView(View.Full.class)
    @JsonFormat(pattern="dd.MM.yyyy")
    private Date birthDay;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String postalCode;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String country;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String city;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String address;

    @Column(nullable = false)
    @JsonView(View.Full.class)
    private String phoneNumber;

    @Column(unique = true)
    @ElementCollection
    @JsonView(View.Full.class)
    private List<String> instagram;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private List<ReferencePhoto> referencePhotos;

    @JsonView({View.Short.class, View.Full.class, View.OrderUser.class})
    public String getFIO() {
        String fio = this.firstName + " " + this.lastName;
        return secondName == null ? fio : fio.replaceAll("\\s", " " + this.secondName + " ");
    }

}
