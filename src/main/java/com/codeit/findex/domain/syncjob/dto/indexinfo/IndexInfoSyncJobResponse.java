package com.codeit.findex.domain.syncjob.dto.indexinfo;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 서비스 -> 컨트롤러
public record IndexInfoSyncJobResponse(
    Long id,
    JobType jobType,
    Long indexInfoId,
    LocalDate targetDate,
    String worker,
    LocalDateTime jobTime,
    SyncResult result) {}
