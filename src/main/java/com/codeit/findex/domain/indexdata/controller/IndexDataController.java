package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.common.enums.PerformancePeriodType;
import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.IndexDataSearchCondition;
import com.codeit.findex.domain.indexdata.dto.request.IndexChartRequest;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.dto.request.IndexPerformanceRankRequest;
import com.codeit.findex.domain.indexdata.dto.response.IndexChartResponse;
import com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse;
import com.codeit.findex.domain.indexdata.dto.response.RankedIndexPerformanceResponse;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/index-data") // 복수형 사용하도록 수정
@RequiredArgsConstructor
@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public class IndexDataController {

  private final IndexDataService indexDataService;

  @Operation(
      summary = "지수 데이터 목록 조회",
      description = "특정 지수의 시계열 데이터를 조회합니다. 기간 필터링, 정렬 및 커서 기반 페이지네이션을 지원합니다.",
      operationId = "getIndexDatas")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "지수 데이터 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
      })
  @GetMapping
  public ResponseEntity<IndexInfoCursorResponse<IndexDataResponse>> getIndexDatas(
      @Valid @ModelAttribute IndexDataSearchCondition condition) {
    IndexInfoCursorResponse<IndexDataResponse> response = indexDataService.getIndexDatas(condition);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "지수 성과 랭킹 조회", description = "전일, 전주, 전월 대비 지수 성과를 등락률 기준 랭킹으로 조회합니다.")
  @GetMapping("/performance/rank")
  public ResponseEntity<List<RankedIndexPerformanceResponse>> getPerformanceRank(
      @Valid @ModelAttribute IndexPerformanceRankRequest request) {
    return ResponseEntity.ok(indexDataService.getPerformanceRank(request));
  }

  @Operation(
      summary = "관심 지수 성과 조회",
      description = "즐겨찾기로 등록된 지수들의 성과를 조회합니다.",
      operationId = "getFavoriteIndexPerformances")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "관심 지수 성과 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
      })
  @GetMapping("/performance/favorite")
  public ResponseEntity<List<IndexDataFavoriteResponse>> getFavoritePerformances(
      @Parameter(
              description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)",
              schema =
                  @Schema(
                      allowableValues = {"DAILY", "WEEKLY", "MONTHLY"},
                      defaultValue = "DAILY"))
          @RequestParam(name = "periodType", defaultValue = "DAILY")
          PerformancePeriodType periodType) {
    return ResponseEntity.ok(indexDataService.getFavoritePerformances(periodType));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<IndexDataResponse> update(
      @PathVariable Long id, @Valid @RequestBody IndexDataUpdateRequest request) {
    IndexDataResponse response = indexDataService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "지수 차트 조회", description = "지수의 차트 데이터를 조회합니다.")
  @GetMapping("/{id}/chart")
  public ResponseEntity<IndexChartResponse> getChart(
      @Parameter(description = "지수 정보 ID") @PathVariable Long id,
      @Valid @ModelAttribute IndexChartRequest request) {
    return ResponseEntity.ok(indexDataService.getChart(id, request.periodType()));
  }



  @Operation(
          summary = "지수 데이터 등록",
          description = "새로운 지수 데이터를 등록합니다."
  )
  @PostMapping
  public ResponseEntity<IndexDataResponse> create(
          @Valid @RequestBody IndexDataCreateRequest request
  ) {
    IndexDataResponse response = indexDataService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
          summary = "지수 데이터 삭제",
          description = "특정 지수 데이터를 삭제합니다."
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
          @Parameter(description = "지수 데이터 ID") @PathVariable Long id
  ) {
    indexDataService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
