package com.codeit.findex.domain.indexinfo.dto;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

// ReportingPolicy.IGNORE: DTO에 필드가 없으면 무시
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IndexInfoMapper {

  IndexInfoCreateResponse toIndexInfoCreateResponse(IndexInfo indexInfo);
}
