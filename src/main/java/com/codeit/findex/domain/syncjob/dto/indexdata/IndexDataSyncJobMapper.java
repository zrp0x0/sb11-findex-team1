package com.codeit.findex.domain.syncjob.dto.indexdata;

import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import org.mapstruct.Mapping;

public interface IndexDataSyncJobMapper {
  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  IndexInfoSyncJobResponse toResponse(SyncJob syncJob);
}
