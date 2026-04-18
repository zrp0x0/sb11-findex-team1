package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "연동 작업 API", description = "연동 작업 관리 API")
@RestController
@RequestMapping("/api/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

  private final SyncJobService syncJobService;

  @Operation(
      summary = "지수 정보 연동",
      description = "Open API를 통해 지수 정보를 연동합니다.",
      operationId = "syncIndexInfos")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "202", description = "연동 작업 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
      })
  @PostMapping("/index-infos")
  public ResponseEntity<List<IndexInfoSyncJobResponse>> syncIndexInfos(
      @Parameter(hidden = true) HttpServletRequest request) {
    String worker = request.getRemoteAddr();
    List<IndexInfoSyncJobResponse> response = syncJobService.syncIndexInfos(worker);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }
}
