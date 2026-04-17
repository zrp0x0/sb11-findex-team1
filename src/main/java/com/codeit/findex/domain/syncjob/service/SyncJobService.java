package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import com.codeit.findex.domain.syncjob.dto.SyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.SyncJobPageResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobResponse;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncJobService {

  private final SyncJobRepository syncjobRepository;
  private final SyncJobMapper syncJobMapper;

  public SyncJobPageResponse getSyncJobs(SyncJobListRequest request) {
    Sort sort = createSort(request);
    Specification<SyncJob> specification = SyncJobSpecification.withConditions(request);

    List<SyncJob> syncJobs = syncjobRepository.findAll(specification, sort);
    List<SyncJobResponse> content = syncJobs.stream()
        .map(syncJobMapper::toSyncJobResponse)
        .toList();

    return new SyncJobPageResponse(
        content,
        null,
        null,
        content.size(),
        (long) content.size(),
        false
    );
  }

  private Sort createSort(SyncJobListRequest request) {
    String sortField = request.sortField();
    String sortDirection = request.sortDirection();

    String sortBy = "jobTime";
    if ("targetDate".equals(sortField)) {
      sortBy = "targetDate";
    }

    Sort.Direction direction = Sort.Direction.DESC;
    if ("asc".equalsIgnoreCase(sortDirection)) {
      direction = Sort.Direction.ASC;
    }

    return Sort.by(direction, sortBy);
  }
}
