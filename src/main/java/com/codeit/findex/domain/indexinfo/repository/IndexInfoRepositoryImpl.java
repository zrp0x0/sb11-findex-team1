package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoSearchCondition;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.codeit.findex.domain.indexinfo.entity.QIndexInfo.indexInfo;

@RequiredArgsConstructor
public class IndexInfoRepositoryImpl implements IndexInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IndexInfo> findAllByCondition(IndexInfoSearchCondition condition) {
        return queryFactory
                .selectFrom(indexInfo)
                .where(
                        eqIndexClassification(condition.indexClassification()),
                        containsIndexName(condition.indexName()),
                        eqFavorite(condition.favorite()),
                        getCursorCondition(condition)
                )
                .orderBy(getSortOrder(condition), indexInfo.id.asc())
                .limit(condition.getSize() + 1)
                .fetch();
    }

    @Override
    public long countByCondition(IndexInfoSearchCondition condition) {
        Long totalCount = queryFactory
                .select(indexInfo.count())
                .from(indexInfo)
                .where(
                        eqIndexClassification(condition.indexClassification()),
                        containsIndexName(condition.indexName()),
                        eqFavorite(condition.favorite())
                )
                .fetchOne();

        return totalCount != null ? totalCount : 0L;
    }

    //
    private BooleanExpression eqIndexClassification(String classification) {
        return StringUtils.hasText(classification) ? indexInfo.indexClassification.eq(classification) : null;
    }

    private BooleanExpression containsIndexName(String name) {
        return StringUtils.hasText(name) ? indexInfo.indexName.containsIgnoreCase(name) : null;
    }

    private BooleanExpression eqFavorite(Boolean favorite) {
        return favorite != null ? indexInfo.favorite.eq(favorite) : null;
    }

    private OrderSpecifier<?> getSortOrder(IndexInfoSearchCondition condition) {
        Order direction = condition.getSortDirection().equals("desc") ? Order.DESC : Order.ASC;

        return switch (condition.getSortField()) {
            case "indexName" -> new OrderSpecifier<>(direction, indexInfo.indexName);
            case "employedItemsCount" -> new OrderSpecifier<>(direction, indexInfo.employedItemsCount);
            default -> new OrderSpecifier<>(direction, indexInfo.indexClassification);
        };
    }

    private BooleanExpression getCursorCondition(IndexInfoSearchCondition condition) {
        if (!StringUtils.hasText(condition.cursor()) || condition.idAfter() == null) {
            return null; // 첫 페이지 요청이면 커서 조건 무시
        }

        String sortField = condition.getSortField();
        boolean isAsc = condition.getSortDirection().equals("asc");

        return switch (sortField) {
            case "indexName" -> isAsc
                    ? indexInfo.indexName.gt(condition.cursor()).or(indexInfo.indexName.eq(condition.cursor()).and(indexInfo.id.gt(condition.idAfter())))
                    : indexInfo.indexName.lt(condition.cursor()).or(indexInfo.indexName.eq(condition.cursor()).and(indexInfo.id.gt(condition.idAfter())));

            case "employedItemsCount" -> {
                int countCursor = Integer.parseInt(condition.cursor());
                yield isAsc
                        ? indexInfo.employedItemsCount.gt(countCursor).or(indexInfo.employedItemsCount.eq(countCursor).and(indexInfo.id.gt(condition.idAfter())))
                        : indexInfo.employedItemsCount.lt(countCursor).or(indexInfo.employedItemsCount.eq(countCursor).and(indexInfo.id.gt(condition.idAfter())));
            }

            default -> isAsc // indexClassification
                    ? indexInfo.indexClassification.gt(condition.cursor()).or(indexInfo.indexClassification.eq(condition.cursor()).and(indexInfo.id.gt(condition.idAfter())))
                    : indexInfo.indexClassification.lt(condition.cursor()).or(indexInfo.indexClassification.eq(condition.cursor()).and(indexInfo.id.gt(condition.idAfter())));
        };
    }
}
