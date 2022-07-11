package com.demo.searcher.service;

import com.demo.searcher.domain.KeywordCount;
import com.demo.searcher.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchKeywordCountingService {

    private final KeywordRepository keywordRepository;

    public void addKeywordCount(String keyword){
        KeywordCount keywordCount = keywordRepository.findByKeyword(keyword);

        if(keywordCount != null){
            int count = keywordCount.getCount();
            keywordCount.setCount(count + 1);
        } else {
            keywordCount = new KeywordCount();
            keywordCount.setKeyword(keyword);
            keywordCount.setCount(1);
        }

        keywordRepository.save(keywordCount);
    }

    public List<KeywordCount> getKeywordCountList(){
        return keywordRepository.findTop10ByOrderByCountDesc();
    }

}
