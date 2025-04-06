package kripton.userservice.feignClientKeycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final String serverUrl;
    private final WebClient webClient;

    @Autowired
    public AuthService(@Value("${serverUrl}") String serverUrl) {
        this.serverUrl = serverUrl;
        this.webClient = WebClient.builder().baseUrl(serverUrl).build();
    }


    public String logoutFromKeycloak(LogoutRequest request,String token,String realm){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", request.getClient_id());
        formData.add("refresh_token",request.getRefresh_token());
        formData.add("redirect_uri", request.getRedirect_uri());
        return webClient.get()
                .uri("/realms/"+realm+"/protocol/openid-connect/logout")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    public LoginResponse loginToKeycloak(LoginRequest request){

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", request.getClientId());
        formData.add("client_secret", request.getClientSecret());
        formData.add("username",request.getUsername());
        formData.add("password",request.getPassword());
        formData.add("grant_type",request.getGrantType());
        return webClient.post()
                .uri("/realms/"+request.getRealm()+"/protocol/openid-connect/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .block();
    }
}
