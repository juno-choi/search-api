package com.juno.search.config;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.dto.kakao.SearchResponseDto;
import com.juno.search.domain.dto.naver.NaverSearchResponseDto;
import com.juno.search.domain.enums.SearchType;
import com.juno.search.domain.vo.DocumentsVo;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.exception.KakaoServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class SearchClient {
    private final Environment env;
    private final WebClient webClient;

    public SearchListVo search(SearchDto search){
        SearchType type = search.getType();

        if(type == SearchType.NAVER){
            return getSearchByNaver(search);
        }

        return getSearchByKakao(search);
    }

    // 네이버 검색
    private SearchListVo getSearchByNaver(SearchDto search) {
        String baseUrl = env.getProperty("api.naver.url");
        NaverSearchResponseDto naverSearchResponseDto = webClient.get().uri(baseUrl + getSearchUri(search))
                .header("X-Naver-Client-Id", env.getProperty("api.naver.id"))
                .header("X-Naver-Client-Secret", env.getProperty("api.naver.secret"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new IllegalArgumentException("size 1~100 page 1~1000 입니다.")))
                .bodyToMono(NaverSearchResponseDto.class)
                .block();

        List<DocumentsVo> list = naverSearchResponseDto.getItems().stream().map(m ->
                DocumentsVo.builder()
                    .title(m.getTitle())
                    .contents(m.getDescription())
                    .url(m.getLink())
                    .blogName(m.getBloggername())
                    .thumbnail("")
                    .datetime(m.getPostdate())
                    .build()
        ).collect(Collectors.toList());

        return SearchListVo.builder()
                .from("NAVER")
                .listSize(list.size())
                .list(list)
                .build();
    }

    // 카카오 검색
    private SearchListVo getSearchByKakao(SearchDto search) {
        String baseUrl = env.getProperty("api.kakao.url");
        try{
            SearchResponseDto searchResponseDto = webClient.get().uri(baseUrl + getSearchUri(search))
                    .header(AUTHORIZATION, "KakaoAK " + env.getProperty("api.kakao.key"))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new IllegalArgumentException("size, page 1~50 입니다.")))
                    .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(KakaoServerException::new))
                    .bodyToMono(SearchResponseDto.class)
                    .timeout(Duration.ofMillis(5000))   // 5초 동안 답 없으면 타임아웃
                    .block();

            List<DocumentsVo> list = searchResponseDto.getDocuments().stream().map(m ->
                    DocumentsVo.builder()
                            .title(m.getTitle())
                            .contents(m.getContents())
                            .url(m.getUrl())
                            .blogName(m.getBlogname())
                            .thumbnail(m.getThumbnail())
                            .datetime(ZonedDateTime.parse(m.getDatetime()).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                            .build()
            ).collect(Collectors.toList());

            return SearchListVo.builder()
                    .from("KAKAO")
                    .listSize(list.size())
                    .list(list)
                    .build();
        }catch (KakaoServerException e){
            return getSearchByNaver(search);
        }
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
            uriBuilder.append("?query=");
            uriBuilder.append(query);
            uriBuilder.append("&sort=");
            uriBuilder.append(sort);
            uriBuilder.append("&start=");
            uriBuilder.append(page);
            uriBuilder.append("&display=");
            uriBuilder.append(size);
        }
        return uriBuilder;
    }
}
