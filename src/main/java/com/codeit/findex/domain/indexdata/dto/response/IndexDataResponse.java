package com.codeit.findex.domain.indexdata.dto.response;

import com.codeit.findex.domain.common.enums.SourceType;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataResponse(
    Long id,
    Long indexInfoId,
    LocalDate baseDate,
    SourceType sourceType,
    BigDecimal marketPrice,
    BigDecimal closingPrice,
    BigDecimal highPrice,
    BigDecimal lowPrice,
    BigDecimal versus,
    BigDecimal fluctuationRate,
    Long tradingQuantity,
    Long tradingPrice,
    Long marketTotalAmount
) {

  public static IndexDataResponse from(IndexData indexData) {
    return new IndexDataResponse(
        indexData.getId(),
        indexData.getIndexInfo().getId(),
        indexData.getBaseDate(),
        indexData.getSourceType(),
        indexData.getMarketPrice(),
        indexData.getClosingPrice(),
        indexData.getHighPrice(),
        indexData.getLowPrice(),
        indexData.getVersus(),
        indexData.getFluctuationRate(),
        indexData.getTradingQuantity(),
        indexData.getTradingPrice(),
        indexData.getMarketTotalAmount()
    );
  }
}
