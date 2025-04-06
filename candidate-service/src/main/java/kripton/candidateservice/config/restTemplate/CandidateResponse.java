package kripton.candidateservice.config.restTemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponse {
	private List<String> companies_worked_at;
	private List<String> countries;
	private List<String> designation;
	private List<String> skills;
	private List<String> programming_languages_frameworks;
	private List<String> university;
	private String email ;
	private String name;
	private String phone;
	private Integer total_exp;
}

