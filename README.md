# SPRING PLUS

# 📘 프로젝트 기능 및 구현 위치 정리

## 필수 기능 : Level 1

### ✅ 1. 코드 개선 퀴즈 - @Transactional의 이해
- `readOnly` 설정으로 인한 오류 발생을 `readOnly = false` 설정으로 수정  
- **위치**:
  - `TodoService.java`  
- 읽기 전용 트랜잭션에서 쓰기를 수행하면 오류가 발생해야 하지만,  
  H2는 쓰기 제한을 강제하지 않기 때문에 기존 코드도 문제없이 동작했었다.

---

### ✅ 2. 코드 추가 퀴즈 - JWT의 이해
- `User` 정보에 `nickname`을 추가하고, `JwtToken`에 포함되도록 모든 관련 코드 수정  
- **주요 변경 위치**:
  - `User.java`
  - `JwtUtil.java`
  - `JwtFilter.java`

---

### ✅ 3. 코드 개선 퀴즈 - JPA의 이해
- 할 일을 `weather` 또는 `수정일` 기준으로 검색 가능하도록 기능 추가  
- **위치**:
  - `TodoService.java`
  - `TodoRepositoryImpl.java`

---

### ✅ 4. 테스트 코드 퀴즈 - 컨트롤러 테스트의 이해
- 예외 발생 테스트에서 기대하는 반환 상태를 `isOk` → `isBadRequest`로 수정  
- **위치**:
  - `TodoControllerTest.java`

---

### ✅ 5. 코드 개선 퀴즈 - AOP의 이해
- 메서드 실행 **전**에 AOP가 동작하도록 `@After` → `@Before`로 수정  
- **위치**:
  - `AdminAccessLoggingAspect.java`

---

## Level 2

### ✅ 1. JPA Cascade
- 할 일 저장 시 생성한 유저가 자동으로 담당자로 등록되도록 `CascadeType.PERSIST` 설정  
- **위치**:
  - `Todo.java`

---

### ✅ 2. N+1 문제 해결
- `getComments()` API 호출 시 발생하는 N+1 문제를 **Fetch Join**으로 해결  
- **위치**:
  - `CommentRepository.java`

---

### ✅ 3. QueryDSL 적용
- 기존 JPQL 기반 `findByIdWithUser`를 QueryDSL로 변경  
- **위치**:
  - `QuerydslConfig.java`
  - `TodoRepositoryImpl.java`

---

### ✅ 4. Spring Security 적용
- **변경 위치**:
  - `JwtFilter.java`
  - `SecurityConfig.java`
  - `AuthController.java`

---

## 🚀 도전 기능: Level 3

### ✅ 1. QueryDSL 기반 페이징 검색
- 제목, 생성일 범위, 닉네임 조건으로 일정 조회하는 API 구현  
- **위치**:
  - `TodoRepositoryImpl.java`
  - `TodoSearchResponse.java`

---

### ✅ 2. Transaction 심화
- `log` 테이블 생성 후, 매니저 등록 성공 여부와 관계없이 로그 저장  
- **위치**:
  - `Log.java`
  - `ManagerService.java`

---

### ❌ 3. AWS 활용  
- EC2, RDS, S3를 활용한 프로젝트 배포 예정  
- 각 AWS 서비스 간 보안 그룹 설정 필요  
- **진행 상태**: 구현 중

---

### ✅ 4. 대용량 데이터 처리
- MySQL 환경에서 100만 건의 유저 데이터를 생성해 닉네임 기반 조회 성능 테스트  
- **조건**:
  - 닉네임은 고유하게 생성되나, 중복 허용 전제로 테스트
  - 닉네임 "정확히 일치"해야 조회 가능 (부분 일치 X)  
- **위치**:
  - `UserRepositoryImpl.java`
  - `UserServiceTest.java`
- **📌 테스트 결과**
  
![100만개의 데이터 생성 및 조회 결과 수행 시간](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F2kCXg%2FbtsNYuZC0Fx%2FxnZckGcY5OpUSxBzViUQo0%2Fimg.png)

![조회 수행 시간](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcArGbq%2FbtsNYpYkcRO%2FXihJuSfP1FZ8CXk8Y6wYM0%2Fimg.png)

---

## Level 4 (진행 중)

### ❌ 1. Entity 및 Repository CRUD 리팩토링 (Kotlin)

### ❌ 2. Kotlin으로의 전환
   
