---
name: spring-boot-dao-test
description: Spring Boot 프로젝트의 MyBatis DAO와 mapper XML에 대한 데이터베이스 통합 테스트를 작성·보완하고 실패 원인을 진단한다.
---

# Spring Boot MyBatis DAO Test

MyBatis DAO의 공개 계약과 실제 SQL 매핑을 검증하는 재현 가능하고 독립적인 테스트를 만든다. 프로젝트의 기존 테스트 프로필, 데이터베이스, 명명 규칙을 우선한다.

## 필수 준수

이 문서의 모든 규칙은 선택 사항이 아니며 반드시 준수한다. 조건이 명시된 규칙은 해당 조건이 충족되면 예외 없이 적용한다.

## 조사

테스트를 작성하기 전에 대상 DAO 메서드, 호출하는 statement ID, 활성 프로필의 mapper XML, `parameterType`, `resultType` 또는 `resultMap`, 객체-컬럼 매핑, 스키마 제약조건과 seed 데이터를 확인한다.

## 테스트 범위

- Spring context와 테스트 DB를 사용하는 통합 테스트로 실제 SQL, parameter binding, result mapping을 검증한다.
- 프로젝트 설정 전체가 필요하면 `@SpringBootTest`를 따른다.
- DAO나 mapper를 mock으로 대체하지 않는다. mock 테스트는 statement와 SQL을 검증하지 못한다.
- 요청된 DAO 메서드나 회귀에 집중하고 연관 CRUD 전체를 임의로 추가하지 않는다.

## 시나리오 설계

각 테스트는 하나의 관찰 가능한 SQL 동작을 검증한다. 정상 경로와 함께 결과 없음, 동적 검색 조건, 정렬·페이징, 논리 삭제, nullable 필드, 중복 키, `<if>`, `<choose>`, `<foreach>`처럼 쿼리 의미에 필요한 경계 조건만 선택한다.

given-when-then 흐름을 유지한다.

- given: 스키마를 만족하는 최소 데이터를 만들고 충돌하지 않는 키를 사용한다.
- when: 대상 DAO의 공개 메서드를 호출한다.
- then: 대상 DAO 메서드의 반환 결과를 검증한다.

삽입·수정·삭제 결과는 `assertThat(result).isGreaterThan(0);`로 검증한다. `selectOne`의 결과 없음 계약이 `null`인지 확인한다. `selectList`는 빈 목록을 기대하되 SQL에 `ORDER BY`가 없으면 순서를 단언하지 않는다. 목록 테스트는 자신이 준비한 데이터만 검증하며 seed 데이터의 전체 개수에 의존하지 않는다.

## 데이터 격리

- 기존 관례와 호환되면 `@Transactional`로 테스트 데이터를 롤백한다.
- 커밋, 별도 트랜잭션, 동시성을 검증할 때는 명시적이고 안전한 정리를 사용한다.
- 테스트 데이터 식별자는 다음 형식으로 생성하되, 대상 데이터 컬럼 크기에 맞춰 접두어, 날짜 형식 또는 접미어 길이를 조정한다.

```java
// char(30), varchar(30) 30자리
LocalDateTime now = LocalDateTime.now();
String now2 = "TEST_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS")) + "00";
```
- 실행 순서, 운영 데이터, 개발자 로컬 DB 상태에 의존하지 않는다.

## 구현과 검증

Java 테스트 클래스는 다음 순서로 작성하고 검증한다.

1. 테스트 파일명과 클래스명은 대상 DAO의 단순 클래스명 뒤에 `Test`를 붙인다.
2. `package` 선언은 대상 DAO의 패키지와 동일하게 작성한다.
3. 필요한 클래스, 애너테이션, AssertJ assertion을 import하고 잘못되거나 사용하지 않는 import는 제거한다.
4. 클래스에 `@SpringBootTest`, `@Transactional`을 선언하고 테스트 클래스를 작성한다.
5. 대상 DAO를 `@Autowired`로 필드 주입한다. 아래 예시의 타입과 필드명은 실제 대상 DAO에 맞춘다.
6. JUnit 5와 프로젝트의 기존 assertion 라이브러리를 사용하여 테스트 메서드를 작성한다. 테스트 메서드 이름은 반드시 대상 DAO 메서드 이름과 정확히 동일하게 작성하며 접두어나 접미어를 붙이지 않는다. `@DisplayName` 애너테이션은 추가하지 않는다.
7. 대상 mapper XML이 요청하는 파라미터를 확인하고, 테스트 입력 객체의 해당 파라미터를 모두 setter로 설정한다.
8. 조회 테스트에서는 대상 mapper XML의 `SELECT` 컬럼과 `resultType` 또는 `resultMap`을 확인하고, 요청 파라미터와 반환된 응답 파라미터를 AssertJ로 각각 검증한다.
9. 완료 전에 테스트 파일 경로, `package`, 파일명, 클래스명, `@SpringBootTest`, `@Transactional`이 대상 DAO 기준과 일치하는지 확인한다.
10. 테스트 클래스를 컴파일하여 import와 구문 오류가 없는지 확인한다.
