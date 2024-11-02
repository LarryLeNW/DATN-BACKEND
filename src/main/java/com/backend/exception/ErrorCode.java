package com.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_BODY(1000, "Invalid Request Body", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED(1005, "Category not existed", HttpStatus.NOT_FOUND),
    CATEGORYBLOG_NOT_EXISTED(1005,"Category blog not existed",HttpStatus.NOT_FOUND),
    BLOG_NOT_EXISTED(1005,"blog not existed",HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(1005,"review not found",HttpStatus.NOT_FOUND),
    SKU_NOT_FOUND(1005,"SKU_NOT_FOUND ",HttpStatus.NOT_FOUND),    
    PRODUCT_NOT_EXISTED(1005, "Product not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTED(1005, "name Category existed", HttpStatus.NOT_FOUND),
    BRAND_NOT_EXISTED(1005, "Brand not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1005, "Role not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(1002, "Product existed", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(1002, "Category existed", HttpStatus.BAD_REQUEST),
    BRAND_EXISTED(1002, "Brand existed", HttpStatus.BAD_REQUEST),
    INVALID_FIELD_ACCESS(1009, "Invalid Access Field", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
