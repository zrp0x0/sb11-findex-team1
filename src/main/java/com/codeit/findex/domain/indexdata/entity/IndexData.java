package com.codeit.findex.domain.indexdata.entity;

import com.codeit.findex.domain.common.enums.SourceType;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "index_data",
    indexes = {@Index(name = "idx_base_date", columnList = "base_date")},
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_index_info_date",
          columnNames = {"index_info_id", "base_date"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "index_info_id", nullable = false)
  private IndexInfo indexInfo;

  @Column(name = "base_date", nullable = false)
  private LocalDate baseDate;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private SourceType sourceType;

  @Column(precision = 15, scale = 2)
  private BigDecimal marketPrice;

  @Column(precision = 15, scale = 2)
  private BigDecimal closingPrice;

  @Column(precision = 15, scale = 2)
  private BigDecimal highPrice;

  @Column(precision = 15, scale = 2)
  private BigDecimal lowPrice;

  @Column(precision = 15, scale = 2)
  private BigDecimal versus;

  @Column(precision = 5, scale = 2)
  private BigDecimal fluctuationRate; // 등락률은 숫자가 작으므로 precision 5

  private Long tradingQuantity;
  private Long tradingPrice;
  private Long marketTotalAmount;

  public IndexData(
      IndexInfo indexInfo,
      LocalDate baseDate,
      SourceType sourceType,
      BigDecimal marketPrice,
      BigDecimal closingPrice,
      BigDecimal highPrice,
      BigDecimal lowPrice,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      Long tradingQuantity,
      Long tradingPrice,
      Long marketTotalAmount) {
    this.indexInfo = indexInfo;
    this.baseDate = baseDate;
    this.sourceType = sourceType;
    this.marketPrice = marketPrice;
    this.closingPrice = closingPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.tradingQuantity = tradingQuantity;
    this.tradingPrice = tradingPrice;
    this.marketTotalAmount = marketTotalAmount;
  }

  public static IndexData create(
      IndexInfo indexInfo,
      LocalDate baseDate,
      SourceType sourceType,
      BigDecimal marketPrice,
      BigDecimal closingPrice,
      BigDecimal highPrice,
      BigDecimal lowPrice,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      Long tradingQuantity,
      Long tradingPrice,
      Long marketTotalAmount) {
    return new IndexData(
        indexInfo,
        baseDate,
        sourceType,
        marketPrice,
        closingPrice,
        highPrice,
        lowPrice,
        versus,
        fluctuationRate,
        tradingQuantity,
        tradingPrice,
        marketTotalAmount);
  }

  public void update(
      SourceType sourceType,
      BigDecimal marketPrice,
      BigDecimal closingPrice,
      BigDecimal highPrice,
      BigDecimal lowPrice,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      Long tradingQuantity,
      Long tradingPrice,
      Long marketTotalAmount) {
    this.sourceType = sourceType;
    this.marketPrice = marketPrice;
    this.closingPrice = closingPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.tradingQuantity = tradingQuantity;
    this.tradingPrice = tradingPrice;
    this.marketTotalAmount = marketTotalAmount;
  }
}
