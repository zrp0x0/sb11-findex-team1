package com.codeit.findex.domain.indexdata.dto.response;

import com.codeit.findex.domain.indexdata.dto.request.PeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record IndexChartResponse(
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,

    @Schema(description = "지수 분류", example = "KOSPI")
    String indexClassification,

    @Schema(description = "지수명", example = "IT")
    String indexName,

    @Schema(description = "요청된 기간 유형", example = "MONTHLY")
    PeriodType periodType,

    @Schema(description = "차트 원본 데이터(종가) 목록")
    List<ChartDataPoint> dataPoints,

    @Schema(description = "5일 이동평균선 데이터 목록")
    List<ChartDataPoint> ma5DataPoints,

    @Schema(description = "20일 이동평균선 데이터 목록")
    List<ChartDataPoint> ma20DataPoints
) {}
