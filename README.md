# Number.One
- 팀 협업 문서 링크 게시

## 팀원 구성
| 이름 | 역할 | 담당 업무 및 주요 구현 기능 | Github |
| :--- | :--- | :--- | :--- |
| **신홍규** | 팀장 | | zrp0x0@gmail.com |
| **강성민** | 팀원 | | |
| **노정빈** | 팀원 | | |
| **성결** | 팀원 | | |
| **엄주혁** | 팀원 | | |

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



---

## 4. 핵심 기능

### 지표 및 데이터 관리 (Index & Data Management)
- **지표 정보 관리 (Index Info)**: 금융 지표의 기본 정보(티커, 시장, 분류 등)를 CRUD하고 커서 기반 페이지네이션으로 조회합니다.
- **히스토리컬 데이터 (Index Data)**: 일자별 지표 수치를 관리하며, 기간별 수익률 분석 데이터를 제공합니다.
- **차트 및 랭킹**: 프론트엔드 시각화를 위한 차트 데이터 포인트 생성 및 지표별 성과 랭킹 기능을 제공합니다.

### 데이터 동기화 시스템 (Sync System)
- **Open API 연동**: 외부 금융 데이터 API와 연동하여 지표 정보 및 일별 데이터를 동기화합니다.
- **동기화 작업 관리 (Sync Job)**: 수동/자동 동기화 작업의 이력을 추적합니다.
- **자동 동기화 설정 (Auto Sync)**: 지표별로 동기화 주기 및 활성화 여부를 설정하여 스케줄러에 의해 자동으로 데이터를 업데이트합니다.

### 🛠 시스템 공통 기능
- **Global Exception Handling**: 표준화된 에러 응답 객체(ErrorResponse)를 통한 예외 처리.
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

---

## 프로젝트 회고록
- 
