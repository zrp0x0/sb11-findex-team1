package com.codeit.findex.domain.indexinfo.entity;

import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.codeit.findex.domain.common.enums.SourceType;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "index_info", uniqueConstraints = {
        @UniqueConstraint(name = "uk_index_classification_name", columnNames = {"index_classification", "index_name"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index_classification", length = 100, nullable = false)
    private String indexClassification;

    @Column(name = "index_name", length = 100, nullable = false)
    private String indexName;

    private Integer employedItemsCount;

    private LocalDate basePointInTime;

    @Column(precision = 15, scale = 2)
    private BigDecimal baseIndex;

    @Enumerated(EnumType.STRING) // ENUM 이름을 그대로 DB에 저장
    @Column(length = 20, nullable = false)
    private SourceType sourceType;

    @Column(nullable = false)
    private Boolean favorite = false;

    @OneToMany(mappedBy = "indexInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndexData> indexDataList = new ArrayList<>();

    @OneToOne(mappedBy = "indexInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private AutoSyncConfig autoSyncConfig;

    public IndexInfo(
            String indexClassification,
            String indexName,
            Integer employedItemsCount,
            LocalDate basePointInTime,
            BigDecimal baseIndex,
            SourceType sourceType,
            Boolean favorite
    ) {
        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.sourceType = sourceType;
        this.favorite = favorite != null ? favorite : false;
    }

    public static IndexInfo create(
            String indexClassification,
            String indexName,
            Integer employedItemsCount,
            LocalDate basePointInTime,
            BigDecimal baseIndex,
            SourceType sourceType,
            Boolean favorite
    ) {
        return new IndexInfo(
                indexClassification,
                indexName,
                employedItemsCount,
                basePointInTime,
                baseIndex,
                sourceType,
                favorite
        );
    }
}
