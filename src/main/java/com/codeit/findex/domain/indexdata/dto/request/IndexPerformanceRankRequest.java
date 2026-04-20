package com.codeit.findex.domain.indexdata.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IndexPerformanceRankRequest(
    @Schema(description = "지수 정보 ID", example = "1")
    @Positive(message = "지수 정보 ID는 양수여야 합니다.")
    Long indexInfoId,

    @Schema(description = "성과 비교 기간 유형", example = "DAILY")
    @NotNull(message = "기간 유형은 필수입니다.")
    UnitPeriodType periodType,

    @Schema(description = "반환할 랭킹 개수", example = "10")
    @Min(value = 1, message = "랭킹 개수는 1 이상이어야 합니다.")
    Integer limit
) {

}
