package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class IndexInfoServiceTest {

  @Autowired IndexInfoRepository indexInfoRepository;
  @Autowired IndexInfoService indexInfoService;
  @Autowired EntityManager em;

  @Test
  @DisplayName("지수 정보를 실제 DB에 저장하고 조회할 수 있다.")
  void saveAndFind() {
    // given
    IndexInfoCreateRequest request =
        new IndexInfoCreateRequest(
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), false);

    // when
    IndexInfoResponse indexInfo = indexInfoService.createIndexInfo(request);

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

  @Test
  @DisplayName("지수 정보를 수정할 수 있다.")
  void updateIndexInfo() {
    // given
    IndexInfo dummy = IndexInfo.createByUser("KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), false);
    IndexInfo savedInfo = indexInfoRepository.save(dummy);

    // when
    IndexInfoUpdateRequest updateRequest = new IndexInfoUpdateRequest(300, LocalDate.of(2024, 1, 1), new BigDecimal("2000.00"), true);
    indexInfoService.updateIndexInfo(savedInfo.getId(), updateRequest);

    em.flush(); // 서비스에서 변경된 내용을 DB로 보냄
    em.clear(); // 메모리를 비워서 DB에서 객체를 가져오도록 함

    // then
    savedInfo = indexInfoRepository.findById(savedInfo.getId()).orElseThrow();

    assertThat(savedInfo.getEmployedItemsCount()).isEqualTo(300);
    assertThat(savedInfo.getBasePointInTime()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(savedInfo.getBaseIndex()).isEqualByComparingTo(new BigDecimal("2000.00")); // 소수점까지 비교 가능
    assertThat(savedInfo.getFavorite()).isTrue();
  }
}
