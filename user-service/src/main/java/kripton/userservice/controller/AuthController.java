package kripton.userservice.controller;

import kripton.userservice.feignClientKeycloak.AuthService;
import kripton.userservice.feignClientKeycloak.LoginRequest;
import kripton.userservice.feignClientKeycloak.LoginResponse;
import kripton.userservice.service.KeycloakService;
import kripton.userservice.service.UserDTO;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



//@CrossOrigin(origins = "*")
@RestController
@Slf4j
@RequestMapping("/api/users/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final KeycloakService service ;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.loginToKeycloak(request);
    }


    @PostMapping("/register")
    public String register(@RequestBody UserDTO userDTO){
        try {
            log.info("this user is getting registerd");
            return service.addUser (userDTO);

        }catch (Exception e){
            return "" ;
        }
    }
}
