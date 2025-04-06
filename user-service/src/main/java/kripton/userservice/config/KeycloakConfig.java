package kripton.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class KeycloakConfig {

	static Keycloak keycloak = null;
	public final static String realm = "master";
	public final static String appRealm = "spring-boot-microservices-realm";
	final static String clientId = "admin-cli";
	final static String userName = "admin";
	final static String password = "admin";

	public KeycloakConfig() {
	}

	public static Keycloak getInstance(String serverUrl){
		if(keycloak == null){
		log.info ("value of server url {}",serverUrl);
			keycloak = KeycloakBuilder.builder()
					.serverUrl(serverUrl)
					.realm(realm)
					.grantType(OAuth2Constants.PASSWORD)
					.username(userName)
					.password(password)
					.clientId(clientId)
					.resteasyClient(ResteasyClientBuilder.newBuilder ().build ())
					.build();
		}
		return keycloak;
	}
}
