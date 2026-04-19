package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>, IndexInfoRepositoryCustom {

  // API 요구사항: {지수 분류명}, {지수명} 조합값 중복 검증용
  boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);
}
