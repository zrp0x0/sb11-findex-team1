package com.codeit.findex.domain.autosyncconfig.dto;

public record AutoSyncConfigResponse(
    Long id,
    Long indexInfoId,
    String indexClassification,
    String indexName,
    Boolean enabled
) {
}
