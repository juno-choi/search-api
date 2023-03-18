package com.juno.search.service;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.vo.SearchVo;

import java.util.List;

public interface SearchService {
    List<SearchVo> search(SearchDto search);
}
