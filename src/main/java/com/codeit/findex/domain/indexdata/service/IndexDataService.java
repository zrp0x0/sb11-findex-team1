package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.common.enums.PerformancePeriodType;
import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.IndexDataMapper;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.dto.request.PeriodType;
import com.codeit.findex.domain.indexdata.dto.response.ChartDataPoint;
import com.codeit.findex.domain.indexdata.dto.response.IndexChartResponse;
import com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
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
  private final IndexInfoRepository indexInfoRepository;
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
      BigDecimal fluctuationRate = null;
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

  public IndexChartResponse getChart(Long indexInfoId, PeriodType periodType) {
    IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
        .orElseThrow(() -> new IllegalArgumentException("해당 지수 정보가 없습니다. id=" + indexInfoId));

    LocalDate endDate = LocalDate.now();
    LocalDate startDate = switch (periodType) {
      case MONTHLY -> endDate.minusMonths(1);
      case QUARTERLY ->  endDate.minusMonths(3);
      case YEARLY -> endDate.minusYears(1);
    };

    // 이동평균선 20일 계산을 위해 이전 40일치 데이터 여유분 확보
    LocalDate fetchStartDate = startDate.minusDays(40);
    List<IndexData> fetchedData = indexDataRepository
        .findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(indexInfoId, fetchStartDate, endDate);

    List<ChartDataPoint> dataPoints = new ArrayList<>();
    List<ChartDataPoint> ma5DataPoints = new ArrayList<>();
    List<ChartDataPoint> ma20DataPoints = new ArrayList<>();

    for (int i = 0; i < fetchedData.size(); i++) {
      IndexData current = fetchedData.get(i);
      LocalDate currentDate = current.getBaseDate();

      // 5일 이동평균선 계산
      BigDecimal ma5 = null;
      if (i >= 4) {
        BigDecimal sum5 = BigDecimal.ZERO;
        for (int j = i -4; j < i; j++) {
          sum5 = sum5.add(fetchedData.get(j).getClosingPrice());
        }
        ma5 = sum5.divide(BigDecimal.valueOf(5), 2, RoundingMode.HALF_UP);
      }

      // 20일 이동평균선 계산
      BigDecimal ma20 = null;
      if (i >= 19) {
        BigDecimal sum20 = BigDecimal.ZERO;
        for (int j = i - 19; j < i; j++) {
          sum20 = sum20.add(fetchedData.get(j).getClosingPrice());
        }
        ma20 = sum20.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP);
      }

      // 조회 결과는 요청된 시작일(startDate) 데이터부터만 차트 결과로 반환
      if (!currentDate.isBefore(startDate)) {
        dataPoints.add(new ChartDataPoint(currentDate, current.getClosingPrice()));
        if (ma5 != null) {
          ma5DataPoints.add(new ChartDataPoint(currentDate, ma5));
        }
        if (ma20 != null) {
          ma20DataPoints.add(new ChartDataPoint(currentDate, ma20));
        }
      }
    }
    return  new IndexChartResponse(
        indexInfo.getId(),
        indexInfo.getIndexClassification(),
        indexInfo.getIndexName(),
        periodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints
    );
  }

  @Transactional
  public IndexDataResponse update(Long id, IndexDataUpdateRequest request) {
    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 지수 데이터가 없습니다. id=" + id));

    indexData.update(
        request.sourceType(),
        request.marketPrice(),
        request.closingPrice(),
        request.highPrice(),
        request.lowPrice(),
        request.versus(),
        request.fluctuationRate(),
        request.tradingQuantity(),
        request.tradingPrice(),
        request.marketTotalAmount()
    );
    return indexDataMapper.toResponse(indexData);
  }
}
