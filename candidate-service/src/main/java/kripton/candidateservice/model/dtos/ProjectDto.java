package kripton.candidateservice.model.dtos;


import jakarta.persistence.Column;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

	private Long id;

	private String title;
	@Column (length = 500)
	private String description ;

	private List<TaskDto> tasks;
}
