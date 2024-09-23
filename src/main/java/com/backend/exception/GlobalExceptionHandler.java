package com.backend.exception;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.backend.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
    	  List<String> errorMessages = exception.getBindingResult()
    	            .getFieldErrors()
    	            .stream()
    	            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
    	            .collect(Collectors.toList());

    	    ApiResponse apiResponse = new ApiResponse();
    	    apiResponse.setCode(ErrorCode.INVALID_KEY.getCode());
    	    apiResponse.setMessage(String.join(", ", errorMessages)); 
    	    return ResponseEntity.badRequest().body(apiResponse);
    }
    
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_REQUEST_BODY.getCode());
        apiResponse.setMessage(ErrorCode.INVALID_REQUEST_BODY.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errorMessages = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_KEY.getCode());
        apiResponse.setMessage(String.join(", ", errorMessages));

        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("IllegalArgumentException: ", exception);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_FIELD_ACCESS.getCode());  
        apiResponse.setMessage("Invalid field access: " + exception.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setCode(ErrorCode.INVALID_FIELD_ACCESS.getCode());  
    	apiResponse.setMessage( exception.getMessage());
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
   
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage("Not found this route API");
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value =NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage(exception.getMessage());
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage(exception.getMessage());
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = AuthenticationServiceException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationServiceException(AuthenticationServiceException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage("Not Auth");
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage("Invalid Token");
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalStateException(IllegalStateException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage(exception.getMessage());
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage(exception.getMessage());
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = ParseException.class)
    public ResponseEntity<ApiResponse> handleParseException(ParseException exception) {
    	ApiResponse apiResponse = new ApiResponse();
    	apiResponse.setMessage(exception.getMessage());
    	return ResponseEntity.badRequest().body(apiResponse);
    }


    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
