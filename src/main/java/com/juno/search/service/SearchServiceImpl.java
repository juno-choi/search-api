package com.juno.search.service;

import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.entity.Search;
import com.juno.search.domain.vo.SearchVo;
import com.juno.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService{
    private final SearchClient searchClient;
    private final SearchRepository searchRepository;

    @Override
    @Transactional
    public SearchVo search(SearchDto search) {
        String keyword = search.getQuery().trim().toLowerCase(Locale.ROOT);

        Optional<Search> findSearch = searchRepository.findByKeyword(keyword);
        saveSearch(keyword, findSearch);

        SearchVo searchVo = searchClient.search(search);
        return searchVo;
    }

    private void saveSearch(String keyword, Optional<Search> findSearch) {
        if(findSearch.isPresent()){
            findSearch.get().plusCount();
            return ;
        }
        searchRepository.save(Search.of(keyword));
    }
}
