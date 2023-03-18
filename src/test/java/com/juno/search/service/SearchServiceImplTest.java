package com.juno.search.service;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchServiceImplTest {

    @Autowired
    private SearchService searchService;

    @Test
    @DisplayName("검색에 성공한다.")
    void searchSuccess1() throws Exception {
        //given
        SortType sort = SortType.A;
        SearchDto searchDto = SearchDto.of(sort, 1, 10, "kakao bank", SearchType.KAKAO);
        //when
        searchService.search(searchDto);
        //then
    }
}