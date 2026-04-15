package com.codeit.findex.domain.indexinfo.controller;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateResponse;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    @PostMapping
    public ResponseEntity<IndexInfoCreateResponse> createIndexInfo(
            @Valid @RequestBody IndexInfoCreateRequest request
    ) {
        IndexInfoCreateResponse response = indexInfoService.createIndexInfo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
