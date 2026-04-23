package com.codeit.findex.domain.syncjob.dto.indexdata;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record SyncIndexDataRequest(
    @Schema(description = "지수 정보 ID 목록 (비어 있을 경우 모든 지수 대상)", example = "[1,2,3]")
    List<Long> indexInfoIds,
    @Schema(description = "대상 날짜 (부터)", example = "2026-04-01")
    @NotNull LocalDate baseDateFrom,
    @Schema(description = "대상 날짜 (까지)", example = "2026-04-21")
    @NotNull LocalDate baseDateTo
) {}
