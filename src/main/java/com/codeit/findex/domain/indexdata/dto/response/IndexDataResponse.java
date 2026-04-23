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
) {}
