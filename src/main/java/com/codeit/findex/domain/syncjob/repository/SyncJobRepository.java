package com.codeit.findex.domain.syncjob.repository;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SyncJobRepository
    extends JpaRepository<SyncJob, Long>, JpaSpecificationExecutor<SyncJob> {

  // 최신성공
  Optional<SyncJob> findTopByJobTypeAndIndexInfo_IdAndResultOrderByTargetDateDesc(
      JobType jobType,
      Long indexInfoId,
      SyncResult result
  );
}
