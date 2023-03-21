package com.juno.search.domain.dto.kakao;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class BadRequest {
    private String errorType;
    private String message;
}
