package com.juno.search.controller;

import com.juno.search.docs.TestSupport;
import com.juno.search.domain.entity.Search;
import com.juno.search.repository.SearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest extends TestSupport {
    @Autowired
    private SearchRepository searchRepository;

    @Test
    @DisplayName("size가 50보다 크면 실패한다. (kakao)")
    void searchFail1() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                        .param("page", "1")
                        .param("size", "51")
                        .param("query", "kakao bank")
                        .param("type", "kakao")
        ).andDo(print());

        //then
        String content = perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        perform.andExpect(status().is4xxClientError());
        assertTrue(content.contains("FAIL"));
    }

    @Test
    @DisplayName("page가 50보다 크면 실패한다. (kakao)")
    void searchFail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                        .param("page", "51")
                        .param("size", "10")
                        .param("query", "kakao bank")
                        .param("type", "kakao")
        ).andDo(print());

        //then
        String content = perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        perform.andExpect(status().is4xxClientError());
        assertTrue(content.contains("FAIL"));
    }

    @Test
    @DisplayName("size가 100보다 크면 실패한다. (naver)")
    void searchFail3() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                        .param("page", "1")
                        .param("size", "101")
                        .param("query", "kakao bank")
                        .param("type", "naver")
        ).andDo(print());

        //then
        String content = perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        perform.andExpect(status().is4xxClientError());
        assertTrue(content.contains("FAIL"));
    }

    @Test
    @DisplayName("page가 1000보다 크면 실패한다. (naver)")
    void searchFail4() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                        .param("page", "1001")
                        .param("size", "10")
                        .param("query", "kakao bank")
                        .param("type", "naver")
        ).andDo(print());

        //then
        String content = perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        perform.andExpect(status().is4xxClientError());
        assertTrue(content.contains("FAIL"));
    }

    @Test
    @DisplayName("검색에 성공한다.")
    void searchSuccess1() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/v1/search").param("sort", "a")
                .param("page", "1")
                .param("size", "10")
                .param("query", "kakao bank")
        ).andDo(print());

        //then
        String content = perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("SUCCESS"));
    }

    @Test
    @DisplayName("top10 검색에 성공한다.")
    void topSearchSuccess1() throws Exception {
        //given
        String top1 = "juno";
        Map<String, Integer> map = new HashMap<>();
        map.put(top1, 10000);
        map.put("luna", 9999);
        map.put("kakao", 123);
        map.put("naver", 1234);
        map.put("최준호", 1);
        map.put("Spring", 133);
        map.put("api", 101);
        map.put("java", 122);
        map.put("kotlin", 332);
        map.put("kopring", 4441);
        map.put("k", 2);
        map.put("a", 3);

        Set<String> keys = map.keySet();
        for(String key : keys){
            searchRepository.save(Search.of(key, map.get(key)));
        }

        //when
        ResultActions perform = mockMvc.perform(get("/v1/search/top"))
                .andDo(print());
        //then
        perform.andExpect(status().is2xxSuccessful());
        assertTrue(perform.andReturn()
                .getResponse()
                .getContentAsString()
                .contains("{\"keyword\":\"juno\",\"count\":10000,\"rank\":1}"));
    }
}