package com.juno.search.controller;

import com.juno.search.docs.TestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest extends TestSupport {
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
}