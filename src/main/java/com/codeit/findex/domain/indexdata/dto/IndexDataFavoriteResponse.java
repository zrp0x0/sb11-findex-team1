package com.codeit.findex.domain.indexdata.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataFavoriteResponse(
    // indexInfo
    Long indexId,
    String indexClassification,
    String indexName,

    // indexData
    LocalDate baseDate,
    BigDecimal closingPrice,
    BigDecimal versus,
    BigDecimal fluctuationRate
) {
}
