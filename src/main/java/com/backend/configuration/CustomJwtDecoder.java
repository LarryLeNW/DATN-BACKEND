package com.backend.configuration;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.backend.dto.request.auth.IntrospectRequest;
import com.backend.dto.response.auth.IntrospectResponse;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
	@Value("${jwt.signerKey}")
	private String signerKey;

	@Autowired
	private AuthenticationService authenticationService;

	private NimbusJwtDecoder nimbusJwtDecoder = null;

	@Override
	public Jwt decode(String token) throws JwtException {
		IntrospectResponse response;

		try {

			response = authenticationService.introspect(IntrospectRequest.builder().token(token).build());

			if (!response.isValid())
				log.info("Token invalid");

			if (Objects.isNull(nimbusJwtDecoder)) {
				SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
				nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512)
						.build();
			}

		} catch (JOSEException | ParseException e) {
			throw new JwtException(e.getMessage());
		}
		return nimbusJwtDecoder.decode(token);
	}
}