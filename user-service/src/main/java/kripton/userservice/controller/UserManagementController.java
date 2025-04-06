package kripton.userservice.controller;

import kripton.userservice.service.KeycloakService;
import kripton.userservice.service.ResetPasswordRequest;
import kripton.userservice.service.UserDTO;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping (path = "api/users/manage")
@RequiredArgsConstructor
public class UserManagementController {


	private final KeycloakService service ;


	@PostMapping
	public String addUser(@RequestBody UserDTO userDTO){
		return service.addUser(userDTO);
	}

	@GetMapping
	List<UserDTO> getUsers(){
		return service.getUsers ();
	}

	@GetMapping("/users")
	List<UserDTO> getAllUsers(){
		return service.getMembers ();
	}


	@GetMapping (path = "/{userName}")
	public UserDTO getUser(@PathVariable("userName") String userName){
		return service.getUser(userName);
	}


	@GetMapping (path = "/id/{user-id}")
	public UserDTO getUserById(@PathVariable("user-id") String userId){
		return service.getUserById (userId);
	}

	@PutMapping(path = "/update/{userId}")
	public String updateUser(@PathVariable("userId") String userId, @RequestBody UserDTO userDTO){
		service.updateUser(userId, userDTO);
		return "User Details Updated Successfully.";
	}


	@DeleteMapping(path = "/delete/{userId}")
	public String deleteUser(@PathVariable("userId") String userId){
		service.deleteUser(userId);
		return "User Deleted Successfully.";
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		// Check if old password is correct
		service.resetPassword (request.getNewPassword (),request.getUserId ());
		return ResponseEntity.ok().build();
	}
	@GetMapping(path = "/verification-link/{userId}")
	public String sendVerificationLink(@PathVariable("userId") String userId){
		service.sendVerificationLink(userId);
		return "Verification Link Send to Registered E-mail Id.";
	}

	@GetMapping(path = "/reset-password/{userId}")
	public String sendResetPassword(@PathVariable("userId") String userId){
		service.sendResetPassword(userId);
		return "Reset Password Link Send Successfully to Registered E-mail Id.";
	}
}
