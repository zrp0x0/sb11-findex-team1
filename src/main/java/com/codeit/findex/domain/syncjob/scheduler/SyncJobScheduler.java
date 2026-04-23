package com.codeit.findex.domain.syncjob.scheduler;

import com.codeit.findex.domain.syncjob.service.SyncJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncJobScheduler {

  private final SyncJobService syncJobService;

  // 배치실행
  @Scheduled(cron = "${findex.sync-job.auto-sync.cron}")
  public void runAutoSyncIndexDataBatch() {
    syncJobService.runAutoSyncIndexDataBatch();
  }
}
