package com.codeit.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record IndexDataExportCSVRequest(
    Long indexInfoId,
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
    @Pattern(
            regexp =
                "^(baseDate|marketPrice|closingPrice|highPrice|lowPrice|versus|fluctuationRate|tradingQuantity|tradingPrice|marketTotalAmount)$",
            message = "올바른 정렬 필드가 아닙니다.")
        String sortField,
    @Pattern(regexp = "^(?i)(asc|desc)$", message = "정렬 방향은 asc 또는 desc만 가능합니다.")
        String sortDirection) {
  public IndexDataExportCSVRequest {
    if (sortField == null || sortField.isBlank()) {
      sortField = "baseDate";
    }
    if (sortDirection == null || sortDirection.isBlank()) {
      sortDirection = "desc";
    }
  }
}
