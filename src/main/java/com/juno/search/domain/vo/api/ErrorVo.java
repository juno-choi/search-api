package com.juno.search.domain.vo.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorVo {
    private String message;
}
