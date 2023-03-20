package com.juno.search.repository;

import com.juno.search.domain.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchRepository extends JpaRepository<Search, Long> {
    Optional<Search> findByKeyword(String keyword);
    List<Search> findTop10ByOrderByCountDesc();
}
