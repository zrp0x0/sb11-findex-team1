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
    Long indexInfoId = request.indexInfoId();
    Boolean enabled = request.enabled();

    Sort sort = createSort(request); // 정렬 조건

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
      autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_IdAndEnabled(indexInfoId, enabled,
          sort);
    }

    List<AutoSyncConfig> filteredItems = autoSyncConfigs.stream()
        .filter(config -> isAfterCursor(config, request))
        .toList(); // 커서 이후 필터링

    boolean hasNext = filteredItems.size() > size;

    List<AutoSyncConfig> pageItems = filteredItems.stream()
        .limit(size)
        .toList(); // 항목 추출

    List<AutoSyncConfigResponse> content = pageItems.stream()
        .map(this::toResponse)
        .toList();

    String nextCursor = null;
    Long nextIdAfter = null;

    // 다음 페이지 커서 계산
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

  private AutoSyncConfigResponse toResponse(AutoSyncConfig autoSyncConfig) {
    return new AutoSyncConfigResponse(
        autoSyncConfig.getId(),
        autoSyncConfig.getIndexInfo().getId(),
        autoSyncConfig.getIndexInfo().getIndexClassification(),
        autoSyncConfig.getIndexInfo().getIndexName(),
        autoSyncConfig.getEnabled()
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
}
