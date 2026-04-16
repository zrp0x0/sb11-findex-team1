package com.codeit.findex.domain.indexinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoUpdateRequest(
    @NotNull(message = "채용 종목 수는 필수입니다.")
    @PositiveOrZero(message = "채용 종목 수는 음수일 수 없습니다.")
    Integer employedItemsCount,

    @NotNull(message = "기준 시점은 필수입니다.")
    LocalDate basePointInTime,

    @NotNull(message = "기준 지수는 필수입니다.")
    BigDecimal baseIndex,

    // null이면 false로 지정됨
    Boolean favorite
) {

}
