package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.dto.IndexDataSearchCondition;
import com.codeit.findex.domain.indexdata.entity.IndexData;

import java.util.List;

public interface IndexDataRepositoryCustom {

  // 1. 조건에 맞는 지수 데이터 목록을 페이징(limit + 1)해서 가져오는 메서드
  List<IndexData> findAllByCondition(IndexDataSearchCondition condition);

  // 2. 조건에 맞는 전체 지수 데이터의 개수를 세는 메서드 (hasNext와 별개로 전체 페이지/개수 정보 제공용)
  long countByCondition(IndexDataSearchCondition condition);
}
