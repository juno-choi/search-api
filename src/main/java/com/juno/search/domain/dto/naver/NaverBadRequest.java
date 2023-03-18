package com.juno.search.domain.dto.naver;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverBadRequest {
    private String errorMessage;
    private String errorCode;
}
