package com.codeit.findex.domain.autosyncconfig.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record AutoSyncConfigListRequest(
    Long indexInfoId,
    Boolean enabled,
    Long idAfter,
    String cursor,
    @Pattern(regexp = "^(indexInfo.indexName|enabled)$")
    String sortField,
    @Pattern(regexp = "^(asc|desc)$")
    String sortDirection,
    @Min(1)
    Integer size
) {

}
