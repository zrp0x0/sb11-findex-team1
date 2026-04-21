package com.codeit.findex.domain.indexdata.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지수 성과 랭킹 조회 기간 유형")
public enum UnitPeriodType {
  DAILY,
  WEEKLY,
  MONTHLY,
}
