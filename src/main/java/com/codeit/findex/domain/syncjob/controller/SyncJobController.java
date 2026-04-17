package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import com.codeit.findex.domain.syncjob.dto.SyncJobPageResponse;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "연동 작업 API", description = "연동 작업 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class SyncJobController {

  private final SyncJobService syncJobService;

  @Operation(summary = "연동 작업 목록 조회",
      description = "연동 작업 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
      operationId = "getSyncJobList")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "자동 연동 설정 목록 조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @GetMapping
  public ResponseEntity<SyncJobPageResponse> getSyncjobs(
      @Valid SyncJobListRequest request) {
    SyncJobPageResponse response = syncJobService.getSyncJobs(request);
    return ResponseEntity.ok(response);
  }
}
