package com.codeit.findex.domain.autosyncconfig.dto;

public record AutoSyncConfigListRequest(
    Long indexInfoId,
    Boolean enabled,
    Long idAfter,
    String cursor,
    String sortField,
    String sortDirection,
    Integer size
) {
}
