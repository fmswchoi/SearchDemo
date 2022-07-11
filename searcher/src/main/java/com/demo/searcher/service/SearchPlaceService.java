package com.demo.searcher.service;

import core.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchPlaceService {

    private final KaKaoPlaceSearchService kaKaoPlaceSearchService;

    private final NaverPlaceSearchService naverPlaceSearchService;


    public Set<String> getPlaceList(String keyword) {
        //TODO: kakao 혹은 naver api 에서 Exception 이 발생했을때, 처리 필요
        // 둘의 API 결과를 Cache 에 저장 하고.. 먼저 Cache 로 보여주는 형식
        // cache 주기는 1분정도? 5분정도? 로 설정하고, cache에 없으면, rest call 하는 방식,
        // 1) Exception 이 발생하면 -> cache 값을 보여준다.
        // 2) 최종 결과가 없으면, 검색된 결과가 존재하지 않습니다. 문구로 나오도록한다.

        List<String> kakaoPlaceNameList = kaKaoPlaceSearchService.getKaKaoPlaceList(keyword);
        log.debug("#### KaKao place Name List >> {}", kakaoPlaceNameList);

        List<String> naverPlaceList = naverPlaceSearchService.getNaverPlaceList(keyword);
        log.debug("#### Naver place Name List >> {}", naverPlaceList);

        Map<String, List<String>> nameListMap = new HashMap<>();
        nameListMap.put(Constants.NAVER_NAME, naverPlaceList);

        if (kakaoPlaceNameList != null && kakaoPlaceNameList.size() > 0) {

        }
        Set<String> result = matchPlaceList(kakaoPlaceNameList, nameListMap);

        return result;
    }

    private Set<String> matchPlaceList(List<String> kakaoPlaceNameList, Map<String, List<String>> nameListMap) {
        List<String> compareList = new LinkedList<>();

        //TODO: 나중에 Google 검색 결과를 하여도,, 아래 로직만 탄다면.. 괜찮게 하려고 했는데,
        // 결국 코드 추가를 많이해야할듯.. 필요없어보인다.
        for (String company : nameListMap.keySet()) {
            if (StringUtils.equals(company, Constants.NAVER_NAME)) {
                compareList = nameListMap.get(company);
            }
        }

        Set<String> result = null;
        if(kakaoPlaceNameList != null && kakaoPlaceNameList.size() > 0 ){
            result = kakaoPlaceNameList.stream().collect(Collectors.toSet());
        }

        if(compareList != null && compareList.size() >0 ){
            result = comparePlaceList(kakaoPlaceNameList, compareList);

            //TODO: 결과값이.. 10개 초과되면 안되는 것을 처리한 로직인데.. 이렇게 해야하나..? 수정필요..
            if (result.size() > 10) {
                int i = 0;
                for (String name : result) {
                    if (i > 10) {
                        result.remove(name);
                    }
                    i++;
                }
            }

        }


        log.debug("### result >> ", result);
        return result;
    }

    private Set<String> comparePlaceList(List<String> kakaoPlaceNameList, List<String> compareList) {
        //TODO: SET 으로 중복제거 하였지만 성능은 너무 안좋다. 이것도 로직 수정 필요

        Set<String> result = new LinkedHashSet<>();

        for (String kakaoName : kakaoPlaceNameList) {
            for (String compareName : compareList) {
                String kakao = StringUtils.deleteWhitespace(kakaoName);
                String naver = StringUtils.deleteWhitespace(compareName);
                if (StringUtils.equals(kakao, naver)) {
                    result.add(kakaoName);
                }
            }
        }

        for (String kakaoName : kakaoPlaceNameList) {
            result.add(kakaoName);
        }

        for (String naverName : compareList) {
            result.add(naverName);
        }


        return result;
    }


}
