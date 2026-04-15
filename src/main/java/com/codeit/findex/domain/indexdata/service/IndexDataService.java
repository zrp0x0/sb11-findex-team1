package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.IndexDataMapper;
import com.codeit.findex.domain.indexdata.entity.IndexData;
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
public class IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexDataMapper indexDataMapper;

  public List<IndexDataFavoriteResponse> getFavoritePerformances() {
    List<IndexData> favoriteIndexDataList =
        indexDataRepository.findByIndexInfo_FavoriteTrueOrderByIndexInfoIdAscBaseDateDesc();

    // 지수별 최신 1건만 유지 - 최신 날짜
    Map<Long, IndexData> latestByIndex = new LinkedHashMap<>();
    for (IndexData favoriteIndexData : favoriteIndexDataList) {
      latestByIndex.putIfAbsent(favoriteIndexData.getIndexInfo().getId(), favoriteIndexData);
    }

    return latestByIndex.values().stream()
        .map(indexDataMapper::toIndexDataFavoriteResponse)
        .toList();
  }
}
