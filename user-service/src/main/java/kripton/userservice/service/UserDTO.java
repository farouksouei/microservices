package kripton.userservice.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties (ignoreUnknown = true)
public class UserDTO {
	private String id;
	private String userName;
	private String emailId;

	private String password;
	private String firstname;
	private String lastname;

	private String role;
}