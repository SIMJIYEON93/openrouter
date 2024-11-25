               # Open Router

이 프로젝트는 OpenRouter을 사용해서 다양한 aiapi를 호출 할수 있도록 만든 프로젝트 입니다.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot)
![Gradle](https://img.shields.io/badge/Gradle-7.x-02303A?style=for-the-badge&logo=gradle)



## 주요 기능

- OpenRouter 사용하여 사용자가 지정한 aiapi 호출하여 mono방식과 flux방식의 응답속도 차이 테스트

## 디렉토리 구조

```
─com
    └─example
        └─aiapi
            ├─aiapi
            └─config
```


### 디렉토리 설명

- `config`: OpenAi서버의 CilentConfig와 SecurityConfig 연결 설정
- `aiapi`: prompt와 max_token으로 mono방식과 flux방식의 응답속도 차이 테스트 API




### 빌드 및 실행 환경
- JDK 17 이상
- Gradle 7.x 이상