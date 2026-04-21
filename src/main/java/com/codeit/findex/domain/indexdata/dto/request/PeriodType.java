package com.codeit.findex.domain.indexdata.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지수 차트 조회 기간 유형")
public enum PeriodType {
  MONTHLY, QUARTERLY, YEARLY
}
