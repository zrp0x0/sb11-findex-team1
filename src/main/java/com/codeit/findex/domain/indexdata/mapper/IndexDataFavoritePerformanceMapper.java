package com.codeit.findex.domain.indexdata.mapper;

import com.codeit.findex.domain.indexdata.dto.IndexDataFavoritePerformanceResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.stereotype.Component;

@Component
public class IndexDataFavoritePerformanceMapper {
  public IndexDataFavoritePerformanceResponse toDto(IndexData data) {
    return new IndexDataFavoritePerformanceResponse(
        data.getIndexInfo().getId(),
        data.getIndexInfo().getIndexClassification(),
        data.getIndexInfo().getIndexName(),
        data.getBaseDate(),
        data.getClosingPrice(),
        data.getVersus(),
        data.getFluctuationRate()
    );
  }
}
