package com.codeit.findex.domain.autosyncconfig.repository;

import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>
{
    List<AutoSyncConfig> findAll();
    List<AutoSyncConfig> findByIndexInfo_Id(Long indexInfoId);
    List<AutoSyncConfig> findByEnabled(Boolean enabled);
    List<AutoSyncConfig> findByIndexInfo_IdAndEnabled(Long indexInfoId, Boolean enabled);

}
