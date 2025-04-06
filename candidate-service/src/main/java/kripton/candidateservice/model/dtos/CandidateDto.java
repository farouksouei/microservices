package kripton.candidateservice.model.dtos;

import java.util.ArrayList;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor

@NoArgsConstructor
@Builder
public class CandidateDto {
	
    private String address;
    private String designition;
    private String email;
    private ArrayList<String> languages;
    private String name;
    private String phone;
    private ArrayList<String> skills;
    private int total_exp;

}
