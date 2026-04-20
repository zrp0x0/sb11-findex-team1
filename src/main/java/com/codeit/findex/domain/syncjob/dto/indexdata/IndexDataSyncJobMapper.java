package com.codeit.findex.domain.syncjob.dto.indexdata;

import com.codeit.findex.domain.syncjob.entity.SyncJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IndexDataSyncJobMapper {
  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  IndexDataSyncJobResponse toResponse(SyncJob syncJob);
}
