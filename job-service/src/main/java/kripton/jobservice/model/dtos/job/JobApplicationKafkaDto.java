package kripton.jobservice.model.dtos.job;

import kripton.jobservice.model.dtos.job.JobApplicationDto;
import lombok.*;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JobApplicationKafkaDto implements Serializable {
    private String jobApplication;
    private String job;
    private String candidate ;



}
