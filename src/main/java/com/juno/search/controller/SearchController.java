package com.juno.search.controller;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.domain.vo.TopSearchVo;
import com.juno.search.domain.vo.api.ApiResponse;
import com.juno.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
@Validated
public class SearchController {
    private final SearchService searchService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<SearchListVo>> search(
            @RequestParam(name = "sort", required = false, defaultValue = "a") String sort,
            @RequestParam(name = "page", required = false, defaultValue = "1")
            @Min(value = 1, message = "최소 1 입니다.")
            @Max(value = 50, message = "최대 50 입니다.") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10")
            @Min(value = 1, message = "최소 1 입니다.")
            @Max(value = 50, message = "최대 50 입니다.") int size,
            @RequestParam(name = "query")
            @Length(min = 1, max = 100, message = "최소 1 최대 100 입니다.") String query,
            @RequestParam(name = "type", required = false, defaultValue = "kakao") String type
    ){
        return ResponseEntity.ok(
                ApiResponse.<SearchListVo>builder()
                        .code("SUCCESS")
                        .message("정상")
                        .data(searchService.search(SearchDto.of(sort, page, size, query, type)))
                        .build()
        );
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<TopSearchVo>> topSearch(){
        return ResponseEntity.ok(ApiResponse.<TopSearchVo>builder()
                .code("SUCCESS")
                .message("정상")
                .data(searchService.topSearch())
                .build());
    }
}
