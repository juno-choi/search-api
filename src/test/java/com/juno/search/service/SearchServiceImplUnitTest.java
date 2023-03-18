package com.juno.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.dto.kakao.BadRequest;
import com.juno.search.domain.dto.kakao.Meta;
import com.juno.search.domain.dto.kakao.SearchResponseDto;
import com.juno.search.domain.dto.naver.Item;
import com.juno.search.domain.dto.naver.NaverSearchResponseDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import com.juno.search.domain.vo.SearchVo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplUnitTest {
    public static MockWebServer mockWebServer;

    private MockEnvironment mockEnvironment = new MockEnvironment();
    private ObjectMapper objectMapper = new ObjectMapper();
    private SearchService searchService;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void down() throws IOException {
        mockWebServer.shutdown();
    }


    @BeforeEach
    void init(){
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient webClient = WebClient.create(baseUrl);
        searchService = new SearchServiceImpl(new SearchClient(mockEnvironment, webClient, objectMapper));
    }

    @Test
    @DisplayName("검색되지 않는 내용일 경우에도 성공한다. (kakao)")
    void searchSuccess1() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "검색 내용이 존재하지 않는 문구", SearchType.KAKAO);

        mockWebServer.enqueue(new MockResponse().setBody(
                objectMapper.writeValueAsString(SearchResponseDto.builder()
                        .documents(new ArrayList<>())
                        .meta(new Meta(0,0,true))
                        .build())
        ).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        //when
        SearchVo search = searchService.search(searchDto);
        //then
        assertTrue(search.getList().size() == 0);
    }

    @Test
    @DisplayName("검색되지 않는 내용일 경우에도 성공한다. (naver)")
    void searchSuccess2() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 10, "검색 내용이 존재하지 않는 문구", SearchType.NAVER);

        mockWebServer.enqueue(new MockResponse().setBody(
                objectMapper.writeValueAsString(NaverSearchResponseDto.builder()
                        .display(10)
                        .start(1)
                        .total(0)
                        .items(new ArrayList<>())
                        .build())
        ).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        //when
        SearchVo search = searchService.search(searchDto);
        //then
        assertTrue(search.getList().size() == 0);
    }


    @Test
    @DisplayName("kakao로 검색에 실패하면 naver로 반환에 성공한다.")
    void searchSuccess3() throws Exception {
        //given
        int size = 10;
        SearchDto searchDto = SearchDto.of(SortType.A, 1, size, "kakao 서버 터짐", SearchType.KAKAO);

        List<Item> items = new ArrayList<>();
        for(int i=0; i<size; i++){
            items.add(Item.builder()
                    .title("제목 "+i)
                    .bloggerlink("블로그 상세 링크")
                    .bloggername("블로그명")
                    .description("내용")
                    .link("블로그 링크")
                    .postdate("29991231")
                    .build());
        }

        mockWebServer.enqueue(new MockResponse().setStatus("HTTP/1.1 504"));
        mockWebServer.enqueue(new MockResponse().setBody(
                objectMapper.writeValueAsString(NaverSearchResponseDto.builder()
                        .display(size)
                        .start(1)
                        .total(0)
                        .items(items)
                        .build())
        ).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        //when
        SearchVo search = searchService.search(searchDto);
        //then
        assertTrue(search.getList().get(0).getTitle().equals("제목 0"));
    }

    @Test
    @DisplayName("검색 수치를 넘어가면 실패한다.")
    void searchFail1() throws Exception {
        //given
        int size = 60;
        SearchDto searchDto = SearchDto.of(SortType.A, 1, size, "사이즈가 너무 큼", SearchType.KAKAO);

        mockWebServer.enqueue(new MockResponse().setStatus("HTTP/1.1 400").setBody(
                objectMapper.writeValueAsString(
                        BadRequest.builder()
                                .errorType("InvalidArgument")
                                .message("page is more than max")
                                .build()
                )
        ));

        //when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> searchService.search(searchDto));

        //then
        assertEquals("size와 page는 최대 50 입니다.", ex.getMessage());
    }
}