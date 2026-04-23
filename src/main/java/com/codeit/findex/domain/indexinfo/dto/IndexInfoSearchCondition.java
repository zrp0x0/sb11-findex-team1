package com.codeit.findex.domain.indexinfo.dto;

import com.codeit.findex.domain.common.enums.SourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

// 검색 조건 DTO
public record IndexInfoSearchCondition(
    String indexClassification,
    String indexName,
    Boolean favorite,
    Long idAfter,
    String cursor,
    @Pattern(
            regexp = "^(indexClassification|indexName|employedItemsCount)$",
            message = "정렬 필드는 indexClassification, indexName, employedItemsCount 중 하나여야 합니다.")
        String sortField,
    @Pattern(regexp = "^(?i)(asc|desc)$", message = "정렬 방향은 asc 또는 desc만 가능합니다.")
        String sortDirection,
    @Min(value = 1, message = "페이지 크기는 최소 1이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다.")
        Integer size) {
  public String getSortField() {
    return (sortField == null || sortField.isBlank()) ? "indexClassification" : sortField;
  }

  public String getSortDirection() {
    return (sortDirection == null || sortDirection.isBlank()) ? "asc" : sortDirection;
  }

  public Integer getSize() {
    return (size == null || size <= 0) ? 10 : size;
  }
}
