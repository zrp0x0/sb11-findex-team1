package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.dto.IndexDataSearchCondition;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.codeit.findex.domain.indexdata.entity.QIndexData.indexData;

@RequiredArgsConstructor
public class IndexDataRepositoryImpl implements IndexDataRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<IndexData> findAllByCondition(IndexDataSearchCondition condition) {
    return queryFactory
        .selectFrom(indexData)
        .where(
            eqIndexInfoId(condition.indexInfoId()),
            betweenDates(condition.startDate(), condition.endDate()),
            getCursorCondition(condition))
        .orderBy(createOrderSpecifier(condition), indexData.id.asc())
        .limit(condition.getSize() + 1)
        .fetch();
  }

  @Override
  public long countByCondition(IndexDataSearchCondition condition) {
    Long count =
        queryFactory
            .select(indexData.count())
            .from(indexData)
            .where(
                eqIndexInfoId(condition.indexInfoId()),
                betweenDates(condition.startDate(), condition.endDate()))
            .fetchOne();
    return count != null ? count : 0L;
  }

  private BooleanExpression eqIndexInfoId(Long indexInfoId) {
    return indexInfoId != null ? indexData.indexInfo.id.eq(indexInfoId) : null;
  }

  private BooleanExpression betweenDates(LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null) {
      return indexData.baseDate.between(startDate, endDate);
    } else if (startDate != null) {
      return indexData.baseDate.goe(startDate);
    } else if (endDate != null) {
      return indexData.baseDate.loe(endDate);
    }
    return null;
  }

  // 커서 로직 처리
  private BooleanExpression getCursorCondition(IndexDataSearchCondition condition) {
    if (condition.cursor() == null || condition.idAfter() == null) {
      return null;
    }

    String cursor = condition.cursor();
    Long idAfter = condition.idAfter();
    boolean isAsc = condition.getSortDirection().equals("asc");
    String sortField = condition.getSortField();

    try {
      // 1. 날짜 타입 (LocalDate)
      if (sortField.equals("baseDate")) {
        LocalDate cursorDate = LocalDate.parse(cursor);
        return isAsc
            ? indexData
                .baseDate
                .gt(cursorDate)
                .or(indexData.baseDate.eq(cursorDate).and(indexData.id.gt(idAfter)))
            : indexData
                .baseDate
                .lt(cursorDate)
                .or(indexData.baseDate.eq(cursorDate).and(indexData.id.gt(idAfter)));
      }

      // 2. 소수점 타입 (BigDecimal)
      if (List.of(
              "marketPrice", "closingPrice", "highPrice", "lowPrice", "versus", "fluctuationRate")
          .contains(sortField)) {
        BigDecimal cursorValue = new BigDecimal(cursor);
        return switch (sortField) {
          case "marketPrice" ->
              buildBigDecimalCursor(indexData.marketPrice, cursorValue, idAfter, isAsc);
          case "closingPrice" ->
              buildBigDecimalCursor(indexData.closingPrice, cursorValue, idAfter, isAsc);
          case "highPrice" ->
              buildBigDecimalCursor(indexData.highPrice, cursorValue, idAfter, isAsc);
          case "lowPrice" -> buildBigDecimalCursor(indexData.lowPrice, cursorValue, idAfter, isAsc);
          case "versus" -> buildBigDecimalCursor(indexData.versus, cursorValue, idAfter, isAsc);
          case "fluctuationRate" ->
              buildBigDecimalCursor(indexData.fluctuationRate, cursorValue, idAfter, isAsc);
          default -> null;
        };
      }

      // 3. 정수 타입 (Long)
      if (List.of("tradingQuantity", "tradingPrice", "marketTotalAmount").contains(sortField)) {
        Long cursorValue = Long.parseLong(cursor);
        return switch (sortField) {
          case "tradingQuantity" ->
              buildLongCursor(indexData.tradingQuantity, cursorValue, idAfter, isAsc);
          case "tradingPrice" ->
              buildLongCursor(indexData.tradingPrice, cursorValue, idAfter, isAsc);
          case "marketTotalAmount" ->
              buildLongCursor(indexData.marketTotalAmount, cursorValue, idAfter, isAsc);
          default -> null;
        };
      }
    } catch (Exception e) {
      // 숫자가 아닌 이상한 문자열이 들어오면 무시
      return null;
    }
    return null;
  }

  // BigDecimal 전용 커서 생성기
  private BooleanExpression buildBigDecimalCursor(
      NumberPath<BigDecimal> path, BigDecimal cursorValue, Long idAfter, boolean isAsc) {
    return isAsc
        ? path.gt(cursorValue).or(path.eq(cursorValue).and(indexData.id.gt(idAfter)))
        : path.lt(cursorValue).or(path.eq(cursorValue).and(indexData.id.gt(idAfter)));
  }

  // Long 전용 커서 생성기
  private BooleanExpression buildLongCursor(
      NumberPath<Long> path, Long cursorValue, Long idAfter, boolean isAsc) {
    return isAsc
        ? path.gt(cursorValue).or(path.eq(cursorValue).and(indexData.id.gt(idAfter)))
        : path.lt(cursorValue).or(path.eq(cursorValue).and(indexData.id.gt(idAfter)));
  }

  // 정렬 도우미
  private OrderSpecifier<?> createOrderSpecifier(IndexDataSearchCondition condition) {
    Order direction = condition.getSortDirection().equals("asc") ? Order.ASC : Order.DESC;

    return switch (condition.getSortField()) {
      case "marketPrice" -> new OrderSpecifier<>(direction, indexData.marketPrice);
      case "closingPrice" -> new OrderSpecifier<>(direction, indexData.closingPrice);
      case "highPrice" -> new OrderSpecifier<>(direction, indexData.highPrice);
      case "lowPrice" -> new OrderSpecifier<>(direction, indexData.lowPrice);
      case "versus" -> new OrderSpecifier<>(direction, indexData.versus);
      case "fluctuationRate" -> new OrderSpecifier<>(direction, indexData.fluctuationRate);
      case "tradingQuantity" -> new OrderSpecifier<>(direction, indexData.tradingQuantity);
      case "tradingPrice" -> new OrderSpecifier<>(direction, indexData.tradingPrice);
      case "marketTotalAmount" -> new OrderSpecifier<>(direction, indexData.marketTotalAmount);
      default -> new OrderSpecifier<>(direction, indexData.baseDate);
    };
  }
}
