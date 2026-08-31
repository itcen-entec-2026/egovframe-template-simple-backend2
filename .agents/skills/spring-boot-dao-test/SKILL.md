---
name: spring-boot-dao-test
description: Spring Boot 프로젝트에서 eGovFrame MyBatis DAO를 분석하고 DAO 메서드별 데이터베이스 통합 테스트 클래스를 구현한다.
---

# Spring Boot DAO Test

대상 DAO를 분석하고 동일한 프로젝트 규칙에 맞는 Spring Boot 데이터베이스 통합 테스트를 구현한다.

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
- 아래 예시의 `SampleDAO`, `SampleVO`, 메서드명과 필드명은 분석한 대상 DAO와 VO의 실제 식별자로 치환한다. `+`, 공백, 중괄호 placeholder처럼 Java 문법에 맞지 않는 표기는 생성하지 않는다.

```java
package com.example.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

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

    @Test
    void insertSample() {
        // given
        SampleVO sampleVO = new SampleVO();
        LocalDateTime now = LocalDateTime.now();
        String test = "test 이백행 " + now + " ";
        sampleVO.setSampleId("TEST_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS")));
        sampleVO.setSampleName(test + "테스트 샘플명");

        // when
        int result = sampleDAO.insertSample(sampleVO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }

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

    @Test
    void selectSampleList() {
        SampleVO insertSampleTestData = insertSampleTestData();

        // given
        SampleVO sampleVO = new SampleVO();
        sampleVO.setSampleId("TEST_" + UUID.randomUUID());
        sampleVO.setSampleName("TEST");

        // when
        List<SampleVO> results = sampleDAO.selectSampleList(sampleVO);

        log.debug("results={}", results);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results)
                .extracting(SampleVO::getSampleId)
                .contains(sampleVO.getSampleId());
    }

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

    private SampleVO insertSampleTestData() {
        SampleVO sampleVO = new SampleVO();
        LocalDateTime now = LocalDateTime.now();
        String test = "test 이백행 " + now + " ";
        sampleVO.setSampleId("TEST_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS")));
        sampleVO.setSampleName(test + "테스트 샘플명");
        sampleDAO.insertSample(sampleVO);
        return sampleVO;
    }
}
```
