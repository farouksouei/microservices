package kripton.qualificationservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "experiences")

public class ExperienceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private String company ;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date fromDate ;
    @Temporal(TemporalType.DATE)
    private Date toDate ;
    private Long candidate_id;
    private Long job_id;

}