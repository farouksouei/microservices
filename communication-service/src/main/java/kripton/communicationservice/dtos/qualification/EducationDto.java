package kripton.communicationservice.dtos.qualification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)

public class EducationDto implements Serializable {
    private String university;
    private Degree degree;
    private Date fromDate;
    private Date toDate;
    private Long candidate_id;
}
