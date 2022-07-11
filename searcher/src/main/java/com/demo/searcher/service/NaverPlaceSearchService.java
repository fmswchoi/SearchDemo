package com.demo.searcher.service;

import com.demo.searcher.utils.RemoveHtmlTags;
import com.demo.searcher.vo.naver.NaverItemListResonseVo;
import com.demo.searcher.vo.naver.NaverResponseVo;
import core.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverPlaceSearchService {

    @Value("${searcher.search.api.naver-client-id:''}")
    private String NAVER_CLIENT_ID;
    @Value("${searcher.search.api.naver-client-secret:''}")
    private String NAVER_CLIENT_SECRET;
    @Value("${searcher.search.api.naver-place-url:''}")
    private String NAVER_PLACE_API_URL;

    private final RestTemplate restTemplate;

    /**
     * > GET /v1/search/local.xml?query=%EC%A3%BC%EC%8B%9D&display=10&start=1&sort=random HTTP/1.1
     * > Host: openapi.naver.com
     * > User-Agent: curl/7.49.1
     * > Accept:
     * > X-Naver-Client-Id: {애플리케이션 등록 시 발급받은 client id 값}
     * > X-Naver-Client-Secret: {애플리케이션 등록 시 발급받은 client secret 값}
     */
    public List<String> getNaverPlaceList(String keyword) {

        StringBuffer stringBuffer = new StringBuffer();
        String url = stringBuffer.append(Constants.NAVER_HOST_URL).append(NAVER_PLACE_API_URL).toString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        httpHeaders.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", keyword)
                .queryParam("display", Constants.DEFAULT_RESULT_SIZE)
                .build();

        HttpEntity<?> httpEntity = new HttpEntity(httpHeaders);

        List<String> placeNameList = null;

        try {
            ResponseEntity<NaverResponseVo> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, NaverResponseVo.class);

            NaverResponseVo naverResponseVo = response.getBody();

            List<NaverItemListResonseVo> placeList = naverResponseVo.getItems();

            if (placeList != null && placeList.size() > 0) {
                placeNameList = placeList.stream()
                        .map(place -> RemoveHtmlTags.removeHtmlTag(place.getTitle()))
                        .collect(Collectors.toCollection(LinkedList::new));
            }
        } catch (Exception e) {
            log.error("### NAVER API ERROR >> {} ", e);
        }

        return placeNameList;
    }
}
