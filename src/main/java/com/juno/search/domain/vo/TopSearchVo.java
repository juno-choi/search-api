package com.juno.search.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TopSearchVo {
    private List<SearchVo> list;
}
