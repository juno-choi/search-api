package com.juno.search.domain.dto.kakao;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponseDto {
    private Meta meta;
    private List<Documents> documents;
}
