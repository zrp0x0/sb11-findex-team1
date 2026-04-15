package com.codeit.findex.domain.indexdata.dto.request;

import com.codeit.findex.domain.common.enums.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataUpdateRequest (
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

}