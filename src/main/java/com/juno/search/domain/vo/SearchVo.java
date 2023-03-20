package com.juno.search.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SearchVo {
    private String keyword;
    private int count;
    private int rank;
}
