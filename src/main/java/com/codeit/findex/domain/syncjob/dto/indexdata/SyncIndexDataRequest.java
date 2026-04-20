package com.codeit.findex.domain.syncjob.dto.indexdata;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record SyncIndexDataRequest(
    List<Long> indexInfoIds,
    @NotNull LocalDate baseDateFrom,
    @NotNull LocalDate baseDateTo
) {}
