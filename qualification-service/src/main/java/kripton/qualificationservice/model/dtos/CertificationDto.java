package kripton.qualificationservice.model.dtos;

import lombok.*;

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
