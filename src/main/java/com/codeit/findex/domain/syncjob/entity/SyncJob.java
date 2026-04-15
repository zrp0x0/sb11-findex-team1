// SyncJob.java (연동 작업 이력)
package com.codeit.findex.domain.syncjob.entity;

import com.codeit.findex.domain.common.enums.JobType;
import com.codeit.findex.domain.common.enums.SyncResult;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "sync_job",
    indexes = {@Index(name = "idx_job_time", columnList = "job_time")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SyncJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private JobType jobType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "index_info_id") // 특정 지수 없이 전체 연동일 수 있으니 Nullable 허용
  private IndexInfo indexInfo;

  private LocalDate targetDate;

  @Column(length = 50, nullable = false)
  private String worker; // "system" 또는 요청 IP

  @Column(name = "job_time", nullable = false)
  private LocalDateTime jobTime;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private SyncResult result;

  public SyncJob(
      Long id,
      JobType jobType,
      IndexInfo indexInfo,
      LocalDate targetDate,
      String worker,
      LocalDateTime jobTime,
      SyncResult result) {
    this.id = id;
    this.jobType = jobType;
    this.indexInfo = indexInfo;
    this.targetDate = targetDate;
    this.worker = worker;
    this.jobTime = jobTime;
    this.result = result;
  }

  public static SyncJob create(
      Long id,
      JobType jobType,
      IndexInfo indexInfo,
      LocalDate targetDate,
      String worker,
      LocalDateTime jobTime,
      SyncResult result) {
    return new SyncJob(id, jobType, indexInfo, targetDate, worker, jobTime, result);
  }
}
