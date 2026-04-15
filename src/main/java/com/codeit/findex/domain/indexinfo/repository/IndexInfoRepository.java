package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {

    boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);
}
