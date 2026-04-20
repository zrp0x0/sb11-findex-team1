package com.codeit.findex.domain.syncjob.validation;

import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SyncJobDateRangeValidator
    implements ConstraintValidator<ValidSyncJobDateRange, SyncJobListRequest> {

  @Override
  public boolean isValid(
      SyncJobListRequest request,
      ConstraintValidatorContext context // false일 때 기본 메시지 출력(globalError)
  ) {
    if (request == null) {
      return true;
    }

    if (request.baseDateFrom() != null
        && request.baseDateTo() != null
        && request.baseDateFrom().isAfter(request.baseDateTo())) {
      return false;
    }

    if (request.jobTimeFrom() != null
        && request.jobTimeTo() != null
        && request.jobTimeFrom().isAfter(request.jobTimeTo())) {
      return false;
    }

    return true;
  }
}
