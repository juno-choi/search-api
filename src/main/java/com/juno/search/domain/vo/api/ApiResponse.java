package com.juno.search.domain.vo.api;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;
}
