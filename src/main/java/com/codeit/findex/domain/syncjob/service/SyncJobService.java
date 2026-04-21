package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SourceType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.client.IndexInfoOpenApiClient;
import com.codeit.findex.domain.syncjob.client.OpenApiIndexInfoResponse;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import com.codeit.findex.domain.syncjob.dto.SyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.SyncJobPageResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobResponse;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SyncJobService {

  // 날짜 입력이 없는경우 / 휴일 대비
  private static final int MAX_LOOKBACK_DAYS = 30;

  private final IndexInfoOpenApiClient indexInfoOpenApiClient;
  private final IndexInfoSyncJobMapper indexInfoSyncJobMapper;
  private final SyncJobRepository syncJobRepository;
  private final IndexInfoRepository indexInfoRepository;
  private final SyncJobMapper syncJobMapper;

  @Transactional
  public List<IndexInfoSyncJobResponse> syncIndexInfos(String worker) {
    String resolvedWorker = (worker == null || worker.isBlank()) ? "system" : worker;
    List<OpenApiIndexInfoResponse> openApiRows = fetchLatestAvailableRows();
    List<IndexInfoSyncJobResponse> syncJobResponses = new ArrayList<>();

    for (OpenApiIndexInfoResponse row : openApiRows) {
      SyncJob savedJob = saveSyncJob(row, resolvedWorker);
      syncJobResponses.add(indexInfoSyncJobMapper.toResponse(savedJob));
    }
    return syncJobResponses;
  }

  // client를 호출하여 외부 API 데이터를 받아옴 / 최신(처음으로 존재) 유효 날짜 데이터 반환
  private List<OpenApiIndexInfoResponse> fetchLatestAvailableRows() {
    LocalDate baseDate = LocalDate.now();

    for (int i = 0; i < MAX_LOOKBACK_DAYS; i++) {
      LocalDate targetDate = baseDate.minusDays(i);

      try {
        List<OpenApiIndexInfoResponse> rows = indexInfoOpenApiClient.fetchIndexInfos(targetDate);
        if (!rows.isEmpty()) {
          return rows;
        }
      } catch (RuntimeException e) {
        throw new IllegalStateException("지수 정보 연동 호출 실패: " + e.getMessage(), e);
      }
    }

    try {
      return indexInfoOpenApiClient.fetchIndexInfos(null);
    } catch (RuntimeException e) {
      throw new IllegalStateException("지수 정보 연동 호출 실패: " + e.getMessage(), e);
    }
  }

  private SyncJob saveSyncJob(OpenApiIndexInfoResponse row, String worker) {
    try {
      // null 체크
      validateRow(row);

      IndexInfo indexInfo = upsertIndexInfo(row);

      SyncJob successJob =
          SyncJob.create(
              null,
              JobType.INDEX_INFO,
              indexInfo,
              row.basePointInTime(),
              worker,
              LocalDateTime.now(),
              SyncResult.SUCCESS);
      return syncJobRepository.save(successJob);
    } catch (RuntimeException e) {
      SyncJob failedJob =
          SyncJob.create(
              null,
              JobType.INDEX_INFO,
              null,
              row.basePointInTime(),
              worker,
              LocalDateTime.now(),
              SyncResult.FAILED);
      return syncJobRepository.save(failedJob);
    }
  }

  private void validateRow(OpenApiIndexInfoResponse row) {
    if (row.employedItemsCount() == null) {
      throw new IllegalStateException("채용 종목 수가 없습니다.");
    }
    if (row.basePointInTime() == null) {
      throw new IllegalStateException("기준 시점이 없습니다.");
    }
    if (row.baseIndex() == null) {
      throw new IllegalStateException("기준 지수 값이 없습니다.");
    }
  }

  private IndexInfo upsertIndexInfo(OpenApiIndexInfoResponse row) {
    return indexInfoRepository
        .findByIndexClassificationAndIndexName(row.indexClassification(), row.indexName())
        .map(
            existing -> {
              existing.update(
                  row.employedItemsCount(),
                  row.basePointInTime(),
                  row.baseIndex(),
                  existing.getFavorite());
              return existing;
            })
        .orElseGet(
            () ->
                indexInfoRepository.save(
                    IndexInfo.create(
                        row.indexClassification(),
                        row.indexName(),
                        row.employedItemsCount(),
                        row.basePointInTime(),
                        row.baseIndex(),
                        SourceType.OPEN_API,
                        false)));
  }

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
