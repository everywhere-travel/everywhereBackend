package com.everywhere.backend.config;

import com.everywhere.backend.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JWTFilter jwtRequestFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize -> authorize
                        // TODO: Permitir acceso público a las rutas de login, registro y endpoints públicos como Swagger UI
                        .requestMatchers(antMatcher("/auth/**")).permitAll()
                        .requestMatchers(antMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()
                        .requestMatchers(antMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(antMatcher("/swagger-resources/**")).permitAll()
                        .requestMatchers(antMatcher("/webjars/**")).permitAll()
                        // TODO: Cualquier otra solicitud requiere autenticación (JWT u otra autenticación configurada)
                        .anyRequest().authenticated()
                )

                // TODO: Permite la autenticación básica (para testing con Postman, por ejemplo)
                //.httpBasic(Customizer.withDefaults())
                // TODO: Desactiva el formulario de inicio de sesión predeterminado, ya que se usará JWT
                .formLogin(AbstractHttpConfigurer::disable)
                // TODO: Configura el manejo de excepciones para autenticación. Usa JwtAuthenticationEntryPoint para manejar errores 401 (no autorizado)
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // TODO: Configura la política de sesiones como "sin estado" (stateless), ya que JWT maneja la autenticación, no las sesiones de servidor
                .sessionManagement(h -> h.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // TODO: Agrega la configuración para JWT en el filtro antes de los filtros predeterminados de Spring Security
                .with(new JWTConfigurer(tokenProvider), Customizer.withDefaults());

        // TODO: Añadir el JWTFilter antes del filtro de autenticación de nombre de usuario/contraseña.
        //  Esto permite que el JWTFilter valide el token antes de la autenticación
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // TODO: Proporciona el AuthenticationManager que gestionará la autenticación basada en los detalles de usuario y contraseña
        return authenticationConfiguration.getAuthenticationManager();
    }

}
