package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoSearchCondition;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;

import java.util.List;

public interface IndexInfoRepositoryCustom {
    // 데이터 목록 가져오기
    List<IndexInfo> findAllByCondition(IndexInfoSearchCondition condition);

    // 전체 개수 세기
    long countByCondition(IndexInfoSearchCondition condition);
}
