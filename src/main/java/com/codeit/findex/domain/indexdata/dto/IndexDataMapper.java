package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.dto.response.IndexPerformanceResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;

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

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "indexInfo", source = "indexInfo")
  @Mapping(target = "sourceType", source = "request.sourceType")
// toEntity -> toIndexData로 이름을 더 명확하게 변경했습니다.
  IndexData toIndexData(IndexDataCreateRequest request, IndexInfo indexInfo);


  @Mapping(source = "currentData.indexInfo.id", target = "indexInfoId")
  @Mapping(source = "currentData.indexInfo.indexClassification", target = "indexClassification")
  @Mapping(source = "currentData.indexInfo.indexName", target = "indexName")
// 매개변수로 받은 나머지 값들을 결과 DTO의 필드에 매핑
  IndexPerformanceResponse toIndexPerformanceResponse(
          IndexData currentData,
          BigDecimal versus,
          BigDecimal fluctuationRate,
          BigDecimal currentPrice,
          BigDecimal beforePrice
  );
}
