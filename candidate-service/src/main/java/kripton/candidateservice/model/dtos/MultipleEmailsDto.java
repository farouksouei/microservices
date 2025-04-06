package kripton.candidateservice.model.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class MultipleEmailsDto {
	private List<Long> candidates;
	private String topic;
}
