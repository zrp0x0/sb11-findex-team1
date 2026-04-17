package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

  private final SyncJobService syncJobService;

  @PostMapping("/index-infos")
  public ResponseEntity<List<IndexInfoSyncJobResponse>> syncIndexInfos() {
    return ResponseEntity.ok(syncJobService.syncIndexInfos("worker1"));
  }

}
