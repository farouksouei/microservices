package kripton.communicationservice.dtos.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)

public class JobApplicationDto implements Serializable {
    private Long id;
    private boolean availability ;
    private Date createdAt ;
    private double desiredSalary ;
    private String coverLetter ;
    private Long candidate_id ;
}
