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
public class CertificationDto implements Serializable {
    private String name;
    private Date certDate;
    private Long candidate_id;
}
