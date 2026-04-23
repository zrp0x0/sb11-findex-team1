package com.codeit.findex.domain.indexdata.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record IndexDataSearchCondition(
    Long indexInfoId,
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
    Long idAfter,
    String cursor,
    @Pattern(
            regexp =
                "^(baseDate|marketPrice|closingPrice|highPrice|lowPrice|versus|fluctuationRate|tradingQuantity|tradingPrice|marketTotalAmount)$",
            message = "유효하지 않은 정렬 필드입니다.")
        String sortField,
    @Pattern(regexp = "^(?i)(asc|desc)$", message = "정렬 방향은 asc 또는 desc만 가능합니다.")
        String sortDirection,
    @Min(value = 1, message = "페이지 크기는 최소 1 이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다.")
        Integer size) {
  public String getSortField() {
    return (sortField == null || sortField.isBlank()) ? "baseDate" : sortField;
  }

  public String getSortDirection() {
    return (sortDirection == null || sortDirection.isBlank())
        ? "desc"
        : sortDirection.toLowerCase();
  }

  public Integer getSize() {
    return (size == null) ? 10 : size;
  }
}
