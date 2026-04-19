package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

  // 추후 주석 삭제 및, QueryDSL 적용예정
  @EntityGraph(attributePaths = "indexInfo")
  List<IndexData> findByIndexInfo_FavoriteTrueOrderByIndexInfoIdAscBaseDateDesc();

  @EntityGraph(attributePaths = "indexInfo")
  List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
      Long indexInfoId, LocalDate startDate, LocalDate endDate);
}
