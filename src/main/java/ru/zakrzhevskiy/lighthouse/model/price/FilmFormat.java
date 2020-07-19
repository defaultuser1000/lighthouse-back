package ru.zakrzhevskiy.lighthouse.model.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FILM_FORMATS")
public class FilmFormat extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("filmFormatId")
    private Long id;

    @Column(unique = true)
    private String value;

}
