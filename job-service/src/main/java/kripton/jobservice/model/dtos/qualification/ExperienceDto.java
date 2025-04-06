package kripton.jobservice.model.dtos.qualification;

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
    private Date from ;
    @Temporal(TemporalType.DATE)
    private Date to ;
    private String description;
    private Long candidate_id;
    private List<SkillDto> skills;
}
