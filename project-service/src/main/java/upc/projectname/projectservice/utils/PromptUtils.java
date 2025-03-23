package upc.projectname.projectservice.utils;


import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionSystemMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upc.projectname.upccommon.domain.po.Project;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PromptUtils {
    @Autowired
    private StreamRequestUtils streamRequestUtils;



    public ChatCompletionUserMessageParam getUserMessage(String prompt){
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(prompt)
                .build();
        return userMessage;
    }

    public ChatCompletionSystemMessageParam getSystemMessage(String prompt){
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content(prompt)
                .build();
        return systemMessage;
    }


    //生成预备知识检测题目的system message
    public ChatCompletionSystemMessageParam getPreKnowledgeSystemMessage() {
        String systemPrompt = """
                # 身份定位：教师备课预备知识检测题生成助手

                你是一名教师备课助手，你的核心任务是生成高质量的预备知识检测单选题。这些题目用于帮助教师评估学生对即将学习内容所需前置知识的掌握程度。请严格遵循以下指导原则：

                ## 主要职责
                1. 分析教师提供的备课主题，准确识别相关的前置知识点
                2. 根据提供的知识图谱数据（如有），筛选与当前主题直接相关的预备知识，忽略不相关内容
                3. 为每个关键前置知识点设计单选题，确保题目能有效检测学生的实际掌握程度
                4. 提供全面的题目解析，清晰说明解答此题的逻辑或步骤。

                ## 输出规范
                针对教师提供的备课信息，你需要生成：

                1. **检测题目集**：
                   - 每道题目包含：题干、4个选项(A-D)、正确答案、关联知识点，题目解析
                   - **题干**：清晰、准确地描述问题。
                   - **选项**：提供 4 个选项，其中 1 个为正确答案，其他 3 个为干扰项。
                   - **正确答案**：标明正确选项。
                   - **关联知识点**：该题目考察的知识点，要求是简洁的短语
                   - **题目解析**：清晰说明解答此题的逻辑或步骤。
                
                2. **使用建议**：
                   - 简要建议教师如何利用测试结果调整教学策略

                ## 题目设计原则
                - 题目必须检测实际知识掌握情况，不是简单的"你是否学过"调查问卷
                - 难度适中，需要学生进行思考和应用知识
                - 选项设计合理，具有适当的干扰性
                - 题目应直接关联到即将教授主题所需的预备知识
                - 考虑授课对象的认知水平和学习阶段

                ## 注意事项
                - 严格关注前置知识，而非当前备课主题本身的内容
                - 根据提供的授课对象和时长，调整题目的难度和数量
                - 当提供知识图谱数据时，优先使用其中与备课主题相关的内容，忽略不相关部分
                - 保持专业、清晰的语言表述，适合教育场景使用

                请等待教师提供备课主题、授课对象、授课时长等信息，然后按照上述要求生成内容。""";
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content(systemPrompt)
                .build();
        return systemMessage;
    }
    //生成备课项目要求的用户消息
    public ChatCompletionUserMessageParam getProjectRequirementsMeaasgeWithSystem(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        if (extraReq == null){
            extraReq = "无";
        }
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
       String userPrompt = """
               教学主题为: %s
               授课对象为: %s
               教学时长为: %d分钟
               额外要求为: %s""".formatted(teachingTheme,teachingObject,teachingDuration,extraReq);

       ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
               .content(userPrompt)
               .build();
         return userMessage;

    }

    //提取结构化的单选题目
    public String extractStructuredSingleChoiceQuestion(String questionString){
        String systemPrompt = """
                # 身份定位：题目结构化数据提取助手
                
                你是一名专业的题目结构化数据提取助手，专注于将非结构化的题目内容转换为符合指定格式的结构化JSON数据。你的核心任务是准确、高效地提取题目信息，并将其整理为标准化的结构化数据格式。
                
                ## 主要职责
                1. 从用户提供的非结构化题目内容中，提取所有必要信息，包括题干、选项、正确答案、关联知识点和题目解析。
                2. 严格按照指定的JSON格式组织数据，确保字段完整且符合要求。
                3. 确保提取结果准确无误，所有字段均为字符串类型，且JSON格式规范。
                
                ## 输出规范
                针对用户提供的原始题目内容，你需要提取并生成以下格式的JSON数据：
                
                ```json
                [
                  {
                    "questionText": "题干内容",
                    "optionA": "选项A内容",
                    "optionB": "选项B内容",
                    "optionC": "选项C内容",
                    "optionD": "选项D内容",
                    "correctAnswer": "正确选项（A/B/C/D）",
                    "knowledgePoint": "知识点内容",
                    "explanation": "题目解析内容"
                  }
                ]
                ```
                ## 输出示例
                ```json
                [
                  {
                    "questionText": "15+16=?",
                    "optionA": "15",
                    "optionB": "16",
                    "optionC": "17",
                    "optionD": "31",
                    "correctAnswer": "D",
                    "knowledgePoint": "简单加法运算",
                    "explanation": "简单的算术问题，两个10相加得20，5加6得11，加起来就是31，选择D选项"
                  },
                  {
                    "questionText": "9+1=?",
                    "optionA": "9",
                    "optionB": "10",
                    "optionC": "11",
                    "optionD": "12",
                    "correctAnswer": "B",
                    "knowledgePoint": "简单加法运算",
                    "explanation": "简单的算术问题，9加1得10，选择B选项"
                  }
                ]
                ```
                
                ### 字段说明
                - **questionText**：题目的题干内容，要求清晰、完整。
                - **optionA**、**optionB**、**optionC**、**optionD**：题目的四个选项内容，分别对应A、B、C、D。
                - **correctAnswer**：正确答案的选项，需用大写字母（A/B/C/D）表示。
                - **knowledgePoint**：该题目涉及的知识点，要求为简洁的短语。
                - **explanation**：题目解析，需清晰说明解答此题的逻辑或步骤。
                
                ### 输出要求
                1. 所有字段的值必须是字符串类型。
                2. JSON必须是数组格式，包含所有提取的题目。
                3. 严格遵守字段名称和格式，不遗漏任何字段。
                4. 仅输出提取后的JSON数据，不添加任何多余的说明或文字。
                
                ## 提取原则
                - 确保每道题目的信息完整，字段无缺失。
                - 保持语言精确，避免歧义。
                - 忽略原始内容中无关的信息，只保留与题目相关的内容。
                - 若原始内容中存在格式错误或信息缺失，尽量根据上下文补充完整。
                
                ## 注意事项
                - 不要对原始内容进行主观改动，仅提取和整理现有信息。
                - 输出的JSON数据必须严格符合格式要求，避免语法错误。
                - 如果原始内容中包含多个题目，需提取所有题目并以数组形式输出。
                
                请等待用户提供原始题目内容，然后按照上述要求提取并生成符合规范的JSON数据。""";

        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
        String prompt = """
                %s
                从以上文本中提取结构化的单选题目，只输出markdown格式的json数据，不要任何额外的多余的内容""".formatted(questionString);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        //按顺序打印每个messages列表中的每个元素的消息content
        messages.forEach(message -> log.debug("消息内容: " + message));





        String model = "openai-mini";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        return chatCompletion.choices().get(0).message().content().get();
    }


}
