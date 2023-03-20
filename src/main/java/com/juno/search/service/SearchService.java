package com.juno.search.service;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.vo.SearchListVo;
import com.juno.search.domain.vo.TopSearchVo;

public interface SearchService {
    SearchListVo search(SearchDto search);
    TopSearchVo topSearch();
}
