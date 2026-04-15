package com.codeit.findex.domain.autosyncconfig.repository;

import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>
{
    List<AutoSyncConfig> findByIndexInfo_Id(Long indexInfoId, Sort sort);
    List<AutoSyncConfig> findByEnabled(Boolean enabled, Sort sort);
    List<AutoSyncConfig> findByIndexInfo_IdAndEnabled(Long indexInfoId, Boolean enabled, Sort sort);

}
