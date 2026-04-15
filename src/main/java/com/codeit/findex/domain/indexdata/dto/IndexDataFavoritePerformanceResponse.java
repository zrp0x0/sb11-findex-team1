package com.codeit.findex.domain.indexdata.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataFavoritePerformanceResponse(
    // index_info
    Long id,
    String indexClassification,
    String indexName,

    // index_data -
    LocalDate baseDate,
    BigDecimal closingPrice,
    BigDecimal versus,
    BigDecimal fluctuationRate
) {
}
