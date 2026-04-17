package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IndexDataMapper {

  @Mapping(source = "indexInfo.id", target = "indexId")
  @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
  @Mapping(source = "indexInfo.indexName", target = "indexName")
  IndexDataFavoriteResponse toIndexDataFavoriteResponse(IndexData indexData);

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse toResponse(IndexData indexData);
}
