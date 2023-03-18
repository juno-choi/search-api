package com.juno.search.exception.advice;

import com.juno.search.domain.vo.api.ApiError;
import com.juno.search.domain.vo.api.ErrorVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CommonAdvice {
    @ExceptionHandler
    public ResponseEntity<ApiError> illegalArgumentException(IllegalArgumentException e){
        List<ErrorVo> errors = new ArrayList<>();
        errors.add(ErrorVo.builder()
                .message(e.getMessage())
                .build());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiError.builder()
                        .code("FAIL")
                        .message("실패")
                        .errors(errors)
                        .build()
        );
    }
}
