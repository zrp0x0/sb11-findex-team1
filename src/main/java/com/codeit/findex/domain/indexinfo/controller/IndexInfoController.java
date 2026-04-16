package com.codeit.findex.domain.indexinfo.controller;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지수 정보 API", description = "지수 정보 관리 API")
@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {

  private final IndexInfoService indexInfoService;

  @PostMapping
  public ResponseEntity<IndexInfoResponse> createIndexInfo(
      @Valid @RequestBody IndexInfoCreateRequest request) {
    IndexInfoResponse response = indexInfoService.createIndexInfo(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "지수 정보 수정", description = "기존 지수 정보를 수정합니다.", operationId = "updateIndexInfo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필드 값 등)"),
      @ApiResponse(responseCode = "404", description = "수정할 지수 정보를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류"),
      @ApiResponse(responseCode = "200", description = "지수 정보 수정 성공")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<IndexInfoResponse> updateIndexInfo(@PathVariable Long id,
      @Valid @RequestBody IndexInfoUpdateRequest request) {
    IndexInfoResponse response = indexInfoService.updateIndexInfo(id, request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "지수 정보 삭제", description = "지수 정보를 삭제합니다. 관련된 지수 데이터도 함께 삭제됩니다.", operationId = "deleteIndexInfo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "삭제할 지수 정보를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류"),
      @ApiResponse(responseCode = "204", description = "지수 정보 삭제 성공")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteIndexInfo(@PathVariable Long id) {
    indexInfoService.deleteIndexInfo(id);
    return ResponseEntity.noContent().build();
  }
}
