package kripton.candidateservice.model.dtos;

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
    private Date fromDate;
    private Date toDate;
    private Long candidate_id;
}
