package com.juno.search.domain.dto;

import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

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
        return new SearchDto(sort, page, size, query, type);
    }

    public static SearchDto of(String sort, int page, int size, String query, String type){
        SortType sortType = SortType.A;
        if(sort.toLowerCase(Locale.ROOT).equals("r")){
            sortType = SortType.R;
        }

        SearchType searchType = SearchType.KAKAO;
        if(type.toLowerCase(Locale.ROOT).equals("naver")){
            searchType = SearchType.NAVER;
        }

        return new SearchDto(sortType, page, size, query, searchType);
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
