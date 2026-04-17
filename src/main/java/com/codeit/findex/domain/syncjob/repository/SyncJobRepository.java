package com.codeit.findex.domain.syncjob.repository;

import com.codeit.findex.domain.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {

}
