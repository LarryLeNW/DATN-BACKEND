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
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.backend.constant.EndpointConstants;
import com.backend.dto.request.auth.IntrospectRequest;
import com.backend.dto.response.auth.IntrospectResponse;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
	@Value("${jwt.signerKey}")
	private String signerKey;

	@Autowired
	private AuthenticationService authenticationService;

	private NimbusJwtDecoder nimbusJwtDecoder = null;

	@Autowired
	private PathMatcher pathMatcher;
	
	@Override
	public Jwt decode(String token) throws JwtException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String requestUri = request.getRequestURI();
		String requestMethod = request.getMethod();
		IntrospectResponse response;
		
		try {

			response = authenticationService.introspect(IntrospectRequest.builder().token(token).build());

			if (!response.isValid() && isPublicEndpoint(requestUri, requestMethod))
				return createDummyJwt();

			if (Objects.isNull(nimbusJwtDecoder)) {
				SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
				nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512)
						.build();
			}

		} catch (JOSEException | ParseException e) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		return nimbusJwtDecoder.decode(token);
	}

	private boolean isPublicEndpoint(String uri, String method) {
		switch (method) {
		case "GET":
			return matchesAny(uri, EndpointConstants.PUBLIC_GET_ENDPOINTS);
		case "POST":
			return matchesAny(uri, EndpointConstants.PUBLIC_POST_ENDPOINTS);
		case "DELETE":
			return matchesAny(uri, EndpointConstants.PUBLIC_DELETE_ENDPOINTS);
		case "PUT":
			return matchesAny(uri, EndpointConstants.PUBLIC_PUT_ENDPOINTS);
		default:
			return false;
		}
	}

	private boolean matchesAny(String uri, String[] publicEndpoints) {
		for (String endpoint : publicEndpoints) {
			if (pathMatcher.match(endpoint, uri)) { 
				return true;
			}
		}
		return false;
	}

	private Jwt createDummyJwt() {
		return Jwt.withTokenValue("dummy-token").header("alg", "none").claim("scope", "public").build();
	}
}