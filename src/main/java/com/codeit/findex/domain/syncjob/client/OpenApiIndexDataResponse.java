package com.codeit.findex.domain.syncjob.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OpenApiIndexDataResponse(
    // 지수 분류명
    String indexClassification,
    // 지수명
    String indexName,
    // 기준일(해당 지수 데이터의 날짜)
    LocalDate baseDate,
    // 시가
    BigDecimal marketPrice,
    // 종가
    BigDecimal closingPrice,
    // 고가
    BigDecimal highPrice,
    // 저가
    BigDecimal lowPrice,
    // 증감액(전일)
    BigDecimal versus,
    // 등락률
    BigDecimal fluctuationRate,
    // 거래량
    Long tradingQuantity,
    // 거래대금
    Long tradingPrice,
    // 시가총액
    Long marketTotalAmount) {}
