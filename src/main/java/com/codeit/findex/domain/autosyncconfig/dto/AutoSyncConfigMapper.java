package com.codeit.findex.domain.autosyncconfig.dto;

import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AutoSyncConfigMapper {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
  @Mapping(source = "indexInfo.indexName", target = "indexName")
  AutoSyncConfigResponse toAutoSyncConfigResponse(AutoSyncConfig autoSyncConfig);
}
