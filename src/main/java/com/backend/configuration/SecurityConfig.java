package com.backend.configuration;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.backend.constant.EndpointConstants;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

	@Autowired
	@Lazy
	private CustomJwtDecoder customJwtDecoder;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity
				.cors()
				.and()
				.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, EndpointConstants.PUBLIC_GET_ENDPOINTS)
						.permitAll().requestMatchers(HttpMethod.POST, EndpointConstants.PUBLIC_POST_ENDPOINTS).permitAll()
						.requestMatchers(HttpMethod.PUT, EndpointConstants.PUBLIC_PUT_ENDPOINTS).permitAll()
						.requestMatchers(HttpMethod.DELETE,
								EndpointConstants.PUBLIC_DELETE_ENDPOINTS)
						.permitAll().anyRequest()
						.authenticated());

		httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
						.jwtAuthenticationConverter(jwtAuthenticationConverter()))
				.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

		httpSecurity.csrf(AbstractHttpConfigurer::disable);

		return httpSecurity.build();
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOrigin("http://localhost:3000"); // Cụ thể origin
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}

	
}
