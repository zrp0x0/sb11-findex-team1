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
public class IndexInfoOpenApiClient {

  // yyyyMMdd형식
  private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

  private final RestClient.Builder restClientBuilder;

  @Value("${open-api.stock-index.base-url}")
  private String baseUrl;

  @Value("${open-api.stock-index.service-key:}")
  private String serviceKey;

  @Value("${open-api.stock-index.num-of-rows:100}")
  private int numOfRows;

  public List<OpenApiIndexInfoResponse> fetchIndexInfos(LocalDate baseDate) {
    validateConfig();

    RestClient restClient = restClientBuilder.baseUrl(baseUrl).build();

    int pageNo = 1;
    int totalCount = Integer.MAX_VALUE;
    List<OpenApiIndexInfoResponse> results = new ArrayList<>();

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
                        .queryParam("numOfRows", numOfRows);

                    if (baseDate != null) {
                      uriBuilder.queryParam("basDt", baseDate.format(BASIC_DATE));
                    }

                    return uriBuilder.build();
                  })
              .retrieve()
              .body(JsonNode.class);

      // 응답은 item ⊂ items ⊂ body ⊂ response 구조
      if (root == null || root.isNull()) {
        throw new IllegalStateException("Open API 응답 본문이 비어 있습니다.");
      }

      JsonNode response = root.path("response");
      if (response.isMissingNode() || response.isNull()) {
        throw new IllegalStateException("Open API 응답에 response가 없습니다.");
      }

      JsonNode body = response.path("body");
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
          OpenApiIndexInfoResponse parsed = parse(node);
          if (parsed != null) {
            results.add(parsed);
          }
        }
      } else if (itemNode.isObject()) {
        OpenApiIndexInfoResponse parsed = parse(itemNode);
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

  private OpenApiIndexInfoResponse parse(JsonNode node) {
    String indexClassification = text(node, "idxCsf");
    String indexName = text(node, "idxNm");

    if (indexClassification == null || indexName == null) {
      return null;
    }

    return new OpenApiIndexInfoResponse(
        indexClassification,
        indexName,
        integer(node, "epyItmsCnt"),
        date(node, "basPntm"),
        decimal(node, "basIdx"));
  }

  // 문자열 변환, 공백 제거, null/공백 정리
  private String text(JsonNode node, String field) {
    String value = node.path(field).asText(null);
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private Integer integer(JsonNode node, String field) {
    String raw = text(node, field);
    if (raw == null) {
      return null;
    }
    try {
      return Integer.valueOf(raw);
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

  private BigDecimal decimal(JsonNode node, String field) {
    String raw = text(node, field);
    if (raw == null) {
      return null;
    }
    try {
      return new BigDecimal(raw);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  // 설정 값 예외처리
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
}
