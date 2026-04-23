package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.syncjob.validation.ValidSyncJobDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValidSyncJobDateRange
public record SyncJobListRequest(
    @Schema(description = "연동 작업 유형", example = "INDEX_DATA")
    JobType jobType,
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @Schema(description = "대상 날짜 (부터)", example = "2026-04-01")
    LocalDate baseDateFrom,
    @Schema(description = "대상 날짜 (까지)", example = "2026-04-21")
    LocalDate baseDateTo,
    @Schema(description = "작업자", example = "127.0.0.1")
    String worker,
    @Schema(description = "작업 일시 (부터)", example = "2026-04-21T10:00:00")
    LocalDateTime jobTimeFrom,
    @Schema(description = "작업 일시 (까지)", example = "2026-04-21T23:59:59")
    LocalDateTime jobTimeTo,
    @Schema(description = "작업 상태", example = "SUCCESS")
    SyncResult status,
    @Schema(description = "이전 페이지 마지막 요소 ID", example = "10")
    Long idAfter,
    @Schema(description = "커서 (다음 페이지 시작점)", example = "2026-04-21T10:30:00")
    String cursor,
    @Schema(description = "정렬 필드", example = "jobTime", defaultValue = "jobTime")
    @Pattern(regexp = "^(targetDate|jobTime)$")
    String sortField,
    @Schema(description = "정렬 방향", example = "desc", defaultValue = "desc")
    @Pattern(regexp = "^(asc|desc)$")
    String sortDirection,
    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Positive
    Integer size
) {
}
