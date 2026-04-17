package com.codeit.findex.domain.syncjob.dto.indexinfo;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IndexInfoSyncJobResponse(
    Long id,
    JobType jobType,
    Long indexInfoId,
    LocalDate targetDate,
    String worker,
    LocalDateTime jobTime,
    SyncResult result) {}
