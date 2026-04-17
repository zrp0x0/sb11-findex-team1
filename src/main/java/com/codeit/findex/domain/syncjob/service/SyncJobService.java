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
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SyncJobService {

  private final IndexInfoOpenApiClient indexInfoOpenApiClient;
  private final IndexInfoSyncJobMapper indexInfoSyncJobMapper;
  private final SyncJobRepository syncJobRepository;
  private final IndexInfoRepository indexInfoRepository;

  @Transactional
  public List<IndexInfoSyncJobResponse> syncIndexInfos(String worker) {
    String resolvedWorker = (worker == null || worker.isBlank()) ? "system" : worker;
    LocalDate baseDate = LocalDate.now();

    List<OpenApiIndexInfoResponse> openApiRows;
    try {
      openApiRows = indexInfoOpenApiClient.fetchIndexInfos(baseDate);
    } catch (RuntimeException e) {
      throw new IllegalStateException("지수 정보 연동 호출 실패", e);
    }

    List<IndexInfoSyncJobResponse> syncJobResponses = new ArrayList<>();

    for (OpenApiIndexInfoResponse row : openApiRows) {
      SyncJob savedJob = saveSyncJob(row, resolvedWorker);
      syncJobResponses.add(indexInfoSyncJobMapper.toResponse(savedJob));
    }
    return syncJobResponses;
  }

  // 성공, 실패 체크
  private SyncJob saveSyncJob(OpenApiIndexInfoResponse row, String worker) {
    try {
      // DB에 반영(upsert)
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

  private IndexInfo upsertIndexInfo(OpenApiIndexInfoResponse row) {
    return indexInfoRepository
        .findByIndexClassificationAndIndexName(row.indexClassification(), row.indexName())
        // 값이 있을 경우
        .map(
            existing -> {
              existing.update(
                  row.employedItemsCount(),
                  row.basePointInTime(),
                  row.baseIndex(),
                  existing.getFavorite());
              return existing;
            })
        // 값이 없을 경우
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
}
