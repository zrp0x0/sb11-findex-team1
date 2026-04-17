package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.syncjob.entity.SyncJob;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SyncJobMapper {

  @Mapping(target = "indexInfoId", source = "indexInfo.id")
  SyncJobResponse toSyncJobResponse(SyncJob syncJob);

  List<SyncJobResponse> toSyncJobResponseList(List<SyncJob> syncJobList);


}
