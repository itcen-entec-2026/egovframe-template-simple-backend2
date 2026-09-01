---
name: spring-boot-dao-test
description: Spring Boot와 MyBatis 환경에서 DAO 또는 Mapper의 실제 Bean, Mapper XML, 데이터베이스를 검증하는 JUnit 5 통합 테스트를 @SpringBootTest 기반으로 생성한다. 테스트 슬라이스, mock 단위 테스트, 서비스·컨트롤러 테스트에는 사용하지 않는다.
---

# Spring Boot MyBatis DAO/Mapper Integration Test

Spring Boot + MyBatis 프로젝트의 DAO/Mapper 통합 테스트 코드를 자동 생성한다. 생성 결과는 프로젝트의 실제 타입과 메서드만 사용하는 컴파일 가능한 Java 코드여야 한다.

## 사용법

슬래시 명령 뒤에 테스트할 DAO 또는 Mapper의 실제 클래스·인터페이스명을 한 개 지정한다.

```text
/spring-boot-dao-test <DAO또는Mapper명>
```

사용 예:

```text
/spring-boot-dao-test BBSAttributeManageDAO
```

이 명령을 받으면 `BBSAttributeManageDAO` 소스, 호출하는 MyBatis statement, 대응 Mapper XML과 VO/DTO를 먼저 찾은 뒤 이 문서의 필수 순서대로 통합 테스트를 생성한다. 동일한 단순 이름이 여러 개면 package를 확인해 대상을 확정하며, 대상 소스나 Mapper XML을 찾지 못하면 임의 구현하지 않고 누락된 경로를 보고한다.

## 최우선 필수 규칙

다음 규칙은 기존 테스트 관례나 일반적인 선택지보다 우선한다.

- 테스트 프레임워크는 JUnit 5로 고정하고 `org.junit.jupiter.api.Test`를 사용한다.
- 모든 검증은 AssertJ의 `assertThat`을 사용한다. JUnit assertion을 혼용하지 않는다.
- Spring 테스트 컨텍스트를 로딩하는 어노테이션은 반드시 `@SpringBootTest`만 사용한다.
- 테스트 클래스에 `@Transactional`을 반드시 적용하여 각 테스트 종료 후 DB 변경을 롤백한다.
- 테스트 클래스에 Lombok `@Slf4j`를 반드시 적용한다.
- `@MybatisTest`, `@JdbcTest`, `@DataJpaTest`, `@ContextConfiguration`과 다른 테스트 슬라이스·컨텍스트 구성 어노테이션을 사용하지 않는다.
- 프로젝트 전용 테스트 부모 클래스나 공통 통합 테스트 클래스를 상속하지 않는다.
- DAO/Mapper는 실제 Spring Bean을 `@Autowired`로 주입한다. mock, spy, stub, fake 또는 직접 `new`로 만든 DAO/Mapper를 사용하지 않는다.
- 실제 MyBatis Mapper XML과 프로젝트가 구성한 실제 DB 또는 테스트 DB를 사용한다.
- 테스트 메서드는 `// Given`, `// When`, `// Then` 구조로 작성한다.
- AssertJ에 `.as()`, `.withMessage()` 또는 사용자 정의 실패 메시지를 자동 추가하지 않는다.
- 존재하지 않는 메서드, 필드, 생성자, Bean, fixture, 테스트 유틸리티를 추측해 만들지 않는다.
- 기존 소스의 package, 타입, 메서드명, Bean 구조와 테스트 경로를 우선한다.
- 분석으로 확인되지 않은 API가 필요하면 임의 생성하지 말고 부족한 정보를 보고한다.

테스트 클래스의 필수 어노테이션 조합은 인자 없는 `@SpringBootTest`, `@Transactional`, `@Slf4j`이다. 이 중 Spring 컨텍스트를 구성하는 어노테이션은 `@SpringBootTest` 하나뿐이다. `@ActiveProfiles`, `@Sql`, `@TestPropertySource`, `@DirtiesContext` 등 다른 컨텍스트·테스트 구성 어노테이션은 자동 추가하지 않는다. `@Transactional` 롤백을 기본 데이터 정리 수단으로 사용하고 각 테스트가 필요한 fixture는 실제 DAO/Mapper 메서드로 준비한다.

## 실제 사람이 작성하는 필수 순서

아래 순서를 바꾸거나 건너뛰지 않는다. 테스트 코드를 먼저 만든 뒤 실제 식별자를 끼워 맞추지 않는다.

### 1. 대상 DAO/Mapper를 연다

- package, 클래스·인터페이스명, Spring Bean 등록 어노테이션과 상위 타입을 확인한다.
- 공개 메서드를 위에서 아래로 읽고 테스트 대상 메서드명, 파라미터 타입, 반환 타입과 선언 예외를 기록한다.
- DAO 구현이면 각 메서드가 호출하는 MyBatis statement ID를 확인한다.

### 2. 대응하는 Mapper XML을 찾는다

- namespace와 statement id가 Java 코드와 정확히 연결되는지 확인한다.
- `insert`, 단건 `select`, 목록 `selectList`, `update`, `delete`로 메서드 유형을 분류한다.
- SQL의 `parameterType`, `resultType`, `resultMap`, 동적 조건, 조인, 정렬과 페이징을 읽는다.
- Java 메서드와 statement를 연결할 수 없으면 추측하지 않고 불일치를 보고한다.

### 3. 실제 VO/DTO와 DB 제약을 확인한다

- 입력·결과 타입의 실제 생성자, setter/getter, 필드 타입을 확인한다.
- SQL에서 사용하는 PK, 필수 컬럼, 검색 조건과 결과 컬럼을 VO/DTO 필드에 대응시킨다.
- DDL이나 기존 SQL에서 PK, 유니크, NOT NULL, 문자열 길이, FK와 코드 값 제약을 확인한다.
- 존재하지 않는 필드, builder, 생성자나 테스트 유틸리티를 만들지 않는다.

### 4. 테스트 실행 환경과 기존 관례를 확인한다

- `src/test`에서 가까운 DAO/Mapper `@SpringBootTest` 테스트를 찾는다.
- 테스트 DB 설정, 필요한 프로필, 초기 데이터와 Maven/Gradle wrapper를 확인한다.
- 기존 package, Bean 주입 방식과 명명 규칙은 따르되 이 스킬의 필수 규칙과 충돌하면 이 스킬을 우선한다.

### 5. 메서드별 테스트 시나리오를 먼저 정한다

각 실제 메서드에 대해 다음 네 가지를 정한 뒤 코드를 쓴다.

1. Given에 필요한 독립적인 테스트 데이터
2. When에서 호출할 정확한 메서드와 인자
3. Then에서 사용할 최소 AssertJ 검증
4. FK·조인 충족을 위한 부모 또는 연관 데이터

대상에 존재하지 않는 CRUD 유형은 계획하거나 생성하지 않는다. 조회·수정·삭제에 선행 등록이 필요하면 실제 insert 메서드를 공통 fixture로 사용할지 결정한다.

### 6. 테스트 클래스 골격을 먼저 만든다

- 운영 클래스와 같은 package의 `src/test/java` 대응 경로에 `<대상단순명>Test.java`를 만든다.
- JUnit 5, AssertJ, `@SpringBootTest`, `@Transactional`, `@Slf4j`와 실제 Bean 주입 코드만 먼저 작성한다.
- 이 단계에서 import와 대상 Bean 타입이 실제로 존재하는지 확인한다.

### 7. 충돌하지 않는 테스트 데이터를 구현한다

- `TEST_`와 `LocalDateTime`의 년월일시분초 값을 결합한 20자리 문자열로 PK·유니크 값을 만든다.
- 컬럼 길이와 FK 순서를 지킨다.
- 공통 fixture가 필요하면 테스트 클래스 내부 private 메서드로 만들고 실제 DAO/Mapper API만 호출한다.
- 각 테스트가 다른 테스트의 데이터나 실행 순서에 의존하지 않게 한다.

### 8. 테스트 메서드를 한 개씩 작성하고 확인한다

실제 DAO/Mapper 선언 순서에 맞춰 한 메서드씩 작성한다. 각 메서드는 `// Given`, `// When`, `// Then` 순서를 지킨다.

- `insert`: 데이터를 생성하고 실제 insert를 호출한 뒤 `int` 결과를 `isGreaterThan(0)`으로 검증한다.
- 단건 `select`: 먼저 데이터를 등록하고 고유 조건으로 조회한 뒤 `isNotNull()`과 핵심 필드를 검증한다.
- 목록 `selectList`: 먼저 데이터를 등록하고 검색 조건을 지정한 뒤 목록 존재·비어 있지 않음·포함 여부를 검증한다.
- `update`: 먼저 데이터를 등록하고 PK와 변경값을 설정한 뒤 `int` 결과를 `isGreaterThan(0)`으로 검증한다.
- `delete`: 먼저 데이터를 등록하고 실제 삭제 조건을 만든 뒤 `int` 결과를 `isGreaterThan(0)`으로 검증한다.

한 테스트를 작성할 때마다 메서드 호출, 인자 타입, setter/getter와 반환 타입을 원본 소스와 다시 대조한다.

### 9. 컴파일 오류를 먼저 제거한다

- package, import, 클래스명, 메서드명과 타입을 다시 확인한다.
- 존재하지 않는 API를 새로 만들어 오류를 숨기지 않는다.
- 대상 테스트만 컴파일 또는 실행하여 Java 코드와 Spring Bean 구성이 유효한지 확인한다.

### 10. 실제 통합 테스트를 실행하고 원인별로 수정한다

- 먼저 대상 테스트 클래스만 실행한다.
- 실패를 컴파일, Spring 컨텍스트·Bean 로딩, Mapper statement 등록, DB 연결·스키마, fixture·제약조건, assertion 실패로 구분한다.
- 원인에 해당하는 부분만 수정하고 같은 대상 테스트를 다시 실행한다.
- 대상 테스트가 성공하면 필요할 때만 관련 테스트 범위를 넓힌다.
## 허용되는 클래스 형태

대상 클래스와 같은 package를 사용하고 `src/test/java`의 대응 경로에 `<대상단순명>Test.java`를 만든다.

```java
package com.example.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class SampleMapperTest {

    @Autowired
    private SampleMapper sampleMapper;

    // 실제 대상 메서드의 테스트와 클래스 내부 fixture만 둔다.
}
```

`SampleMapper`, `SampleVO`, 필드와 메서드명은 반드시 분석한 프로젝트의 실제 식별자로 치환한다. `@Slf4j` 이외에 프로젝트에 없는 Lombok builder, 생성자 또는 객체 생성 유틸리티를 추가하지 않는다.

## 테스트 데이터 규칙

- 각 테스트가 필요한 데이터를 직접 준비하고 다른 테스트의 실행 여부나 순서에 의존하지 않는다.
- 문자열 식별자는 `TEST_` 5자리와 `LocalDateTime`의 `yyyyMMddHHmmssS` 15자리를 결합해 총 20자리로 생성한다.
- `DateTimeFormatter.ofPattern("yyyyMMddHHmmssS")`를 사용한다. 연·월·일·시·분·초와 초의 1자리 소수부를 포함한다.
- 예: `String testId = "TEST_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS"));`
- 동일 테스트에서 여러 식별자를 연속 생성해 충돌 가능성이 있으면 한 번 생성한 식별자를 관련 데이터에 재사용하고, 서로 다른 PK가 반드시 필요하면 실제 프로젝트의 검증된 ID 생성 방식을 확인한다. 20자리를 넘기거나 다른 임의 생성 방식으로 전환하지 않는다.
- 관련 행들이 같은 키를 사용해야 하면 테스트 지역 변수로 한 번 생성해 재사용한다.
- FK와 조인이 있으면 확인된 DAO/Mapper 메서드로 부모·연관 데이터를 먼저 등록한다.
- 기존 DB에 있다고 가정한 고정 PK, 전체 목록 크기 또는 전역 count에 의존하지 않는다.
- 클래스 내부 fixture는 실제 VO/DTO와 실제 DAO/Mapper API만 사용한다.
- 데이터 정리가 필요하면 프로젝트에서 확인된 삭제 메서드만 사용한다. 확인되지 않은 cleanup 유틸리티를 만들지 않는다.

## 유형별 생성 규칙

대상에 실제로 존재하는 메서드 유형만 생성한다. 누락된 CRUD 메서드를 완성도 목적으로 임의 추가하지 않는다.

### insert

반환 타입이 `int`이면 결과는 아래 형태로만 검증한다. `isEqualTo(1)`, `isOne()`이나 메시지 체이닝으로 바꾸지 않는다.

```java
@Test
void insertSample() {
    // Given
    SampleVO sample = new SampleVO();
    sample.setSampleId("TEST_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS")));
    sample.setSampleName("TEST_SAMPLE");

    // When
    int result = sampleMapper.insertSample(sample);

    // Then
    assertThat(result).isGreaterThan(0);
}
```

### select

단건 조회는 테스트가 준비한 고유 키로 조회한다. `isNotNull()`과 쿼리 계약상 필요한 핵심 필드 비교만 사용한다.

```java
@Test
void selectSample() {
    // Given
    SampleVO saved = insertSampleTestData();
    SampleVO condition = new SampleVO();
    condition.setSampleId(saved.getSampleId());

    // When
    SampleVO result = sampleMapper.selectSample(condition);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getSampleId()).isEqualTo(saved.getSampleId());
    assertThat(result.getSampleName()).isEqualTo(saved.getSampleName());
}
```

모든 필드를 기계적으로 비교하지 않고 Mapper XML이 조회하며 업무상 중요한 필드만 선택한다.

### selectList

테스트 데이터가 선택되도록 실제 검색 조건을 지정한다. `isNotNull()`, `isNotEmpty()`, `size()`와 `contains()` 계열 중 필요한 검증만 사용한다.

```java
@Test
void selectSampleList() {
    // Given
    SampleVO saved = insertSampleTestData();
    SampleVO condition = new SampleVO();
    condition.setSampleId(saved.getSampleId());

    // When
    List<SampleVO> results = sampleMapper.selectSampleList(condition);

    // Then
    assertThat(results).isNotNull();
    assertThat(results).isNotEmpty();
    assertThat(results)
            .extracting(SampleVO::getSampleId)
            .contains(saved.getSampleId());
}
```

정확히 한 건이 보장될 때만 `hasSize(1)` 또는 `assertThat(results.size()).isEqualTo(1)`을 사용한다. 기존 데이터가 포함될 수 있는 목록에는 고정 크기 검증을 추가하지 않는다.

### update

실제 등록 메서드로 행을 준비하고 PK와 변경 필드를 설정한다. 반환 타입이 `int`이면 결과는 아래 형태로만 검증한다.

```java
@Test
void updateSample() {
    // Given
    SampleVO saved = insertSampleTestData();
    SampleVO update = new SampleVO();
    update.setSampleId(saved.getSampleId());
    update.setSampleName("TEST_UPDATED");

    // When
    int result = sampleMapper.updateSample(update);

    // Then
    assertThat(result).isGreaterThan(0);
}
```

### delete

실제 등록 메서드로 행을 준비하고 삭제 메서드가 요구하는 실제 타입과 키를 사용한다. 반환 타입이 `int`이면 결과는 아래 형태로만 검증한다.

```java
@Test
void deleteSample() {
    // Given
    SampleVO saved = insertSampleTestData();
    SampleVO condition = new SampleVO();
    condition.setSampleId(saved.getSampleId());

    // When
    int result = sampleMapper.deleteSample(condition);

    // Then
    assertThat(result).isGreaterThan(0);
}
```

### 클래스 내부 fixture

조회·수정·삭제 준비에 실제 insert 메서드가 있을 때만 만든다. fixture에서도 `int` 결과에 동일한 AssertJ 규칙을 적용한다.

```java
private SampleVO insertSampleTestData() {
    SampleVO sample = new SampleVO();
    sample.setSampleId("TEST_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS")));
    sample.setSampleName("TEST_SAMPLE");

    int result = sampleMapper.insertSample(sample);
    assertThat(result).isGreaterThan(0);

    return sample;
}
```

이 fixture 이름도 예시다. 대상 테스트 클래스 안에 실제 타입과 API에 맞게 함께 생성하고 존재하지 않는 외부 유틸리티를 호출하지 않는다.

## 금지 패턴

- JUnit 4의 `org.junit.Test`, `@RunWith` 또는 JUnit 4 assertion
- JUnit Jupiter assertion과 AssertJ 혼용
- `@MybatisTest`, `@JdbcTest`, `@DataJpaTest`, `@ContextConfiguration` 또는 임의 context loader
- `extends ProjectIntegrationTest`, `extends BaseTest` 등 부모 클래스 상속
- `@Mock`, `@MockBean`, `@SpyBean`, Mockito 또는 가짜 구현체
- Mapper XML을 거치지 않는 직접 JDBC 호출로 대상 DAO/Mapper 테스트 대체
- 존재하지 않는 builder, setter, 조회·삭제 메서드 또는 ID 생성 Bean 추측
- 빈 assertion, 주석 처리된 assertion 또는 로그 출력만 있는 테스트
- `if (result != null)` 또는 `if (!results.isEmpty())` 내부에서만 assertion을 수행하는 실패 회피
- `assertThat(result).as("...")`, `assertThat(result).withMessage("...")` 형태의 메시지 체이닝
- 테스트 메서드 간 변경 가능한 데이터 공유와 실행 순서 의존

## 실행 명령과 실패 처리

프로젝트에 실제로 존재하는 wrapper와 빌드 도구를 우선한다.

```text
Maven:  ./mvnw -Dtest=SampleMapperTest test
         또는 mvn -Dtest=SampleMapperTest test
Gradle: ./gradlew test --tests '*SampleMapperTest'
```

명령의 `SampleMapperTest`는 생성한 실제 테스트 클래스명으로 바꾼다. 환경 문제로 실행하지 못하면 테스트 성공으로 간주하지 않고 필요한 DB 설정과 정확한 실패 원인을 보고한다.
## 완료 조건

- 생성된 Java 테스트가 컴파일된다.
- 클래스가 `@SpringBootTest`, `@Transactional`, `@Slf4j`를 모두 사용하고 금지된 컨텍스트 어노테이션을 포함하지 않는다.
- DAO/Mapper가 실제 Spring Bean으로 주입된다.
- 모든 호출과 필드 접근이 실제 프로젝트 소스에 존재한다.
- 각 테스트가 Given / When / Then과 지정된 AssertJ 검증 형태를 따른다.
- 대상 테스트를 실행해 결과를 보고한다. 환경 문제면 필요한 DB 설정과 정확한 실패 원인을 보고한다.
- 수정 파일, 커버한 메서드, 실행 명령과 결과, 발견한 소스·매핑 불일치를 요약한다.
