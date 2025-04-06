package kripton.qualificationservice.model.dtos;

import kripton.qualificationservice.model.entities.Degree;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EducationDto implements Serializable {
    private String university;
    private Degree degree;
    private Date fromDate;
    private Date toDate;
    private Long candidate_id;
}
