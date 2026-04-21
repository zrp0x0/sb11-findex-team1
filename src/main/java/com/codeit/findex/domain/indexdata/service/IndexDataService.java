package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.common.enums.PerformancePeriodType;
import com.codeit.findex.domain.indexdata.dto.IndexDataFavoriteResponse;
import com.codeit.findex.domain.indexdata.dto.IndexDataMapper;
import com.codeit.findex.domain.indexdata.dto.IndexDataSearchCondition;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.dto.request.IndexPerformanceRankRequest;
import com.codeit.findex.domain.indexdata.dto.request.PeriodType;
import com.codeit.findex.domain.indexdata.dto.request.UnitPeriodType;
import com.codeit.findex.domain.indexdata.dto.response.ChartDataPoint;
import com.codeit.findex.domain.indexdata.dto.response.IndexChartResponse;
import com.codeit.findex.domain.indexdata.dto.response.IndexDataResponse;
import com.codeit.findex.domain.indexdata.dto.response.IndexPerformanceResponse;
import com.codeit.findex.domain.indexdata.dto.response.RankedIndexPerformanceResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCursorResponse;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.codeit.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataService {

  private static final int DEFAULT_RANK_LIMIT = 10;

  private final IndexDataRepository indexDataRepository;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataMapper indexDataMapper;

  public IndexInfoCursorResponse<IndexDataResponse> getIndexDatas(
      IndexDataSearchCondition condition) {

    // 1. 데이터 조회 (limit + 1)
    List<IndexData> entities = indexDataRepository.findAllByCondition(condition);

    // 2. 전체 데이터 개수 카운트
    long totalElements = indexDataRepository.countByCondition(condition);

    // 3. 다음 페이지 여부 확인 및 리스트 자르기
    boolean hasNext = entities.size() > condition.getSize();
    if (hasNext) {
      entities.remove(condition.getSize().intValue());
    }

    // 4. 다음 페이지 커서 값 추출
    String nextCursor = null;
    Long nextIdAfter = null;

    if (hasNext && !entities.isEmpty()) {
      IndexData lastItem = entities.get(entities.size() - 1);
      nextIdAfter = lastItem.getId();

      nextCursor =
          switch (condition.getSortField()) {
            case "baseDate" -> lastItem.getBaseDate().toString();
            case "marketPrice" -> lastItem.getMarketPrice().toString();
            case "closingPrice" -> lastItem.getClosingPrice().toString();
            case "highPrice" -> lastItem.getHighPrice().toString();
            case "lowPrice" -> lastItem.getLowPrice().toString();
            case "versus" -> lastItem.getVersus().toString();
            case "fluctuationRate" -> lastItem.getFluctuationRate().toString();
            case "tradingQuantity" -> lastItem.getTradingQuantity().toString();
            case "tradingPrice" -> lastItem.getTradingPrice().toString();
            case "marketTotalAmount" -> lastItem.getMarketTotalAmount().toString();
            default -> lastItem.getBaseDate().toString();
          };
    }

    // 5. Entity -> DTO 변환 (기존 MapStruct 활용)
    List<IndexDataResponse> dtoList = entities.stream().map(indexDataMapper::toResponse).toList();

    // 6. 응답 객체 조립 후 반환
    return new IndexInfoCursorResponse<>(
        dtoList, nextCursor, nextIdAfter, condition.getSize(), totalElements, hasNext);
  }

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
      IndexData latest = indexDataList.get(0);

      if (latest.getClosingPrice() == null || latest.getBaseDate() == null) {
        continue;
      }

      LocalDate targetDate = getTargetDate(latest.getBaseDate(), performancePeriodType);
      IndexData compareData = findCompareData(indexDataList, targetDate);

      BigDecimal currentPrice = latest.getClosingPrice();
      BigDecimal beforePrice = null;
      BigDecimal versus = null;
      BigDecimal fluctuationRate = null;

      // 과거 데이터가 존재할 때만 대비(versus)와 등락률(fluctuationRate) 계산
      if (compareData != null && compareData.getClosingPrice() != null) {
        beforePrice = compareData.getClosingPrice();
        versus = currentPrice.subtract(beforePrice);

        if (beforePrice.compareTo(BigDecimal.ZERO) != 0) {
          fluctuationRate =
              versus
                  .divide(beforePrice, 6, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
                  .setScale(2, RoundingMode.HALF_UP);
        }
      }

      // 과거 데이터가 없어서 versus 등이 null이더라도 일단 응답 리스트에는 무조건 추가
      responses.add(
          indexDataMapper.toIndexDataFavoriteResponse(
              latest, versus, fluctuationRate, currentPrice, beforePrice));
    }
    return responses;
  }

  private LocalDate getTargetDate(LocalDate latestDate, PerformancePeriodType periodType) {
    return switch (periodType) {
      case DAILY -> latestDate.minusDays(1);
      case WEEKLY -> latestDate.minusWeeks(1);
      case MONTHLY -> latestDate.minusMonths(1);
    };
  }

  private IndexData findCompareData(List<IndexData> indexDataList, LocalDate targetDate) {
    for (int i = 1; i < indexDataList.size(); i++) {
      IndexData indexData = indexDataList.get(i);
      if (indexData.getBaseDate() != null && !indexData.getBaseDate().isAfter(targetDate)) {
        return indexData;
      }
    }
    return null;
  }

  public List<RankedIndexPerformanceResponse> getPerformanceRank(
      IndexPerformanceRankRequest request) {
    Optional<IndexData> latestData = indexDataRepository.findTopByOrderByBaseDateDesc();
    if (latestData.isEmpty()) {
      return new ArrayList<>();
    }

    LocalDate currentDate = latestData.get().getBaseDate();
    LocalDate targetDate = getRankTargetDate(currentDate, request.periodType());
    List<IndexData> currentDataList = findCurrentData(currentDate, request.indexInfoId());

    List<IndexPerformanceResponse> performances = new ArrayList<>();
    for (IndexData currentData : currentDataList) {
      toIndexPerformance(currentData, targetDate).ifPresent(performances::add);
    }

    performances.sort(Comparator.comparing(IndexPerformanceResponse::fluctuationRate).reversed());

    int limit = resolveRankLimit(request.limit());
    List<RankedIndexPerformanceResponse> ranks = new ArrayList<>();
    for (int i = 0; i < performances.size() && i < limit; i++) {
      ranks.add(new RankedIndexPerformanceResponse(performances.get(i), i + 1));
    }
    return ranks;
  }

  private LocalDate getRankTargetDate(LocalDate currentDate, UnitPeriodType periodType) {
    if (periodType == null) {
      throw new IllegalArgumentException("기간 유형은 필수입니다.");
    }
    return switch (periodType) {
      case DAILY -> currentDate.minusDays(1);
      case WEEKLY -> currentDate.minusWeeks(1);
      case MONTHLY -> currentDate.minusMonths(1);
    };
  }

  private List<IndexData> findCurrentData(LocalDate currentDate, Long indexInfoId) {
    if (indexInfoId == null) {
      return indexDataRepository.findByBaseDate(currentDate);
    }
    return indexDataRepository.findByBaseDateAndIndexInfoId(currentDate, indexInfoId);
  }

  private Optional<IndexPerformanceResponse> toIndexPerformance(
      IndexData currentData, LocalDate targetDate) {
    if (currentData.getClosingPrice() == null) {
      return Optional.empty();
    }

    IndexInfo indexInfo = currentData.getIndexInfo();
    Optional<IndexData> compareDataOptional =
        indexDataRepository.findFirstByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
            indexInfo.getId(), targetDate);
    if (compareDataOptional.isEmpty()) {
      return Optional.empty();
    }

    IndexData compareData = compareDataOptional.get();
    BigDecimal beforePrice = compareData.getClosingPrice();
    if (beforePrice == null || beforePrice.compareTo(BigDecimal.ZERO) == 0) {
      return Optional.empty();
    }

    BigDecimal currentPrice = currentData.getClosingPrice();
    BigDecimal versus = currentPrice.subtract(beforePrice);
    BigDecimal fluctuationRate =
        versus
            .divide(beforePrice, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);

    return Optional.of(
        new IndexPerformanceResponse(
            indexInfo.getId(),
            indexInfo.getIndexClassification(),
            indexInfo.getIndexName(),
            versus,
            fluctuationRate,
            currentPrice,
            beforePrice));
  }

  private int resolveRankLimit(Integer limit) {
    if (limit == null) {
      return DEFAULT_RANK_LIMIT;
    }
    if (limit < 1) {
      throw new IllegalArgumentException("랭킹 개수는 1 이상이어야 합니다.");
    }
    return limit;
  }

  public IndexChartResponse getChart(Long indexInfoId, PeriodType periodType) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(indexInfoId)
            .orElseThrow(() -> new IllegalArgumentException("해당 지수 정보가 없습니다. id=" + indexInfoId));

    LocalDate endDate = LocalDate.now();
    LocalDate startDate =
        switch (periodType) {
          case MONTHLY -> endDate.minusMonths(1);
          case QUARTERLY -> endDate.minusMonths(3);
          case YEARLY -> endDate.minusYears(1);
        };

    LocalDate fetchStartDate = startDate.minusDays(40);
    List<IndexData> fetchedData =
        indexDataRepository.findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
            indexInfoId, fetchStartDate, endDate);

    List<ChartDataPoint> dataPoints = new ArrayList<>();
    List<ChartDataPoint> ma5DataPoints = new ArrayList<>();
    List<ChartDataPoint> ma20DataPoints = new ArrayList<>();

    for (int i = 0; i < fetchedData.size(); i++) {
      IndexData current = fetchedData.get(i);
      LocalDate currentDate = current.getBaseDate();

      BigDecimal ma5 = null;
      if (i >= 4) {
        BigDecimal sum5 = BigDecimal.ZERO;
        for (int j = i - 4; j <= i; j++) {
          sum5 = sum5.add(fetchedData.get(j).getClosingPrice());
        }
        ma5 = sum5.divide(BigDecimal.valueOf(5), 2, RoundingMode.HALF_UP);
      }

      BigDecimal ma20 = null;
      if (i >= 19) {
        BigDecimal sum20 = BigDecimal.ZERO;
        for (int j = i - 19; j <= i; j++) {
          sum20 = sum20.add(fetchedData.get(j).getClosingPrice());
        }
        ma20 = sum20.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP);
      }

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
    return new IndexChartResponse(
        indexInfo.getId(),
        indexInfo.getIndexClassification(),
        indexInfo.getIndexName(),
        periodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints);
  }

  @Transactional
  public IndexDataResponse update(Long id, IndexDataUpdateRequest request) {
    IndexData indexData =
        indexDataRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 지수 데이터가 없습니다. id=" + id));

    indexData.update(
        request.marketPrice(),
        request.closingPrice(),
        request.highPrice(),
        request.lowPrice(),
        request.versus(),
        request.fluctuationRate(),
        request.tradingQuantity(),
        request.tradingPrice(),
        request.marketTotalAmount());
    return indexDataMapper.toResponse(indexData);
  }

  @Transactional
  public IndexDataResponse create(IndexDataCreateRequest request) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(request.indexInfoId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "해당 지수 정보를 찾을 수 없습니다. id=" + request.indexInfoId()));

    if (indexDataRepository.existsByIndexInfo_IdAndBaseDate(
        request.indexInfoId(), request.baseDate())) {
      throw new IllegalArgumentException("해당 날짜의 데이터가 이미 존재합니다.");
    }

    IndexData indexData = indexDataMapper.toIndexData(request, indexInfo);
    IndexData savedData = indexDataRepository.save(indexData);

    return indexDataMapper.toResponse(savedData);
  }

  @Transactional
  public void delete(Long id) {
    IndexData indexData =
        indexDataRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("삭제할 지수 데이터를 찾을 수 없습니다. ID: " + id));

    indexDataRepository.delete(indexData);
  }
}
