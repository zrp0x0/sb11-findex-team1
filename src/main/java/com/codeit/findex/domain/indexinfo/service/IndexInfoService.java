package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.*;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoMapper;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexInfoService {

  private final IndexInfoRepository indexInfoRepository;
  private final IndexInfoMapper indexInfoMapper;

  public IndexInfoCursorResponse<IndexInfoResponse> getIndexInfos(IndexInfoSearchCondition condition) {

    // 1. 데이터 목록 조회 (limit + 1)
    List<IndexInfo> entities = indexInfoRepository.findAllByCondition(condition);

    // 2. 전체 개수 카운트
    long totalElements = indexInfoRepository.countByCondition(condition);

    // 3. 다음 페이지 여부 계산
    boolean hasNext = entities.size() > condition.getSize();
    if (hasNext) {
      entities.remove(condition.getSize().intValue());
    }

    // 4. 다음 페이지 요청용 커서 값 추출
    String nextCursor = null;
    Long nextIdAfter = null;
    if (hasNext && !entities.isEmpty()) {
      IndexInfo lastItem = entities.get(entities.size() - 1);
      nextIdAfter = lastItem.getId();

      nextCursor = switch (condition.getSortField()) {
        case "indexName" -> lastItem.getIndexName();
        case "employedItemsCount" -> String.valueOf(lastItem.getEmployedItemsCount());
        default -> lastItem.getIndexClassification();
      };
    }

    // 5. Entity -> Dto 변환
    List<IndexInfoResponse> dtoList = entities.stream()
            .map(indexInfoMapper::toIndexInfoResponse)
            .toList();

    // 6. 응답 객체 조립 후 반환
    return new IndexInfoCursorResponse<>(
            dtoList,
            nextCursor,
            nextIdAfter,
            condition.getSize(),
            totalElements,
            hasNext
    );
  }

  @Transactional
  public IndexInfoResponse createIndexInfo(IndexInfoCreateRequest request) {

    // 1. 중복 검증 로직
    if (indexInfoRepository.existsByIndexClassificationAndIndexName(
        request.indexClassification(), request.indexName())) {
      throw new IllegalArgumentException("이미 동일한 지수 분류명과 지수명이 존재합니다.");
    }

    // 2. Entity 생성 (사용자가 직접 등록하는 API이므로 SourceType은 USER로 고정)
    IndexInfo newIndexInfo =
        IndexInfo.createByUser(
            request.indexClassification(),
            request.indexName(),
            request.employedItemsCount(),
            request.basePointInTime(),
            request.baseIndex(),
            request.favorite());

    // 3. DB 저장
    IndexInfo savedIndexInfo = indexInfoRepository.save(newIndexInfo);

    return indexInfoMapper.toIndexInfoResponse(savedIndexInfo);
  }

  public IndexInfoResponse readIndexInfoById(Long id) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("지수 정보를 찾을 수 없습니다 ID: " + id));
    return indexInfoMapper.toIndexInfoResponse(indexInfo);
  }

  public List<IndexInfoSummaryResponse> findAllIndexInfoSummaries() {
    return indexInfoRepository.findAllSummaries();
  }

  @Transactional
  public IndexInfoResponse updateIndexInfo(Long id, IndexInfoUpdateRequest request) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("지수 정보를 찾을 수 없습니다 ID: " + id));

    indexInfo.update(
        request.employedItemsCount(),
        request.basePointInTime(),
        request.baseIndex(),
        request.favorite());

    return indexInfoMapper.toIndexInfoResponse(indexInfo);
  }

  @Transactional
  public void deleteIndexInfo(Long id) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("지수 정보를 찾을 수 없습니다. ID: " + id));

    indexInfoRepository.delete(indexInfo);
  }
}
