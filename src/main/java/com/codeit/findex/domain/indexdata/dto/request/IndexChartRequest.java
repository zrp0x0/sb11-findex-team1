package com.codeit.findex.domain.indexdata.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "지수 차트 조회 요청 DTO")
public record IndexChartRequest(
    @Schema(description = "조회 기간 유형", example = "MONTHLY")
    @NotNull(message = "기간 유형은 필수입니다.")
    PeriodType periodType
) {}
