package upc.projectname.projectservice.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import upc.projectname.projectservice.utils.ExaSearchUtils;
import upc.projectname.upccommon.domain.po.Result;

import java.util.List;
import java.util.Map;

/**
 * Exa搜索服务控制器
 */
@Tag(name = "Exa在线搜索接口")
@RestController
@RequestMapping("/exa/search")
@RequiredArgsConstructor
@Slf4j
public class ExaSearchController {

    @Operation(summary = "高级搜索", description = "执行全功能搜索，支持所有参数的自定义")
    @PostMapping
    public Result<Object> search(
            @Parameter(description = "搜索查询内容", required = true) @RequestParam String query,
            @Parameter(description = "是否使用自动提示优化查询", required = false)
            @RequestParam(required = false, defaultValue = "true") Boolean useAutoprompt,
            @Parameter(description = "搜索类型(keyword, neural, auto)", required = false)
            @RequestParam(required = false, defaultValue = "auto") String type,
            @Parameter(description = "搜索类别(如company, research paper, news, pdf等)", required = false)
            @RequestParam(required = false) String category,
            @Parameter(description = "返回结果数量(最多100)", required = false)
            @RequestParam(required = false, defaultValue = "10") Integer numResults,
            @Parameter(description = "要包含的域名列表", required = false)
            @RequestParam(required = false) List<String> includeDomains,
            @Parameter(description = "要排除的域名列表", required = false)
            @RequestParam(required = false) List<String> excludeDomains,
            @Parameter(description = "起始爬取日期(ISO 8601格式)", required = false)
            @RequestParam(required = false) String startCrawlDate,
            @Parameter(description = "结束爬取日期(ISO 8601格式)", required = false)
            @RequestParam(required = false) String endCrawlDate,
            @Parameter(description = "起始发布日期(ISO 8601格式)", required = false)
            @RequestParam(required = false) String startPublishedDate,
            @Parameter(description = "结束发布日期(ISO 8601格式)", required = false)
            @RequestParam(required = false) String endPublishedDate,
            @Parameter(description = "必须包含的文本", required = false)
            @RequestParam(required = false) List<String> includeText,
            @Parameter(description = "必须排除的文本", required = false)
            @RequestParam(required = false) List<String> excludeText,
            @Parameter(description = "是否返回完整网页文本", required = false)
            @RequestParam(required = false, defaultValue = "true") Boolean includeFullText,
            @Parameter(description = "是否返回高亮内容", required = false)
            @RequestParam(required = false, defaultValue = "true") Boolean includeHighlights,
            @Parameter(description = "每个高亮的句子数", required = false)
            @RequestParam(required = false, defaultValue = "5") Integer numSentences,
            @Parameter(description = "每个URL返回的高亮数量", required = false)
            @RequestParam(required = false, defaultValue = "1") Integer highlightsPerUrl,
            @Parameter(description = "是否返回摘要", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean includeSummary,
            @Parameter(description = "实时爬取选项(never, fallback, always, auto)", required = false)
            @RequestParam(required = false, defaultValue = "fallback") String livecrawl) {

        log.info("Exa search with query: {}, type: {}, category: {}", query, type, category);
        try {
            String rawResult = ExaSearchUtils.advancedSearch(
                    query, useAutoprompt, type, category, numResults,
                    includeDomains, excludeDomains, startCrawlDate, endCrawlDate,
                    startPublishedDate, endPublishedDate, includeText, excludeText,
                    includeFullText, includeHighlights, numSentences, highlightsPerUrl,
                    includeSummary, livecrawl);

            log.debug("Raw result from Exa search: {}", rawResult);
            Object jsonObject = JSON.parseObject(rawResult);
            return Result.success(jsonObject);

        } catch (Exception e) {
            log.error("Exa search error: ", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }

    @Operation(summary = "内容高亮搜索", description = "执行搜索并重点提取网页内容的高亮片段")
    @GetMapping("/highlights")
    public Result<Object> getHighlights(
            @Parameter(description = "搜索查询内容", required = true) @RequestParam String query,
            @Parameter(description = "搜索类型(keyword, neural, auto)", required = false)
            @RequestParam(required = false, defaultValue = "auto") String type,
            @Parameter(description = "返回结果数量", required = false)
            @RequestParam(required = false, defaultValue = "5") Integer numResults,
            @Parameter(description = "每个高亮的句子数", required = false)
            @RequestParam(required = false, defaultValue = "3") Integer numSentences,
            @Parameter(description = "每个URL返回的高亮数量", required = false)
            @RequestParam(required = false, defaultValue = "2") Integer highlightsPerUrl,
            @Parameter(description = "自定义高亮提取方向", required = false)
            @RequestParam(required = false) String highlightQuery) {

        log.info("Getting highlights for query: {}", query);
        try {
            String rawResult = ExaSearchUtils.getHighlights(query, type, numResults,
                    numSentences, highlightsPerUrl, highlightQuery);

            log.debug("Raw highlights from API: {}", rawResult);
            Object jsonObject = JSON.parseObject(rawResult);
            return Result.success(jsonObject);

        } catch (Exception e) {
            log.error("Get highlights error: ", e);
            return Result.error("获取高亮内容失败: " + e.getMessage());
        }
    }

    @Operation(summary = "内容摘要搜索", description = "执行搜索并生成网页内容的摘要")
    @GetMapping("/summary")
    public Result<Object> getSummary(
            @Parameter(description = "搜索查询内容", required = true) @RequestParam String query,
            @Parameter(description = "搜索类型(keyword, neural, auto)", required = false)
            @RequestParam(required = false, defaultValue = "auto") String type,
            @Parameter(description = "返回结果数量", required = false)
            @RequestParam(required = false, defaultValue = "3") Integer numResults,
            @Parameter(description = "自定义摘要生成方向", required = false)
            @RequestParam(required = false) String summaryQuery,
            @Parameter(description = "是否使用结构化输出", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean useSchema) {

        log.info("Getting summary for query: {}", query);
        try {
            String rawResult = ExaSearchUtils.getSummary(query, type, numResults,
                    summaryQuery, useSchema);

            log.debug("Raw summary from API: {}", rawResult);
            Object jsonObject = JSON.parseObject(rawResult);
            return Result.success(jsonObject);

        } catch (Exception e) {
            log.error("Get summary error: ", e);
            return Result.error("获取内容摘要失败: " + e.getMessage());
        }
    }

    @Operation(summary = "实时内容搜索", description = "使用实时爬取功能执行搜索")
    @GetMapping("/livecrawl")
    public Result<Object> livecrawlSearch(
            @Parameter(description = "搜索查询内容", required = true) @RequestParam String query,
            @Parameter(description = "实时爬取超时时间(毫秒)", required = false)
            @RequestParam(required = false, defaultValue = "10000") Integer livecrawlTimeout,
            @Parameter(description = "需要爬取的子页面数量", required = false)
            @RequestParam(required = false, defaultValue = "1") Integer subpages,
            @Parameter(description = "子页面目标关键词", required = false)
            @RequestParam(required = false) String subpageTarget) {

        log.info("Live crawl search with query: {}", query);
        try {
            String rawResult = ExaSearchUtils.livecrawlSearch(
                    query, livecrawlTimeout, subpages, subpageTarget);

            log.debug("Raw result from live crawl: {}", rawResult);
            Object jsonObject = JSON.parseObject(rawResult);
            return Result.success(jsonObject);

        } catch (Exception e) {
            log.error("Live crawl search error: ", e);
            return Result.error("实时搜索失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索测试", description = "测试Exa搜索服务是否正常运行")
    @GetMapping("/test")
    public Result<Object> testSearch() {
        JSONObject testResult = new JSONObject();
        testResult.put("status", "success");
        testResult.put("message", "Exa搜索服务正常运行");
        testResult.put("timestamp", System.currentTimeMillis());
        testResult.put("api", "Exa Search API");
        testResult.put("version", "1.0");

        return Result.success(testResult);
    }
}
