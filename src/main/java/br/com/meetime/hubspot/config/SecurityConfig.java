package br.com.meetime.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Permite acesso a todas as rotas sem login
            .csrf(csrf -> csrf.disable()) // Desativa proteção CSRF
            .formLogin(form -> form.disable()) // Desativa a tela de login
            .httpBasic(httpBasic -> httpBasic.disable()); // Desativa autenticação básica
        
        return http.build();
    }
	
}
