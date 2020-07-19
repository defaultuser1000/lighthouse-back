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
@Table(name = "MESSAGES")
public class Message extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("messageId")
    @Column(updatable = false, insertable = false)
    private Long id;

//    @OneToOne(cascade = CascadeType.ALL)
    @Column(name = "user_id", nullable = false)
    private Long userId;


//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(length = 2000)
    private String text;

    @Lob
    @ElementCollection
    private Set<byte[]> attachments;

}
