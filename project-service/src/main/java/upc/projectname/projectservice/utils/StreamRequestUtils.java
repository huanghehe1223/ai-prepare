package upc.projectname.projectservice.utils;

import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StreamRequestUtils {
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


     public ChatCompletion  simpleChat (String baseUrl,String apiKey,String model,List<ChatCompletionMessageParam> messages) {
        OpenAISdkUtils openAISdkUtils = new OpenAISdkUtils();
        OpenAIClient openAIClient = openAISdkUtils.createOpenAiClient(apiKey, baseUrl);
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
        return chatCompletion;

    }


}
