# Number.One
- 팀 협업 문서 링크 게시

## 팀원 구성
| 이름 | Github |
| :--- | :--- |
| **신홍규** | zrp0x0@gmail.com |
| **강성민** | |
| **노정빈** | |
| **성결** | |
| **엄주혁** | |
| **정수용** | |

---

## 프로젝트 소개
금융 지표 데이터를 체계적으로 관리하고 외부 API와 실시간으로 동기화하는 Spring Boot 기반 백엔드 시스템입니다.

- **Findex**는 국내외 주요 금융 지표(KOSPI, S&P 500 등)의 메타데이터와 히스토리컬 데이터를 관리합니다.
- 공공데이터포털 등의 Open API와 연동하여 자동으로 데이터를 수집(Sync)하고, 이를 분석하여 기간별 수익률 및 차트 데이터를 제공합니다.
- **프로젝트 기간**: 2026.04.13 - 2026.04.24

---

## 기술 스택
- **Framework**: Spring Boot 3.3.3
- **Language**: Java 17
- **Database**: PostgreSQL (Railway), MySQL (Local)
- **ORM/Query**: Spring Data JPA, QueryDSL
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Deployment**: Railway.app
- **Other**: Open API Integration, Spring Scheduling

---

## 3. 팀원 구성 및 역할

**신홍규**

- 프로젝트 초기 환경 및 아키텍처 설정
  - H2 인메모리 데이터베이스를 활용해 로컬/테스트 환경을 세팅
  - MapStruct를 도입하여 Entity와 DTO 간의 객체 매핑 계층을 명확히 분리하고 보일러플레이트 코드 최소화
  - Global Exception Handler를 구성하여 비즈니스 예외(400, 404)와 서버 에러(500)의 응답 규격을 일관되게 정의
- 사용자 수동 지수 정보 등록 API
  - POST /api/index-infos 엔드포인트 구현
  - 사용자가 수동으로 관리하는 커스텀 지수를 등록하는 기능 구현
  - 데이터 무결성을 위해 지수 분류명(indexClassification)과 지수명(indexName) 조합의 복합 유니크 검증 로직 적용하여 중복 데이터 적재 원천 차단
  - 수동 등록 지수 특성에 맞춰 SourceType을 USER로 고정 처리하여 API 명세서와의 동기화 및 클라이언트 입력 책임 분리
- 지수 정보 단건 조회 API
  - GET /api/index-infos/{id} 엔드포인트 구현
  - 특정 지수 ID를 기반으로 지수의 기본 정보 및 메타데이터를 응답하는 상세 조회 기능 구현
  - 존재하지 않는 지수 ID 요청 시 서버 에러(500)가 발생하지 않도록, Optional 체이닝을 활용해 EntityNotFoundException을 명시적으로 던져 404 Not Found로 안전하게 처리
  - MapStruct 매퍼를 통해 영속성 컨텍스트에 묶인 Entity가 Controller 계층으로 노출되지 않도록 데이터 반환 경계 캡슐화
- 지수 정보 목록 페이징 조회 API
  - GET /api/index-infos 엔드포인트 구현
  - 지수 분류, 이름 등 다양한 검색 조건에 유연하게 대응하기 위해 동적 쿼리 기반의 목록 조회 기능 구현
  - 지수 목록 조회 시 발생할 수 있는 Offset 페이징의 성능 병목을 예방하고자 커서 기반 페이징 아키텍처 적용
  - 응답 DTO에 hasNext(다음 페이지 여부) 및 nextCursor(다음 조회 기준점) 값을 포함시켜, 프론트엔드 환경에서 무한 스크롤 지원

**강성민**

- 지수 정보 수정 API
 - PATCH 메서드를 활용하여 특정 지수의 데이터(채용 종목 수, 기준 시점, 기준 지수, 즐겨찾기 여부)를 업데이트하는 엔드포인트 구현
- 지수 정보 삭제 API
 - DELETE 메서드를 활용한 지수 정보 안전 삭제 로직 구현
 - JPA 엔티티 연관관계 옵션(Cascade)을 활용하여 부모 지수 정보가 삭제될 때 연관된 하위 지수 데이터들도 고아 객체 없이 함께 제거되도록 설정해 DB 데이터 무결성 보장
- 지수 요약 목록 조회 API
 - 이름 등 특정 조건으로 지수를 검색할 때, 불필요한 전체 데이터 대신 핵심 정보만 담긴 요약 목록을 GET 요청으로 반환하여 네트워크 부하를 줄이고 조회 성능을 최적화한 엔드포인트 구현
- 지수 데이터 CSV Export API
 - 서버에 저장된 지수 데이터를 추출하여 클라이언트가 CSV 파일로 다운로드할 수 있는 기능 구현
 - 다운로드할 데이터가 없는 경우 Custom Exception을 발생시키고, @ExceptionHandler를 통해 클라이언트에게 규격화된 에러 응답(JSON)을 안전하게 전달하도록 전역 예외 처리 로직 강화

**성결**

- 자동 연동 설정 관리 API
 - 자동 연동 설정 목록을 GET 요청을 사용하여 조회하는 API 구현
 - 지수별 자동 연동 활성화 여부를 관리하는 API 엔드포인트
- 자동 연동 설정 수정 API
 - PATCH 요청을 사용하여 자동 연동 설정의 활성화 여부 수정 처리
 - 연동 작업 관리 API
 - 연동 작업 목록을 GET 요청을 사용하여 조회하는 API 구현
 - 연동 작업 이력의 필터링, 정렬, 커서 기반 페이지네이션을 관리하는 API 엔드포인트
- 자동 연동 스케줄러 및 배치
 - Spring Scheduler를 사용하여 지수 데이터 자동 연동 배치 실행 기능 구현
 - 활성화된 자동 연동 설정을 기준으로 주기적 연동 작업을 관리하는 자동화 엔드포인트
- 배치 이력 저장 로직- 자동 실행 결과에 따라 연동 작업 성공 및 실패 이력 저장 처리

**엄주혁**

- 지수 데이터 수정 API
- PATCH HTTP 메서드를 활용하여 특정 지수 데이터의 기준일, 종가, 대비, 등락률, 거래량 등 주요 정보를 수정할 수 있는 엔드포인트 구현
- 요청 데이터 검증을 통해 잘못된 입력값을 방지하고, 존재하지 않는 지수 데이터 수정 요청 시 Custom Exception을 발생시켜 안정적인 예외 처리 흐름을 구성
- 지수 차트 조회 API
 - 특정 지수의 기간별 데이터를 GET 요청으로 조회하여 차트 화면에서 활용할 수 있는 시계열 데이터 반환 API 구현
 - 기준일을 기준으로 정렬된 지수 데이터를 제공하여 클라이언트가 일별 추이, 변동 흐름, 등락률 등을 시각화할 수 있도록 조회 로직 구성
- 지수 성과 랭킹 조회 API
 - GET /api/index-data/performance/rank 엔드포인트를 통해 여러 지수의 성과를 비교하고 순위 형태로 조회할 수 있는 API 구현
 - 기간별 수익률 및 변동률 데이터를 기반으로 지수 성과를 계산하고, 랭킹 형태의 응답 DTO로 가공하여 클라이언트가 성과 비교 화면에서 효율적으로 활용할 수 있도록 처리


**정수용**

- 관심지수 성과조회 API
 - GET /api/index-data/performance/favorite 엔드포인트 구현
  - DAILY / WEEKLY / MONTHLY 기간 기준으로 비교 시점을 계산해 성과를 조회하도록 구성
  - 즐겨찾기 지수만 대상으로 최신 종가와 비교 시점 종가를 사용해 대비값(versus), 등락률(fluctuationRate) 산출
  - 비교 데이터가 없는 경우에도 응답 구조를 유지해 화면이 끊기지 않도록 처리
- 지수 정보 연동 API
 - POST /api/sync-jobs/index-infos 엔드포인트 구현
  - Open API 연동 시 최신 유효 데이터를 찾기 위해 최근 날짜 기준 역탐색(최대 30일) 로직 적용
  - 지수 분류명 + 지수명 기준으로 upsert(있으면 갱신, 없으면 생성) 처리하여 중복 적재 방지
  - 연동 결과를 성공/실패 단위로 SyncJob 이력에 기록해 추적 가능성 강화
  - 신규 지수 생성 시 자동 연동 설정(AutoSyncConfig)도 함께 생성되도록 처리
- 지수 데이터 연동 API
 - POST /api/sync-jobs/index-data 엔드포인트 구현
  - 연동 대상 지수 목록과 기간(baseDateFrom ~ baseDateTo)을 받아 조건 기반 동기화 수행
  - 입력 날짜 유효성(필수값, 시작일/종료일 순서) 및 대상 지수 존재 여부 검증
  - 동일 지수·동일 날짜 데이터는 upsert 방식으로 반영해 중복 저장 없이 최신 상태 유지
  - 연동 건별 성공/실패를 SyncJob에 기록해 실행 결과를 운영 관점에서 확인 가능하도록 구성

---

## 4. 핵심 기능

### 지표 및 데이터 관리 (Index & Data Management)
- **지표 정보 관리 (Index Info)**: 금융 지표의 기본 정보를 CRUD하고 커서 기반 페이지네이션으로 조회합니다.
- **히스토리컬 데이터 (Index Data)**: 일자별 지표 수치를 관리하며, 기간별 수익률 분석 데이터를 제공합니다.
- **차트 및 랭킹**: 프론트엔드 시각화를 위한 차트 데이터 포인트 생성 및 지표별 성과 랭킹 기능을 제공합니다.

### 데이터 동기화 시스템 (Sync System)
- **Open API 연동**: 외부 금융 데이터 API와 연동하여 지표 정보 및 일별 데이터를 동기화합니다.
- **동기화 작업 관리 (Sync Job)**: 수동/자동 동기화 작업의 이력을 추적합니다.
- **자동 동기화 설정 (Auto Sync)**: 지표별로 동기화 주기 및 활성화 여부를 설정하여 스케줄러에 의해 자동으로 데이터를 업데이트합니다.

### 🛠 시스템 공통 기능
- **Global Exception Handling**: 표준화된 에러 응답 객체를 통한 예외 처리.
- **CSV 데이터 내보내기**: 특정 지표의 데이터를 CSV 형식으로 다운로드할 수 있는 API를 제공합니다.
- **Swagger UI**: 모든 API 엔드포인트를 문서화하여 테스트 환경을 제공합니다.

---

## 5. 파일 구조
```text
src/main/java/com/codeit/findex
 ┣ 📂 domain
 ┃ ┣ 📂 indexinfo       # 지표 메타데이터 관리
 ┃ ┣ 📂 indexdata       # 지표별 상세 수치 및 분석
 ┃ ┣ 📂 syncjob         # API 동기화 작업 로그 및 클라이언트
 ┃ ┣ 📂 autosyncconfig  # 스케줄링 및 자동화 설정
 ┃ ┗ 📂 common          # 공통 Enum 및 상수
 ┣ 📂 global
 ┃ ┣ 📂 config          # Querydsl, OpenAPI, Scheduling 설정
 ┃ ┗ 📂 error           # 예외 처리 및 에러 응답 정의
 ┗ 📜 P0FindexApplication.java
```

---

## 구현 홈페이지
- https://sb11-findex-team1-production.up.railway.app/#/dashboard
<img width="876" height="840" alt="image" src="https://github.com/user-attachments/assets/8c3038f4-0538-417b-a7e3-a611d0fa566a" />
<img width="877" height="853" alt="image" src="https://github.com/user-attachments/assets/8b8e53b5-aa21-48d5-9479-579cf8b5afbe" />
<img width="877" height="852" alt="image" src="https://github.com/user-attachments/assets/1008befb-b5bf-47e1-9060-50eb125ce737" />
<img width="880" height="845" alt="image" src="https://github.com/user-attachments/assets/a4ce889c-ebb1-4d38-8494-3f1fffe0a5e9" />

---

## 프로젝트 회고록
- 
