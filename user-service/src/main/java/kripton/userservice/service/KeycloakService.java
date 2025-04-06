package kripton.userservice.service;

import kripton.userservice.config.Credentials;
import kripton.userservice.config.KeycloakConfig;
import kripton.userservice.service.feignCandidate.CandidateFeignClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
@Slf4j
public class KeycloakService {


	private final String serverUrl;
	private final CandidateFeignClient candidateFeignClient ;
	public KeycloakService (@Value ("${serverUrl}")String serverUrl,CandidateFeignClient candidateFeignClient){
		this.candidateFeignClient = candidateFeignClient;
		this.serverUrl = serverUrl;
	}


	public String addUser(UserDTO userDTO){
		UserRepresentation user = new UserRepresentation();
		CredentialRepresentation passwordCredentials;
		if(userDTO.getPassword () != null ){
			passwordCredentials = Credentials.createPasswordCredentials (userDTO.getPassword ());
		}else {
			passwordCredentials = Credentials.createPasswordCredentials ("admin");
		}
		user.setCredentials(Collections.singletonList(passwordCredentials));
		user.setUsername(userDTO.getUserName());
		user.setFirstName(userDTO.getFirstname());
		user.setLastName(userDTO.getLastname());
		user.setEmail(userDTO.getEmailId());
		user.setEmailVerified (true);
		user.setEnabled(true);
		UsersResource instance = getInstance();
		Response response = instance.create (user);
		System.out.println("response: " + response);
		String userId = CreatedResponseUtil.getCreatedId(response);
		RoleRepresentation candidateRole = KeycloakConfig
				.getInstance(serverUrl)
				.realm(KeycloakConfig.appRealm)
				.roles()
				.list()
				.stream()
				.filter(role -> role.getName().equals("candidate"))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Role not found"));
		UserResource userResource = instance.get (userId);
		userResource.roles ().realmLevel ().add (Collections.singletonList (candidateRole));
		candidateFeignClient.autoCreatedCandidate (userDTO.getFirstname (),userDTO.getLastname (),userDTO.getEmailId());
		return userId ;
	}

	public UserDTO getUser(String userName){
		UsersResource usersResource = getInstance();
		UserRepresentation userRepresentation = usersResource.search (userName, true).get (0);
		return convertUser (userRepresentation);
	}

	private UserDTO convertUser(UserRepresentation representation){
		return UserDTO.builder ()
				.id (representation.getId ())
				.userName (representation.getUsername ())
				.firstname (representation.getFirstName ())
				.lastname (representation.getLastName ())
				.emailId (representation.getEmail ())
				.build ();
	}
	public List<UserDTO>getMembers(){
		RolesResource rolesResource = getRolesInstance ();
		RoleResource managerRole = rolesResource.get ("manager");
		RoleResource recruiterRole = rolesResource.get ("recruiter");
		RoleResource candidateRole = rolesResource.get ("candidate");
		Set<UserRepresentation> managers = managerRole.getRoleUserMembers ();
		Set<UserRepresentation> recruiters = recruiterRole.getRoleUserMembers ();
		Set<UserRepresentation> candidates = candidateRole.getRoleUserMembers ();
		Set<UserDTO> managersDtos = new HashSet<> ();
		Set<UserDTO> recruitersDtos = new HashSet<> ();
		Set<UserDTO> candidatesDtos = new HashSet<> ();

		managers.forEach (representation -> {
			UserDTO user = convertUser (representation);
			user.setRole ("manager");
			managersDtos.add (user);
		});
		recruiters.forEach (representation -> {
			UserDTO user = convertUser (representation);
			user.setRole ("recruiter");
			recruitersDtos.add (user);
		});
		candidates.forEach (representation -> {
			UserDTO user = convertUser (representation);
			user.setRole ("candidate");
			candidatesDtos.add (user);
		});

		List<UserDTO> usersInDB = new ArrayList<> ();
		usersInDB.addAll (candidatesDtos);
		usersInDB.addAll (recruitersDtos);
		usersInDB.addAll (managersDtos);

		return usersInDB;
	}
	public UserDTO getUserById(String userId){
		UsersResource usersResource = getInstance();
		UserRepresentation userRepresentation = usersResource.get (userId).toRepresentation ();
		return convertUser (userRepresentation);
	}

	public List<UserDTO> getUsers(){
		UsersResource usersResource = getInstance ();
		List<UserRepresentation> users = usersResource.list ();
		List<UserDTO> usersDtos= new ArrayList<> ();
		users.forEach (userRepresentation -> {
			UserDTO userDTO = convertUser (userRepresentation);
			usersDtos.add (userDTO);
		});
		return usersDtos;
	}
	public void updateUser(String userId, UserDTO userDTO){
		CredentialRepresentation credential = Credentials.createPasswordCredentials(userDTO.getPassword());
		UserRepresentation user = new UserRepresentation();
		user.setUsername(userDTO.getUserName());
		user.setFirstName(userDTO.getFirstname());
		user.setLastName(userDTO.getLastname());
		user.setEmail(userDTO.getEmailId());
//		check if password is not duplicated : keycloak error !
//		user.setCredentials(Collections.singletonList(credential));
		UsersResource usersResource = getInstance();
		usersResource.get(userId).update(user);
	}
	public void deleteUser(String userId){
		UsersResource usersResource = getInstance();
		usersResource.get(userId)
				.remove();
	}

	public void resetPassword(String newPassword,String userId){
		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
		credentialRepresentation.setTemporary(false);
		credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
		credentialRepresentation.setValue(newPassword);
		UsersResource usersResource = getInstance ();
		usersResource.get (userId).resetPassword (credentialRepresentation);
	}

	public void sendVerificationLink(String userId){
		UsersResource usersResource = getInstance();
		usersResource.get(userId)
				.sendVerifyEmail();
	}

	public void sendResetPassword(String userId){
		UsersResource usersResource = getInstance();

		usersResource.get(userId)
				.executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
	}


	public UsersResource getInstance(){
		return KeycloakConfig.getInstance(serverUrl).realm(KeycloakConfig.appRealm).users();
	}

	public RolesResource getRolesInstance(){
		return KeycloakConfig.getInstance (serverUrl).realm (KeycloakConfig.appRealm).roles ();
	}


}

