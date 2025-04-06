package kripton.userservice.feignClientKeycloak;

import feign.Param;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LoginRequest {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String realm ;
}
