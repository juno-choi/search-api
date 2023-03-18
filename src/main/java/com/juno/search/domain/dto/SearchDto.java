package com.juno.search.domain.dto;

import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchDto {
    private SortType sort;
    private int page;
    private int size;
    private String query;
    private SearchType type;

    public static SearchDto of(SortType sort, int page, int size, String query, SearchType type){
        /**
         * TODO
         * 1. 만약 Naver라면 query를 encode 해주어야 한다.
         * 2. sort type에 따라 변경해주어야 한다.
         */

        return new SearchDto(sort, page, size, query, type);
    }

    public SearchDto(SortType sort, int page, int size, String query, SearchType type) {
        this.sort = sort;
        this.page = page;
        this.size = size;
        this.query = query;
        this.type = type;
    }

    public String getSortByType() {
        SortType sort = this.sort;
        SearchType type = this.type;
        if(type == SearchType.NAVER){
            if(sort == SortType.A){
                return "sim";
            }
            return "date";
        }

        if(sort == SortType.A){
            return "accuracy";
        }
        return "recency";
    }
}
