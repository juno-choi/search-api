package com.juno.search.exception.advice;

import com.juno.search.domain.vo.api.ApiError;
import com.juno.search.domain.vo.api.ErrorVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = "com.juno.search")
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
                        .message("잘못된 입력입니다.")
                        .errors(errors)
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> missingServletRequestParameterException(MissingServletRequestParameterException e){
        StringBuilder sb = new StringBuilder();
        sb.append("필수 parameter = ");
        sb.append(e.getParameterName());
        sb.append("("+e.getParameterType()+")");

        List<ErrorVo> errors = new ArrayList<>();
        errors.add(ErrorVo.builder()
                .message(sb.toString())
                .build());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiError.builder()
                        .code("FAIL")
                        .message("parameter 필수 값을 확인해 주세요.")
                        .errors(errors)
                        .build()
        );
    }
}
