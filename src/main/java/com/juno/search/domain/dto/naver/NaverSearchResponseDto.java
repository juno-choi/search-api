package com.juno.search.domain.dto.naver;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverSearchResponseDto {
    private int total;
    private String lastBuildDate;
    private int display;
    private int start;
    private List<Item> items;
}
