package com.juno.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.entity.Search;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import com.juno.search.domain.vo.DocumentsVo;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.domain.vo.SearchVo;
import com.juno.search.domain.vo.TopSearchVo;
import com.juno.search.repository.SearchRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplUnitTest {
    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private HashOperations hashOperations;

    @Mock
    private SearchClient searchClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

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
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.get(anyString(), anyString())).willReturn(null);
        doNothing().when(hashOperations).put(anyString(), any(), any());
        given(objectMapper.writeValueAsString(any())).willReturn("");

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
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.get(anyString(), anyString())).willReturn(null);
        doNothing().when(hashOperations).put(anyString(), any(), any());
        given(objectMapper.writeValueAsString(any())).willReturn("");
        //when
        SearchListVo search = searchService.search(searchDto);
        //then
        assertTrue(search.getList().size() == 0);
    }

    @Test
    @DisplayName("한번 검색한 내용은 redis에서 데이터를 가져오는데 성공한다.")
    void searchSuccess3() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "캐싱될 문구", SearchType.KAKAO);
        
        List<DocumentsVo> documentsVos = new ArrayList<>();
        for(int i=1; i<=10; i++){
            documentsVos.add(DocumentsVo.builder()
                    .title("제목"+i)
                    .contents("내용"+i)
                    .blogName("블로그 이름")
                    .thumbnail("썸네일 주소")
                    .datetime("20230301")
                    .url("url")
                    .build());
        }
        SearchListVo searchListVo = SearchListVo.builder()
                .from("KAKAO")
                .listSize(10)
                .list(documentsVos)
                .build();

        String searchListVoAsString = new ObjectMapper().writeValueAsString(searchListVo);

        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.get(anyString(), anyString())).willReturn(searchListVoAsString);
        given(objectMapper.readValue(searchListVoAsString, SearchListVo.class)).willReturn(searchListVo);

        //when
        SearchListVo search = searchService.search(searchDto);

        //then
        assertNotNull(search);
        verify(objectMapper, times(1)).readValue(searchListVoAsString, SearchListVo.class);
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