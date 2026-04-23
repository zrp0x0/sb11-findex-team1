package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncJobResponse(
    Long id,
    JobType jobType,
    Long indexInfoId,
    LocalDate targetDate,
    String worker,
    LocalDateTime jobTime,
    SyncResult result
) {

}
