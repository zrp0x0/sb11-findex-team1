package com.codeit.findex.domain.autosyncconfig.Service;

import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigListRequest;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigPageResponse;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigResponse;
import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.codeit.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AutoSyncConfigService {

  private final AutoSyncConfigRepository autoSyncConfigRepository;

  public AutoSyncConfigService(AutoSyncConfigRepository autoSyncConfigRepository) {
    this.autoSyncConfigRepository = autoSyncConfigRepository;
  }

  public AutoSyncConfigPageResponse getAutoSyncConfigs(AutoSyncConfigListRequest request) {
    // 인덱스 ID와 활성화 여부가 조회조건이어서 if-else로 분기
    Long indexInfoId = request.indexInfoId();
    Boolean enabled = request.enabled();
    Sort sort = createSort(request);

    List<AutoSyncConfig> autoSyncConfigs;

    if (indexInfoId == null && enabled == null) {
      autoSyncConfigs = autoSyncConfigRepository.findAll(sort);
    } else if (indexInfoId != null && enabled == null) {
      autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_Id(indexInfoId, sort);
    } else if (indexInfoId == null) {
      autoSyncConfigs = autoSyncConfigRepository.findByEnabled(enabled, sort);
    } else {
      autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_IdAndEnabled(indexInfoId, enabled,
          sort);
    }

    List<AutoSyncConfigResponse> content = autoSyncConfigs.stream()
        .map(this::toResponse)
        .toList();

    return new AutoSyncConfigPageResponse( // 최종 응답 목록 반환
        content,
        null,
        null,
        content.size(),
        (long) content.size(),
        false
    );
  }

  // 개별 데이터 DTO 변환
  private AutoSyncConfigResponse toResponse(AutoSyncConfig autoSyncConfig) {
    return new AutoSyncConfigResponse(
        autoSyncConfig.getId(),
        autoSyncConfig.getIndexInfo().getId(),
        autoSyncConfig.getIndexInfo().getIndexClassification(),
        autoSyncConfig.getIndexInfo().getIndexName(),
        autoSyncConfig.getEnabled()
    );
  }

  // 2가지 조건이라 if-else로 정렬
  private Sort createSort(AutoSyncConfigListRequest request) {
    String sortField = request.sortField();
    String sortDirection = request.sortDirection();

    String sortBy;
    if ("enabled".equals(sortField)) {
      sortBy = "enabled";
    } else {
      sortBy = "indexInfo.indexName";
    }

    Sort.Direction direction;
    if ("desc".equalsIgnoreCase(sortDirection)) {
      direction = Sort.Direction.DESC;
    } else {
      direction = Sort.Direction.ASC;
    }

    return Sort.by(direction, sortBy);
  }
}
