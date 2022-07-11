package com.demo.searcher.service;

import com.demo.searcher.utils.RemoveHtmlTags;
import com.demo.searcher.vo.kakao.KaKaoDocumentsResponseVo;
import com.demo.searcher.vo.kakao.KaKaoResponseVo;
import core.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
public class KaKaoPlaceSearchService {

    @Value("${searcher.search.api.kakao-rest-api-key:''}")
    private String KAKAO_REST_API_KEY;

    @Value("${searcher.search.api.kakao-place-url:''}")
    private String KAKAO_PLACE_API_URL;

    private final RestTemplate restTemplate;

    /**
     * GET /v2/local/search/keyword.${FORMAT} HTTP/1.1
     * Host: dapi.kakao.com
     * Authorization: KakaoAK ${REST_API_KEY}
     */
    public List<String> getKaKaoPlaceList(String keyword) {

        StringBuffer stringBuffer = new StringBuffer();
        String url = stringBuffer.append(Constants.KAKAO_HOST_URL).append(KAKAO_PLACE_API_URL).toString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, Constants.KAKAO_TOKEN_NAME + " " + KAKAO_REST_API_KEY);
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", keyword)
                .queryParam("page", Constants.DEFAULT_RESULT_PAGE)
                .queryParam("size", Constants.DEFAULT_RESULT_SIZE)
                .build();

        HttpEntity<?> httpEntity = new HttpEntity(httpHeaders);

        List<String> placeNameList = null;
        try {
            ResponseEntity<KaKaoResponseVo> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, KaKaoResponseVo.class);

            KaKaoResponseVo kaKaoResponseVo = response.getBody();

            List<KaKaoDocumentsResponseVo> placeList = kaKaoResponseVo.getDocuments();

            if (placeList != null && placeList.size() > 0) {
                placeNameList = placeList.stream()
                        .map(place -> RemoveHtmlTags.removeHtmlTag(place.getPlaceName()))
                        .collect(Collectors.toCollection(LinkedList::new));
            }

        } catch (Exception e) {
            log.error("### KAKAO API ERROR >> {}", e);
        }
        
        return placeNameList;

    }
}
