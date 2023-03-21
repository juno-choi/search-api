package com.juno.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.repository.SearchRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplUnitTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchClient searchClient;

    @Test
    @DisplayName("검색되지 않는 내용일 경우에도 성공한다. (kakao)")
    void searchSuccess1() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "검색 내용이 존재하지 않는 문구", SearchType.KAKAO);
        given(searchClient.search(any())).willReturn(SearchListVo.builder()
                .from("KAKAO")
                .listSize(0)
                .list(new ArrayList<>())
                .build());

        //when
        SearchListVo search = searchService.search(searchDto);

        //then
        assertTrue(search.getList().size() == 0);
    }

    @Test
    @DisplayName("검색되지 않는 내용일 경우에도 성공한다. (naver)")
    void searchSuccess2() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "검색 내용이 존재하지 않는 문구", SearchType.NAVER);
        given(searchClient.search(any())).willReturn(SearchListVo.builder()
                .from("NAVER")
                .listSize(0)
                .list(new ArrayList<>())
                .build());
        //when
        SearchListVo search = searchService.search(searchDto);
        //then
        assertTrue(search.getList().size() == 0);
    }

}