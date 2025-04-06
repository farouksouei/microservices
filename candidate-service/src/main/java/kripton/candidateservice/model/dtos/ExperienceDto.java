package kripton.candidateservice.model.dtos;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDto implements Serializable {
    private String title;
    private String company;
    @Temporal(TemporalType.DATE)
    private Date fromDate ;
    @Temporal(TemporalType.DATE)
    private Date toDate ;
    private String description;
    private Long candidate_id;
}
