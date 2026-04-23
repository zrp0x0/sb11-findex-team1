package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.dto.SyncJobListRequest;
import com.codeit.findex.domain.syncjob.dto.SyncJobPageResponse;
import com.codeit.findex.domain.syncjob.dto.indexdata.IndexDataSyncJobResponse;
import com.codeit.findex.domain.syncjob.dto.indexdata.SyncIndexDataRequest;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import com.codeit.findex.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
          @ApiResponse(
              responseCode = "200",
              description = "연동 작업 생성 성공",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = IndexInfoSyncJobResponse.class))),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(
              responseCode = "500",
              description = "서버 오류",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class)))
      })
  @PostMapping("/index-infos")
  public ResponseEntity<List<IndexInfoSyncJobResponse>> syncIndexInfos(
      @Parameter(hidden = true) HttpServletRequest request) {
    String worker = request.getRemoteAddr();
    List<IndexInfoSyncJobResponse> response = syncJobService.syncIndexInfos(worker);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @Operation(
      summary = "지수 데이터 연동",
      description = "Open API를 통해 지수 데이터를 연동합니다.",
      operationId = "syncIndexData")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "연동 작업 생성 성공",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = IndexDataSyncJobResponse.class))),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(
              responseCode = "404",
              description = "지수 정보를 찾을 수 없음",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(
              responseCode = "500",
              description = "서버 내부 오류",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class)))
      })
  @PostMapping("/index-data")
  public ResponseEntity<List<IndexDataSyncJobResponse>> syncIndexData(
      @Valid @RequestBody SyncIndexDataRequest request,
      @Parameter(hidden = true) HttpServletRequest httpRequest) {
    String worker = httpRequest.getRemoteAddr();
    List<IndexDataSyncJobResponse> response =
        syncJobService.syncIndexData(
            request.indexInfoIds(), request.baseDateFrom(), request.baseDateTo(), worker);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @Operation(
      summary = "연동 작업 목록 조회",
      description = "연동 작업 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
      operationId = "getSyncJobList")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "연동 작업 목록 조회 성공",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = SyncJobPageResponse.class))),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청 (유효하지 않은 필터 값 등)",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(
              responseCode = "500",
              description = "서버 오류",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = ErrorResponse.class)))
      })
  @GetMapping
  public ResponseEntity<SyncJobPageResponse> getSyncjobs(
      @ParameterObject @Valid SyncJobListRequest request) {
    SyncJobPageResponse response = syncJobService.getSyncJobs(request);
    return ResponseEntity.ok(response);
  }
}
