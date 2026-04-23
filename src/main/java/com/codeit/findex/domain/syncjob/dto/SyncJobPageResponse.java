package com.codeit.findex.domain.syncjob.dto;

import java.util.List;

public record SyncJobPageResponse(
    List<SyncJobResponse> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) {

}
