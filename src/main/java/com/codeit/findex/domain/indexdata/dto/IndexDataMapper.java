package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IndexDataMapper {

  @Mapping(source = "latest.indexInfo.id", target = "indexInfoId")
  @Mapping(source = "latest.indexInfo.indexClassification", target = "indexClassification")
  @Mapping(source = "latest.indexInfo.indexName", target = "indexName")
  @Mapping(source = "versus", target = "versus")
  @Mapping(source = "fluctuationRate", target = "fluctuationRate")
  @Mapping(source = "currentPrice", target = "currentPrice")
  @Mapping(source = "beforePrice", target = "beforePrice")

  IndexDataFavoriteResponse toIndexDataFavoriteResponse(
      IndexData latest,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      BigDecimal currentPrice,
      BigDecimal beforePrice);

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse toResponse(IndexData indexData);
}
