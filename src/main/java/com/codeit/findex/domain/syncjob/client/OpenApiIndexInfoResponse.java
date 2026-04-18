package com.codeit.findex.domain.syncjob.client;

import java.math.BigDecimal;
import java.time.LocalDate;

// 외부 API -> 서비스
public record OpenApiIndexInfoResponse(
    String indexClassification,
    String indexName,
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex) {}
