package com.example.paymentservice.exception;

import com.example.paymentservice.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorMessage> customException(CustomException customException) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .message(customException.getMessage())
                .error(customException.getError())
                .build(),
                HttpStatus.valueOf(customException.getStatus()));
    }
}
