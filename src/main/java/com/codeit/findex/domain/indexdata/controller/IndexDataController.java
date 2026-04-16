package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
