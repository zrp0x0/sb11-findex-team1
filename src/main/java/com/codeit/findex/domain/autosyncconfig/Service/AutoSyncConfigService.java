package com.codeit.findex.domain.autosyncconfig.Service;

import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigListRequest;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigPageResponse;
import com.codeit.findex.domain.autosyncconfig.dto.AutoSyncConfigResponse;
import com.codeit.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.codeit.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    public AutoSyncConfigService(AutoSyncConfigRepository autoSyncConfigRepository) {
        this.autoSyncConfigRepository = autoSyncConfigRepository;
    }

    public AutoSyncConfigPageResponse getAutoSyncConfigs(AutoSyncConfigListRequest request) {
        List<AutoSyncConfig> autoSyncConfigs = autoSyncConfigRepository.findAll();

        List<AutoSyncConfigResponse> content = autoSyncConfigs.stream()
                .map(this::toResponse)
                .toList();

        //페이지네이션 구현 이전 임시 응답 양식
        return new AutoSyncConfigPageResponse(
                content,
                null,
                null,
                content.size(),
                (long) content.size(),
                false
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
}
