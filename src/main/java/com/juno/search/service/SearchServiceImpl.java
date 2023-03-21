package com.juno.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.search.config.SearchClient;
import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.entity.Search;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.domain.vo.SearchVo;
import com.juno.search.domain.vo.TopSearchVo;
import com.juno.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService{
    private final SearchClient searchClient;
    private final SearchRepository searchRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SearchListVo search(SearchDto search) {
        String keyword = search.getQuery().trim().toLowerCase(Locale.ROOT);
        String hashKey = getHashKey(search);
        SearchListVo searchListVo;

        Optional<Search> findSearch = searchRepository.findByKeyword(keyword);
        saveSearch(keyword, findSearch);

        Optional<Object> optional = Optional.ofNullable(redisTemplate.opsForHash().get(keyword, hashKey));
        if(optional.isPresent()){
            return getSearchListVo(optional);
        }

        searchListVo = searchClient.search(search);
        saveRedis(keyword, hashKey, searchListVo);
        return searchListVo;
    }

    private void saveRedis(String keyword, String hashKey, SearchListVo searchListVo) {
        try {
            redisTemplate.opsForHash().put(keyword, hashKey, objectMapper.writeValueAsString(searchListVo));
        } catch (JsonProcessingException e) {
            log.error("write error", e);
        }
        redisTemplate.expire(keyword, 5L, TimeUnit.MINUTES);  // 5분간 캐싱
    }

    private String getHashKey(SearchDto search) {
        StringBuilder hashKeyBuilder = new StringBuilder();
        hashKeyBuilder.append(search.getType());
        hashKeyBuilder.append("/");
        hashKeyBuilder.append(search.getSort());
        hashKeyBuilder.append("/");
        hashKeyBuilder.append(search.getPage());
        hashKeyBuilder.append("/");
        hashKeyBuilder.append(search.getSize());
        return hashKeyBuilder.toString();
    }

    private SearchListVo getSearchListVo(Optional<Object> optional) {
        String searchListVoStr = (String) optional.get();
        SearchListVo searchListVo = null;
        try {
            searchListVo = objectMapper.readValue(searchListVoStr, SearchListVo.class);
        } catch (JsonProcessingException e) {
            log.error("read error", e);
        }
        return searchListVo;
    }

    private void saveSearch(String keyword, Optional<Search> findSearch) {
        if(findSearch.isPresent()){
            findSearch.get().plusCount();
            return ;
        }
        searchRepository.save(Search.of(keyword));
    }

    @Override
    public TopSearchVo topSearch() {
        List<Search> top10ByOrderByCountDesc = searchRepository.findTop10ByOrderByCountDescModifiedAtDesc();

        int rank = 1;
        List<SearchVo> list = new LinkedList<>();
        for(Search s : top10ByOrderByCountDesc){
            list.add(SearchVo.builder()
                    .keyword(s.getKeyword())
                    .count(s.getCount())
                    .rank(rank++)
                    .build());
        }

        return TopSearchVo.builder()
                .list(list)
                .build();
    }
}
