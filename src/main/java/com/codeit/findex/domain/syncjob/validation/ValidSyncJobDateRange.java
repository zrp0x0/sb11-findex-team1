package com.codeit.findex.domain.syncjob.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 클래스 위에 어노테이션을 붙여 두 가지 검증을 진행
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SyncJobDateRangeValidator.class)
public @interface ValidSyncJobDateRange {

  String message() default "날짜 범위가 올바르지 않습니다."; // 기본 메시지

  Class<?>[] groups() default {}; // 표준 형식, 사용 X

  Class<? extends Payload>[] payload() default {}; // 표준 형식,  사용X
}
