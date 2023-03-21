package com.juno.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.entity.Search;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.domain.vo.SearchVo;
import com.juno.search.domain.vo.TopSearchVo;
import com.juno.search.repository.SearchRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("top10 검색에 성공한다.")
    void topSearchSuccess1() throws Exception {
        //given
        List<Search> list = new ArrayList<>();
        int count = 10000;
        for(int i=1; i<=10; i++){
            list.add(Search.of("키워드"+i, count-i));
        }

        given(searchRepository.findTop10ByOrderByCountDescModifiedAtDesc()).willReturn(list);
        
        //when
        TopSearchVo topSearchVo = searchService.topSearch();
        
        //then
        assertEquals("키워드1", topSearchVo.getList().get(0).getKeyword());
    }
    
    @Test
    @DisplayName("top10 랭킹값 검색에 성공한다.")
    void topSearchSuccess2() throws Exception {
        //given
        List<Search> list = new ArrayList<>();
        int count = 10000;
        for(int i=1; i<=10; i++){
            list.add(Search.of("키워드"+i, count-i));
        }

        given(searchRepository.findTop10ByOrderByCountDescModifiedAtDesc()).willReturn(list);

        //when
        TopSearchVo topSearchVo = searchService.topSearch();

        //then
        List<SearchVo> searchVoList = topSearchVo.getList();
        for(int i=1; i<=10; i++){
            SearchVo searchVo = searchVoList.get(i-1);
            assertEquals(i, searchVo.getRank());
            assertEquals(count-i, searchVo.getCount());
        }
    }
}