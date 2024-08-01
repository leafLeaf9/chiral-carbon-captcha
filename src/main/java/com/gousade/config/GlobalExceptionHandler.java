package com.gousade.config;

import com.gousade.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author woxigsd@gmail.com
 * @date 2020-8-12 8:53:01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param e exception
     * @return 文件异常
     */
    @ExceptionHandler(MultipartException.class)
    public Object handleMultipartException(MultipartException e) {
        log.error("发生文件异常", e);
        return ResponseResult.renderError().message("发生文件异常：" + e.getCause().getMessage());
    }

  /*  @ExceptionHandler(UnauthorizedException.class)
    public ResponseResult handleUnauthorizedException(UnauthorizedException e, HttpServletResponse response) {
        log.error("发生权限异常", e);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseResult.renderError().message("发生权限异常：无权限。" + e.getCause().getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseResult handleAuthorizationException(AuthorizationException e, HttpServletResponse response) {
        log.error("发生权限异常", e);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseResult.renderError().message("发生权限异常：未认证。" + e.getCause().getMessage());
    }*/

    @ExceptionHandler(IOException.class)
    public ResponseResult handleIOException(IOException e) {
        log.error("发生IO异常", e);
        return ResponseResult.renderError().message("发生IO异常：" + e.getCause().getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseResult> handleNullPointerException(NullPointerException e) {
        String message = "发生空指针异常。";
        log.error(message, e);
        ResponseResult responseResult = ResponseResult.renderError().message(message);
        return new ResponseEntity<>(responseResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseResult> handleSQLException(SQLException e) {
        log.error("发生sql异常", e);
        ResponseResult responseResult = ResponseResult.renderError().message("发生数据库异常，请联系系统管理员。");
        return new ResponseEntity<>(responseResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseResult> handleException(BindException e) {
        BindingResult result = e.getBindingResult();
        FieldError fieldError = result.getFieldError();
        String message;
        if (fieldError != null) {
            message = String.format("%s。%s.%s：%s", fieldError.getDefaultMessage(),
                    fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue());
        } else {
            List<ObjectError> errors = result.getAllErrors();
            message = errors.get(0).getDefaultMessage();
        }
        ResponseResult responseResult = ResponseResult.renderBadRequest().message(message);
        log.error("发生参数校验异常", e);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseResult> handleException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        log.error("发生参数校验异常", e);
        ResponseResult responseResult = ResponseResult.renderBadRequest().message(message);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * RequestBody 上校验失败后抛出的异常是 MethodArgumentNotValidException 异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseResult> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
        log.error("发生参数校验异常。", e);
        ResponseResult responseResult = ResponseResult.renderBadRequest().message(message);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleException(Exception e) {
        String message = "发生未知异常";
        log.error(message, e);
        ResponseResult responseResult = ResponseResult.renderError().message(e.getMessage());
        return new ResponseEntity<>(responseResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
