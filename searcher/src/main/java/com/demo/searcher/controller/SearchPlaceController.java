package com.demo.searcher.controller;

import com.demo.searcher.domain.KeywordCount;
import com.demo.searcher.service.SearchKeywordCountingService;
import com.demo.searcher.service.SearchPlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/searcher")
@RequiredArgsConstructor
public class SearchPlaceController {

    private final SearchPlaceService searchPlaceService;

    private final SearchKeywordCountingService searchKeywordCountingService;

    @GetMapping(value = "/search/place")
    public Set<String> getPlaceList(@RequestParam String keyword) {
        log.debug("## Search keyword >> {} ", keyword);

        try {
            searchKeywordCountingService.addKeywordCount(keyword);
        } catch (Exception e) {
            log.error("### FAIL TO ADD KEYWORD COUNT >> {}", e);
        }

        Set<String> placeNameList = searchPlaceService.getPlaceList(keyword);

        return placeNameList;
    }


    @GetMapping(value = "/search/place/count")
    public List<KeywordCount> getKeywordCount() {
        return searchKeywordCountingService.getKeywordCountList();
    }

}
