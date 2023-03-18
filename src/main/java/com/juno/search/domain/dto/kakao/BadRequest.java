package com.juno.search.domain.dto.kakao;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BadRequest {
    private String errorType;
    private String message;
}
