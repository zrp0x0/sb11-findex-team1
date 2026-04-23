package com.codeit.findex.domain.syncjob.dto.indexdata;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IndexDataSyncJobResponse(
    Long id,
    JobType jobType,
    Long indexInfoId,
    LocalDate targetDate,
    String worker,
    LocalDateTime jobTime,
    SyncResult result) {}
