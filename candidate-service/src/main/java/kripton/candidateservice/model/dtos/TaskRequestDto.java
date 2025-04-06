package kripton.candidateservice.model.dtos;

import kripton.candidateservice.model.entities.Priority;
import kripton.candidateservice.model.entities.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskRequestDto {
	private TaskDto taskDto;
	private String creator;
	private Set<String> users = new HashSet<> ();
}
