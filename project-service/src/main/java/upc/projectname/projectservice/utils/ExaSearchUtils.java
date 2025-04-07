package upc.projectname.projectservice.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exa搜索API工具类
 */
@Slf4j
@Component
public class ExaSearchUtils {

    private static String apiKey;
    private static final String EXA_API_URL = "https://api.exa.ai/search";
    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${exa.api.key}")
    public void setApiKey(String key) {
        apiKey = key;
    }

    /**
     * 执行高级搜索请求
     */
    public static String advancedSearch(String query, Boolean useAutoprompt, String type,
                                        String category, Integer numResults,
                                        List<String> includeDomains, List<String> excludeDomains,
                                        String startCrawlDate, String endCrawlDate,
                                        String startPublishedDate, String endPublishedDate,
                                        List<String> includeText, List<String> excludeText,
                                        Boolean includeFullText, Boolean includeHighlights,
                                        Integer numSentences, Integer highlightsPerUrl,
                                        Boolean includeSummary, String livecrawl) {

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("useAutoprompt", useAutoprompt);
        requestBody.put("type", type);
        // 初始化excludeDomains（如果为null）
        if (excludeDomains == null) {
            excludeDomains = new ArrayList<>();
        }
        excludeDomains.add("github.com");
        if (category != null) requestBody.put("category", category);
        if (numResults != null) requestBody.put("numResults", numResults);
        if (includeDomains != null && !includeDomains.isEmpty()) requestBody.put("includeDomains", includeDomains);
        if (excludeDomains != null && !excludeDomains.isEmpty()) requestBody.put("excludeDomains", excludeDomains);
        if (startCrawlDate != null) requestBody.put("startCrawlDate", startCrawlDate);
        if (endCrawlDate != null) requestBody.put("endCrawlDate", endCrawlDate);
        if (startPublishedDate != null) requestBody.put("startPublishedDate", startPublishedDate);
        if (endPublishedDate != null) requestBody.put("endPublishedDate", endPublishedDate);
        if (includeText != null && !includeText.isEmpty()) requestBody.put("includeText", includeText);
        if (excludeText != null && !excludeText.isEmpty()) requestBody.put("excludeText", excludeText);

        // 构建contents字段
        Map<String, Object> contents = new HashMap<>();
        contents.put("text", includeFullText);

        if (includeHighlights) {
            Map<String, Object> highlights = new HashMap<>();
            highlights.put("numSentences", numSentences);
            highlights.put("highlightsPerUrl", highlightsPerUrl);
            contents.put("highlights", highlights);
        }

        if (includeSummary) {
            contents.put("summary", new HashMap<>());
        }

        contents.put("livecrawl", livecrawl);
        requestBody.put("contents", contents);

        return executeRequest(requestBody);
    }

    /**
     * 获取内容高亮
     */
    public static String getHighlights(String query, String type, Integer numResults,
                                       Integer numSentences, Integer highlightsPerUrl,
                                       String highlightQuery) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("type", type);
        requestBody.put("numResults", numResults);

        Map<String, Object> contents = new HashMap<>();
        contents.put("text", false);  // 不需要全文

        Map<String, Object> highlights = new HashMap<>();
        highlights.put("numSentences", numSentences);
        highlights.put("highlightsPerUrl", highlightsPerUrl);
        if (highlightQuery != null) {
            highlights.put("query", highlightQuery);
        }
        contents.put("highlights", highlights);

        requestBody.put("contents", contents);

        return executeRequest(requestBody);
    }

    /**
     * 获取内容摘要
     */
    public static String getSummary(String query, String type, Integer numResults,
                                    String summaryQuery, Boolean useSchema) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("type", type);
        requestBody.put("numResults", numResults);

        Map<String, Object> contents = new HashMap<>();
        contents.put("text", false);  // 不需要全文

        Map<String, Object> summary = new HashMap<>();
        if (summaryQuery != null) {
            summary.put("query", summaryQuery);
        }

        if (useSchema) {
            // 这里可以添加默认的schema或者从外部配置中获取
            Map<String, Object> schema = new HashMap<>();
            // 添加schema配置
            summary.put("schema", schema);
        }

        contents.put("summary", summary);
        requestBody.put("contents", contents);

        return executeRequest(requestBody);
    }

    /**
     * 实时爬取搜索
     */
    public static String livecrawlSearch(String query, Integer livecrawlTimeout,
                                         Integer subpages, String subpageTarget) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("type", "keyword");  // 实时爬取一般用keyword模式

        Map<String, Object> contents = new HashMap<>();
        contents.put("livecrawl", "always");
        contents.put("livecrawlTimeout", livecrawlTimeout);
        contents.put("subpages", subpages);

        if (subpageTarget != null) {
            contents.put("subpageTarget", subpageTarget);
        }

        requestBody.put("contents", contents);

        return executeRequest(requestBody);
    }

    /**
     * 执行API请求
     */
    private static String executeRequest(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                EXA_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("API调用失败: " + response.getStatusCode() + " - " + response.getBody());
        }
    }
}
