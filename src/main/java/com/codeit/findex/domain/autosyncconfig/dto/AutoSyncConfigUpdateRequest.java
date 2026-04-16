package com.codeit.findex.domain.autosyncconfig.dto;

import jakarta.validation.constraints.NotNull;

public record AutoSyncConfigUpdateRequest(
    @NotNull
    Boolean enabled
) {

}
