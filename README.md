# 📗 Search API

---

## 📄 skill

`language` java11

`framework` spring 2.7.10

`tool` gradle

`docs` restdocs

`port` 8080

서버 실행 후 클릭해 주세요 ([문서 보기](http://localhost:8080/docs.html))

--- 

## 📄 jar 다운로드 링크

### ✅ main 브랜치 링크
[다운로드 링크](https://github.com/juno-choi/search-api/raw/main/jar/search-0.1.jar)

`파일 위치` https://github.com/juno-choi/search-api/tree/main/jar

### ✅ redis 브랜치 링크

```
해당 브랜치는 Embedded Redis가 적용되어 있는 소스로 
OS 환경에 따라 실행이 되지 않을 수 있어 따로 분리해두었습니다.
```

[다운로드 링크](https://github.com/juno-choi/search-api/raw/main/jar/redis/search-0.1.jar)

`파일 위치` https://github.com/juno-choi/search-api/tree/main/jar/redis

---

## 📄 추가로 구현한 기능

### ✅ docs 추가

restdocs를 사용하여 [api 문서](http://localhost:8080/docs.html) 를 확인할 수 있도록 하였습니다. 

### ✅ naver 검색 기능 추가

`/v1/search/naver (get)`으로 검색했을 때 naver api를 통해 검색할 수 있도록 기능을 추가하였습니다.

### ✅ redis 기능 추가 (feature/redis 브랜치에서만)

`/v1/search`로 검색했을 때 검색한 내용을 redis에 5분간 저장하여 cache처럼 사용할 수 있도록 구현하였습니다.

해당 기능은 사용자가 많을 경우 검색마다 api를 호출하여 많은 리소스를 사용하는 부분을 줄이기 위해 redis를 추가하여 해결하기 위해 노력했습니다.

해당 소스는 [feature/redis 브랜치](https://github.com/juno-choi/search-api/tree/feature/redis) 에서 확인해볼 수 있습니다 :)

---

## 📄 사용한 오픈 라이브러리

1. MockWebServer
   - 단위 테스트를 위해 사용

2. EmbeddedRedis
   - Redis 단위 테스트를 위해 사용
     (Mac M1~2 환경을 고려하여 feature/redis 브랜치에 따로 분리하였습니다.)

