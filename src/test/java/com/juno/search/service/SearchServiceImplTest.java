package com.juno.search.service;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.entity.Search;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import com.juno.search.domain.vo.SearchVo;
import com.juno.search.repository.SearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional(readOnly = true)
class SearchServiceImplTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchRepository searchRepository;

    @Test
    @DisplayName("카카오 검색에 성공한다. (정확도)")
    void searchSuccess1() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "카카오", SearchType.KAKAO);

        //when
        SearchVo search = searchService.search(searchDto);

        //then
        assertNotNull(search.getList());
    }


    @Test
    @DisplayName("카카오 검색에 성공한다. (최신순)")
    void searchSuccess2() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.R, 1, 10, "카카오", SearchType.KAKAO);

        //when
        SearchVo search = searchService.search(searchDto);

        //then
        LocalDate parse1 = LocalDate.parse(search.getList().get(0).getDatetime(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate parse2 = LocalDate.parse(search.getList().get(0).getDatetime(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        int compare = parse1.compareTo(parse2);
        assertTrue(compare >= 0);
    }

    @Test
    @DisplayName("네이버 검색에 성공한다. (정확도)")
    void searchSuccess3() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "네이버", SearchType.NAVER);

        //when
        SearchVo search = searchService.search(searchDto);

        //then
        assertNotNull(search.getList());
    }

    @Test
    @DisplayName("네이버 검색에 성공한다. (최신순)")
    void searchSuccess4() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.R, 1, 10, "네이버", SearchType.NAVER);

        //when
        SearchVo search = searchService.search(searchDto);

        //then
        LocalDate parse1 = LocalDate.parse(search.getList().get(0).getDatetime(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate parse2 = LocalDate.parse(search.getList().get(0).getDatetime(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        int compare = parse1.compareTo(parse2);
        assertTrue(compare >= 0);
    }

    @Test
    @DisplayName("검색어 입력시 keyword 데이터 저장에 성공한다.")
    void searchSuccess5() throws Exception {
        //given
        String keyword = "카카오";
        SearchDto searchDto = SearchDto.of(SortType.R, 1, 10, keyword, SearchType.KAKAO);

        //when
        searchService.search(searchDto);

        //then
        String findKeyword = searchRepository.findByKeyword(keyword).get().getKeyword().toLowerCase(Locale.ROOT);
        assertTrue(findKeyword.equals(keyword.trim().toLowerCase(Locale.ROOT)));
    }

    @Test
    @DisplayName("이미 존재하는 검색어는 조회수를 증가키는데 성공한다.")
    @Transactional
    void searchSuccess6() throws Exception {
        //given
        String keyword = "카카오2";
        Search saveSearch = searchRepository.save(Search.of(keyword));
        saveSearch.plusCount();
        saveSearch.plusCount();
        saveSearch.plusCount();
        saveSearch.plusCount(); // 이미 조회수 5를 가진 데이터 생성

        SearchDto searchDto = SearchDto.of(SortType.R, 1, 10, keyword, SearchType.KAKAO);

        //when
        searchService.search(searchDto);

        //then
        int count = searchRepository.findByKeyword(keyword).get().getCount();
        assertEquals(6, count);
    }
}