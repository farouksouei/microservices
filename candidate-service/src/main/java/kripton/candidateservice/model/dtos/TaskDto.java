package kripton.candidateservice.model.dtos;

import kripton.candidateservice.model.entities.Priority;
import kripton.candidateservice.model.entities.TaskStatus;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
	private Long id;
	private String title ;
	private Date deadline;
	private Date createdAt;
	private Date modifiedAt;
	private String creatorId;
	private TaskStatus status ;

	private Set<String> users = new HashSet<> ();
	private String description ;
	private Priority priority ;
	private List<UserDto> userDtos ;

}
