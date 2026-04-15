package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.IndexDataFavoritePerformanceResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.mapper.IndexDataFavoritePerformanceMapper;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataFavoritePerformanceService {

  private final IndexDataRepository indexDataRepository;
  private final IndexDataFavoritePerformanceMapper indexDataFavoritePerformanceMapper;

  public List<IndexDataFavoritePerformanceResponse> getFavoritePerformances() {
    List<IndexData> favoriteIndexDataList =
        indexDataRepository.findByIndexInfo_FavoriteTrueOrderByIndexInfoIdAscBaseDateDesc();

    // 지수별 최신 1건만 유지 - 최신 날짜
    Map<Long, IndexData> latestByIndex = new LinkedHashMap<>();
    for (IndexData favoriteIndexData  : favoriteIndexDataList) {
      latestByIndex.putIfAbsent(favoriteIndexData.getIndexInfo().getId(), favoriteIndexData);
    }

    return latestByIndex.values().stream()
        .map(indexDataFavoritePerformanceMapper::toDto)
        .toList();
  }
}
