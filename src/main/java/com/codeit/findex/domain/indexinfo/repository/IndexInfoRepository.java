package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {

  // API 요구사항: {지수 분류명}, {지수명} 조합값 중복 검증용
  boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

  // 같은 지수가 DB에 있는지 확인용 - 중복 지수는 업데이트로 처리를 위해 필요
  Optional<IndexInfo> findByIndexClassificationAndIndexName(
      String indexClassification,
      String indexName
  );
}
