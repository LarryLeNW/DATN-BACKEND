package com.backend.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;
import java.util.UUID;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.constant.PredefinedRole;
import com.backend.constant.Type.UserStatusType;
import com.backend.dto.request.auth.AuthenticationRequest;
import com.backend.dto.request.auth.IntrospectRequest;
import com.backend.dto.request.auth.LogoutRequest;
import com.backend.dto.request.auth.RefreshRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.auth.AuthenticationResponse;
import com.backend.dto.response.auth.IntrospectResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.InvalidatedToken;
import com.backend.entity.Role;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.UserMapper;
import com.backend.repository.InvalidatedTokenRepository;
import com.backend.repository.user.RoleRepository;
import com.backend.repository.user.UserRepository;
import com.backend.utils.Helpers;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
	UserRepository userRepository;
	UserMapper userMapper;

	@Autowired
	@Lazy
	PasswordEncoder passwordEncoder;
	InvalidatedTokenRepository invalidatedTokenRepository;
	RoleRepository roleRepository;
	MailService mailService;

	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;

	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION_TOKEN;

	@NonFinal
	@Value("${jwt.refreshable-duration}")
	protected long REFRESHABLE_DURATION;

	@NonFinal
	@Value("${jwt.register-duration}")
	protected long REGISTER_DURATION;

	@NonFinal
	@Value("${CLIENT_URL}")
	protected String CLIENT_URL;

	public String register(UserCreationRequest request) {
		// find user email
		User userFound = userRepository.findByEmail(request.getEmail());

		if (userFound != null && userFound.getStatus().equals(UserStatusType.ACTIVED)) {
			throw new RuntimeException("Email has already been used.");
		}

		if (userFound == null) {
			userFound = new User(); 
			userFound.setEmail(request.getEmail());
			userFound.setPassword(passwordEncoder.encode(request.getPassword()));
			Role roleUser = roleRepository.findByName(PredefinedRole.USER_NAME);

			if (roleUser == null) {
				throw new RuntimeException("Role " + PredefinedRole.USER_NAME + " not created.");
			}

			userFound.setRole(roleUser);
		}

		userFound = userRepository.save(userFound);

		var token = generateToken(userFound, REGISTER_DURATION);

		String message = "<h1> This is link to confirm register DATN WEBSITE  <a href='" + CLIENT_URL
				+ "/confirm-register?token=" + token
				+ " ' style='color : blue'>Please click here to confirm</a> Thank You !!!</h1>";
		mailService.send("DATN WEBSITE", message, request.getEmail());

		return token;
	}

	public UserResponse verifyRegister(String token) throws JOSEException, ParseException {
		SignedJWT signedJWT = verifyToken(token, false);

		String userId = signedJWT.getJWTClaimsSet().getSubject();

		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		if (user.getStatus().equals(UserStatusType.ACTIVED))
			throw new AppException(ErrorCode.USER_NOT_EXISTED);

		user.setStatus(UserStatusType.ACTIVED);

		return userMapper.toUserResponse(userRepository.save(user));
	}

	public UserResponse getMyInfo() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}

		String userId = authentication.getName();

		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		return userMapper.toUserResponse(user);
	}

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		boolean isValid = true;

		try {
			verifyToken(token, false);
		} catch (AppException e) {
			isValid = false;
		}

		return IntrospectResponse.builder().valid(isValid).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request ) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		User user = userRepository.findByEmail(request.getEmail()); 
		if(user == null || !user.getStatus().equals(UserStatusType.ACTIVED)) throw new AppException(ErrorCode.USER_NOT_EXISTED);

		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
		
		if (!authenticated)
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		var token = generateToken(user, VALID_DURATION_TOKEN);
		
		return new AuthenticationResponse(token, true, userMapper.toUserResponse(user));
	}

	public void logout(LogoutRequest request) throws ParseException, JOSEException {
		try {
			var signToken = verifyToken(request.getToken(), true);

			String jit = signToken.getJWTClaimsSet().getJWTID();
			Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

			InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

			invalidatedTokenRepository.save(invalidatedToken);
		} catch (AppException exception) {
			log.info("Token already expired");
		}
	}

	public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
		var signedJWT = verifyToken(request.getToken(), true);

		var jit = signedJWT.getJWTClaimsSet().getJWTID();
		var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

		invalidatedTokenRepository.save(invalidatedToken);

		var userId = signedJWT.getJWTClaimsSet().getSubject();

		var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

		var token = generateToken(user, VALID_DURATION_TOKEN);

		return AuthenticationResponse.builder().token(token).authenticated(true).build();
	}

	private String generateToken(User user, long valid_duration) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getId()).issuer("lebatrinh.com")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(valid_duration, ChronoUnit.SECONDS).toEpochMilli()))
				.jwtID(UUID.randomUUID().toString()).claim("scope", buildScope(user))
				.build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			log.error("Cannot create token", e);
			throw new RuntimeException(e);
		}
	}

	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
						.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		if (!(verified && expiryTime.after(new Date())))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		return signedJWT;
	}

	public static String buildScope(User user) {
		StringJoiner scopeJoiner = new StringJoiner(" ");

		Role role = user.getRole();
		if (role != null) {
			scopeJoiner.add("ROLE_" + role.getName());
			if (role.getRoleModulePermissions() != null) {
				role.getRoleModulePermissions().forEach(rmp -> {
					String moduleName = rmp.getModule().getName();
					String permissionName = rmp.getPermission().getName();

					scopeJoiner.add(String.format("%s_%s", moduleName.toUpperCase(), permissionName.toUpperCase()));
				});
			}
		}

		return scopeJoiner.toString();
	}

}
