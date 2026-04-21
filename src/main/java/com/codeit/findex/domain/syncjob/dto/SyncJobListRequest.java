package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.syncjob.validation.ValidSyncJobDateRange;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValidSyncJobDateRange
public record SyncJobListRequest(
    JobType jobType,
    Long indexInfoId,
    LocalDate baseDateFrom,
    LocalDate baseDateTo,
    String worker,
    LocalDateTime jobTimeFrom,
    LocalDateTime jobTimeTo,
    SyncResult status,
    Long idAfter,
    String cursor,
    @Pattern(regexp = "^(targetDate|jobTime)$")
    String sortField,
    @Pattern(regexp = "^(asc|desc)$")
    String sortDirection,
    @Positive
    Integer size
) {

}
