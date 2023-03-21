package com.juno.search.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.dto.kakao.BadRequest;
import com.juno.search.domain.dto.kakao.Meta;
import com.juno.search.domain.dto.kakao.SearchResponseDto;
import com.juno.search.domain.dto.naver.Item;
import com.juno.search.domain.dto.naver.NaverBadRequest;
import com.juno.search.domain.dto.naver.NaverSearchResponseDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.enums.SortType;
import com.juno.search.domain.vo.SearchListVo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchClientTest {
    public static MockWebServer mockWebServer;

    private MockEnvironment mockEnvironment = new MockEnvironment();
    private ObjectMapper objectMapper = new ObjectMapper();
    private SearchClient searchClient;


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
        searchClient = new SearchClient(mockEnvironment, webClient);
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
        SearchListVo search = searchClient.search(searchDto);
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
        SearchListVo search = searchClient.search(searchDto);
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
        SearchListVo search = searchClient.search(searchDto);
        //then
        assertTrue(search.getList().get(0).getTitle().equals("제목 0"));
    }

    @Test
    @DisplayName("size가 50을 넘으면 실패한다. (kakao)")
    void searchFail1() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 51, "사이즈가 너무 큼", SearchType.KAKAO);

        mockWebServer.enqueue(new MockResponse().setStatus("HTTP/1.1 400").setBody(
                objectMapper.writeValueAsString(
                        BadRequest.builder()
                                .errorType("InvalidArgument")
                                .message("size is more than max")
                                .build()
                )
        ));

        //when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> searchClient.search(searchDto));

        //then
        assertEquals("size, page 1~50 입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("page가 50을 넘으면 실패한다. (kakao)")
    void searchFail2() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 51, 10, "사이즈가 너무 큼", SearchType.KAKAO);

        mockWebServer.enqueue(new MockResponse().setStatus("HTTP/1.1 400").setBody(
                objectMapper.writeValueAsString(
                        BadRequest.builder()
                                .errorType("InvalidArgument")
                                .message("page is more than max")
                                .build()
                )
        ));

        //when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> searchClient.search(searchDto));

        //then
        assertEquals("size, page 1~50 입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("size가 100을 넘으면 실패한다. (naver)")
    void searchFail3() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1, 101, "사이즈가 너무 큼", SearchType.NAVER);

        mockWebServer.enqueue(new MockResponse().setStatus("HTTP/1.1 400").setBody(
                objectMapper.writeValueAsString(
                        NaverBadRequest.builder()
                                .errorMessage("Invalid display value (부적절한 display 값입니다.)")
                                .errorCode("SE02")
                                .build()
                )
        ));

        //when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> searchClient.search(searchDto));

        //then
        assertEquals("size 1~100 page 1~1000 입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("page가 1000을 넘으면 실패한다. (naver)")
    void searchFail4() throws Exception {
        //given
        SearchDto searchDto = SearchDto.of(SortType.A, 1001, 10, "사이즈가 너무 큼", SearchType.NAVER);

        mockWebServer.enqueue(new MockResponse().setStatus("HTTP/1.1 400").setBody(
                objectMapper.writeValueAsString(
                        NaverBadRequest.builder()
                                .errorMessage("Invalid display value (부적절한 display 값입니다.)")
                                .errorCode("SE02")
                                .build()
                )
        ));

        //when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> searchClient.search(searchDto));

        //then
        assertEquals("size 1~100 page 1~1000 입니다.", ex.getMessage());
    }
}