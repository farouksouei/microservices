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
@Table(name = "educations")

public class EducationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String university ;
    @Enumerated(EnumType.STRING)
    private Degree degree ;
    @Temporal(TemporalType.DATE)
    private Date fromDate ;
    @Temporal(TemporalType.DATE)
    private Date toDate ;
    private Long candidate_id;
    private Long job_id;
}