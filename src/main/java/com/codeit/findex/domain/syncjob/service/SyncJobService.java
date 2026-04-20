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

  private final SyncJobRepository syncJobRepository;
  private final SyncJobMapper syncJobMapper;

  public SyncJobPageResponse getSyncJobs(SyncJobListRequest request) {
    Sort sort = createSort(request);
    Specification<SyncJob> specification = SyncJobSpecification.withConditions(request);

    Integer requestSize = request.size();
    int size = (requestSize == null || requestSize <= 0) ? 10 : requestSize;

    List<SyncJob> syncJobs = syncJobRepository.findAll(specification, sort);

    boolean hasNext = syncJobs.size() > size;

    List<SyncJob> pageItems = syncJobs.stream()
        .limit(size)
        .toList();

    List<SyncJobResponse> content = pageItems.stream()
        .map(syncJobMapper::toSyncJobResponse)
        .toList();

    String nextCursor = null;
    Long nextIdAfter = null;

    if (hasNext && !pageItems.isEmpty()) {
      SyncJob lastItem = pageItems.get(pageItems.size() - 1);
      nextIdAfter = lastItem.getId();

      if ("targetDate".equals(request.sortField())) {
        nextCursor = String.valueOf(lastItem.getTargetDate());
      } else {
        nextCursor = String.valueOf(lastItem.getJobTime());
      }
    }

    return new SyncJobPageResponse(
        content,
        nextCursor,
        nextIdAfter,
        content.size(),
        (long) syncJobs.size(),
        hasNext
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
