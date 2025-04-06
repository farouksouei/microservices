package kripton.qualificationservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "certifications")
public class CertificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name ;
    @Temporal(TemporalType.DATE)
    private Date certDate ;
    private Long candidate_id;
    private Long job_id;
}