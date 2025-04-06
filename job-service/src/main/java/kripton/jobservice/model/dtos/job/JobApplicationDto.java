package kripton.jobservice.model.dtos.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kripton.jobservice.model.dtos.candidate.CandidateDetailsDto;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationDto implements Serializable {
    private Long id;
    private boolean availability ;
    private double desiredSalary ;
    private String coverLetter ;
    private Date createdAt ;
    CandidateDetailsDto candidate;

    JobDto job;

}
