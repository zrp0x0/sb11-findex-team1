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
        // 인덱스 ID와 활성화 여부가 조건, 이후 분기
        Long indexInfoId = request.indexInfoId();
        Boolean enabled = request.enabled();

        List<AutoSyncConfig> autoSyncConfigs;

        if (indexInfoId == null && enabled == null) {
            autoSyncConfigs = autoSyncConfigRepository.findAll();
        } else if (indexInfoId != null && enabled == null) {
            autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_Id(indexInfoId);
        } else if (indexInfoId == null) {
            autoSyncConfigs = autoSyncConfigRepository.findByEnabled(enabled);
        } else {
            autoSyncConfigs = autoSyncConfigRepository.findByIndexInfo_IdAndEnabled(indexInfoId, enabled);
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
}
