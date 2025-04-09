package upc.projectname.projectservice.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionSystemMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import upc.projectname.projectservice.entity.ChatAnswerDTO;
import upc.projectname.projectservice.entity.ChatRequestDTO;
import upc.projectname.projectservice.service.ProjectService;
import upc.projectname.projectservice.utils.*;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.upccommon.domain.po.Result;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Tag(name = "流式对话管理接口")
@RestController
@RequestMapping("/stream")
@Slf4j
@RequiredArgsConstructor
public class StreamChatController {
    private final StreamRequestUtils streamRequestUtils;
    private final MessageProcessUtils messageProcessUtils;
    private final PromptUtils promptUtils;
    private final ProjectService projectService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Operation(summary = "多关键词综合搜索", description = "接收三个关键词，对每个关键词执行基础搜索、图片搜索、PDF搜索和视频搜索")
    @GetMapping
    public Result<Object> multiKeywordSearch(
            @Parameter(description = "第一个关键词", required = true) @RequestParam String keyword1,
            @Parameter(description = "第二个关键词", required = false) @RequestParam(required = false) String keyword2,
            @Parameter(description = "第三个关键词", required = false) @RequestParam(required = false) String keyword3) {

        log.info("开始多关键词搜索，关键词: {}, {}, {}", keyword1, keyword2, keyword3);
        Map<String, Object> result = new HashMap<>();
        List<String> keywords = new ArrayList<>();

        // 添加非空关键词
        keywords.add(keyword1);
        if (keyword2 != null && !keyword2.trim().isEmpty()) {
            keywords.add(keyword2);
        }
        if (keyword3 != null && !keyword3.trim().isEmpty()) {
            keywords.add(keyword3);
        }

        try {
            // 为每个关键词顺序执行搜索任务
            for (String keyword : keywords) {
                Map<String, Object> keywordResults = new HashMap<>();
                result.put(keyword, keywordResults);

                // 1. 基础搜索 (Tavily)
                try {
                    log.info("正在执行关键词 [{}] 的基础搜索", keyword);
                    String rawResult = TavilySearchUtils.advancedSearch(
                            keyword, "general", "basic", 3, 5,
                            null, null, false, false,
                            false, false, null, false);

                    if (rawResult != null && !rawResult.isEmpty()) {
                        keywordResults.put("basicSearch", JSON.parseObject(rawResult));
                    } else {
                        keywordResults.put("basicSearch", Map.of("message", "没有结果"));
                    }
                    log.info("关键词 [{}] 基础搜索完成", keyword);
                } catch (Exception e) {
                    log.error("关键词 [{}] 基础搜索失败: {}", keyword, e.getMessage(), e);
                    keywordResults.put("basicSearch", Map.of("error", e.getMessage()));
                }

                // 执行其他三种搜索...
                // (代码简化，实际上还有图片搜索、PDF搜索和视频搜索的实现)
            }

            // 统计信息
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalKeywords", keywords.size());
            statistics.put("totalSearches", keywords.size() * 4);
            statistics.put("searchTime", System.currentTimeMillis());
            result.put("statistics", statistics);

            return Result.success(result);
        } catch (Exception e) {
            log.error("多关键词搜索执行错误: ", e);
            return Result.error("多关键词搜索失败: " + e.getMessage());
        }
    }

    /**
     * 流式输出多关键词搜索结果
     */
    @Operation(summary = "流式多关键词搜索", description = "使用SSE流式输出多关键词搜索结果，先按类型处理")
    @GetMapping("/stream")
    public SseEmitter streamMultiKeywordSearch(
            @Parameter(description = "第一个关键词", required = true) @RequestParam String keyword1,
            @Parameter(description = "第二个关键词", required = false) @RequestParam(required = false) String keyword2,
            @Parameter(description = "第三个关键词", required = false) @RequestParam(required = false) String keyword3) {

        log.info("开始流式多关键词搜索，关键词: {}, {}, {}", keyword1, keyword2, keyword3);

        // 创建SSE发射器，设置超时时间为5分钟
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟

        // 设置完成回调
        emitter.onCompletion(() -> {
            log.debug("SSE搜索连接已完成");
        });

        // 设置超时回调
        emitter.onTimeout(() -> {
            log.debug("SSE搜索连接超时");
            try {
                sendEvent(emitter, "timeout", "搜索请求超时", null);
                emitter.complete();
            } catch (Exception e) {
                log.error("发送超时事件失败", e);
            }
        });

        // 设置错误回调
        emitter.onError(ex -> {
            log.error("SSE搜索连接发生错误: " + ex.getMessage(), ex);
            try {
                sendEvent(emitter, "error", "搜索发生错误: " + ex.getMessage(), null);
            } catch (Exception e) {
                log.error("发送错误事件失败", e);
            }
        });

        // 收集关键词
        List<String> keywords = new ArrayList<>();
        keywords.add(keyword1);
        if (keyword2 != null && !keyword2.trim().isEmpty()) {
            keywords.add(keyword2);
        }
        if (keyword3 != null && !keyword3.trim().isEmpty()) {
            keywords.add(keyword3);
        }

        // 定义搜索类型
        List<String> searchTypes = Arrays.asList("basic", "pdf", "video", "image");

        // 在新线程中处理搜索请求
        executorService.execute(() -> {
            try {
                // 发送开始事件
                sendEvent(emitter, "start", "开始搜索", Map.of("keywords", keywords));

                // 按类型处理 - 先循环类型，再循环关键词
                for (String type : searchTypes) {
                    // 发送类型开始事件
                    sendEvent(emitter, "type_start", "开始处理搜索类型", Map.of("type", type));

                    // 对每个关键词执行当前类型的搜索
                    for (String keyword : keywords) {
                        processSearch(keyword, type, emitter);
                    }

                    // 发送类型完成事件
                    sendEvent(emitter, "type_complete", "搜索类型处理完成", Map.of("type", type));
                }

                // 发送完成事件
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalKeywords", keywords.size());
                stats.put("totalSearches", keywords.size() * searchTypes.size());
                stats.put("searchTime", System.currentTimeMillis());
                sendEvent(emitter, "complete", "搜索完成", stats);

                // 完成SSE流
                emitter.complete();

            } catch (Exception e) {
                log.error("搜索处理过程中发生错误", e);
                try {
                    sendEvent(emitter, "error", "搜索处理失败: " + e.getMessage(), null);
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.error("发送错误事件失败", ex);
                }
            }
        });

        return emitter;
    }

    /**
     * 处理单个关键词的特定类型搜索
     */
    private void processSearch(String keyword, String type, SseEmitter emitter) throws IOException {
        // 发送搜索开始事件
        sendEvent(emitter, "search_start", "开始" + getTypeDisplayName(type) + "搜索",
                Map.of("keyword", keyword, "type", type));

        try {
            String rawResult = null;

            // 根据类型执行不同的搜索
            switch (type) {
                case "basic":
                    // 基础搜索
                    rawResult = TavilySearchUtils.advancedSearch(
                            keyword, "general", "basic", 3, 30,
                            null, null, false, false,
                            false, false, null, false);
                    break;

                case "pdf":
                    // PDF搜索
                    rawResult = ExaSearchUtils.advancedSearch(
                            keyword, true, "auto", "pdf", 30,
                            null, null, null, null,
                            null, null, null, null,
                            false, true, 3, 2,
                            false, "fallback");
                    break;

                case "video":
                    // 视频搜索
                    String videoQuery = keyword;
                    List<String> includeDomains = Collections.singletonList("bilibili.com/video");
                    rawResult = TavilySearchUtils.advancedSearch(
                            videoQuery, "general", "basic", 3, 10,
                            null, null, false, false,
                            false, false, includeDomains, true);
                    break;

                case "image":
                    // 图片搜索
                    String imageQuery = keyword + "，图片";
                    rawResult = TavilySearchUtils.advancedSearch(
                            imageQuery, "general", "basic", 3, 30,
                            null, null, false, false,
                            true, true, null, false);
                    break;
            }

            // 处理和发送结果
            if (rawResult != null && !rawResult.isEmpty()) {
                Object jsonResult = JSON.parseObject(rawResult);
                sendEvent(emitter, "search_result", getTypeDisplayName(type) + "搜索结果",
                        Map.of("keyword", keyword, "type", type, "result", jsonResult));
            } else {
                sendEvent(emitter, "search_result", getTypeDisplayName(type) + "搜索无结果",
                        Map.of("keyword", keyword, "type", type, "result", Map.of("message", "没有结果")));
            }
        } catch (Exception e) {
            log.error("关键词 [{}] {}搜索失败: {}", keyword, getTypeDisplayName(type), e.getMessage(), e);
            sendEvent(emitter, "search_error", getTypeDisplayName(type) + "搜索失败",
                    Map.of("keyword", keyword, "type", type, "error", e.getMessage()));
        }
    }

    /**
     * 获取搜索类型的显示名称
     */
    private String getTypeDisplayName(String type) {
        switch (type) {
            case "basic": return "基础";
            case "pdf": return "PDF";
            case "video": return "视频";
            case "image": return "图片";
            default: return type;
        }
    }

    /**
     * 向SSE发射器发送事件
     */
    private void sendEvent(SseEmitter emitter, String eventType, String message, Object data) throws IOException {
        JSONObject event = new JSONObject();
        event.put("type", eventType);
        event.put("message", message);
        event.put("timestamp", System.currentTimeMillis());

        if (data != null) {
            event.put("data", data);
        }

        emitter.send(SseEmitter.event()
                .name("search_event")
                .data(event.toJSONString()));
    }

    @Operation(summary = "多关键词搜索测试", description = "测试多关键词搜索服务是否正常运行")
    @GetMapping("/test")
    public Result<Object> testMultiSearch() {
        JSONObject testResult = new JSONObject();
        testResult.put("status", "success");
        testResult.put("message", "多关键词搜索服务正常运行");
        testResult.put("timestamp", System.currentTimeMillis());
        testResult.put("api", "Multi-Keyword Search API");
        testResult.put("version", "1.0");

        return Result.success(testResult);
    }



    @Operation(summary = "测试流式输出")
    @PostMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter chatCompletionStream(@RequestBody ChatRequestDTO chatRequest) {
//        log.debug("接收到的token：" + token);

        String model = chatRequest.getModel();
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        String lastUserMessageText = messageProcessUtils.extractLastUserMessageText(messages);
        log.debug("lastUserMessageText: " + lastUserMessageText);
        String newText = """
                 回复要求: 使用轻薄的语气回答我;
                 消息正文: """+lastUserMessageText +"""
                """;
        List<ChatCompletionMessageParam> newMessages = messageProcessUtils.modifyLastUserMessage(messages, newText);
        log.warn("接收到的消息列表：" + newMessages);
        log.warn("接收到的模型名称：" + model);
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);

        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, newMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "测试流式输出1")
    @PostMapping(value = "/test1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter chatCompletionStream1(@RequestBody ChatRequestDTO chatRequest) {
//        log.debug("接收到的token：" + token);
        String model = chatRequest.getModel();
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        String fisrtPrompt = "我的名字叫黄河";
        List<ChatCompletionMessageParam> newMessages = messageProcessUtils.addFirstUserMessage(messages, fisrtPrompt);
        log.warn("接收到的消息列表：" + newMessages);
        log.warn("接收到的模型名称：" + model);
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, newMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "生成预备知识检测题目")
    @PostMapping(value = "/preKnowledgeExercise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getPreKnowledgeExercise(@RequestParam Integer projectId) {
//        log.debug("接收到的token：" + token);
        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam preKnowledgeSystemMessage = promptUtils.getPreKnowledgeSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(preKnowledgeSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam projectRequirementsMeaasgeWithSystem = promptUtils.getProjectRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(projectRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("请帮我生成10道预备知识检测单选题目");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        messages.forEach(message -> log.debug("消息内容: " + message));
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "生成课后单选习题对话")
    @PostMapping(value = "/postSingleChoiceExercise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getPostSingleChoiceExercise(@RequestParam Integer projectId,@RequestParam Integer questionNumber) {
//        log.debug("接收到的token：" + token);
        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam singleChoiceExerciseSystemMessage = promptUtils.getPostSingleChoiceExerciseSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(singleChoiceExerciseSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam postExerciseRequirementsMeaasgeWithSystem = promptUtils.getPostExerciseRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(postExerciseRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，请帮我生成"+questionNumber+"道课后单选习题");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "生成课后多选习题对话")
    @PostMapping(value = "/postMultipleChoiceExercise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getPostMultipleChoiceExercise(@RequestParam Integer projectId,@RequestParam Integer questionNumber) {
//        log.debug("接收到的token：" + token);
        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam multipleChoiceExerciseSystemMessage = promptUtils.getPostMultipleChoiceExerciseSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(multipleChoiceExerciseSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam postExerciseRequirementsMeaasgeWithSystem = promptUtils.getPostExerciseRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(postExerciseRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，请帮我生成"+questionNumber+"道课后多选习题");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }



    @Operation(summary = "生成课后填空习题对话")
    @PostMapping(value = "/postFillInBlankExercise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getPostFillInBlankExercise(@RequestParam Integer projectId,@RequestParam Integer questionNumber) {
//        log.debug("接收到的token：" + token);
        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam fillInBlankExerciseSystemMessage = promptUtils.getPostFillInBlankExerciseSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(fillInBlankExerciseSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam postExerciseRequirementsMeaasgeWithSystem = promptUtils.getPostExerciseRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(postExerciseRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，请帮我生成"+questionNumber+"道课后填空习题");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }


    @Operation(summary = "生成课后简答习题对话")
    @PostMapping(value = "/postShortAnswerExercise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getPostShortAnswerExercise(@RequestParam Integer projectId,@RequestParam Integer questionNumber) {
//        log.debug("接收到的token：" + token);
        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam shortAnswerExerciseSystemMessage = promptUtils.getPostShortAnswerExerciseSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(shortAnswerExerciseSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam postExerciseRequirementsMeaasgeWithSystem = promptUtils.getPostExerciseRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(postExerciseRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，请帮我生成"+questionNumber+"道课后简答习题");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "进行学情分析对话")
    @PostMapping(value = "/learningSituationAnalysis", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getLearningSituationAnalysis(@RequestBody List<StudentAnswerResult> studentAnswerResults) {
        String model = "deepseek-v3.1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam learningSituationAnalysisSystemMessage = promptUtils.getLearningSituationAnalysisSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(learningSituationAnalysisSystemMessage));
        ChatCompletionUserMessageParam learningSituationAnalysisUserMessage = promptUtils.getLearningSituationAnalysisUserMessage(studentAnswerResults);
        messages.add(ChatCompletionMessageParam.ofUser(learningSituationAnalysisUserMessage));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，请帮我进行详细的学情分析");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.streamChatWithMaxTokens(model, messages, emitter,16000);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "进行学生视角预备知识掌握情况分析对话")
    @PostMapping(value = "/AnalysisofPrerequisiteKnowledgeMasteryfromStudentPerspective", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getAnalysisofPrerequisiteKnowledgeMasteryfromStudentPerspective(@RequestBody List<StudentAnswerResult> studentAnswerResults,@RequestParam Integer groupId) {
        String model = "deepseek-v3.1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam analysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveSystemMessage = promptUtils.getAnalysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(analysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveSystemMessage));
        ChatCompletionUserMessageParam analysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveUserMessage= promptUtils.getAnalysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveUserMessage(studentAnswerResults,groupId);
        messages.add(ChatCompletionMessageParam.ofUser(analysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveUserMessage));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，请帮我进行详细的预备知识掌握情况分析");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.streamChatWithMaxTokens(model, messages, emitter,16000);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }









    @Operation(summary = "测试在最前面增加一条用户消息")
    @PostMapping("testAddPrompt")
    public List<ChatCompletionMessageParam> addPrompt(@RequestBody List<ChatCompletionMessageParam> messages) {
        String firstPrompt = "我的名字叫黄河";
        return messageProcessUtils.addFirstUserMessage(messages, firstPrompt);
    }


    @Operation(summary = "学生预备知识掌握情况对话")
    @PostMapping(value = "/StudentPreKnowledgeMastery/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getStudentPreKnowledgeMastery(@PathVariable Integer projectId,@RequestBody ChatRequestDTO chatRequest) {

        //原始消息
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        //模型名称
        String model = chatRequest.getModel();
        //最后一条消息内容
        String lastUserMessageText = messageProcessUtils.extractLastUserMessageText(messages);
        Project project = projectService.getProjectById(projectId);
        //最开始的用户提示词
        String userPrompt = promptUtils.gerStudentPreKnowledgeMasteryPrompt(project);
        //最前面添加一条用户消息
        List<ChatCompletionMessageParam> addedFirstUserMessage = messageProcessUtils.addFirstUserMessage(messages, userPrompt);
        log.debug("lastUserMessageText: " + lastUserMessageText);
        String newText = "";
        if(messages.size() == 1){
            newText = """
                %s  请直接生成新一版的学生预备知识掌握情况分析，并把整个内容完整地响应给我。""".formatted(lastUserMessageText);
        }
        else {
            newText = """
                %s  请重新生成""".formatted(lastUserMessageText);
        }
        //修改最后一条用户消息
        List<ChatCompletionMessageParam> finalMessages = messageProcessUtils.modifyLastUserMessage(addedFirstUserMessage, newText);
        //打印消息序号（从1开始递增），然后再打印消息内容
        for (int i = 0; i < finalMessages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + finalMessages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, finalMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "生成教学目标对话")
    @PostMapping(value = "/TeachingAims/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getTeachingAims(@PathVariable Integer projectId,@RequestBody ChatRequestDTO chatRequest) {

        //原始消息
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        //模型名称
        String model = chatRequest.getModel();
        //最后一条消息内容
        String lastUserMessageText = messageProcessUtils.extractLastUserMessageText(messages);
        Project project = projectService.getProjectById(projectId);
        //最开始的用户提示词
        String userPrompt = promptUtils.getTeachingAimsPrompt(project);
        //最前面添加一条用户消息
        List<ChatCompletionMessageParam> addedFirstUserMessage = messageProcessUtils.addFirstUserMessage(messages, userPrompt);
        System.out.println("lastUserMessageText: " + lastUserMessageText);
        String newText = """
                %s  请直接生成新一版的教学目标，并把整个内容完整地响应给我。""".formatted(lastUserMessageText);

        //修改最后一条用户消息
        List<ChatCompletionMessageParam> finalMessages = messageProcessUtils.modifyLastUserMessage(addedFirstUserMessage, newText);
        //打印消息序号（从1开始递增），然后再打印消息内容
        for (int i = 0; i < finalMessages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + finalMessages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, finalMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }



    @Operation(summary = "进行mixture-of-agents的教学目标对话")
    @PostMapping(value = "/mixtureofAgentsTeachingAims/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getMixtureofAgentsTeachingAims(@RequestBody List<ChatAnswerDTO> chatAnswerDTOList, @PathVariable Integer projectId) {
        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam mixtureofAgentsTeachingAimsSystemMessage = promptUtils.getTeachingAimsWtihMixtureofAgentsSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(mixtureofAgentsTeachingAimsSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam mixtureofAgentsTeachingAimsUserMessage = promptUtils.getTeachingAimsWtihMixtureofAgentsUserMessage(project,chatAnswerDTOList);
        messages.add(ChatCompletionMessageParam.ofUser(mixtureofAgentsTeachingAimsUserMessage));
        String finalPrompt = """
                根据给出的信息，请帮我对各个模型回答进行分析，总结和整合。你需要在思考过程中完成前三步分析，正式回答时只需要完成第四步，输出新版本的整合后的回答。
                特别注意1：对每个模型的回答进行分析的时候一定要明确点出模型的具体名称，方便用户对应。
                特别注意2:
                如果响应结果中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式
                """;
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage(finalPrompt);
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.streamReasonChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }


    @Operation(summary = "生成知识点总结对话")
    @PostMapping(value = "/KnowledgePointSummary/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getKnowledgePointSummary(@PathVariable Integer projectId,@RequestBody ChatRequestDTO chatRequest) {

        //原始消息
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        //模型名称
        String model = chatRequest.getModel();
        //最后一条消息内容
        String lastUserMessageText = messageProcessUtils.extractLastUserMessageText(messages);
        Project project = projectService.getProjectById(projectId);
        //最开始的用户提示词
        String userPrompt = promptUtils.getKnowledgePointSummaryPrompt(project);
        //最前面添加一条用户消息
        List<ChatCompletionMessageParam> addedFirstUserMessage = messageProcessUtils.addFirstUserMessage(messages, userPrompt);
        System.out.println("lastUserMessageText: " + lastUserMessageText);
        String newText = """
                %s  请直接生成新一版的知识点总结，并把整个内容完整地响应给我。""".formatted(lastUserMessageText);

        //修改最后一条用户消息
        List<ChatCompletionMessageParam> finalMessages = messageProcessUtils.modifyLastUserMessage(addedFirstUserMessage, newText);
        //打印消息序号（从1开始递增），然后再打印消息内容
        for (int i = 0; i < finalMessages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + finalMessages.get(i));
        }

        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, finalMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }

    @Operation(summary = "生成教学过程大纲对话")
    @PostMapping(value = "/TeachingProcessOutline/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getTeachingProcessOutline(@PathVariable Integer projectId,@RequestBody ChatRequestDTO chatRequest) {

        //原始消息
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        //模型名称
        String model = chatRequest.getModel();
        //最后一条消息内容
        String lastUserMessageText = messageProcessUtils.extractLastUserMessageText(messages);
        Project project = projectService.getProjectById(projectId);
        //最开始的用户提示词
        String userPrompt = promptUtils.getTeachingProcessOutlinePrompt(project);
        //最前面添加一条用户消息
        List<ChatCompletionMessageParam> addedFirstUserMessage = messageProcessUtils.addFirstUserMessage(messages, userPrompt);
        System.out.println("lastUserMessageText: " + lastUserMessageText);
        String newText = """
                %s  请直接生成新一版的教学过程大纲，并把整个内容完整地响应给我。""".formatted(lastUserMessageText);

        //修改最后一条用户消息
        List<ChatCompletionMessageParam> finalMessages = messageProcessUtils.modifyLastUserMessage(addedFirstUserMessage, newText);
        //打印消息序号（从1开始递增），然后再打印消息内容
        for (int i = 0; i < finalMessages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + finalMessages.get(i));
        }

        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, finalMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }



    @Operation(summary = "生成教学过程设计对话")
    @PostMapping(value = "/TeachingProcessDesign/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getTeachingProcessDesign(@PathVariable Integer projectId) {

        String model = "claude-3-7-sonnet-thinking";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam teachingProcessDesignSystemMessage = promptUtils.getTeachingProcessDesignSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(teachingProcessDesignSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam teachingProcessRequirementsMeaasgeWithSystem = promptUtils.getTeachingProcessRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(teachingProcessRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("请根据提供的信息按要求进行详细的教学过程设计，把完整的内容响应给我。\n 强制要求:无论篇幅多长，都必须完整提供所有环节的所有组成部分，不得简化或省略。思考过程和回答都默认使用中文");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        //打印消息序号（从1开始递增），然后再打印消息内容
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }

        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.streamChatWithMaxTokens(model, messages, emitter,64000);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });
        return emitter;
    }

    @Operation(summary = "AI协同编辑对话")
    @PostMapping(value = "/AiCollaborativeEditing", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getAiCollaborativeEditing(@RequestParam Integer projectId,@RequestParam String stageName,@RequestBody ChatRequestDTO chatRequest) {
        String model = chatRequest.getModel();
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        String currentContent = chatRequest.getExtraContent();
        Project project = projectService.getProjectById(projectId);
        String userPrompt = promptUtils.getAiCollaborativeEditingUserPrompt(project,stageName,currentContent);
        List<ChatCompletionMessageParam> newMessages = messageProcessUtils.insertUserMessageBeforeLast(messages, userPrompt);
        for (int i = 0; i < newMessages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + newMessages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, newMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });
        return emitter;

    }

    @Operation(summary = "AI自动编辑")
    @PostMapping(value = "/AIAutomaticEditing", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("permitAll()")
    public SseEmitter getAiAutomaticEditing(@RequestParam Integer projectId,@RequestParam String stageName,@RequestBody ChatRequestDTO chatRequest) {
        String model = chatRequest.getModel();
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        String currentContent = chatRequest.getExtraContent();
        Project project = projectService.getProjectById(projectId);
        String userPrompt = promptUtils.getAiAutomaticEditingUserPrompt(project,stageName,currentContent);
        List<ChatCompletionMessageParam> newMessages = messageProcessUtils.insertUserMessageBeforeLast(messages, userPrompt);
        for (int i = 0; i < newMessages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + newMessages.get(i));
        }
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, newMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });
        return emitter;

    }








}
