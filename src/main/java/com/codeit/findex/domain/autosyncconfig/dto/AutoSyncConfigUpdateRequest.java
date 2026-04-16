package com.codeit.findex.domain.autosyncconfig.dto;

import jakarta.validation.constraints.NotNull;

public record AutoSyncConfigUpdateRequest(
    @NotNull(message = "활성화 여부는 필수입니다.")
    Boolean enabled
) {

}
