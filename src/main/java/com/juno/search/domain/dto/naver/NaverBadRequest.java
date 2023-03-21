package com.juno.search.domain.dto.naver;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class NaverBadRequest {
    private String errorMessage;
    private String errorCode;
}
