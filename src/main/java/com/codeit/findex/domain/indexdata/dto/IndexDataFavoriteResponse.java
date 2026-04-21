package com.codeit.findex.domain.indexdata.dto;

import java.math.BigDecimal;

public record IndexDataFavoriteResponse(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    BigDecimal versus,
    BigDecimal fluctuationRate,
    BigDecimal currentPrice,
    BigDecimal beforePrice) {}
