package com.juno.search.service;

import com.juno.search.domain.dto.SearchDto;
import com.juno.search.domain.vo.SearchVo;

public interface SearchService {
    SearchVo search(SearchDto search);
}
