package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateResponse;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class IndexInfoServiceTest {

  @Autowired IndexInfoService indexInfoService;

  @Test
  @DisplayName("지수 정보를 실제 DB에 저장하고 조회할 수 있다.")
  void saveAndFind() {
    // given
    IndexInfoCreateRequest request =
        new IndexInfoCreateRequest(
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), false);

    // when
    IndexInfoCreateResponse indexInfo = indexInfoService.createIndexInfo(request);

    // then
    assertThat(indexInfo.id()).isNotNull();
    assertThat(indexInfo.indexClassification()).isEqualTo("KOSPI시리즈");
  }

  @Test
  @DisplayName("지수 분류명과 지수명으로 중복 여부를 확인할 수 있다.")
  void existsByIndexClassificationAndIndexName() {
    // given
    IndexInfoCreateRequest request1 =
        new IndexInfoCreateRequest(
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), false);
    indexInfoService.createIndexInfo(request1);

    // when
    IndexInfoCreateRequest request2 =
        new IndexInfoCreateRequest(
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), false);

    // then
    assertThatThrownBy(() -> indexInfoService.createIndexInfo(request2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 동일한 지수 분류명과 지수명이 존재합니다.");
  }
}
