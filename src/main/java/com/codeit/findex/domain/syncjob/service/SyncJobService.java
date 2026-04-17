package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobMapper;
import com.codeit.findex.domain.syncjob.dto.indexinfo.IndexInfoSyncJobResponse;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SyncJobService {
  private final IndexInfoSyncJobMapper indexInfoSyncJobMapper;
  private final SyncJobRepository syncJobRepository;
  private final IndexInfoRepository indexInfoRepository;

  @Transactional
  public List<IndexInfoSyncJobResponse> syncIndexInfos(String worker) {}
}
