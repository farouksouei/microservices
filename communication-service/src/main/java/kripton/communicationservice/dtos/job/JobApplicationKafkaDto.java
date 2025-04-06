package kripton.communicationservice.dtos.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)

public class JobApplicationKafkaDto implements Serializable {
    private String jobApplication;
    private String job;
    private String candidate ;



}
