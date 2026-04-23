package com.codeit.findex.domain.indexdata.dto.response;

import com.codeit.findex.domain.common.enums.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataCreateResponse(
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
        BigDecimal tradingPrice,
        BigDecimal marketTotalAmount
) {}