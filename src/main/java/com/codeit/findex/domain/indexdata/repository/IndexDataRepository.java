package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

}
