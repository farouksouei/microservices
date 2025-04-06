package kripton.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

 @Bean
 public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
  serverHttpSecurity.authorizeExchange(exchange ->
                  exchange
                          .pathMatchers("/api/users/auth/**").permitAll()
                          .anyExchange().authenticated())
          .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

  serverHttpSecurity.cors().configurationSource(request -> {
   CorsConfiguration cors = new CorsConfiguration();
   cors.setAllowedOrigins(Arrays.asList("*"));
   cors.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
   cors.setAllowedHeaders(Arrays.asList("*"));
   return cors;
  }).and().csrf().disable();
  return serverHttpSecurity.build();
 }
}





