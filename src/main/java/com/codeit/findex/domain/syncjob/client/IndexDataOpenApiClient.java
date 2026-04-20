package com.codeit.findex.domain.syncjob.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class IndexDataOpenApiClient {

  private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

  private final RestClient.Builder restClientBuilder;

  @Value("${open-api.stock-index.base-url}")
  private String baseUrl;

  @Value("${open-api.stock-index.service-key:}")
  private String serviceKey;

  @Value("${open-api.stock-index.num-of-rows:100}")
  private int numOfRows;

  public List<OpenApiIndexDataResponse> fetchIndexData(
      LocalDate fromDate, LocalDate toDate, String indexName) {
    validateConfig();
    validateRequestDate(fromDate, toDate);

    RestClient restClient = restClientBuilder.baseUrl(baseUrl).build();

    int pageNo = 1;
    int totalCount = Integer.MAX_VALUE;
    List<OpenApiIndexDataResponse> results = new ArrayList<>();

    while ((pageNo - 1) * numOfRows < totalCount) {
      final int currentPageNo = pageNo;

      JsonNode root =
          restClient
              .get()
              .uri(
                  uriBuilder -> {
                    uriBuilder
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("resultType", "json")
                        .queryParam("pageNo", currentPageNo)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("beginBasDt", fromDate.format(BASIC_DATE))
                        .queryParam("endBasDt", toDate.format(BASIC_DATE));

                    // 지수는 선택조건
                    if (indexName != null && !indexName.isBlank()) {
                      uriBuilder.queryParam("idxNm", indexName.trim());
                    }
                    return uriBuilder.build();
                  })
              .retrieve()
              .body(JsonNode.class);
      if (root == null || root.isNull()) {
        throw new IllegalStateException("Open API 응답 본문이 비어 있습니다.");
      }

      JsonNode body = root.path("body");
      if (body.isMissingNode() || body.isNull()) {
        throw new IllegalStateException("Open API 응답에 body가 없습니다.");
      }

      JsonNode totalCountNode = body.path("totalCount");
      if (totalCountNode.isMissingNode() || totalCountNode.isNull()) {
        throw new IllegalStateException("Open API 응답에 totalCount가 없습니다.");
      }
      totalCount = totalCountNode.asInt();

      if (totalCount == 0) {
        break;
      }

      JsonNode itemNode = body.path("items").path("item");
      if (itemNode.isMissingNode() || itemNode.isNull()) {
        throw new IllegalStateException("Open API 응답에 items.item이 없습니다.");
      }

      if (itemNode.isArray()) {
        for (JsonNode node : itemNode) {
          OpenApiIndexDataResponse parsed = parse(node);
          if (parsed != null) {
            results.add(parsed);
          }
        }
      } else if (itemNode.isObject()) {
        OpenApiIndexDataResponse parsed = parse(itemNode);
        if (parsed != null) {
          results.add(parsed);
        }
      } else {
        throw new IllegalStateException("Open API items.item 형식이 올바르지 않습니다.");
      }
      pageNo++;
    }
    return results;
  }

  private OpenApiIndexDataResponse parse(JsonNode node) {
    String indexClassification = text(node, "idxCsf");
    String indexName = text(node, "idxNm");
    LocalDate baseDate = date(node, "basDt");

    // 핵심 식별값 누락 row는 스킵
    if (indexClassification == null || indexName == null || baseDate == null) {
      return null;
    }

    return new OpenApiIndexDataResponse(
        indexClassification,
        indexName,
        baseDate,
        decimal(node, "mkp"),
        decimal(node, "clpr"),
        decimal(node, "hipr"),
        decimal(node, "lopr"),
        decimal(node, "vs"),
        decimal(node, "fltRt"),
        longValue(node, "trqu"),
        longValue(node, "trPrc"),
        longValue(node, "lstgMrktTotAmt"));
  }

  // 문자열 변환, 공백 제거, null/공백 정리
  private String text(JsonNode node, String field) {
    String value = node.path(field).asText(null);
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private BigDecimal decimal(JsonNode node, String field) {
    String raw = text(node, field);
    if (raw == null) {
      return null;
    }
    try {
      return new BigDecimal(raw.replace(",", ""));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Long longValue(JsonNode node, String field) {
    String raw = text(node, field);
    if (raw == null) {
      return null;
    }
    try {
      return Long.valueOf(raw.replace(",", ""));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private LocalDate date(JsonNode node, String field) {
    String raw = text(node, field);
    if (raw == null) {
      return null;
    }
    try {
      return LocalDate.parse(raw, BASIC_DATE);
    } catch (Exception e) {
      return null;
    }
  }

  // 설정파일 예외처리
  private void validateConfig() {
    if (baseUrl == null || baseUrl.isBlank()) {
      throw new IllegalStateException("open-api.stock-index.base-url 설정이 필요합니다.");
    }
    if (serviceKey == null || serviceKey.isBlank()) {
      throw new IllegalStateException("open-api.stock-index.service-key 설정이 필요합니다.");
    }
    if (numOfRows < 1) {
      throw new IllegalStateException("numOfRows는 1 이상이어야 합니다. 현재값: " + numOfRows);
    }
  }

  // Date값 예외처리
  private void validateRequestDate(LocalDate fromDate, LocalDate toDate) {
    if (fromDate == null || toDate == null) {
      throw new IllegalArgumentException("대상 날짜(fromDate, toDate)는 필수입니다.");
    }
    if (fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("fromDate는 toDate보다 이후일 수 없습니다.");
    }
  }
}
