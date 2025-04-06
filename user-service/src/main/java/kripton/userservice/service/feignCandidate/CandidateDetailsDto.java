package kripton.userservice.service.feignCandidate;


import lombok.*;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailsDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
