package upc.projectname.projectservice.utils;

import com.openai.models.ChatCompletionContentPart;
import com.openai.models.ChatCompletionContentPartText;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import upc.projectname.projectservice.entity.ChatAnswerDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageProcessUtils {

    /**
     * 提取消息列表中最后一个用户消息的文本内容
     *
     * @param messages 消息列表
     * @return 最后一个用户消息的文本内容，如果没有有效消息则返回空字符串
     */
    public String extractLastUserMessageText(List<ChatCompletionMessageParam> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }

        // 获取最后一条消息
        ChatCompletionMessageParam lastMessage = messages.get(messages.size() - 1);
        if (!lastMessage.isUser()) {
            return ""; // 最后一条不是用户消息
        }

        // 获取用户消息
        ChatCompletionUserMessageParam userMessage = lastMessage.user().get();
        ChatCompletionUserMessageParam.Content content = userMessage.content();

        // 处理纯文本消息
        if (content.isText()) {
            return content.text().get();
        }

        // 处理图片消息（包含多个ContentParts）
        if (content.isArrayOfContentParts()) {
            List<ChatCompletionContentPart> contentParts = content.arrayOfContentParts().get();
            StringBuilder textContent = new StringBuilder();

            // 遍历所有ContentPart，提取文本部分
            for (ChatCompletionContentPart part : contentParts) {
                if (part.isText()) {
                    ChatCompletionContentPartText textPart = part.text().get();
                    textContent.append(textPart.text());
                }
            }

            return textContent.toString();
        }

        return ""; // 未找到有效的文本内容
    }

    /**
     * 修改消息列表中的最后一条用户消息的内容
     *
     * @param messages 消息列表
     * @param newText 新的文本内容
     * @return 修改后的消息列表
     */
    public List<ChatCompletionMessageParam> modifyLastUserMessage(List<ChatCompletionMessageParam> messages, String newText) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("消息列表不能为空");
        }

        // 创建消息列表的副本，避免直接修改原始列表
        List<ChatCompletionMessageParam> modifiedMessages = new ArrayList<>(messages);

        // 获取最后一条消息
        ChatCompletionMessageParam lastMessage = modifiedMessages.get(modifiedMessages.size() - 1);

        // 验证最后一条消息是否为用户消息
        if (!lastMessage.isUser()) {
            throw new IllegalArgumentException("最后一条消息不是用户消息");
        }

        // 获取用户消息
        ChatCompletionUserMessageParam userMessage = lastMessage.user().get();

        // 获取content
        ChatCompletionUserMessageParam.Content content = userMessage.content();

        // 根据消息类型进行处理
        if (content.isText()) {
            // 纯文本消息处理
            String originalText = content.text().get();
            log.debug("原始文本消息：{}", originalText);

            // 构造新的用户消息
            ChatCompletionUserMessageParam newUserMessage = ChatCompletionUserMessageParam.builder()
                    .content(newText)
                    .build();

            // 替换最后一条消息
            modifiedMessages.set(modifiedMessages.size() - 1, ChatCompletionMessageParam.ofUser(newUserMessage));
        }
        // 图片消息处理
        else if (content.isArrayOfContentParts()) {
            // 获取内容部分列表
            List<ChatCompletionContentPart> contentParts = content.arrayOfContentParts().get();
            List<ChatCompletionContentPart> newContentParts = new ArrayList<>();

            // 遍历所有内容部分
            for (ChatCompletionContentPart part : contentParts) {
                if (part.isText()) {
                    // 处理文本部分
                    ChatCompletionContentPartText originalText = part.text().get();
                    log.debug("图片消息中的原始文本：{}", originalText.text());

                    // 创建新的文本部分
                    ChatCompletionContentPartText newTextPart = ChatCompletionContentPartText.builder()
                            .text(newText)
                            .build();

                    newContentParts.add(ChatCompletionContentPart.ofText(newTextPart));
                }
                else if (part.isImageUrl()) {
                    // 保留图片部分不变
                    newContentParts.add(part);
                }
                else {
                    // 保留其他类型部分不变
                    newContentParts.add(part);
                }
            }

            // 构造新的用户消息
            ChatCompletionUserMessageParam newUserMessage = ChatCompletionUserMessageParam.builder()
                    .contentOfArrayOfContentParts(newContentParts)
                    .build();

            // 替换最后一条消息
            modifiedMessages.set(modifiedMessages.size() - 1, ChatCompletionMessageParam.ofUser(newUserMessage));
        }
        else {
            throw new IllegalArgumentException("不支持的消息内容类型");
        }

        return modifiedMessages;
    }


    /**
     * 在消息列表的最前面添加一条用户消息
     *
     * @param messages 原始消息列表
     * @param firstPrompt 要添加的用户消息内容
     * @return 添加了首条用户消息后的新消息列表
     */
    public List<ChatCompletionMessageParam> addFirstUserMessage(List<ChatCompletionMessageParam> messages, String firstPrompt) {
        if (messages == null) {
            messages = new ArrayList<>();
        }

        // 创建消息列表的副本，避免直接修改原始列表
        List<ChatCompletionMessageParam> newMessages = new ArrayList<>();

        // 构造新的用户消息
        ChatCompletionUserMessageParam newUserMessage = ChatCompletionUserMessageParam.builder()
                .content(firstPrompt)
                .build();

        // 添加新的用户消息作为第一条消息
        newMessages.add(ChatCompletionMessageParam.ofUser(newUserMessage));

        // 添加原有的所有消息
        newMessages.addAll(messages);

        return newMessages;
    }


    /**
     * 在消息列表的最后一条消息之前插入一条新的用户消息
     * （即新消息成为倒数第二条，原最后一条保持为最后一条）
     *
     * @param messages 原始消息列表
     * @param userPrompt 要添加的用户消息内容
     * @return 插入了新用户消息的消息列表
     */
    public List<ChatCompletionMessageParam> insertUserMessageBeforeLast(List<ChatCompletionMessageParam> messages, String userPrompt) {
        // 处理null情况
        if (messages == null) {
            messages = new ArrayList<>();
        }

        // 创建消息列表的副本
        List<ChatCompletionMessageParam> newMessages = new ArrayList<>(messages);

        // 构造新的用户消息
        ChatCompletionUserMessageParam newUserMessage = ChatCompletionUserMessageParam.builder()
                .content(userPrompt)
                .build();

        if (newMessages.isEmpty()) {
            // 如果原列表为空，直接添加新消息
            newMessages.add(ChatCompletionMessageParam.ofUser(newUserMessage));
        } else {
            // 在最后一个位置之前插入新消息
            newMessages.add(newMessages.size() - 1, ChatCompletionMessageParam.ofUser(newUserMessage));
        }

        return newMessages;
    }



    public String formatChatAnswers(List<ChatAnswerDTO> answers) {
        StringBuilder result = new StringBuilder();
        int modelCount = 1;

        for (ChatAnswerDTO answer : answers) {
            if (modelCount > 1) {
                result.append("\n");
            }
            result.append("模型").append(modelCount)
                    .append("(").append(answer.getModelName()).append(")的回答：\n")
                    .append(answer.getAnswerContent());
            modelCount++;
        }

        return result.toString();
    }





}
