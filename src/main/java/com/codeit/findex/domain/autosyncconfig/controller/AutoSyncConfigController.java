package com.codeit.findex.domain.autosyncconfig.controller;

import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigListRequest;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigPageResponse;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigResponse;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "자동 연동 설정 API", description = "자동 연동 설정 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

  private final AutoSyncConfigService autoSyncConfigService;

  @Operation(summary = "자동 연동 설정 목록 조회", description = "자동 연동 설정 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.", operationId = "getAutoSyncConfigList")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "자동 연동 설정 목록 조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @GetMapping
  public ResponseEntity<AutoSyncConfigPageResponse> getAutoSyncConfigs(
      @Valid AutoSyncConfigListRequest request) {
    AutoSyncConfigPageResponse response = autoSyncConfigService.getAutoSyncConfigs(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "자동 연동 설정 수정", description = "기존 자동 연동 설정을 수정합니다.", operationId = "updateAutoSyncConfig")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "자동 연동 설정 수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 설정 값 등)"),
      @ApiResponse(responseCode = "404", description = "수정할 자동 연동 설정을 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<AutoSyncConfigResponse> updateAutoSyncConfig(
      @PathVariable Long id,
      @RequestBody @Valid AutoSyncConfigUpdateRequest request
  ) {
    AutoSyncConfigResponse response =
        autoSyncConfigService.updateAutoSyncConfig(id, request);
    return ResponseEntity.ok(response);
  }
}
