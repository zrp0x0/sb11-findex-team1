package com.codeit.findex.domain.syncjob.dto.indexinfo;

import com.codeit.findex.domain.syncjob.entity.SyncJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IndexInfoSyncJobMapper {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  IndexInfoSyncJobResponse toResponse(SyncJob syncJob);
}
