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

  private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

  private final RestClient.Builder restClientBuilder;

  // yaml(설정 파일)로 입력받음
  @Value("${open-api.stock-index.base-url}")
  private String baseUrl;

  @Value("${open-api.stock-index.service-key}")
  private String serviceKey;

  @Value("${open-api.stock-index.num-of-rows:100}")
  private int numOfRows;

  public List<OpenApiIndexInfoResponse> fetchIndexInfos(LocalDate baseDate) {
    if (baseDate == null) {
      throw new IllegalArgumentException("baseDate는 필수입니다.");
    }
    if (baseUrl == null || baseUrl.isBlank()) {
      throw new IllegalStateException("open-api.stock-index.base-url 설정이 필요합니다.");
    }
    if (serviceKey == null || serviceKey.isBlank()) {
      throw new IllegalStateException("open-api.stock-index.service-key 설정이 필요합니다.");
    }
    if (numOfRows < 1) {
      throw new IllegalStateException("numOfRows는 1 이상이어야 합니다. 현재값: " + numOfRows);
    }

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
                  uriBuilder ->
                      uriBuilder
                          .queryParam("serviceKey", serviceKey)
                          .queryParam("resultType", "json")
                          .queryParam("pageNo", currentPageNo)
                          .queryParam("numOfRows", numOfRows)
                          .queryParam("basDt", baseDate.format(BASIC_DATE))
                          .build())
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

      // 정상 조회 결과가 0건이면 [] 반환위해 종료
      if(totalCount == 0) {
        break;
      }
      JsonNode itemNode = body.path("items").path("item");
      if (itemNode.isMissingNode() || itemNode.isNull()) {
        throw new IllegalStateException("Open API 응답에 items.item이 없습니다.");
      }

      if (itemNode.isArray()) {
        for (JsonNode node : itemNode) {
          OpenApiIndexInfoResponse parsed = parse(node);
          // null일시 다음 데이터(items.item)로 넘어감.
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

  // items.item 객체 or Object하나를 입력받음
  private OpenApiIndexInfoResponse parse(JsonNode node) {
    String indexClassification = text(node, "idxCsf");
    String indexName = text(node, "idxNm");

    // 필수값 누락
    if (indexClassification == null || indexName == null) {
      return null;  // 이 행만 스킵 / 다음 item으로 넘어감
    }

    return new OpenApiIndexInfoResponse(
        indexClassification,
        indexName,
        // 값이 없는 등 올바르지 않으면 null로 매핑
        integer(node, "epyItmsCnt"),
        date(node, "basPntm"),
        decimal(node, "basIdx"));
  }

  // 필드/값이 없거나 공백일때 null로 변환
  private String text(JsonNode node, String field) {
    String value = node.path(field).asText(null);
    return isBlank(value) ? null : value.trim();
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

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
