package com.codeit.findex.domain.autosyncconfig.dto;

import java.util.List;

public record AutoSyncConfigPageResponse(
        List<AutoSyncConfigResponse> content,
        String nextCursor,
        Long nextIdAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}
