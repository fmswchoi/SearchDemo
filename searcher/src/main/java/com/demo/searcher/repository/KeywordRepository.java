package com.demo.searcher.repository;

import com.demo.searcher.domain.KeywordCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<KeywordCount, Integer> {

    KeywordCount findByKeyword(String keyword);

    List<KeywordCount> findTop10ByOrderByCountDesc();
}
