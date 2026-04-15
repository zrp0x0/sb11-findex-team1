package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.IndexDataFavoritePerformanceResponse;
import com.codeit.findex.domain.indexdata.service.IndexDataFavoritePerformanceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-data/performance")
@RequiredArgsConstructor
public class IndexDataFavoritePerformanceController {

  private final IndexDataFavoritePerformanceService indexDataFavoritePerformanceService;

  @GetMapping("/favorite")
  public List<IndexDataFavoritePerformanceResponse> getFavoritePerformances() {
    return indexDataFavoritePerformanceService.getFavoritePerformances();
  }
}
