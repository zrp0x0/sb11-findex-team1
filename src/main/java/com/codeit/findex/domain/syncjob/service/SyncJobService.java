package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SourceType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.client.IndexDataOpenApiClient;
import com.codeit.findex.domain.syncjob.client.IndexInfoOpenApiClient;
import com.codeit.findex.domain.syncjob.client.OpenApiIndexDataResponse;
import com.codeit.findex.domain.syncjob.client.OpenApiIndexInfoResponse;
import com.codeit.findex.domain.syncjob.dto.indexdata.IndexDataSyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.indexdata.IndexDataSyncJobResponse;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SyncJobService {

  private static final int MAX_LOOKBACK_DAYS = 30;

  private final IndexInfoOpenApiClient indexInfoOpenApiClient;
  private final IndexDataOpenApiClient indexDataOpenApiClient;

  private final IndexInfoSyncJobMapper indexInfoSyncJobMapper;
  private final IndexDataSyncJobMapper indexDataSyncJobMapper;

  private final SyncJobRepository syncJobRepository;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;

  @Transactional
  public List<IndexInfoSyncJobResponse> syncIndexInfos(String worker) {
    String resolvedWorker = (worker == null || worker.isBlank()) ? "system" : worker;
    List<OpenApiIndexInfoResponse> openApiRows = fetchLatestAvailableRows();
    List<IndexInfoSyncJobResponse> syncJobResponses = new ArrayList<>();

    for (OpenApiIndexInfoResponse row : openApiRows) {
      SyncJob savedJob = saveIndexInfosSyncJob(row, resolvedWorker);
      syncJobResponses.add(indexInfoSyncJobMapper.toResponse(savedJob));
    }
    return syncJobResponses;
  }

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

  private SyncJob saveIndexInfosSyncJob(OpenApiIndexInfoResponse row, String worker) {
    try {
      validateIndexInfosRow(row);

      IndexInfo indexInfo = upsertIndexInfos(row);

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

  private void validateIndexInfosRow(OpenApiIndexInfoResponse row) {
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

  private IndexInfo upsertIndexInfos(OpenApiIndexInfoResponse row) {
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

  @Transactional
  public List<IndexDataSyncJobResponse> syncIndexData(
      List<Long> indexInfoIds, LocalDate baseDateFrom, LocalDate baseDateTo, String worker) {
    validateIndexDataDate(baseDateFrom, baseDateTo);
    String resolvedWorker = (worker == null || worker.isBlank()) ? "system" : worker;
    List<IndexDataSyncJobResponse> syncJobResponses = new ArrayList<>();

    try {
      List<IndexInfo> targets = resolveTargetIndexes(indexInfoIds);

      if (targets.isEmpty()) {
        List<OpenApiIndexDataResponse> openApiRows =
            indexDataOpenApiClient.fetchIndexData(baseDateFrom, baseDateTo, null);

        for (OpenApiIndexDataResponse row : openApiRows) {
          SyncJob savedJob = saveIndexDataSyncJob(row, resolvedWorker);
          syncJobResponses.add(indexDataSyncJobMapper.toResponse(savedJob));
        }
        return syncJobResponses;
      }

      for (IndexInfo target : targets) {
        List<OpenApiIndexDataResponse> openApiRows =
            indexDataOpenApiClient.fetchIndexData(baseDateFrom, baseDateTo, target.getIndexName());

        for (OpenApiIndexDataResponse row : openApiRows) {
          if (!isSameIndex(row, target)) {
            continue;
          }
          SyncJob savedJob = saveIndexDataSyncJob(row, resolvedWorker);
          syncJobResponses.add(indexDataSyncJobMapper.toResponse(savedJob));
        }
      }

      return syncJobResponses;
    } catch (IllegalArgumentException | EntityNotFoundException e) { // 400, 404는 핸들러처리 > 그대로 전달
      throw e;
    } catch (RuntimeException e) {
      throw new IllegalStateException("지수 데이터 연동 호출 실패: " + e.getMessage(), e); // 나머지 500
    }
  }

  private List<IndexInfo> resolveTargetIndexes(List<Long> indexInfoIds) {
    if (indexInfoIds == null || indexInfoIds.isEmpty()) {
      return List.of();
    }

    Set<Long> requestedIds = new LinkedHashSet<>(indexInfoIds);
    List<IndexInfo> indexInfos = indexInfoRepository.findAllById(requestedIds);

    if (indexInfos.size() != requestedIds.size()) {
      Set<Long> foundIds = indexInfos.stream().map(IndexInfo::getId).collect(Collectors.toSet());
      requestedIds.removeAll(foundIds);
      throw new EntityNotFoundException("존재하지 않는 지수 ID가 있습니다: " + requestedIds);
    }

    return indexInfos;
  }

  private boolean isSameIndex(OpenApiIndexDataResponse row, IndexInfo target) {
    if (row == null) {
      return false;
    }
    return target.getIndexName().equals(row.indexName())
        && target.getIndexClassification().equals(row.indexClassification());
  }

  private SyncJob saveIndexDataSyncJob(OpenApiIndexDataResponse row, String worker) {
    LocalDate targetDate = (row == null) ? null : row.baseDate();

    try {
      validateIndexDataRow(row);

      IndexInfo indexInfo = findOrCreateIndexInfo(row);
      upsertIndexData(row, indexInfo);

      SyncJob successJob =
          SyncJob.create(
              null,
              JobType.INDEX_DATA,
              indexInfo,
              targetDate,
              worker,
              LocalDateTime.now(),
              SyncResult.SUCCESS);

      return syncJobRepository.save(successJob);
    } catch (RuntimeException e) {
      SyncJob failedJob =
          SyncJob.create(
              null,
              JobType.INDEX_DATA,
              null,
              targetDate,
              worker,
              LocalDateTime.now(),
              SyncResult.FAILED);

      return syncJobRepository.save(failedJob);
    }
  }

  private IndexData upsertIndexData(OpenApiIndexDataResponse row, IndexInfo indexInfo) {
    return indexDataRepository
        .findByIndexInfoAndBaseDate(indexInfo, row.baseDate())
        .map(
            existing -> {
              existing.update(
                  SourceType.OPEN_API,
                  row.marketPrice(),
                  row.closingPrice(),
                  row.highPrice(),
                  row.lowPrice(),
                  row.versus(),
                  row.fluctuationRate(),
                  row.tradingQuantity(),
                  row.tradingPrice(),
                  row.marketTotalAmount());
              return existing;
            })
        .orElseGet(
            () ->
                indexDataRepository.save(
                    IndexData.create(
                        indexInfo,
                        row.baseDate(),
                        SourceType.OPEN_API,
                        row.marketPrice(),
                        row.closingPrice(),
                        row.highPrice(),
                        row.lowPrice(),
                        row.versus(),
                        row.fluctuationRate(),
                        row.tradingQuantity(),
                        row.tradingPrice(),
                        row.marketTotalAmount())));
  }

  private IndexInfo findOrCreateIndexInfo(OpenApiIndexDataResponse row) {
    return indexInfoRepository
        .findByIndexClassificationAndIndexName(row.indexClassification(), row.indexName())
        .orElseGet(
            () ->
                indexInfoRepository.save(
                    IndexInfo.create(
                        row.indexClassification(),
                        row.indexName(),
                        null,
                        null,
                        null,
                        SourceType.OPEN_API,
                        false)));
  }

  private void validateIndexDataDate(LocalDate fromDate, LocalDate toDate) {
    if (fromDate == null || toDate == null) {
      throw new IllegalArgumentException("대상 날짜(fromDate, toDate)는 필수입니다.");
    }
    if (fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("fromDate는 toDate보다 이후일 수 없습니다.");
    }
  }

  private void validateIndexDataRow(OpenApiIndexDataResponse row) {
    if (row == null) {
      throw new IllegalStateException("연동 데이터가 없습니다.");
    }
    if (row.indexClassification() == null || row.indexClassification().isBlank()) {
      throw new IllegalStateException("지수 분류명이 없습니다.");
    }
    if (row.indexName() == null || row.indexName().isBlank()) {
      throw new IllegalStateException("지수명이 없습니다.");
    }
    if (row.baseDate() == null) {
      throw new IllegalStateException("기준일이 없습니다.");
    }
  }
}
