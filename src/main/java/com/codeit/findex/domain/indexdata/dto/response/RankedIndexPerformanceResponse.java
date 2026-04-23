package com.codeit.findex.domain.indexdata.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "순위가 포함된 지수 성과 정보 DTO")
public record RankedIndexPerformanceResponse(
    @Schema(description = "지수 성과 정보")
    IndexPerformanceResponse performance,

    @Schema(description = "성과 순위", example = "1")
    int rank
) {

}
