package com.juno.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.dto.kakao.SearchResponseDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.vo.DocumentsVo;
import com.juno.search.domain.vo.SearchVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService{
    private final Environment env;
    private final SearchClient searchClient;
    private final ObjectMapper objectMapper;

    @Override
    public SearchVo search(SearchDto search) {
        // TODO 다음, 네이버 값에 따라 검색 조건 바꾸기
        String sort = search.getSortByType();
        int page = search.getPage();
        int size = search.getSize();
        String query = search.getQuery();
        SearchType type = search.getType();

        StringBuilder uriBuilder = getSearchUri(sort, page, size, query, type);

        SearchVo result = null;
        if(type == SearchType.NAVER){

        }else{
            result = getSearchByKakao(uriBuilder);
        }

        return result;
    }

    private SearchVo getSearchByKakao(StringBuilder uriBuilder) {
        SearchResponseDto searchResponseDto = searchClient.kakaoSearch().get()
                .uri(uriBuilder.toString())
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

    private StringBuilder getSearchUri(String sort, int page, int size, String query, SearchType type) {
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
