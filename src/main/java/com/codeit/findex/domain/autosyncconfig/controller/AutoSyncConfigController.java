package com.codeit.findex.domain.autosyncconfig.controller;

import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigListRequest;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigPageResponse;
import com.codeit.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

  private final AutoSyncConfigService autoSyncConfigService;

  public AutoSyncConfigController(AutoSyncConfigService autoSyncConfigService) {
    this.autoSyncConfigService = autoSyncConfigService;
  }

  @GetMapping
  public AutoSyncConfigPageResponse getAutoSyncConfigs(AutoSyncConfigListRequest request) {
    return autoSyncConfigService.getAutoSyncConfigs(request);
  }
}
