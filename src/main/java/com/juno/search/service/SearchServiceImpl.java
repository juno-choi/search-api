package com.juno.search.service;

import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.dto.kakao.SearchResponseDto;
import com.juno.search.domain.enums.SearchType;
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

    @Override
    public List<SearchVo> search(SearchDto search) {
        // TODO 다음, 네이버 값에 따라 검색 조건 바꾸기
        String sort = search.getSortByType();
        int page = search.getPage();
        int size = search.getSize();
        String query = search.getQuery();
        SearchType type = search.getType();

        StringBuilder uriBuilder = getSearchUri(sort, page, size, query, type);

        SearchResponseDto searchResponseDto = searchClient.kakaoSearch().get()
                .uri(uriBuilder.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SearchResponseDto.class)
                .timeout(Duration.ofMillis(5000))   // 5초 동안 답 없으면 타임아웃
                .blockOptional().orElseThrow(
                        // TODO 네이버와 다음 익셉션 만들어서 처리해야 함.
                        () -> new IllegalArgumentException("우선은 이렇게 던짐 추후에 네이버와 다음 따로 분리")
                );
        log.debug(searchResponseDto.toString());
        return null;
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
