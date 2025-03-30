package upc.projectname.projectservice.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import upc.projectname.projectservice.utils.TavilySearchUtils;
import upc.projectname.upccommon.domain.po.Result;

import java.util.List;

/**
 * Tavily搜索服务控制器
 */
@Tag(name = "在线搜索接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class TavilySearchController {

    @Operation(summary = "搜索", description = "执行全功能搜索，支持所有参数的自定义")
    @PostMapping
    public Result<Object> search(
            @Parameter(description = "搜索查询内容", required = true) @RequestParam String query,
            @Parameter(description = "搜索类别: general或news", required = false)
            @RequestParam(required = false, defaultValue = "general") String topic,
            @Parameter(description = "搜索深度: basic或advanced", required = false)
            @RequestParam(required = false, defaultValue = "basic") String searchDepth,
            @Parameter(description = "每个来源的内容块数量(1-3)", required = false)
            @RequestParam(required = false, defaultValue = "3") Integer chunksPerSource,
            @Parameter(description = "最大结果数(0-20)", required = false)
            @RequestParam(required = false, defaultValue = "10") Integer maxResults,
            @Parameter(description = "时间范围(day,week,month,year,d,w,m,y)", required = false)
            @RequestParam(required = false) String timeRange,
            @Parameter(description = "往前推的天数(仅新闻类别有效)", required = false)
            @RequestParam(required = false, defaultValue = "3") Integer days,
            @Parameter(description = "是否包含答案(true/false)", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean includeAnswer,
            @Parameter(description = "是否包含原始内容", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean includeRawContent,
            @Parameter(description = "是否包含图片", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean includeImages,
            @Parameter(description = "是否包含图片描述", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean includeImageDescriptions,
            @Parameter(description = "要包含的域名列表", required = false)
            @RequestParam(required = false) List<String> includeDomains,
            @Parameter(description = "要排除的域名列表", required = false)
            @RequestParam(required = false, defaultValue = "wikipedia.org") List<String> excludeDomains) {

        log.info("Search with query: {}, topic: {}, depth: {}", query, topic, searchDepth);
        try {
            String rawResult = TavilySearchUtils.advancedSearch(
                    query, topic, searchDepth, chunksPerSource, maxResults,
                    timeRange, days, includeAnswer, includeRawContent,
                    includeImages, includeImageDescriptions, includeDomains);
            log.debug("Raw result from search: {}", rawResult);
            // 解析为JSON对象
            Object jsonObject = JSON.parseObject(rawResult);
            return Result.success(jsonObject);

        } catch (Exception e) {
            log.error("Search error: ", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取简洁回答", description = "基于查询内容获取AI生成的简洁回答")
    @GetMapping("/answer")
    public Result<Object> getAnswer(
            @Parameter(description = "搜索问题") @RequestParam String query) {
        log.info("Getting answer for query: {}", query);
        try {
            String rawAnswer = TavilySearchUtils.getAnswer(query);
            log.debug("Raw answer from API: {}", rawAnswer);

            // 尝试解析为JSON对象
            try {
                Object jsonObject = JSON.parseObject(rawAnswer);
                return Result.success(jsonObject);
            } catch (Exception e) {
                // 如果不是有效的JSON，则将结果包装为JSON对象
                JSONObject wrapper = new JSONObject();
                wrapper.put("answer", rawAnswer);
                return Result.success(wrapper);
            }
        } catch (Exception e) {
            log.error("Get answer error: ", e);
            return Result.error("获取答案失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索并提取特定字段", description = "从搜索结果中提取指定字段的数据")
    @GetMapping("/extract")
    public Result<Object> searchAndExtract(
            @Parameter(description = "搜索查询内容") @RequestParam String query,
            @Parameter(description = "要提取的字段名") @RequestParam String field) {
        log.info("Search and extract with query: {}, field: {}", query, field);
        try {
            // 使用主搜索功能，但设置最小参数
            String rawResult = TavilySearchUtils.advancedSearch(
                    query, "general", "basic", 3, 5,
                    null, null, false, false,
                    false, false, null);
            log.debug("Raw result for extraction: {}", rawResult);

            JSONObject jsonObject = JSON.parseObject(rawResult);

            if (jsonObject.containsKey(field)) {
                Object fieldValue = jsonObject.get(field);

                // 如果提取的字段值是字符串且可能是JSON，尝试解析它
                if (fieldValue instanceof String) {
                    String fieldStr = (String) fieldValue;
                    if ((fieldStr.trim().startsWith("{") && fieldStr.trim().endsWith("}")) ||
                            (fieldStr.trim().startsWith("[") && fieldStr.trim().endsWith("]"))) {
                        try {
                            Object parsedField = JSON.parseObject(fieldStr);
                            return Result.success(parsedField);
                        } catch (Exception e) {
                            log.debug("Field value is not valid JSON, returning as string");
                            // 解析失败，返回原始值
                        }
                    }
                }

                return Result.success(fieldValue);
            } else {
                return Result.error("未找到字段: " + field);
            }
        } catch (Exception e) {
            log.error("Search and extract error: ", e);
            return Result.error("搜索提取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索测试", description = "测试Tavily搜索服务是否正常运行")
    @GetMapping("/test")
    public Result<Object> testSearch() {
        JSONObject testResult = new JSONObject();
        testResult.put("status", "success");
        testResult.put("message", "Tavily搜索服务正常运行");
        testResult.put("timestamp", System.currentTimeMillis());

        return Result.success(testResult);
    }
}
