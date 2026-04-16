package com.codeit.findex.domain.autosyncconfig.controller;

import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigListRequest;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigPageResponse;
import com.codeit.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

  private final AutoSyncConfigService autoSyncConfigService;

  @GetMapping
  public ResponseEntity<AutoSyncConfigPageResponse> getAutoSyncConfigs(
      @Valid AutoSyncConfigListRequest request) {
    AutoSyncConfigPageResponse response = autoSyncConfigService.getAutoSyncConfigs(request);
    return ResponseEntity.ok(response);
  }
}
