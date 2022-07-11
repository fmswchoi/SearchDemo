# SearchDemo

## 개발환경
- JAVA11, Spring Boot, JPA, H2 사용

### 버전 정보
1. JAVA 11
2. Spring Boot 2.6.9 

### 설정파일(application.properties) KAKAO API 연동 정보 
1. searcher.search.api.kakao-place-url=/v2/local/search/keyword.json
2. searcher.search.api.kakao-rest-api-key=040ceb0b99b99ff7229dbe78edeade79

### 설정파일(application.properties) NAVER API 연동 정보
1. searcher.search.api.naver-place-url=/v1/search/local.json
2. searcher.search.api.naver-client-id=sBHFCiFaMCWLwH24ELnd
3. searcher.search.api.naver-client-secret=q6w4MkCkoO


## 설명

### 장소 검색 API
1. API-URL : /searcher/search/place 
2. requset param : (String) keyword
3. response : List(String) <br>

 ※ 예제 <br>
 - 요청 : http://localhost:8080/searcher/search/place?&keyword=고기집 <br>
 - 응답 : ["금돼지식당", "숙성도 노형점", "통나무닭갈비 본점", "가보정 1관", "몽탄", "강릉짬뽕순두부 동화가든 본점", "향유갈비", "신촌황소곱창 인계직영점", 100년영동생갈비", 숙성도 노형본관"]

### 중복 처리 기준
KAKAO API 와 NAVER API 의 결과값 중 동일한 장소라고 판단하는 기준은 장소명에서 HTML 태그를 제거하고, <br>
모든 공백을 제거한 문자열이 동일한 경우 동일하다고 판단한다. <br>
※ NAVER 장소 검색 API 에서는 장소명에 HTML 태그가 존재<br>


### 검색 키워드 목록 API
1. API-URL : /search/place/count
2. requset param : 
3. response :
- seq (int) : (auto increment) 시퀀스값 (PK값)
- keyword (String) : 검색 키워드(PK 값)
- count (int) : 검색 횟수
4. H2 DB 사용 

※ 예제
- 요청 : http://localhost:8080/searcher/search/place/count
- 응답 : [ {"seq" : 1, "keyword":고기집", "count":1} ] 


