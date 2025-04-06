package kripton.jobservice.model.dtos.qualification;

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
public class EducationDto implements Serializable {
    private String university;
    private Degree degree;
    private Date from;
    private Date to;
    private Long candidate_id;
}
