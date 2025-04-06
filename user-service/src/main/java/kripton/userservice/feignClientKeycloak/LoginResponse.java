package kripton.userservice.feignClientKeycloak;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LoginResponse {

    private String access_token;
    private String refresh_token;
    private int expires_in;
    private int refresh_expires_in;
    private String scope ;
    private String session_state;
}
