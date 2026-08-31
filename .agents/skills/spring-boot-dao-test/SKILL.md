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
- `import`: 대상 DAO, JUnit 5 `@Test`, Spring Boot `@SpringBootTest`, Spring `@Transactional`과 주입에 필요한 import를 선언한다.
- `@SpringBootTest`
- `@Transactional`
- `class`: 대상 DAO의 단순 클래스명 뒤에 `Test`를 붙인다.
- `private 대상 DAO;`: 대상 DAO 타입의 private 필드를 선언하고 주입한다.
- `@Test void`: 대상 DAO의 각 메서드와 이름이 동일한 테스트 메서드를 작성한다.

```java
package 대상_DAO와_동일한_package;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class 대상DAOTest {

    @Autowired
    private 대상DAO 대상DAO;

    @Test
    void insert + 대상 DAO 메서드명() {
        // given
        대상VO 대상VO = new 대상VO();

        // when
        int result = 대상DAO.insert + 대상 DAO 메서드명(대상VO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }

    @Test
    void select + 대상 DAO 메서드명() {
        // given
        대상VO 대상VO = new 대상VO();

        // when
        반환VO result = 대상DAO.select + 대상 DAO 메서드명(대상VO);

        log.debug("result={}", result);

        // then
        assertThat(result).isNotNull();

        assertThat(result.get{PK필드명}()).isEqualTo(대상VO.get{PK필드명}());
    }

    @Test
    void selectList + 대상 DAO 메서드명() {
        // given
        대상VO 대상VO = new 대상VO();

        // when
        List<반환VO> results = 대상DAO.selectList + 대상 DAO 메서드명(대상VO);

        log.debug("results={}", results);

        // then
        assertThat(results).isNotEmpty();

        assertThat(results) .anyMatch(result -> result.get{비교필드명}().equals(대상VO.get{비교필드명}()));
    }

    @Test
    void update + 대상 DAO 메서드명() {
        // given
        대상VO 대상VO = new 대상VO();

        // when
        int result = 대상DAO.update + 대상 DAO 메서드명(대상VO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }

    @Test
    void delete + 대상 DAO 메서드명() {
        // given
        대상VO 대상VO = new 대상VO();

        // when
        int result = 대상DAO.delete + 대상 DAO 메서드명(대상VO);

        log.debug("result={}", result);

        // then
        assertThat(result).isGreaterThan(0);
    }
}
```
