package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.reference_gallery.ReferencePhoto;

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
    private byte[] avatar;

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

    @Column(unique = true)
    @ElementCollection
    private List<String> instagram;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private List<ReferencePhoto> referencePhotos;

    public String getFIO() {
        String fio = this.firstName + " " + this.lastName;
        return secondName == null ? fio : fio.replaceAll("\\s", " " + this.secondName + " ");
    }

}
