package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), null);

    // when
    IndexInfoResponse indexInfo = indexInfoService.createIndexInfo(request);

    // then
    assertThat(indexInfo.id()).isNotNull();
    assertThat(indexInfo.favorite()).isFalse(); // null로 들어왔으므로 false 여야함
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
  @DisplayName("지수 정보를 id로 조회할 수 있다")
  void findIndexInfoById() {
    // given
    IndexInfoCreateRequest request =
        new IndexInfoCreateRequest(
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), null);
    IndexInfoResponse response = indexInfoService.createIndexInfo(request);

    // when
    IndexInfoResponse result = indexInfoService.readIndexInfoById(response.id());

    // then
    assertThat(result).isEqualTo(response);
  }

  /**
   * TODO
   * query validation / request validation 예외 공통 응답이 잘 나가는지 테스트 (성결님 코드 머지 후)
   */

  @Test
  @DisplayName("존재하지 않는 id로 지수 정보를 요청할 수 없다.")
  void notFindIndexInfoById() {
    // given
    Long id = 999L;

    // when & then
    assertThatThrownBy(() -> indexInfoService.readIndexInfoById(id))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("지수 정보를 찾을 수 없습니다 ID: " + id);
  }

  @Test
  @DisplayName("지수 정보를 수정할 수 있다.")
  void updateIndexInfo() {
    // given
    IndexInfo dummy =
        IndexInfo.createByUser(
            "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2023, 1, 1), new BigDecimal("1000.00"), false);
    IndexInfo savedInfo = indexInfoRepository.save(dummy);

    // when
    IndexInfoUpdateRequest updateRequest =
        new IndexInfoUpdateRequest(300, LocalDate.of(2024, 1, 1), new BigDecimal("2000.00"), true);
    indexInfoService.updateIndexInfo(savedInfo.getId(), updateRequest);

    em.flush(); // 서비스에서 변경된 내용을 DB로 보냄
    em.clear(); // 메모리를 비워서 DB에서 객체를 가져오도록 함

    // then
    savedInfo = indexInfoRepository.findById(savedInfo.getId()).orElseThrow();

    assertThat(savedInfo.getEmployedItemsCount()).isEqualTo(300);
    assertThat(savedInfo.getBasePointInTime()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(savedInfo.getBaseIndex())
        .isEqualByComparingTo(new BigDecimal("2000.00")); // 소수점까지 비교 가능
    assertThat(savedInfo.getFavorite()).isTrue();
  }
}
