package upc.projectname.projectservice.utils;


import com.openai.models.ChatCompletionSystemMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import org.springframework.stereotype.Component;
import upc.projectname.upccommon.domain.po.Project;

@Component
public class PromptUtils {



    public ChatCompletionUserMessageParam getUserMessage(String prompt){
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(prompt)
                .build();
        return userMessage;
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
                4. 提供全面的题目解析，阐明每道题目考察的具体知识点及其与主课题的关联
                                
                ## 输出规范
                针对教师提供的备课信息，你需要生成：
                                
                1. **检测题目集**：
                   - 每道题目包含：题干、4个选项(A-D)、正确答案、详细解析
                                
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


}
