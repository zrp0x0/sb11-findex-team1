package com.codeit.findex.domain.indexinfo.dto;

import com.codeit.findex.domain.common.enums.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoResponse(
    Long id,
    String indexClassification,
    String indexName,
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex,
    SourceType sourceType,
    Boolean favorite) {
  public static IndexInfoResponse create(
      Long id,
      String indexClassification,
      String indexName,
      Integer employedItemsCount,
      LocalDate basePointInTime,
      BigDecimal baseIndex,
      SourceType sourceType,
      Boolean favorite) {
    return new IndexInfoResponse(
        id,
        indexClassification,
        indexName,
        employedItemsCount,
        basePointInTime,
        baseIndex,
        sourceType,
        favorite);
  }
}
