package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employe extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private Long matricule;
    private String cin;
    private String email;
    private String civilite;
    private String csp;
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employe that)) return false;
        return Objects.equals(matricule, that.matricule);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(matricule);
    }
}
