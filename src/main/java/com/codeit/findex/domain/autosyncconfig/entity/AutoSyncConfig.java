// AutoSyncConfig.java (자동 연동 설정)
package com.codeit.findex.domain.autosyncconfig.entity;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auto_sync_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoSyncConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 관계. 대상 지수는 유일해야 하므로 unique = true
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private Boolean enabled = false;

    public AutoSyncConfig(IndexInfo indexInfo, Boolean enabled) {
        this.indexInfo = indexInfo;
        this.enabled = enabled != null ? enabled : false;
    }

    public static AutoSyncConfig create(IndexInfo indexInfo, Boolean enabled) {
        return new AutoSyncConfig(indexInfo, enabled);
    }
}