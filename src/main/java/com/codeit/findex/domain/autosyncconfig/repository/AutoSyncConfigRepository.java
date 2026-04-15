package com.codeit.findex.domain.autosyncconfig.repository;

import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>
{
}
