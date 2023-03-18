package com.juno.search.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.dto.kakao.SearchResponseDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.vo.DocumentsVo;
import com.juno.search.domain.vo.SearchVo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class SearchClient {
    private final Environment env;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public SearchVo search(SearchDto search){
        SearchType type = search.getType();

        if(type == SearchType.NAVER){
            return getSearchByNaver(search);
        }

        return getSearchByKakao(search);
    }

    // 네이버 검색
    private SearchVo getSearchByNaver(SearchDto search) {
        String baseUrl = env.getProperty("api.naver.url");
        SearchResponseDto searchResponseDto = webClient.get().uri(baseUrl + getSearchUri(search))
                .header(AUTHORIZATION, "Naver "+ env.getProperty("api.naver.key"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SearchResponseDto.class)
                .timeout(Duration.ofMillis(5000))   // 5초 동안 답 없으면 타임아웃
                .blockOptional().orElseThrow(
                        () -> new IllegalArgumentException("검색 내용이 없습니다.")
                );

        List<DocumentsVo> list = objectMapper.convertValue(searchResponseDto.getDocuments(), new TypeReference<List<DocumentsVo>>(){});
        return SearchVo.builder()
                .list(list)
                .build();
    }

    // 카카오 검색
    private SearchVo getSearchByKakao(SearchDto search) {
        String baseUrl = env.getProperty("api.kakao.url");
        SearchResponseDto searchResponseDto = webClient.get().uri(baseUrl + getSearchUri(search))
                .header(AUTHORIZATION, "KakaoAK " + env.getProperty("api.kakao.key"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SearchResponseDto.class)
                .timeout(Duration.ofMillis(5000))   // 5초 동안 답 없으면 타임아웃
                .blockOptional().orElseThrow(
                        () -> new IllegalArgumentException("검색 내용이 없습니다.")
                );

        List<DocumentsVo> list = objectMapper.convertValue(searchResponseDto.getDocuments(), new TypeReference<List<DocumentsVo>>(){});
        return SearchVo.builder()
                .list(list)
                .build();
    }

    private StringBuilder getSearchUri(SearchDto search) {
        String sort = search.getSortByType();
        int page = search.getPage();
        int size = search.getSize();
        String query = search.getQuery();
        SearchType type = search.getType();

        StringBuilder uriBuilder = new StringBuilder();
        if(type == SearchType.KAKAO){
            uriBuilder.append("?sort=");
            uriBuilder.append(sort);
            uriBuilder.append("&page=");
            uriBuilder.append(page);
            uriBuilder.append("&size=");
            uriBuilder.append(size);
            uriBuilder.append("&query=");
            uriBuilder.append(query);
        }else if(type == SearchType.NAVER){

        }
        return uriBuilder;
    }
}
