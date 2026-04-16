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
  IndexDataFavoriteResponse toIndexDataFavoriteResponse(
      IndexData latest,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      BigDecimal currentPrice,
      BigDecimal beforePrice);
}
