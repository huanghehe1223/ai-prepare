package upc.projectname.projectservice.controller;


import com.openai.models.ChatCompletionMessageParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import upc.projectname.projectservice.entity.ChatRequestDTO;
import upc.projectname.projectservice.utils.MessageProcessUtils;
import upc.projectname.projectservice.utils.StreamRequestUtils;

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
        SseEmitter emitter = new SseEmitter(600000L);
        // 设置完成回调
        emitter.onCompletion(() -> {
            System.out.println("SSE连接已完成");
        });
        // 设置超时回调
        emitter.onTimeout(() -> {
            System.out.println("SSE连接超时");
            emitter.complete();
        });
        // 设置错误回调
        emitter.onError(ex -> {
            System.out.println("SSE连接发生错误: " + ex.getMessage());
        });

        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {

            if (model.equals("deepseek-r1")){
                streamRequestUtils.streamReasonChat(model, newMessages, emitter);
            }
            else {
                streamRequestUtils.streamChat(model, newMessages, emitter);
            }
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
        SseEmitter emitter = new SseEmitter(600000L);
        // 设置完成回调
        emitter.onCompletion(() -> {
            System.out.println("SSE连接已完成");
        });
        // 设置超时回调
        emitter.onTimeout(() -> {
            System.out.println("SSE连接超时");
            emitter.complete();
        });
        // 设置错误回调
        emitter.onError(ex -> {
            System.out.println("SSE连接发生错误: " + ex.getMessage());
        });

        // 使用线程池异步处理，避免阻塞主线程
        executorService.execute(() -> {

            if (model.equals("deepseek-r1")){
                streamRequestUtils.streamReasonChat(model, newMessages, emitter);
            }
            else {
                streamRequestUtils.streamChat(model, newMessages, emitter);
            }
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



}
