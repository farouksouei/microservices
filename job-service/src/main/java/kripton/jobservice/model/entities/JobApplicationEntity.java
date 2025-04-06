package kripton.jobservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "job_application")
public class JobApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private boolean availability ;
    private double desiredSalary ;
    private String coverLetter ;

    @Temporal (TemporalType.TIMESTAMP)

    private Date createdAt ;
    private Long candidate_id ;
    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonIgnore
    private JobEntity job;



}