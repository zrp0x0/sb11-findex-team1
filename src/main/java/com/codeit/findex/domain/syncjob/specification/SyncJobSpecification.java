package com.codeit.findex.domain.syncjob.specification;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class SyncJobSpecification {

  private SyncJobSpecification() {
  }

  //필드별 조건을 메서드들로 쪼개서 마지막에 조합
  public static Specification<SyncJob> withConditions(SyncJobListRequest request) {
    return Specification.where(hasJobType(request.jobType()))
        .and(hasIndexInfoId(request.indexInfoId()))
        .and(targetDateFrom(request.baseDateFrom()))
        .and(targetDateTo(request.baseDateTo()))
        .and(hasWorker(request.worker()))
        .and(jobTimeFrom(request.jobTimeFrom()))
        .and(jobTimeTo(request.jobTimeTo()))
        .and(hasResult(request.status()));
  }

  public static Specification<SyncJob> hasJobType(JobType jobType) {
    return (root, query, criteriaBuilder) ->
        jobType == null ? null : criteriaBuilder.equal(root.get("jobType"), jobType);
  }

  public static Specification<SyncJob> hasIndexInfoId(Long indexInfoId) {
    return (root, query, criteriaBuilder) ->
        indexInfoId ==
            null ? null : criteriaBuilder.equal(root.get("indexInfo").get("id"), indexInfoId);
  }

  public static Specification<SyncJob> targetDateFrom(LocalDate baseDateFrom) {
    return (root, query, criteriaBuilder) ->
        baseDateFrom ==
            null ? null
            : criteriaBuilder.greaterThanOrEqualTo(root.get("targetDate"), baseDateFrom);
  }

  public static Specification<SyncJob> targetDateTo(LocalDate baseDateTo) {
    return (root, query, criteriaBuilder) ->
        baseDateTo ==
            null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("targetDate"), baseDateTo);
  }

  // 문자열에 공백 뿐인 경우에도 조건 추가 X (null and blank)
  public static Specification<SyncJob> hasWorker(String worker) {
    return (root, query, criteriaBuilder) ->
        worker ==
            null || worker.isBlank() ? null : criteriaBuilder.equal(root.get("worker"), worker);
  }

  public static Specification<SyncJob> jobTimeFrom(LocalDateTime jobTimeFrom) {
    return (root, query, criteriaBuilder) ->
        jobTimeFrom ==
            null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("jobTime"), jobTimeFrom);
  }

  public static Specification<SyncJob> jobTimeTo(LocalDateTime jobTimeTo) {
    return (root, query, criteriaBuilder) ->
        jobTimeTo ==
            null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("jobTime"), jobTimeTo);
  }

  public static Specification<SyncJob> hasResult(SyncResult result) {
    return (root, query, criteriaBuilder) ->
        result == null ? null : criteriaBuilder.equal(root.get("result"), result);
  }
}
