package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.common.enums.PerformancePeriodType;
import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.IndexDataMapper;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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

  public List<IndexDataFavoriteResponse> getFavoritePerformances(
      PerformancePeriodType performancePeriodType) {
    List<IndexData> favoriteIndexDataList =
        indexDataRepository.findByIndexInfo_FavoriteTrueOrderByIndexInfoIdAscBaseDateDesc();

    Map<Long, List<IndexData>> groupedByIndex = new LinkedHashMap<>();

    for (IndexData favoriteIndexData : favoriteIndexDataList) {
      Long indexInfoId = favoriteIndexData.getIndexInfo().getId();
      groupedByIndex.computeIfAbsent(indexInfoId, key -> new ArrayList<>()).add(favoriteIndexData);
    }

    List<IndexDataFavoriteResponse> responses = new ArrayList<>();

    for (List<IndexData> indexDataList : groupedByIndex.values()) {
      // 첫번째가 가장 날짜가 늦은(최신) 데이터
      IndexData latest = indexDataList.get(0);

      // 최신 데이터에 종가, 날짜가 없으면 건너뛰기
      if (latest.getClosingPrice() == null || latest.getBaseDate() == null) {
        continue;
      }

      LocalDate targetDate = getTargetDate(latest.getBaseDate(), performancePeriodType);
      IndexData compareData = findCompareData(indexDataList, targetDate);

      if (compareData == null
          || compareData.getClosingPrice() == null
          || compareData.getBaseDate() == null) {
        continue;
      }

      BigDecimal currentPrice = latest.getClosingPrice();
      BigDecimal beforePrice = compareData.getClosingPrice();
      BigDecimal versus = currentPrice.subtract(beforePrice);

      // 등락률 계산 - (현재가 - 이전가) / 이전가 * 100
      BigDecimal fluctuationRate = BigDecimal.ZERO;
      if (beforePrice.compareTo(BigDecimal.ZERO) != 0) {
        fluctuationRate =
            versus
                .divide(beforePrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
      }

      responses.add(
          indexDataMapper.toIndexDataFavoriteResponse(
              latest, versus, fluctuationRate, currentPrice, beforePrice));
    }
    return responses;
  }

  // periodType에 따라 비교 기준 날짜 계산 메서드
  private LocalDate getTargetDate(LocalDate latestDate, PerformancePeriodType periodType) {
    return switch (periodType) {
      case DAILY -> latestDate.minusDays(1);
      case WEEKLY -> latestDate.minusWeeks(1);
      case MONTHLY -> latestDate.minusMonths(1);
    };
  }

  // periodType에 따라 비교할 데이터(compareData) 찾는 메서드
  // targetDate보다 같거나 더 과거인 것 중 첫 번째(주말, 공휴일 대비)
  private IndexData findCompareData(List<IndexData> indexDataList, LocalDate targetDate) {
    for (int i = 1; i < indexDataList.size(); i++) {
      IndexData indexData = indexDataList.get(i);

      if (indexData.getBaseDate() != null && !indexData.getBaseDate().isAfter(targetDate)) {
        return indexData;
      }
    }
    return null;
  }
}
