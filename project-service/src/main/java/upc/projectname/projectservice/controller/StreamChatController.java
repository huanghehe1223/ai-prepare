package upc.projectname.projectservice.controller;


import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionSystemMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import upc.projectname.projectservice.entity.ChatRequestDTO;
import upc.projectname.projectservice.service.ProjectService;
import upc.projectname.projectservice.utils.MessageProcessUtils;
import upc.projectname.projectservice.utils.PromptUtils;
import upc.projectname.projectservice.utils.StreamRequestUtils;
import upc.projectname.upccommon.domain.po.Project;

import java.util.ArrayList;
import java.util.List;
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
        String newText = """
                %s  请直接生成新一版的学情分析，并把整个内容完整地响应给我。""".formatted(lastUserMessageText);

        //修改最后一条用户消息
        List<ChatCompletionMessageParam> fimalMessages = messageProcessUtils.modifyLastUserMessage(addedFirstUserMessage, newText);
        fimalMessages.forEach(message -> log.debug("消息内容: " + message));
        // 创建一个可以保持连接很长时间的SseEmitter（10分钟超时）
        SseEmitter emitter = streamRequestUtils.createConfiguredEmitter(600000L);
        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {
            streamRequestUtils.StreamRequestChat(model, fimalMessages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });

        return emitter;
    }






}
