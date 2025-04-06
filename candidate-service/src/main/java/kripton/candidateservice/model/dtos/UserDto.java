package kripton.candidateservice.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties (ignoreUnknown = true)

public class UserDto implements Serializable {

	private String id ;

	private String userName;
	private String emailId;
	@JsonIgnore
	private String password;
	private String firstname;
	private String lastname;

	private List<String> realmRoles;
}