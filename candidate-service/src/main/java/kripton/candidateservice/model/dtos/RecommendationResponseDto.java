package kripton.candidateservice.model.dtos;

import java.util.List;

import lombok.Data;

@Data
public class RecommendationResponseDto {
	
	private String phone;
    private List<String> cv_link;
    private String email;
    private String name;
    private String score;

}
