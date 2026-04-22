package com.codeit.findex.domain.autosyncconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record AutoSyncConfigListRequest(
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @Schema(description = "활성화 여부", example = "true")
    Boolean enabled,
    @Schema(description = "이전 페이지 마지막 요소 ID", example = "10")
    Long idAfter,
    @Schema(description = "커서 (다음 페이지 시작점)", example = "KOSPI 200")
    String cursor,
    @Schema(description = "정렬 필드", example = "indexInfo.indexName", defaultValue = "indexInfo.indexName")
    @Pattern(regexp = "^(indexInfo.indexName|enabled)$")
    String sortField,
    @Schema(description = "정렬 방향", example = "asc", defaultValue = "asc")
    @Pattern(regexp = "^(asc|desc)$")
    String sortDirection,
    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Positive
    Integer size
) {

}
