package upc.projectname.projectservice.utils;

import com.alibaba.fastjson2.JSONObject;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StreamRequestUtils {

    @Autowired OpenAISdkUtils openAISdkUtils;

    /**
     * 处理流式请求并将结果发送到SSE emitter
     *
     * @param baseUrl API基础URL
     * @param apiKey API密钥
     * @param model 模型名称
     * @param messages 对话记录
     * @param emitter SSE发射器
     */
    public void processStreamRequestMessages(String baseUrl, String apiKey, String model, List<ChatCompletionMessageParam> messages, SseEmitter emitter) {
        try {
            OpenAISdkUtils openAISdkUtils = new OpenAISdkUtils();
            OpenAIClient openAIClient = openAISdkUtils.createOpenAiClient(apiKey, baseUrl);



            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(messages)
                    .build();

            // 使用try-with-resources确保资源正确释放
            try (StreamResponse<ChatCompletionChunk> streamResponse =
                         openAIClient.chat().completions().createStreaming(params)) {

                streamResponse.stream().forEach(chunk -> {
                    try {
                        // 检查chunk.choices()是否为空
                        List<ChatCompletionChunk.Choice> choices = chunk.choices();
                        if (choices != null && !choices.isEmpty()) {
                            // 从chunk中提取内容
                            ChatCompletionChunk.Choice choice = choices.get(0);
                            if (choice.delta() != null) {
                                Optional<String> optionalString = choice.delta().content();

                                if (optionalString.isPresent()) {
                                    // 发送数据到客户端
                                    String content = optionalString.get();
                                    emitter.send(content);
                                }
                            }

                            // 检查finishReason是否存在
                            Optional<ChatCompletionChunk.Choice.FinishReason> finishReason = choice.finishReason();

                            // 如果有结束标志，则结束流
                            if (finishReason.isPresent() && finishReason.get().equals(ChatCompletionChunk.Choice.FinishReason.STOP)) {
                                log.warn("接收到结束标志，但是不关闭SSE连接");
                            }
                        }
                    } catch (IOException e) {
                        log.info("发生错误a，关闭SSE连接");
                        emitter.completeWithError(e);
                    }
                });//for each处理结束

                log.warn("所有chunk处理完毕，但是不关闭SSE连接");
            //请求发送结束
            } catch (Exception e) {
                log.info("发生错误b，关闭SSE连接");
                emitter.completeWithError(e);
            }
        } catch (Exception e) {
            log.info("发生错误c，关闭SSE连接");
            emitter.completeWithError(e);
        }
    }

    public void streamChat(String model, List<ChatCompletionMessageParam> messages, SseEmitter emitter) {
        try {

            OpenAIClient openAIClient = openAISdkUtils.defaultClient;
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(messages)
                    .build();

            // 标记是否是第一个chunk
            final boolean[] isFirstChunk = {true};



            // 使用try-with-resources确保资源正确释放
            try (StreamResponse<ChatCompletionChunk> streamResponse =
                         openAIClient.chat().completions().createStreaming(params)) {

                streamResponse.stream().forEach(chunk -> {
                    try {
                        if (model.equals("QwQ-32B")&&isFirstChunk[0]) {
                            isFirstChunk[0] = false;
                            // 发送开始标记
                            JSONObject messageChunk = new JSONObject();
                            messageChunk.put("status","answering");
                            messageChunk.put("content","<think>");
                            emitter.send(messageChunk.toJSONString());
                        }

                        // 检查chunk.choices()是否为空
                        List<ChatCompletionChunk.Choice> choices = chunk.choices();
                        if (choices != null && !choices.isEmpty()) {
                            // 从chunk中提取内容
                            ChatCompletionChunk.Choice choice = choices.get(0);
                            if (choice.delta() != null) {
                                Optional<String> optionalString = choice.delta().content();

                                if (optionalString.isPresent()&&!optionalString.get().isEmpty()) {
                                    // 发送数据到客户端
                                    String content = optionalString.get();
                                    JSONObject messageChunk = new JSONObject();
                                    messageChunk.put("status","answering");
                                    messageChunk.put("content",content);
                                    emitter.send(messageChunk.toJSONString());
                                }
                            }

                            // 检查finishReason是否存在
                            Optional<ChatCompletionChunk.Choice.FinishReason> finishReason = choice.finishReason();

                            // 如果有结束标志，则结束流
                            if (finishReason.isPresent() && finishReason.get().equals(ChatCompletionChunk.Choice.FinishReason.STOP)) {
                                log.warn("接收到结束标志，但是不关闭SSE连接");
                            }
                        }
                        //在这里对每一个chunk进行处理


                    } catch (IOException e) {
                        log.info("发生错误a，关闭SSE连接");
                        emitter.completeWithError(e);
                    }
                });//for each处理结束

                //所有的chunk处理完毕

                log.warn("所有chunk处理完毕，但是不关闭SSE连接");
                //请求发送结束
            } catch (Exception e) {
                log.info("发生错误b，关闭SSE连接");
                emitter.completeWithError(e);
            }
        } catch (Exception e) {
            log.info("发生错误c，关闭SSE连接");
            emitter.completeWithError(e);
        }
    }




/**
 * 处理包含推理过程的流式请求，特别适用于 deepseek-r1 等模型
 * 将推理阶段的内容和回答内容都发送到前端，并在推理阶段结束时发送</think>标记
 *
 * @param model 模型名称
 * @param messages 对话记录
 * @param emitter SSE发射器
 */
public void streamReasonChat(String model, List<ChatCompletionMessageParam> messages, SseEmitter emitter) {
    try {
        OpenAIClient openAIClient = openAISdkUtils.defaultClient;
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .build();

        // 标记是否已经结束了推理阶段
        final boolean[] reasoningEnded = {false};
        // 标记是否发送过内容
        final boolean[] sentAnyReasoningContent = {false};
        // 标记是否是第一个chunk
        final boolean[] isFirstChunk = {true};




        // 使用try-with-resources确保资源正确释放
        try (StreamResponse<ChatCompletionChunk> streamResponse =
                     openAIClient.chat().completions().createStreaming(params)) {

            streamResponse.stream().forEach(chunk -> {
                try {
                    if (isFirstChunk[0]) {
                        isFirstChunk[0] = false;
                        // 发送开始标记
                        JSONObject messageChunk = new JSONObject();
                        messageChunk.put("status","answering");
                        messageChunk.put("content","<think>");
                        emitter.send(messageChunk.toJSONString());
                    }
                    // 检查chunk.choices()是否为空
                    List<ChatCompletionChunk.Choice> choices = chunk.choices();
                    if (choices != null && !choices.isEmpty()) {
                        // 从chunk中提取内容
                        ChatCompletionChunk.Choice choice = choices.get(0);
                        if (choice.delta() != null) {
                            // 处理推理内容
                            if (!reasoningEnded[0] && choice.delta()._additionalProperties()!=null
                                    && choice.delta()._additionalProperties().containsKey("reasoning_content")) {
                                // 获取推理内容
                                String reasoningContent = choice.delta()._additionalProperties()
                                        .get("reasoning_content").toString();
                                sentAnyReasoningContent[0] = true;
                                
                                // 发送推理内容到前端
                                if (!reasoningContent.isEmpty()) {
                                    JSONObject messageChunk = new JSONObject();
                                    messageChunk.put("status","answering");
                                    messageChunk.put("content",reasoningContent);
                                    emitter.send(messageChunk.toJSONString());
                                }
                            }

                            if(!choice.delta()._additionalProperties().containsKey("reasoning_content")){
                                // 如果没有reasoning_content这个key，表示推理阶段已结束
                                if (!reasoningEnded[0] && sentAnyReasoningContent[0]) {
                                    // 发送推理结束标记
                                    JSONObject messageChunk = new JSONObject();
                                    messageChunk.put("status","answering");
                                    messageChunk.put("content","</think>");
                                    emitter.send(messageChunk.toJSONString());
                                    reasoningEnded[0] = true;
                                }

                            }
                            
                            // 处理正常内容
                            Optional<String> optionalString = choice.delta().content();
                            if (optionalString.isPresent()&&!optionalString.get().isEmpty()) {
                                // 发送正常内容到客户端
                                String content = optionalString.get();
                                JSONObject messageChunk = new JSONObject();
                                messageChunk.put("status","answering");
                                messageChunk.put("content",content);
                                emitter.send(messageChunk.toJSONString());
                            }
                        }

                        // 检查finishReason是否存在
                        Optional<ChatCompletionChunk.Choice.FinishReason> finishReason = choice.finishReason();

                        // 如果有结束标志，则标记流结束
                        if (finishReason.isPresent() && finishReason.get().equals(ChatCompletionChunk.Choice.FinishReason.STOP)) {
                            log.warn("接收到结束标志，但是不关闭SSE连接");
                        }
                    }
                } catch (IOException e) {
                    log.info("发生错误a，关闭SSE连接");
                    emitter.completeWithError(e);
                }
            });//for each处理结束

            //所有的chunk处理完毕
            log.warn("所有chunk处理完毕，但是不关闭SSE连接");
            //请求发送结束
        } catch (Exception e) {
            log.info("发生错误b，关闭SSE连接");
            emitter.completeWithError(e);
        }
    } catch (Exception e) {
        log.info("发生错误c，关闭SSE连接");
        emitter.completeWithError(e);
    }
}


    /**
     * 处理流式请求并将结果发送到SSE emitter
     *
     * @param baseUrl API基础URL
     * @param apiKey API密钥
     * @param model 模型名称
     * @param prompt  用户消息
     * @param emitter SSE发射器
     */

    public void processStreamRequestPrompt(String baseUrl, String apiKey, String model, String prompt, SseEmitter emitter) {
        try {
            OpenAISdkUtils openAISdkUtils = new OpenAISdkUtils();
            OpenAIClient openAIClient = openAISdkUtils.createOpenAiClient(apiKey, baseUrl);

            // 创建消息列表
            List<ChatCompletionMessageParam> messages = new ArrayList<>();

            // 添加用户消息
            ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                    .content(prompt)
                    .build();
            messages.add(ChatCompletionMessageParam.ofUser(userMessage));

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(messages)
                    .build();

            // 使用try-with-resources确保资源正确释放
            try (StreamResponse<ChatCompletionChunk> streamResponse =
                         openAIClient.chat().completions().createStreaming(params)) {

                streamResponse.stream().forEach(chunk -> {
                    try {
                        // 检查chunk.choices()是否为空
                        List<ChatCompletionChunk.Choice> choices = chunk.choices();
                        if (choices != null && !choices.isEmpty()) {
                            // 从chunk中提取内容
                            ChatCompletionChunk.Choice choice = choices.get(0);
                            if (choice.delta() != null) {
                                Optional<String> optionalString = choice.delta().content();

                                if (optionalString.isPresent()) {
                                    // 发送数据到客户端
                                    String content = optionalString.get();
                                    emitter.send(content);
                                }
                            }

                            // 检查finishReason是否存在
                            Optional<ChatCompletionChunk.Choice.FinishReason> finishReason = choice.finishReason();

                            // 如果有结束标志，则结束流
                            if (finishReason.isPresent() && finishReason.get().equals(ChatCompletionChunk.Choice.FinishReason.STOP)) {
                                log.warn("接收到结束标志，但是不关闭SSE连接");
                            }
                        }
                    } catch (IOException e) {
                        log.info("发生错误a，关闭SSE连接");
                        emitter.completeWithError(e);
                    }
                });//for each处理结束

                log.warn("所有chunk处理完毕，但是不关闭SSE连接");
                //请求发送结束
            } catch (Exception e) {
                log.info("发生错误b，关闭SSE连接");
                emitter.completeWithError(e);
            }
        } catch (Exception e) {
            log.info("发生错误c，关闭SSE连接");
            emitter.completeWithError(e);
        }
    }


    public ChatCompletion simpleChat(String model,List<ChatCompletionMessageParam> messages) {
        OpenAIClient openAIClient = openAISdkUtils.defaultClient;
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
        return chatCompletion;
    }


     public ChatCompletion  simpleChatWithConfig (String baseUrl,String apiKey,String model,List<ChatCompletionMessageParam> messages) {
        OpenAISdkUtils openAISdkUtils = new OpenAISdkUtils();
        OpenAIClient openAIClient = openAISdkUtils.createOpenAiClient(apiKey, baseUrl);
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
        return chatCompletion;

    }

    /**
     * 创建并配置SSE发射器
     * @param timeoutMillis 超时时间（毫秒）
     * @return 配置好的SseEmitter实例
     */
    public SseEmitter createConfiguredEmitter(long timeoutMillis) {
        SseEmitter emitter = new SseEmitter(timeoutMillis);

        // 设置完成回调
        emitter.onCompletion(() -> {
            log.debug("SSE连接已完成");
        });

        // 设置超时回调
        emitter.onTimeout(() -> {
            log.debug("SSE连接超时");
            emitter.complete();
        });

        // 设置错误回调
        emitter.onError(ex -> {
            log.error("SSE连接发生错误: " + ex.getMessage(), ex);
        });

        return emitter;
    }


    public void StreamRequestChat(String model, List<ChatCompletionMessageParam> messages, SseEmitter emitter){
        if (model.equals("deepseek-r1")){
            streamReasonChat(model, messages, emitter);
        }
        else {
            streamChat(model, messages, emitter);
        }

    }





}
