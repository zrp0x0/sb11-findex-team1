package com.codeit.findex.domain.indexdata.dto.request;


import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataCreateRequest(
        @NotNull(message = "지수 정보 ID는 필수입니다.")
        Long indexInfoId,

        @NotNull(message = "기준일자는 필수입니다.")
        LocalDate baseDate,

        @NotNull(message = "소스타입이 비워져있습니다.")
        String sourceType,

        BigDecimal marketPrice,
        BigDecimal closingPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        Long tradingQuantity,
        BigDecimal tradingPrice,
        BigDecimal marketTotalAmount
) {
}