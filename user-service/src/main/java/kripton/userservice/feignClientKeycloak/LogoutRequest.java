package kripton.userservice.feignClientKeycloak;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogoutRequest {
    private String redirect_uri;
    private String client_id;
    private String refresh_token;
}
