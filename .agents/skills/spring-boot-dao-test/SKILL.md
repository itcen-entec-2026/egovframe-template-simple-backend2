---
name: spring-boot-dao-test
description: Spring Boot 프로젝트에서 eGovFrame MyBatis DAO를 분석하고 DAO 메서드별 데이터베이스 통합 테스트 클래스를 구현한다.
---

# Spring Boot DAO Test

대상 DAO를 분석하고 동일한 프로젝트 규칙에 맞는 Spring Boot 데이터베이스 통합 테스트를 구현한다.

## 사용법

```text
/spring-boot-dao-test BBSAttributeManageDAO
```

## 대상 DAO 분석

대상 DAO에서 다음 항목을 확인한다.

- `package`
- `import`
- `@Repository`
- `extends EgovAbstractMapper`
- 메서드
  - 등록: `insert`
  - 조회(단건): `select`
  - 조회(멀티건): `select`를 사용하고 postfix로 `List`를 사용
  - 수정: `update`
  - 삭제: `delete`

각 메서드의 이름, 파라미터 타입, 반환 타입과 호출하는 MyBatis statement ID를 확인한다.
대상 mapper XML에서 statement ID별 `parameterType`, `resultType` 또는 `resultMap`, 동적 SQL 조건과 `SELECT` 컬럼을 확인하고 DAO의 파라미터·반환 타입 및 VO 필드와 일치하는지 검증한다.

## 기존 테스트 파일 수정 절차

1. 대상 DAO의 package와 프로젝트의 테스트 소스 경로를 기준으로 기존 `<DAO단순명>Test.java`를 먼저 찾는다.
2. 기존 테스트 파일이 있으면 전체 내용을 확인하고 package, annotation, 주입 필드, import, 테스트 메서드와 fixture 메서드를 파악한다. 같은 테스트 클래스를 새로 만들지 않는다.
3. 사용자가 지정한 DAO 메서드가 있으면 그 메서드에 대응하는 테스트만 수정한다. 지정하지 않았으면 아직 테스트가 없는 DAO 메서드를 하나씩 처리한다.
4. 대응 테스트 메서드가 이미 있으면 해당 메서드 본문만 최소 범위로 수정한다. 없으면 클래스의 마지막 닫는 중괄호 앞에 새 테스트 메서드를 추가한다.
5. 새 코드에 필요한 import와 fixture만 추가한다. 기존 import, annotation, 테스트, fixture와 중복되는 선언을 만들지 않고 관련 없는 코드는 변경하지 않는다.
6. 기존 테스트 파일이 없을 때만 공통 클래스 골격을 한 번 생성한 후 대상 DAO 메서드별 테스트를 추가한다.
7. 프로젝트가 사용하는 Maven 또는 Gradle 명령으로 변경한 테스트 클래스나 테스트 메서드를 우선 실행한다. 실패하면 이번 변경과 직접 관련된 원인만 수정한다.
8. 대상 테스트가 통과하고 중복 선언이나 컴파일 오류가 없으면 수정을 종료한다. 테스트를 실행할 수 없으면 변경 내용과 미검증 사유를 보고한다.

## 대상 DAO 테스트 구현

분석한 대상 DAO를 기준으로 다음 항목을 작성한다.

- `package`: 대상 DAO와 동일한 package를 선언한다.
- `import`: 대상 DAO, `java.util.List`, JUnit 5 `@Test`, AssertJ `assertThat`, Spring Boot `@SpringBootTest`, Spring `@Transactional`과 주입에 필요한 import를 선언한다.
- `@SpringBootTest`
- `@Transactional`
- `class`: 대상 DAO의 단순 클래스명 뒤에 `Test`를 붙인다.
- `private 대상 DAO;`: 대상 DAO 타입의 private 필드를 선언하고 주입한다.
- `@Test void`: 테스트 메서드 이름은 대응하는 대상 DAO 메서드 이름과 정확히 동일하게 작성한다.
- 각 테스트는 자신에게 필요한 데이터를 직접 등록하고 다른 테스트의 실행 여부나 실행 순서에 의존하지 않는다.
- 테스트 데이터 식별자는 중복되지 않게 생성하고 대상 컬럼 길이를 넘지 않도록 형식을 조정한다.
- Java 코드는 DAO 메서드별로 작게 수정한다. 전체 테스트 클래스를 한 번에 다시 작성하지 말고, 대상 메서드 하나에 대응하는 테스트 메서드와 필요한 공통 코드만 추가하거나 변경한다.
- 아래 예시의 `SampleDAO`, `SampleVO`, 메서드명과 필드명은 분석한 대상 DAO와 VO의 실제 식별자로 치환한다. `+`, 공백, 중괄호 placeholder처럼 Java 문법에 맞지 않는 표기는 생성하지 않는다.

### 공통 클래스 골격

```java
package com.example.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class SampleDAOTest {

    @Autowired
    private SampleDAO sampleDAO;

    // 테스트 메서드는 대상 DAO 메서드별로 하나씩 추가한다.
}
```

### 등록 메서드

```java
    @Test
    void insertSample() {
        // given
        SampleVO sampleVO = new SampleVO();
        LocalDateTime now = LocalDateTime.now();
        String test = "test 이백행 " + now + " ";
        sampleVO.setSampleId("TEST_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS")));
        sampleVO.setSampleName(test + "테스트 샘플명");

        // when
        int result = sampleDAO.insertSample(sampleVO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }
```

### 단건 조회 메서드

```java
    @Test
    void selectSample() {
        SampleVO insertSampleTestData = insertSampleTestData();

        // given
        SampleVO sampleVO = new SampleVO();
        sampleVO.setSampleId(insertSampleTestData.getSampleId());

        // when
        SampleVO result = sampleDAO.selectSample(sampleVO);

        log.debug("result={}", result);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSampleId()).isEqualTo(sampleVO.getSampleId());
    }
```

### 목록 조회 메서드

```java
    @Test
    void selectSampleList() {
        SampleVO insertSampleTestData = insertSampleTestData();

        // given
        SampleVO sampleVO = new SampleVO();
        sampleVO.setSampleId(insertSampleTestData.getSampleId());

        // when
        List<SampleVO> results = sampleDAO.selectSampleList(sampleVO);

        log.debug("results={}", results);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results)
                .extracting(SampleVO::getSampleId)
                .contains(sampleVO.getSampleId());
    }
```

### 수정 메서드

```java
    @Test
    void updateSample() {
        SampleVO insertSampleTestData = insertSampleTestData();

        // given
        SampleVO sampleVO = new SampleVO();
        sampleVO.setSampleId(insertSampleTestData.getSampleId());
        sampleVO.setSampleName("UPDATED");

        // when
        int result = sampleDAO.updateSample(sampleVO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }
```

### 삭제 메서드

```java
    @Test
    void deleteSample() {
        SampleVO insertSampleTestData = insertSampleTestData();

        // given
        SampleVO sampleVO = new SampleVO();
        sampleVO.setSampleId(insertSampleTestData.getSampleId());

        // when
        int result = sampleDAO.deleteSample(sampleVO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }
```

### 공통 fixture 메서드

조회·수정·삭제 테스트에 등록 데이터가 필요할 때만 추가한다.

```java
    private SampleVO insertSampleTestData() {
        SampleVO sampleVO = new SampleVO();
        LocalDateTime now = LocalDateTime.now();
        String test = "test 이백행 " + now + " ";
        sampleVO.setSampleId("TEST_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS")));
        sampleVO.setSampleName(test + "테스트 샘플명");
        int result = sampleDAO.insertSample(sampleVO);
        return sampleVO;
    }
```
