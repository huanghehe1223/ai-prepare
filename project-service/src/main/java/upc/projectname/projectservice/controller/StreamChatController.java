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
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
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
    public SseEmitter getLearningSituationAnalysis(List<StudentAnswerResult> studentAnswerResults) {
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

        String model = "deepseek-r1";
        List<ChatCompletionMessageParam> messages = new ArrayList<> ();
        ChatCompletionSystemMessageParam teachingProcessDesignSystemMessage = promptUtils.getTeachingProcessDesignSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(teachingProcessDesignSystemMessage));
        Project project = projectService.getProjectById(projectId);
        ChatCompletionUserMessageParam teachingProcessRequirementsMeaasgeWithSystem = promptUtils.getTeachingProcessRequirementsMeaasgeWithSystem(project);
        messages.add(ChatCompletionMessageParam.ofUser(teachingProcessRequirementsMeaasgeWithSystem));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("请根据提供的信息按要求进行详细的教学过程设计，把完整的内容响应给我。\n 强制要求:无论篇幅多长，都必须完整提供所有环节的所有组成部分，不得简化或省略");
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
            streamRequestUtils.StreamRequestChat(model, messages, emitter);
            emitter.complete();
            log.warn("所有响应处理完毕，在外面关闭SSE连接");
        });
        return emitter;
    }









}
