package kripton.candidateservice.config.kafka;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateAppliedEvent {
    private Long candidateId ;
    private String candidateEmail;
}
