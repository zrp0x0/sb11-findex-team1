package com.codeit.findex.domain.indexdata.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "차트 단일 데이터 포인트 DTO")
public record ChartDataPoint(
    @Schema(description = "기준 일자", example = "2024-01-01")
    LocalDate data,

    @Schema(description = "수치 값(종가, 이동평균선 등)", example = "2850.50")
    BigDecimal value
) {}
