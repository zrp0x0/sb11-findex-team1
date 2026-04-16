package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.common.enums.PerformancePeriodType;
import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public class IndexDataController {

  private final IndexDataService indexDataService;

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
      @PathVariable Long id,
      @RequestBody IndexDataUpdateRequest request
  ) {
    IndexDataResponse response = indexDataService.update(id, request);
    return ResponseEntity.ok(response);
  }
}
