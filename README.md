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
4. H2 DB 사용 : application 재실행시 데이터 삭제

※ 예제
- 요청 : http://localhost:8080/searcher/search/place/count
- 응답 : [ {"seq" : 1, "keyword":고기집", "count":1} ] 

## 개선/고민 사항
1. 현재 각 API 종류별로 application.properties 에 필요한 정보를 넣고 service 로직을 개발해야함
-> API 의 restTemplate 호출 부분을 공통화하고, return 값을 Map 으로 하여, 각 API response 객체들과 맵핑을 한다.
-> 사실 API 별 response 구조가 다르기 때문에, 공통으로 사용하는 것이 맞을까? (새로운 API 추가시 변경영역 최소화를 위한..)

2. 중복 처리 개선이 필요함.
-> kakao 검색 기준으로 동일한 정보들을 먼저 찾아 set에 넣고, 그 뒤에 kakao 검색결과, naver검색결과를 set에 넣고있음
-> 10개 초과의 결과가 나오지 않도록, 다넣은 set에서 10개 초과의 값들은 제거하도록 함.
-> 성능적으로 굉장히 비효율적이기에, 다른 로직으로 개선필요.

3. 대용량 트래픽을 위한 개선사항
-> 검색을 하였을 때, 먼저 해당 결과값을 cache 에 저장을 한다.
-> 나중에 kakao 검색 API 혹은 다른 API들에서 오류가 발생하면, 그래도 결과는 보여줘야하니, cache에 저장된 결과를 보여준다.
-> cache 의 주기는 1~2분으로 설정한다.
-> 이러한 API 호출 및 cache에 저장하는 로직은 비동기로 처리하도록 한다. ( Message Queue 적용 ?)
