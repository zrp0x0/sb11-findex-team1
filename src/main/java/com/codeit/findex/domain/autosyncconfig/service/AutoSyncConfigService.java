package com.codeit.findex.domain.autosyncconfig.service;

import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigListRequest;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigMapper;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigPageResponse;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigResponse;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.codeit.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {

  private final AutoSyncConfigRepository autoSyncConfigRepository;
  private final AutoSyncConfigMapper autoSyncConfigMapper;

  public AutoSyncConfigPageResponse getAutoSyncConfigs(AutoSyncConfigListRequest request) {
    Long indexInfoId = request.indexInfoId();
    Boolean enabled = request.enabled();
    Sort sort = createSort(request);

    Integer requestSize = request.size();
    int size = (requestSize == null || requestSize <= 0) ? 10 : requestSize;

    List<AutoSyncConfig> autoSyncConfigs;
    if (indexInfoId == null && enabled == null) {
      autoSyncConfigs = autoSyncConfigRepository.findAll(sort);
    } else if (indexInfoId != null && enabled == null) {
      autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_Id(indexInfoId, sort);
    } else if (indexInfoId == null) {
      autoSyncConfigs = autoSyncConfigRepository.findByEnabled(enabled, sort);
    } else {
      autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_IdAndEnabled(
          indexInfoId,
          enabled,
          sort
      );
    }

    List<AutoSyncConfig> filteredItems = autoSyncConfigs.stream()
        .filter(config -> isAfterCursor(config, request))
        .toList();

    boolean hasNext = filteredItems.size() > size;
    List<AutoSyncConfig> pageItems = filteredItems.stream()
        .limit(size)
        .toList();

    List<AutoSyncConfigResponse> content = pageItems.stream()
        .map(autoSyncConfigMapper::toAutoSyncConfigResponse)
        .toList();

    String nextCursor = null;
    Long nextIdAfter = null;
    if (hasNext && !pageItems.isEmpty()) {
      AutoSyncConfig lastItem = pageItems.get(pageItems.size() - 1);
      nextIdAfter = lastItem.getId();

      if ("enabled".equals(request.sortField())) {
        nextCursor = String.valueOf(lastItem.getEnabled());
      } else {
        nextCursor = lastItem.getIndexInfo().getIndexName();
      }
    }

    return new AutoSyncConfigPageResponse(
        content,
        nextCursor,
        nextIdAfter,
        content.size(),
        (long) filteredItems.size(),
        hasNext
    );
  }

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

  private boolean isAfterCursor(AutoSyncConfig config, AutoSyncConfigListRequest request) {
    String cursor = request.cursor();
    Long idAfter = request.idAfter();
    String sortDirection = request.sortDirection();

    if (cursor == null || idAfter == null) {
      return true;
    }

    int compare;
    if ("enabled".equals(request.sortField())) {
      compare = config.getEnabled().compareTo(Boolean.valueOf(cursor));
    } else {
      compare = config.getIndexInfo().getIndexName().compareTo(cursor);
    }

    if ("desc".equalsIgnoreCase(sortDirection)) {
      return compare < 0 || (compare == 0 && config.getId() > idAfter);
    }

    return compare > 0 || (compare == 0 && config.getId() > idAfter);
  }

  @Transactional
  public AutoSyncConfigResponse updateAutoSyncConfig(Long id, AutoSyncConfigUpdateRequest request) {
    AutoSyncConfig autoSyncConfig = autoSyncConfigRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("자동 연동 설정을 찾을 수 없습니다."));

    autoSyncConfig.updateEnabled(request.enabled());
    return autoSyncConfigMapper.toAutoSyncConfigResponse(autoSyncConfig);
  }

  // 대상 조회 메서드
  public List<AutoSyncConfig> getEnabledAutoSyncConfigs() {
    return autoSyncConfigRepository.findByEnabled(true, Sort.by(Sort.Direction.ASC, "id"));
  }

}
