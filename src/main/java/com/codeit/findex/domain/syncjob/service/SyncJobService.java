package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import com.codeit.findex.domain.syncjob.dto.SyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.SyncJobPageResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobResponse;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // 동적쿼리, 필터 조건으로 조회 및 정렬
    List<SyncJob> syncJobs = syncJobRepository.findAll(specification, sort);

    // 커서 뒤에 있는 데이터만 남김
    Integer requestSize = request.size();
    int size = (requestSize == null || requestSize <= 0) ? 10 : requestSize;

    List<SyncJob> filteredItems = syncJobs.stream()
        .filter(syncJob -> isAfterCursor(syncJob, request))
        .toList();
    // 현재 페이지 생성
    boolean hasNext = filteredItems.size() > size;

    List<SyncJob> pageItems = filteredItems.stream()
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

    System.out.println("size param = " + request.size());
    System.out.println("filteredItems size = " + filteredItems.size());
    System.out.println("pageItems size = " + pageItems.size());

    return new SyncJobPageResponse(
        content,
        nextCursor,
        nextIdAfter,
        content.size(),
        (long) syncJobs.size(),
        hasNext
    );
  }

  private boolean isAfterCursor(SyncJob syncJob, SyncJobListRequest request) {
    String cursor = request.cursor();
    Long idAfter = request.idAfter();
    String sortDirection = request.sortDirection();

    if (cursor == null || idAfter == null) {
      return true;
    }

    int compare;

    if ("targetDate".equals(request.sortField())) {
      LocalDate cursorDate = LocalDate.parse(cursor);
      compare = syncJob.getTargetDate().compareTo(cursorDate);
    } else {
      LocalDateTime cursorDateTime = LocalDateTime.parse(cursor);
      compare = syncJob.getJobTime().compareTo(cursorDateTime);
    }

    if ("desc".equalsIgnoreCase(sortDirection)) {
      return compare < 0 || (compare == 0 && syncJob.getId() > idAfter);
    }

    return compare > 0 || (compare == 0 && syncJob.getId() > idAfter);
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
