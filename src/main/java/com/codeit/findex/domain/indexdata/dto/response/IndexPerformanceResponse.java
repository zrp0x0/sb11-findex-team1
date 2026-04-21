package com.codeit.findex.domain.indexdata.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record IndexPerformanceResponse(
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,

    @Schema(description = "지수 분류", example = "KOSPI")
    String indexClassification,

    @Schema(description = "지수명", example = "KOSPI 200")
    String indexName,

    @Schema(description = "기준가 대비 차이", example = "25.12")
    BigDecimal versus,

    @Schema(description = "기준가 대비 등락률", example = "1.42")
    BigDecimal fluctuationRate,

    @Schema(description = "현재 종가", example = "2800.35")
    BigDecimal currentPrice,

    @Schema(description = "비교 기준 종가", example = "2761.12")
    BigDecimal beforePrice

) {

}
