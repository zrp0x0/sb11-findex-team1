package com.codeit.findex.domain.indexinfo.dto;

import java.util.List;

public record IndexInfoCursorResponse<T>(
        List<T> content,
        String nextCursor,
        Long nextIdAfter,
        Integer size,
        Long totalElements,
        boolean hasNext
) {}
