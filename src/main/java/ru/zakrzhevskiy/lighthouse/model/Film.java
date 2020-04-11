package ru.zakrzhevskiy.lighthouse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zakrzhevskiy.lighthouse.model.audit.AuditModel;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FILMS")
public class Film extends AuditModel {

    @Id
    @GeneratedValue
    @JsonProperty("filmId")
    @Column(updatable = false, insertable = false)
    private Long id;
    private Integer filmType;
    private String processingType;
    private Integer quantity;
    private String resolution;
    private String push;

}
