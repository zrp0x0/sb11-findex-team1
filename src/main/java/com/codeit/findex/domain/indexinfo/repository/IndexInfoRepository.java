package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoSummaryResponse;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>, IndexInfoRepositoryCustom {

  // API 요구사항: {지수 분류명}, {지수명} 조합값 중복 검증용
  boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

  // 요약 목록 조회에만 사용되는 필드만 가져오도록 JPQL 사용
  @Query(
      "SELECT new com.codeit.findex.domain.indexinfo.dto.IndexInfoSummaryResponse(i.id, i.indexClassification, i.indexName) FROM IndexInfo i")
  List<IndexInfoSummaryResponse> findAllSummaries();
}
