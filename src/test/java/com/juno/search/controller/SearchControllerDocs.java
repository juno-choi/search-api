package com.juno.search.controller;

import com.juno.search.docs.RestdocsSupport;
import com.juno.search.domain.entity.Search;
import com.juno.search.repository.SearchRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerDocs extends RestdocsSupport {

    @Autowired
    private SearchRepository searchRepository;

    @AfterEach
    void deleteAll(){
        searchRepository.deleteAll();
    }

    @Test
    @DisplayName("/v1/search (kakao 정확도)")
    void searchA() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                .param("page", "1")
                .param("size", "10")
                .param("query", "kakao bank")
                .param("type", "kakao")
        );

        //then
        perform.andDo(docs.document(
                requestParameters(
                        parameterWithName("sort").description("정렬 기준 (a:정확도, r:최신순)"),
                        parameterWithName("page").description("결과 페이지 번호, 1~50 사이의 값, 기본 값 1"),
                        parameterWithName("size").description("한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10"),
                        parameterWithName("query").description("검색을 원하는 질의어").optional(),
                        parameterWithName("type").description("검색 엔진 설정 기본 값 kakao (kakao, naver)")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.from").type(JsonFieldType.STRING).description("결과 값을 가져온 검색 엔진"),
                        fieldWithPath("data.list_size").type(JsonFieldType.NUMBER).description("결과 값 리스트 크기"),
                        fieldWithPath("data.list[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("data.list[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("data.list[].url").type(JsonFieldType.STRING).description("해당 글 url"),
                        fieldWithPath("data.list[].blog_name").type(JsonFieldType.STRING).description("블로그 이름"),
                        fieldWithPath("data.list[].thumbnail").type(JsonFieldType.STRING).description("thumbnail"),
                        fieldWithPath("data.list[].datetime").type(JsonFieldType.STRING).description("등록일")
                )
        ));
    }

    @Test
    @DisplayName("/v1/search (kakao 최신순)")
    void searchR() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "r")
                        .param("page", "1")
                        .param("size", "10")
                        .param("query", "kakao bank")
                        .param("type", "kakao")
        );

        //then
        perform.andDo(docs.document(
                requestParameters(
                        parameterWithName("sort").description("정렬 기준 (a:정확도, r:최신순)"),
                        parameterWithName("page").description("결과 페이지 번호, 1~50 사이의 값, 기본 값 1"),
                        parameterWithName("size").description("한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10"),
                        parameterWithName("query").description("검색을 원하는 질의어").optional(),
                        parameterWithName("type").description("검색 엔진 설정 기본 값 kakao (kakao, naver)")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.from").type(JsonFieldType.STRING).description("결과 값을 가져온 검색 엔진"),
                        fieldWithPath("data.list_size").type(JsonFieldType.NUMBER).description("결과 값 리스트 크기"),
                        fieldWithPath("data.list[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("data.list[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("data.list[].url").type(JsonFieldType.STRING).description("해당 글 url"),
                        fieldWithPath("data.list[].blog_name").type(JsonFieldType.STRING).description("블로그 이름"),
                        fieldWithPath("data.list[].thumbnail").type(JsonFieldType.STRING).description("thumbnail"),
                        fieldWithPath("data.list[].datetime").type(JsonFieldType.STRING).description("등록일")
                )
        ));
    }

    @Test
    @DisplayName("/v1/search (naver 정확도)")
    void searchNaverA() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                        .param("page", "1")
                        .param("size", "10")
                        .param("query", "kakao bank")
                        .param("type", "naver")
        );

        //then
        perform.andDo(docs.document(
                requestParameters(
                        parameterWithName("sort").description("정렬 기준 (a:정확도, r:최신순)"),
                        parameterWithName("page").description("결과 페이지 번호, 1~50 사이의 값, 기본 값 1"),
                        parameterWithName("size").description("한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10"),
                        parameterWithName("query").description("검색을 원하는 질의어").optional(),
                        parameterWithName("type").description("검색 엔진 설정 기본 값 kakao (kakao, naver)")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.from").type(JsonFieldType.STRING).description("결과 값을 가져온 검색 엔진"),
                        fieldWithPath("data.list_size").type(JsonFieldType.NUMBER).description("결과 값 리스트 크기"),
                        fieldWithPath("data.list[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("data.list[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("data.list[].url").type(JsonFieldType.STRING).description("해당 글 url"),
                        fieldWithPath("data.list[].blog_name").type(JsonFieldType.STRING).description("블로그 이름"),
                        fieldWithPath("data.list[].thumbnail").type(JsonFieldType.STRING).description("thumbnail"),
                        fieldWithPath("data.list[].datetime").type(JsonFieldType.STRING).description("등록일")
                )
        ));
    }

    @Test
    @DisplayName("/v1/search (naver 최신순)")
    void searchNaverR() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "r")
                        .param("page", "1")
                        .param("size", "10")
                        .param("query", "kakao bank")
                        .param("type", "naver")
        );

        //then
        perform.andDo(docs.document(
                requestParameters(
                        parameterWithName("sort").description("정렬 기준 (a:정확도, r:최신순)"),
                        parameterWithName("page").description("결과 페이지 번호, 1~50 사이의 값, 기본 값 1"),
                        parameterWithName("size").description("한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10"),
                        parameterWithName("query").description("검색을 원하는 질의어").optional(),
                        parameterWithName("type").description("검색 엔진 설정 기본 값 kakao (kakao, naver)")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.from").type(JsonFieldType.STRING).description("결과 값을 가져온 검색 엔진"),
                        fieldWithPath("data.list_size").type(JsonFieldType.NUMBER).description("결과 값 리스트 크기"),
                        fieldWithPath("data.list[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("data.list[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("data.list[].url").type(JsonFieldType.STRING).description("해당 글 url"),
                        fieldWithPath("data.list[].blog_name").type(JsonFieldType.STRING).description("블로그 이름"),
                        fieldWithPath("data.list[].thumbnail").type(JsonFieldType.STRING).description("thumbnail"),
                        fieldWithPath("data.list[].datetime").type(JsonFieldType.STRING).description("등록일")
                )
        ));
    }


    @Test
    @DisplayName("/v1/search/top")
    void topSearch() throws Exception {
        //given
        String top1 = "juno";
        Map<String, Integer> map = new HashMap<>();
        map.put(top1, 10000);
        map.put("kakao", 123);
        map.put("naver", 1234);
        map.put("최준호", 1);
        map.put("Spring", 133);
        map.put("api", 101);
        map.put("java", 122);
        map.put("kotlin", 332);
        map.put("kopring", 4441);
        map.put("luna", 9999);

        Set<String> keys = map.keySet();
        for(String key : keys){
            searchRepository.save(Search.of(key, map.get(key)));
        }

        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search/top")
        );

        //then
        perform.andDo(docs.document(
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.list[].keyword").type(JsonFieldType.STRING).description("검색어"),
                        fieldWithPath("data.list[].count").type(JsonFieldType.NUMBER).description("검색 횟수"),
                        fieldWithPath("data.list[].rank").type(JsonFieldType.NUMBER).description("순위 (정렬 기준 : 검색 횟수 > 최근 검색일 순)")
                )
        ));
    }
}