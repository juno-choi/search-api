package com.juno.search.service;

import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.entity.Search;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.domain.vo.SearchVo;
import com.juno.search.domain.vo.TopSearchVo;
import com.juno.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService{
    private final SearchClient searchClient;
    private final SearchRepository searchRepository;

    @Override
    @Transactional
    public SearchListVo search(SearchDto search) {
        String keyword = search.getQuery().trim().toLowerCase(Locale.ROOT);

        Optional<Search> findSearch = searchRepository.findByKeyword(keyword);
        saveSearch(keyword, findSearch);

        SearchListVo searchListVo = searchClient.search(search);
        return searchListVo;
    }

    @Override
    public TopSearchVo topSearch() {
        List<Search> top10ByOrderByCountDesc = searchRepository.findTop10ByOrderByCountDesc();

        List<SearchVo> list = top10ByOrderByCountDesc.stream().map(m ->
                SearchVo.builder()
                        .keyword(m.getKeyword())
                        .count(m.getCount())
                        .build()
        ).collect(Collectors.toList());

        return TopSearchVo.builder()
                .list(list)
                .build();
    }

    private void saveSearch(String keyword, Optional<Search> findSearch) {
        if(findSearch.isPresent()){
            findSearch.get().plusCount();
            return ;
        }
        searchRepository.save(Search.of(keyword));
    }
}
