package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.request.IndexChartRequest;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.dto.request.IndexPerformanceRankRequest;
import com.codeit.findex.domain.indexdata.dto.response.IndexChartResponse;
import com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse;
import com.codeit.findex.domain.indexdata.dto.response.RankedIndexPerformanceResponse;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
public class IndexDataController {

  private final IndexDataService indexDataService;

  @Operation(
      summary = "지수 성과 랭킹 조회",
      description = "전일, 전주, 전월 대비 지수 성과를 등락률 기준 랭킹으로 조회합니다."
  )
  @GetMapping("/performance/rank")
  public ResponseEntity<List<RankedIndexPerformanceResponse>> getPerformanceRank(
      @Valid @ModelAttribute IndexPerformanceRankRequest request
  ) {
    return ResponseEntity.ok(indexDataService.getPerformanceRank(request));
  }

  // 비어있을 경우도 200 + []
  @GetMapping("/performance/favorite")
  public ResponseEntity<List<IndexDataFavoriteResponse>> getFavoritePerformances() {
    return ResponseEntity.ok(indexDataService.getFavoritePerformances());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<IndexDataResponse> update(
      @PathVariable Long id,
      @RequestBody IndexDataUpdateRequest request
  ) {
    IndexDataResponse response = indexDataService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "지수 차트 조회",
      description = "지수의 차트 데이터를 조회합니다."
  )
  @GetMapping("/{id}/chart")
  public ResponseEntity<IndexChartResponse> getChart(
      @Parameter(description = "지수 정보 ID")
      @PathVariable Long id,
      @Valid @ModelAttribute IndexChartRequest request
  ) {
    return ResponseEntity.ok(indexDataService.getChart(id, request.periodType()));
  }
}
