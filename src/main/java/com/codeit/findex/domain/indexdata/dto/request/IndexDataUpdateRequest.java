package com.codeit.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record IndexDataUpdateRequest(
    @PositiveOrZero(message = "시가는 0 이상이어야 합니다.") BigDecimal marketPrice,
    @PositiveOrZero(message = "종가는 0 이상이어야 합니다.") BigDecimal closingPrice,
    @PositiveOrZero(message = "고가는 0 이상이어야 합니다.") BigDecimal highPrice,
    @PositiveOrZero(message = "저가는 0 이상이어야 합니다.") BigDecimal lowPrice,
    BigDecimal versus,            // 전일 대비 증감액
    BigDecimal fluctuationRate,   // 등락률 변동률
    @PositiveOrZero(message = "거래량은는 0 이상이어야 합니다.") Long tradingQuantity,
    @PositiveOrZero(message = "거래대금은 0 이상이어야 합니다.") Long tradingPrice,
    @PositiveOrZero(message = "시가총액은 0 이상이어야 합니다.") Long marketTotalAmount
) {

}