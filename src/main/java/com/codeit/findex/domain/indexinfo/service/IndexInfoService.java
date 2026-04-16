package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoMapper;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexInfoService {

  private final IndexInfoRepository indexInfoRepository;
  private final IndexInfoMapper indexInfoMapper;

  @Transactional
  public IndexInfoCreateResponse createIndexInfo(IndexInfoCreateRequest request) {

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

    return indexInfoMapper.toIndexInfoCreateResponse(savedIndexInfo);
  }

  @Transactional
  public void deleteIndexInfo(Long id) {
    IndexInfo indexInfo = indexInfoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("지수 정보를 찾을 수 없습니다. ID: " + id));

    indexInfoRepository.delete(indexInfo);
  }
}
