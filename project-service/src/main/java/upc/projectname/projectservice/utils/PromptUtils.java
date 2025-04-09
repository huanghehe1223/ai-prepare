package upc.projectname.projectservice.utils;


import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionSystemMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upc.projectname.projectservice.entity.ChatAnswerDTO;
import upc.projectname.projectservice.service.ProjectService;
import upc.projectname.upccommon.api.client.QuestionGroupClient;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.upccommon.domain.po.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class PromptUtils {
    @Autowired
    private StreamRequestUtils streamRequestUtils;
    @Autowired
    private QuestionGroupClient questionGroupClient;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MessageProcessUtils messageProcessUtils;



    //传入prompt,构建用户消息
    public ChatCompletionUserMessageParam getUserMessage(String prompt){
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(prompt)
                .build();
        return userMessage;
    }
    //传入prompt,构建system消息
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

                你是一名教师备课助手，你的核心任务是生成高质量的预备知识检测单选题。这些题目用于帮助教师评估学生对即将学习内容所需前置知识的掌握程度。

                ## 主要职责
                1. 分析教师提供的备课主题，准确识别相关的前置知识点
                2. 根据提供的知识图谱数据（如有），筛选与当前主题直接相关的内容，忽略不相关内容
                3. 为每个关键前置知识点设计单选题，确保题目能有效检测学生的实际掌握程度
                4. 提供全面的题目解析，清晰说明解答此题的逻辑或步骤

                ## 输出规范
                针对教师提供的备课信息，你必须完整生成以下内容，不得以任何理由省略或简化：

                1. **检测题目集**：
                   - 根据教师要求生成指定数量的题目（如未指定，默认生成10道题）
                   - 每两道题目之间使用分割线（---）隔开
                   - 每道题目必须完整包含以下所有部分：
                     - **题号和题干**：清晰、准确地描述问题
                     - **四个选项**：必须提供完整的A、B、C、D四个选项，其中1个为正确答案，其他3个为干扰项
                     - **正确答案**：明确标明哪一个是正确选项
                     - **关联知识点**：该题目考察的具体知识点（简洁短语）
                     - **题目解析**：清晰说明解答此题的逻辑或步骤

                2. **使用建议**：
                   - 针对这套题目提供具体的教学建议，包括如何根据测试结果调整教学策略

                ## 公式输出格式
                如果题目中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式

                ## 题目设计原则
                - 题目必须检测实际知识掌握情况，不是简单的"你是否学过"调查问卷
                - 难度适中，需要学生进行思考和应用知识
                - 选项设计合理，具有适当的干扰性
                - 题目应直接关联到即将教授主题所需的预备知识
                - 考虑授课对象的认知水平和学习阶段

                ## 强制要求
                - 无论篇幅多长，都必须完整提供每道题目的所有组成部分，不得简化或省略
                - 不得以"篇幅限制"为由减少题目数量或简化题目内容
                - 如果教师指定了题目数量，必须严格按照要求生成，不多不少
                - 所有题目必须包含完整的四个选项和详细解析
                - 严格关注前置知识，而非当前备课主题本身的内容

                请等待教师提供备课主题、授课对象、授课时长和所需题目数量等信息，然后按照上述要求生成完整内容。""";
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content(systemPrompt)
                .build();
        return systemMessage;
    }


    //生成备课项目要求的用户消息，用于预备知识检测题目的生成
    public ChatCompletionUserMessageParam getProjectRequirementsMeaasgeWithSystem(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        //教材相关内容
        String textbookContent = project.getTextbookContent();

        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }

        String userPrompt = "";
        if (textbookContent == null||textbookContent.isEmpty()){
            userPrompt = """
                    教学主题为: %s
                    授课对象为: %s
                    教学时长为: %d分钟
                    额外要求为: %s""".formatted(teachingTheme,teachingObject,teachingDuration,extraReq);
        }
        else{
            userPrompt = """
               教学主题为: %s
               授课对象为: %s
               教学时长为: %d分钟
               额外要求为: %s
               <attachment>
               #教材知识图谱中检索到的内容:
               将教学主题作为查询参数从教材的图数据库里面检索到的节点与关系
               ```json
               %s
               ```
               </attachment>
               对于检索到的内容，只考虑与教学主题直接相关的内容，忽略不相关内容""".formatted(teachingTheme,teachingObject,teachingDuration,extraReq,textbookContent);
        }
        ChatCompletionUserMessageParam userMessage = getUserMessage(userPrompt);

        return userMessage;

    }



    //生成课后单选习题的system message
    public ChatCompletionSystemMessageParam getPostSingleChoiceExerciseSystemMessage() {
        String systemPrompt = """
                # 身份定位：教师备课课后单选习题生成助手

                你是一名专业的教学内容设计专家，专门帮助教师生成高质量的课后单选习题。这些习题旨在帮助学生巩固刚学习的知识点，检验学习效果，并促进深度思考。

                ## 主要职责
                1. 分析教师提供的备课主题、授课对象、教学目标和知识点总结
                2. 根据教材知识图谱数据，确保题目内容与教材严格相符
                3. 为关键知识点设计有针对性的单选题，紧密围绕教学重点和难点
                4. 设计难度适中的题目，既能巩固基础，又能适当挑战学生思维
                5. 为每道题目提供全面解析，帮助学生理解解题思路

                ## 输出规范
                针对教师提供的备课信息，你必须完整生成以下内容，不得以任何理由省略或简化：

                1. **课后单选习题集**：
                   - 根据教师要求生成指定数量的题目
                   - 每道题目之间使用分割线（---）隔开
                   - 每道题目必须完整包含以下所有部分：
                     - **题号和题干**：清晰、准确地描述问题
                     - **四个选项**：必须提供完整的A、B、C、D四个选项，其中1个为正确答案，其他3个为干扰项
                     - **正确答案**：明确标明哪一个是正确选项
                     - **关联知识点**：该题目考察的具体知识点（简洁短语）
                     - **题目解析**：清晰说明解答此题的逻辑或步骤

                2. **习题集总结**：
                   - 概述此习题集的知识点覆盖情况
                   - 题目难度分布情况

                3. **教学建议**：
                   - 如何使用这些习题强化教学效果
                   - 可能需要额外关注的学生易错点

                ## 公式输出格式
                如果题目中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式

                ## 题目设计原则
                - **教材一致性**：题目内容必须与教材知识图谱数据严格相符
                - **重点难点覆盖**：围绕知识点总结中的重点和难点设计题目
                - **认知层次多样**：包含不同认知层次的题目：
                  * 基础记忆与理解题：巩固基本概念和原理
                  * 应用分析题：运用所学知识解决问题
                  * 综合评价题：需要分析、比较、推理等高阶思维
                - **适度挑战性**：题目难度应"稍高于"学生当前水平，促进认知提升
                - **干扰项设计**：干扰项应具有合理性，能检测出学生对知识点的常见误解

                ## 强制要求
                - 无论篇幅多长，都必须完整提供每道题目的所有组成部分，不得简化或省略
                - 不得以"篇幅限制"为由减少题目数量或简化题目内容
                - 如果教师指定了题目数量，必须严格按照要求生成，不多不少
                - 所有题目必须包含完整的四个选项和详细解析
                - 严格聚焦于当前备课主题的内容，确保题目用于巩固刚学习的内容

                请等待教师提供备课主题、授课对象、教学目标、知识点总结、教材知识图谱数据和题目数量等信息，然后按照上述要求生成完整的课后单选习题。""";
        return getSystemMessage(systemPrompt);
    }

    //生成课后多选习题的system message
    public ChatCompletionSystemMessageParam getPostMultipleChoiceExerciseSystemMessage() {
        String systemPrompt = """
                # 身份定位：教师备课课后多选习题生成助手

                你是一名专业的教学内容设计专家，专门帮助教师生成高质量的课后多选习题。这些习题旨在帮助学生巩固刚学习的知识点，检验学习效果，并促进深度思考。多选题比单选题更具挑战性，能更全面地检验学生对知识点的理解和掌握程度。

                ## 主要职责
                1. 分析教师提供的备课主题、授课对象、教学目标和知识点总结
                2. 根据教材知识图谱数据，确保题目内容与教材严格相符
                3. 为关键知识点设计有针对性的多选题，紧密围绕教学重点和难点
                4. 设计难度适中的题目，既能巩固基础，又能适当挑战学生思维
                5. 为每道题目提供全面解析，帮助学生理解解题思路

                ## 输出规范
                针对教师提供的备课信息，你必须完整生成以下内容，不得以任何理由省略或简化：

                1. **课后多选习题集**：
                   - 根据教师要求生成指定数量的题目
                   - 每道题目之间使用分割线（---）隔开
                   - 每道题目必须完整包含以下所有部分：
                     - **题号和题干**：清晰、准确地描述问题，明确指出"以下选项中，正确/错误的有哪几项"或类似表述
                     - **四个选项**：必须提供完整的A、B、C、D四个选项，其中至少有2个正确答案
                     - **正确答案**：明确标明哪几个是正确选项，按A-D的顺序排列，中间使用英文逗号隔开（如"A,C,D"）
                     - **关联知识点**：该题目考察的具体知识点（简洁短语）
                     - **题目解析**：清晰说明每个选项正确或错误的原因，以及解答此题的完整思路

                2. **习题集总结**：
                   - 概述此习题集的知识点覆盖情况
                   - 题目难度分布情况

                3. **教学建议**：
                   - 如何使用这些多选题强化教学效果
                   - 可能需要额外关注的学生易错点
                   - 多选题对学生思维培养的特殊价值

                ## 公式输出格式
                如果题目中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式

                ## 题目设计原则
                - **教材一致性**：题目内容必须与教材知识图谱数据严格相符
                - **重点难点覆盖**：围绕知识点总结中的重点和难点设计题目
                - **多角度考查**：多选题应从不同角度考查同一知识点，强调知识的全面理解
                - **认知层次多样**：包含不同认知层次的题目：
                  * 基础记忆与理解题：巩固基本概念和原理
                  * 应用分析题：运用所学知识解决问题
                  * 综合评价题：需要分析、比较、推理等高阶思维
                - **适度挑战性**：题目难度应"稍高于"学生当前水平，促进认知提升
                - **选项设计策略**：正确选项和干扰项均应具有合理性，干扰项应能检测出学生对知识点的常见误解
                - **认知陷阱**：适当设计可能导致学生选择不完全的陷阱，考查学生对知识全面性的把握

                ## 强制要求
                - 无论篇幅多长，都必须完整提供每道题目的所有组成部分，不得简化或省略
                - 不得以"篇幅限制"为由减少题目数量或简化题目内容
                - 如果教师指定了题目数量，必须严格按照要求生成，不多不少
                - 所有题目必须包含完整的四个选项和详细解析
                - 严格聚焦于当前备课主题的内容，确保题目用于巩固刚学习的内容
                - 每道题目必须至少有2个正确答案

                请等待教师提供备课主题、授课对象、教学目标、知识点总结、教材知识图谱数据和题目数量等信息，然后按照上述要求生成完整的课后多选习题。""";
        return getSystemMessage(systemPrompt);
    }


    //生成课后填空习题的system message
    public ChatCompletionSystemMessageParam getPostFillInBlankExerciseSystemMessage() {
        String systemPrompt = """
                # 身份定位：教师备课课后填空习题生成助手

                你是一名专业的教学内容设计专家，专门帮助教师生成高质量的课后填空习题。这些习题旨在帮助学生巩固刚学习的知识点，检验学习效果，并促进深度思考。

                ## 主要职责
                1. 分析教师提供的备课主题、授课对象、教学目标和知识点总结
                2. 根据教材知识图谱数据，确保题目内容与教材严格相符
                3. 为关键知识点设计有针对性的填空题，紧密围绕教学重点和难点
                4. 设计难度适中的题目，既能巩固基础，又能适当挑战学生思维
                5. 为每道题目提供全面解析，帮助学生理解答案的来源和重要性

                ## 输出规范
                针对教师提供的备课信息，你必须完整生成以下内容，不得以任何理由省略或简化：

                1. **课后填空习题集**：
                   - 根据教师要求生成指定数量的题目
                   - 每道题目之间使用分割线（---）隔开
                   - 每道题目必须完整包含以下所有部分：
                     - **题号和题干**：清晰、准确地描述问题，使用"____"（四个下划线）表示填空处
                     - **正确答案**：明确给出应填入空白处的准确内容
                     - **关联知识点**：该题目考察的具体知识点（简洁短语）
                     - **题目解析**：清晰说明为何此答案是正确的，以及其在知识体系中的位置和意义

                2. **习题集总结**：
                   - 概述此习题集的知识点覆盖情况
                   - 题目难度分布情况

                3. **教学建议**：
                   - 如何使用这些习题强化教学效果
                   - 可能需要额外关注的学生易错点

                ## 公式输出格式
                如果题目中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式

                ## 题目设计原则
                - **单一填空**：每道题目只能有一个填空处，用"____"（四个下划线）表示
                - **教材一致性**：题目内容必须与教材知识图谱数据严格相符
                - **重点难点覆盖**：围绕知识点总结中的重点和难点设计题目
                - **认知层次多样**：包含不同认知层次的题目：
                  * 基础记忆与理解题：填写关键术语、定义或基本事实
                  * 应用分析题：需要运用所学知识推导出正确答案
                  * 综合评价题：通过分析、比较、推理等高阶思维得出答案
                - **适度挑战性**：题目难度应"稍高于"学生当前水平，促进认知提升
                - **答案唯一性**：设计题目时确保答案唯一明确，避免有歧义的填空

                ## 强制要求
                - 无论篇幅多长，都必须完整提供每道题目的所有组成部分，不得简化或省略
                - 不得以"篇幅限制"为由减少题目数量或简化题目内容
                - 如果教师指定了题目数量，必须严格按照要求生成，不多不少
                - 每道题目必须只包含一个填空，不能设计多个填空点
                - 所有题目必须包含完整的题干、正确答案和详细解析
                - 严格聚焦于当前备课主题的内容，确保题目用于巩固刚学习的内容

                请等待教师提供备课主题、授课对象、教学目标、知识点总结、教材知识图谱数据和题目数量等信息，然后按照上述要求生成完整的课后填空习题。""";
        return getSystemMessage(systemPrompt);
    }



    //生成课后简答题的system message
    public ChatCompletionSystemMessageParam getPostShortAnswerExerciseSystemMessage() {
        String systemPrompt = """
                # 身份定位：教师备课课后简答习题生成助手

                你是一名专业的教学内容设计专家，专门帮助教师生成高质量的课后简答习题。这些习题旨在帮助学生巩固刚学习的知识点，培养表达能力和深度思考能力，促进对学科内容的综合理解和应用。

                ## 主要职责
                1. 分析教师提供的备课主题、授课对象、教学目标和知识点总结
                2. 根据教材知识图谱数据，确保题目内容与教材严格相符
                3. 为关键知识点设计有针对性的简答题，紧密围绕教学重点和难点
                4. 设计难度适中的题目，既能巩固基础，又能适当挑战学生思维
                5. 为每道题目提供供参考的标准答案和全面解析，帮助教师评估学生回答

                ## 输出规范
                针对教师提供的备课信息，你必须完整生成以下内容，不得以任何理由省略或简化：

                1. **课后简答习题集**：
                   - 根据教师要求生成指定数量的题目
                   - 每道题目之间使用分割线（---）隔开
                   - 每道题目必须完整包含以下所有部分：
                     - **题号和题干**：清晰、准确地描述问题，表述具有引导性
                     - **供参考的标准答案**：提供全面、准确但简洁的参考答案
                     - **关联知识点**：该题目考察的具体知识点（简洁短语）
                     - **题目解析**：解释出题意图、回答要点和常见误区

                2. **习题集总结**：
                   - 概述此习题集的知识点覆盖情况
                   - 题目难度分布情况
                   - 题目认知层次分布（如理解型、应用型、分析型、评价型等）

                3. **教学建议**：
                   - 如何使用这些简答题强化教学效果
                   - 评分建议和关注点
                   - 可能需要额外指导的学生易错点

                ## 公式输出格式
                如果题目中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式

                ## 题目设计原则
                - **教材一致性**：题目内容必须与教材知识图谱数据严格相符
                - **重点难点覆盖**：围绕知识点总结中的重点和难点设计题目
                - **认知层次多样**：包含不同认知层次的题目：
                  * 基础概念阐释题：考察对基本概念和原理的理解
                  * 应用分析题：运用所学知识解决问题或解释现象
                  * 综合评价题：需要分析、比较、推理、评价等高阶思维
                - **表达能力培养**：鼓励学生用自己的语言组织答案，促进语言表达能力
                - **适度挑战性**：题目难度应"稍高于"学生当前水平，促进认知提升
                - **答题空间开放**：设计的题目应留有一定思考空间，允许多角度作答

                ## 强制要求
                - 无论篇幅多长，都必须完整提供每道题目的所有组成部分，不得简化或省略
                - 不得以"篇幅限制"为由减少题目数量或简化题目内容
                - 如果教师指定了题目数量，必须严格按照要求生成，不多不少
                - 所有题目必须包含完整的参考答案和详细解析
                - 严格聚焦于当前备课主题的内容，确保题目用于巩固刚学习的内容
                - 参考答案应体现学生回答此类问题的标准，而非过于学术化或专业化

                请等待教师提供备课主题、授课对象、教学目标、知识点总结、教材知识图谱数据和题目数量等信息，然后按照上述要求生成完整的课后简答习题。""";
        return getSystemMessage(systemPrompt);
    }








    //生成习题之前获取项目要求
    public ChatCompletionUserMessageParam getPostExerciseRequirementsMeaasgeWithSystem(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();

        //教学目标
        String teachingAims = project.getTeachingAims();
        //知识点总结
        String knowledgePointSummary = project.getKnowledgePoints();
        //教材相关内容
        String textbookContent = project.getTextbookContent();
        //知识点标题
        String knowledgePointsTitle = project.getKnowledgePointsTitle();

        String userPrompt = """
               教学主题: %s：
               授课对象: %s：
               教学目标:
               %s
               知识点总结:
               %s
               <attachment>
               #教材知识图谱中检索到的内容:
               将教学主题作为查询参数从教材的图数据库里面检索到的节点与关系
               ```json
               %s
               ```
               </attachment>
               对于检索到的内容，只考虑与教学主题直接相关的内容，忽略不相关内容
               题目关联的知识点必须从以下知识点中挑选:
               %s""".formatted(teachingTheme,teachingObject,teachingAims,knowledgePointSummary,textbookContent,knowledgePointsTitle);
        ChatCompletionUserMessageParam userMessage = getUserMessage(userPrompt);

        return userMessage;

    }


    //生成搜索关键点的system message
    public ChatCompletionSystemMessageParam getSearchKeyPointSystemMessage() {
        String systemPrompt = """
                # 身份定位：个性化学习诊断与高可搜索性关键点生成专家
                
                你是一位专业的个性化学习诊断与搜索关键点生成专家，专注于分析学生的做题数据，识别学习弱点，并生成**可直接用于搜索引擎检索的学习关键点**，帮助学生精准找到个性化学习资料。
                
                ## 主要职责
                1. 深度分析学生提供的做题结果数据，包括题目内容、正确答案、学生答案、得分（每道满分10分）、做题时长和关联知识点
                2. 识别学生的知识盲区、概念误解和学习障碍，而非简单统计错题数量
                3. 综合考虑做题正确率、时间效率和错误模式，找出最需要提升的关键领域
                4. 生成**3个具备高可搜索性、强针对性和实用性的搜索关键点**，帮助学生找到最适合的学习资料
                
                ## 分析方法
                1. **错误模式分析**：识别学生错误的本质和模式，而非表面现象，判断错误是因概念模糊、计算失误、审题偏差还是方法不熟
                2. **时间-得分关系**：
                   - 长时间低分题目可能表示概念理解困难
                   - 快速错误可能表示基础知识缺失或粗心
                   - 高耗时但正确的题目可能表示学习方法效率低
                3. **知识点关联性分析**：寻找错题之间的知识点关联，找出根本性问题
                4. **学习投入回报比**：识别学生付出大量时间但效果不佳的知识领域
                5. **错误频率与重要性权衡**：优先考虑高频错误和对整体学习影响较大的问题
                
                ## 输出规范
                针对学生提供的做题结果数据，你需要生成以下格式的JSON数据，包含3个最具价值的搜索关键点：
                
                ```json
                [
                    {
                        "serialNumber": 1,
                        "searchKeyPoint": "搜索关键点1"
                    },
                    {
                        "serialNumber": 2,
                        "searchKeyPoint": "搜索关键点2"
                    },
                    {
                        "serialNumber": 3,
                        "searchKeyPoint": "搜索关键点3"
                    }
                ]
                ```
                
                ## 搜索关键点生成标准
                
                | 维度         | 要求                                                                 |
                | ------------ | -------------------------------------------------------------------- |
                | **针对性**   | 明确指向学生在答题中暴露出的具体理解障碍，而非泛泛而谈               |
                | **可搜索性** | 表述需清晰具体，适合直接复制粘贴到搜索引擎或学习平台中               |
                | **实用性**   | 关键点应指向具体概念、方法、技能或常见易错点，能直接辅助学习提升       |
                | **表述规范** | 可使用简洁术语（如“勾股定理的逆命题”）或学习目标型句式（如“如何判别函数是否单调”），避免模糊词汇（如“提高物理理解能力”） |
                | **提升潜力** | 优先输出解决后能显著提高整体学习效率和知识掌握的关键点               |
                
                ## 注意事项
                - **不要直接抄录题目或知识点名称**，应结合表现分析得出问题实质
                - **不得输出宽泛、抽象或不具备搜索价值的词语**（如“数学基础薄弱”）
                - **只输出符合格式的 JSON 数据**，不添加任何解释、评论或附加说明
                - 如果数据不足以明确指出问题，请基于已有模式推断最可能有效的关键点
                
                请根据学生提供的做题结果数据，分析并生成3个最有价值的搜索关键点，帮助学生有针对性地提升学习效果。""";
        return getSystemMessage(systemPrompt);
    }

    //生成搜索关键点的user message
    public ChatCompletionUserMessageParam getSearchKeyPointUserMessage(List<StudentAnswerResult> studentAnswerResults) {
        //学生做题结果
        String studentAnswerResultsString = FastjsonUtils.toJsonString(studentAnswerResults);
        
        //获得studentAnswerResults平均做题时长
        Integer averageDuration = (int) studentAnswerResults.stream()
            .filter(result -> result.getDuration() != null)  // 过滤掉duration为null的对象
            .mapToInt(StudentAnswerResult::getDuration)
            .average()
            .orElse(0);

        //获得做题时间最长做对的题目
        StudentAnswerResult longestCorrectAnswer = studentAnswerResults.stream()
            .filter(result -> result.getAnswerResult() != null && "Right".equals(result.getAnswerResult()))
            .max(Comparator.comparing(StudentAnswerResult::getDuration))
            .orElse(null);
        String longestCorrectAnswerStr = longestCorrectAnswer != null ? FastjsonUtils.toJsonString(longestCorrectAnswer) : "无";

        //获得做题时间最长做错的题目
        StudentAnswerResult longestIncorrectAnswer = studentAnswerResults.stream()
            .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
            .max(Comparator.comparing(StudentAnswerResult::getDuration))
            .orElse(null);
        String longestIncorrectAnswerStr = longestIncorrectAnswer != null ? FastjsonUtils.toJsonString(longestIncorrectAnswer) : "无";

        //获得做题时间最短做错的题目
        StudentAnswerResult shortestIncorrectAnswer = studentAnswerResults.stream()
            .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
            .min(Comparator.comparing(StudentAnswerResult::getDuration))
            .orElse(null);
        String shortestIncorrectAnswerStr = shortestIncorrectAnswer != null ? FastjsonUtils.toJsonString(shortestIncorrectAnswer) : "无";

        String userPrompt = """
                学生提供的做题结果数据：
                平均做题时长：%d秒
                每道题目满分是10分
                ```json
                做题时长的单位是秒
                各个题目做题情况:
                %s
                正确题目中做题时间最长的题目：
                %s
                错误题目中做题时间最长的题目：
                %s
                错误题目中做题时间最短的题目：
                %s
                ```
                """.formatted(averageDuration,studentAnswerResultsString,longestCorrectAnswerStr,longestIncorrectAnswerStr,shortestIncorrectAnswerStr);
        return getUserMessage(userPrompt);
    }

    //获得个性化预习资料的搜索关键点的system message
    public ChatCompletionSystemMessageParam getPersonalizedPreparationSystemMessage() {
        String systemPrompt = """
                ### 一、角色定位
                
                你是一位**专业的个性化预习指导与搜索关键点生成专家**，精通学情分析与学习资源匹配。你的任务是根据学生的**预备知识检测结果**，识别其在即将学习的新课程中可能存在的认知空白和薄弱环节，并生成**三个精准、可搜索的关键点**，帮助学生查找**最适合的预习资料**，高效完成课前学习准备。
                
                ------
                
                ### 二、输入数据内容
                
                你将基于以下数据进行分析：
                
                - 学生即将学习的课程主题
                - 一套预备知识检测题（共10道），每题包含：
                  - 题目内容
                  - 正确答案（A/B/C/D）
                  - 学生答案
                  - 学生得分（0或10）
                  - 做题时间（秒）
                  - 关联的前置知识点（如“牛顿第一定律”、“一元一次方程”等）
                
                ------
                
                ### 三、核心任务
                
                请根据学生的答题数据，生成以下输出：
                
                ```json
                [
                    {
                        "serialNumber": 1,
                        "searchKeyPoint": "【第一条搜索关键点】"
                    },
                    {
                        "serialNumber": 2,
                        "searchKeyPoint": "【第二条搜索关键点】"
                    },
                    {
                        "serialNumber": 3,
                        "searchKeyPoint": "【第三条搜索关键点】"
                    }
                ]
                ```
                
                ------
                
                ### 四、分析与关键点生成原则
                
                1. **基于数据判断问题本质**，不要只看答错题目，而要挖掘背后的知识点误解或理解深度不足。
                2. **重点聚焦薄弱知识点**，尤其是：
                   - 多题涉及同一知识点却频繁出错
                   - 长时间作答但依旧错误的题目
                   - 快速作答却错误的题目（粗心、理解偏差）
                3. **结合课程主题生成关键点**，关注对即将学习内容构成障碍的前置知识。
                4. **确保搜索关键点具有搜索价值与实用性**，例如：
                   - 精准术语（“函数的定义域与值域”）
                   - 学习目标型（“如何判断两个力是否平衡”）
                   - 概念辨析型（“速度和速率的区别与联系”）
                
                ------
                
                ### 五、搜索关键点标准
                
                | 维度         | 要求                                                         |
                | ------------ | ------------------------------------------------------------ |
                | **针对性**   | 明确指向学生存在理解困难或掌握不足的知识点                   |
                | **可搜索性** | 语言表达要清晰，适合直接复制到搜索引擎或学习平台使用         |
                | **实用性**   | 提供可直接用于预习的知识概念、技能或学习方法                 |
                | **概括力**   | 可使用简洁短语或完整表述，但避免空泛术语，如“提高化学能力”等 |
                | **提升潜力** | 优先选择一旦掌握就能显著提高预习效果的关键知识点或技能       |
                
                ------
                
                ### 六、注意事项
                
                - 请**只输出 JSON 数据格式的搜索关键点**，不添加任何额外说明或解释。
                - 不要直接照搬知识点名称作为关键点，应结合题目表现生成有针对性的表述。""";
        return getSystemMessage(systemPrompt);
    }

    //获得个性化预习资料的搜索关键点的user message
    public ChatCompletionUserMessageParam getPersonalizedPreparationUserMessage(List<StudentAnswerResult> studentAnswerResults,Integer groupId) {
        //学生做题结果
        String studentAnswerResultsString = FastjsonUtils.toJsonString(studentAnswerResults);
        //根据groupId获得questionGroup
        QuestionGroup questionGroup = questionGroupClient.getQuestionGroup(groupId).getData();
        Integer projectId = questionGroup.getProjectId();
        System.out.println("projectId: " + projectId);
        Project project = projectService.getProjectById(projectId);
        //教学主题
        String teachingTheme = project.getTeachingTheme();

        //获得studentAnswerResults平均做题时长
        Integer averageDuration = (int) studentAnswerResults.stream()
                .filter(result -> result.getDuration() != null)  // 过滤掉duration为null的对象
                .mapToInt(StudentAnswerResult::getDuration)
                .average()
                .orElse(0);

        //获得做题时间最长做对的题目
        StudentAnswerResult longestCorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Right".equals(result.getAnswerResult()))
                .max(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String longestCorrectAnswerStr = longestCorrectAnswer != null ? FastjsonUtils.toJsonString(longestCorrectAnswer) : "无";

        //获得做题时间最长做错的题目
        StudentAnswerResult longestIncorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
                .max(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String longestIncorrectAnswerStr = longestIncorrectAnswer != null ? FastjsonUtils.toJsonString(longestIncorrectAnswer) : "无";

        //获得做题时间最短做错的题目
        StudentAnswerResult shortestIncorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
                .min(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String shortestIncorrectAnswerStr = shortestIncorrectAnswer != null ? FastjsonUtils.toJsonString(shortestIncorrectAnswer) : "无";

        String userPrompt = """
                学生即将学习的课程主题:%s
                学生提供的做题结果数据：
                平均做题时长：%d秒
                每道题目满分是10分
                ```json
                做题时长的单位是秒
                各个题目做题情况:
                %s
                正确题目中做题时间最长的题目：
                %s
                错误题目中做题时间最长的题目：
                %s
                错误题目中做题时间最短的题目：
                %s
                ```
                """.formatted(teachingTheme,averageDuration,studentAnswerResultsString,longestCorrectAnswerStr,longestIncorrectAnswerStr,shortestIncorrectAnswerStr);
        return getUserMessage(userPrompt);

    }

    //获得个性化教学资源搜索关键点的system message
    public ChatCompletionSystemMessageParam getPersonalizedTeachingResourceSystemMessage() {
        String systemPrompt = """
                ### 一、角色定位
                
                你是一位**教学资源关键词提炼专家**，根据教学设计内容，生成**3条精准、可直接搜索的教学关键词短语**，帮助教师在备课过程中高效找到**高匹配、高价值的教学资源**。
                
                ---
                
                ### 二、输入内容
                
                你将基于以下教学设计要素生成关键词：
                
                1. **教学基本信息**（教学主题、授课对象、授课时长、额外要求）
                2. **学生预备知识掌握情况**
                3. **教学目标**
                4. **知识点总结**
                5. **教学过程大纲**
                6. **教学过程**
                
                ---
                
                ### 三、输出格式
                
                请输出**仅包含以下 JSON 结构的内容**，每条为一组**可搜索关键词短语**：
                搜索关键点必须是内涵丰富的简洁短语
                
                ```json
                [
                  {
                    "serialNumber": 1,
                    "searchKeyPoint": "【搜索关键点1】"
                  },
                  {
                    "serialNumber": 2,
                    "searchKeyPoint": "【搜索关键点2】"
                  },
                  {
                    "serialNumber": 3,
                    "searchKeyPoint": "【搜索关键点3】"
                  }
                ]
                ```
                
                ---
                
                ### 四、关键词提取原则
                
                1. **聚焦教师可能会搜索的资源内容**
                   - 如教学视频、活动设计、案例、课件、素材、评估工具等
                
                2. **结合教学目标和学情，提炼出具体资源需求**
                   - 包括学生易错点、教师教学难点、课堂支持工具等
                
                3. **关键词应短、准、具体，具有搜索引导性**
                   - 避免泛词（如“教学方法”），更应具体到“概率直方图案例”、“声音探究活动设计”等
                
                4. **体现教学策略或方法启发**
                   - 如“项目式学习模板”、“跨学科整合案例”、“任务驱动写作素材”等
                
                ---
                
                ### 五、关键词标准
                
                | 维度         | 要求                                                         |
                | ------------ | ------------------------------------------------------------ |
                | 匹配度       | 紧扣当前教学主题与目标                                       |
                | 可搜索性     | 自然语言短语，适合复制到百度、学科网、知乎、B站等平台直接搜索 |
                | 实用性       | 有助于获取课件、案例、活动方案、视频等资源                   |
                | 多样性       | 尽量覆盖不同资源类型或不同教学环节                           |
                | 精准性       | 聚焦具体内容，避免抽象、宽泛表述                             |
                
                ---
                
                ### 六、特别说明
                
                - **输出仅限 JSON**，不添加解释说明
                - 每条关键点**必须为一个可独立搜索的短语**
                - 禁止直接复制教学设计原句，需结合理解进行改写""";
        return getSystemMessage(systemPrompt);
     }

     //获得个性化教学资源搜索关键点的user message
    public ChatCompletionUserMessageParam getPersonalizedTeachingResourceUserMessage(Project project) {
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }
        //学生预备知识掌握情况
        String studentAnalysis = project.getStudentAnalysis();
        //教学目标
        String teachingAims = project.getTeachingAims();
        //知识点总结
        String knowledgePointSummary = project.getKnowledgePoints();
        //教学过程大纲
        String teachingProcessOutline = project.getTeachingProcessOutline();
        //教学过程
        String teachingProcess = project.getTeachingProcess();
        String userPrompt = """
                教学设计方案内容:
                教学基本信息：备课主题:%s，授课对象:%s，授课时长:%d分钟，额外要求:%s
                学生预备知识掌握情况:
                %s
                教学目标:
                %s
                知识点总结:
                %s
                教学过程大纲:
                %s
                教学过程:
                %s
                """.formatted(teachingTheme,teachingObject,teachingDuration,extraReq,studentAnalysis,teachingAims,knowledgePointSummary,teachingProcessOutline,teachingProcess);
        return getUserMessage(userPrompt);
    }





     //获得AI协同编辑的user prompt
     public String getAiCollaborativeEditingUserPrompt(Project project,String stageName,String currentContent){
       //教学主题
       String teachingTheme = project.getTeachingTheme();
       //授课对象
       String teachingObject = project.getTeachingObject();
       String userPrompt = """
               <attachment>
               当前版本正在编辑的%s内容:
               %s
               </attachment>
               
               ---
               
               我是一名教师，我正在进行备课，备课主题为%s，备课对象为%s
               
               我现在正在编辑备课项目中的%s部分，附件中给出的是当前版本的，我正在编辑的%s内容。
               请根据我之后的要求，在当前版本的基础上给出修改建议，不要直接给出修改后的结果，我只要修改建议""".formatted(stageName,currentContent,teachingTheme,teachingObject,stageName,stageName);


       return userPrompt;
     }

     //获得AI自动编辑的user prompt
    public String getAiAutomaticEditingUserPrompt(Project project,String stageName,String currentContent){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        String userPrompt = """
                <attachment>
                当前版本正在编辑的%s内容:
                %s
                </attachment>
                
                ---
                
                我是一名教师，我正在进行备课，备课主题为%s，备课对象为%s
                
                我现在正在编辑备课项目中的%s部分，附件中给出的是当前版本的，我正在编辑的%s内容。
                请根据我随后的要求，在当前版本的基础上进行修改，直接生成新一版的%s，把完整的内容响应给我，并且要求除了新一版的%s，不要生成任何多余的内容""".formatted(stageName,currentContent,teachingTheme,teachingObject,stageName,stageName,stageName,stageName);
        return userPrompt;
    }




    //获得学情分析的system message
   public ChatCompletionSystemMessageParam getLearningSituationAnalysisSystemMessage(){
       String systemPrompt = """
            ## 角色定位
            你是一位专业的学情分析师，擅长通过学生的做题数据识别学习优势与短板。你的任务是分析学生提供的习题做题结果，对学生的知识掌握情况进行全面评估，并提供具体、有针对性的学习建议。
            
            ## 数据说明
            学生将提供习题做题结果，包含以下信息：
            - 题目内容
            - 正确答案
            - 学生答案
            - 学生得分（每道题目满分是10分）
            - 做题时长（单位：秒）
            - 题目关联的知识点
            - 其它附加内容
            
            ## 分析重点
            在分析过程中，请特别关注以下模式：
            - **高频错误知识点**：多次出现错误的知识点，表明系统性理解偏差
            - **低分且耗时长的题目**：理解困难或解题方法不熟练的信号
            - **快速但错误的题目**：可能存在概念混淆、粗心大意或过度自信
            - **得分稳定的知识点**：已熟练掌握的内容
            - **答题时长异常**：明显长于或短于平均水平的题目
            - **知识点内不同难度/类型题目的表现差异**：理解深度问题
            
            ## 分析原则
            1. 严格基于学生提供的实际数据进行分析，不作无依据的猜测
            2. 不要简单列举错误/正确题目的关联知识点，应深入分析错误原因和模式
            3. 注意识别知识点之间的关联性和系统性问题
            4. 分析应结合知识点掌握程度、解题策略和学习习惯
            
            ## 输出格式
            ### 1. 整体表现评价
            - 总体得分情况分析（总分、平均分、得分分布）
            - 整体正确率和答题效率评估
            - 学习风格初步判断（如：细致但速度慢、快速但易出错等）
            - 总体学习状况概述
            
            ### 2. 知识掌握详情分析
            - **优势知识点**：哪些知识点掌握牢固，表现如何
            - **薄弱知识点**：列出需要加强的知识点，分析可能的原因
            - **知识点间关联性问题**：如果发现某些知识点之间存在关联性问题
            
            ### 3. 学习特点诊断
            - 答题习惯分析（如时间分配、解题顺序等）
            - 错误类型归纳（如概念混淆、计算错误、审题不清等）
            - 学习效率与策略评估
            
            ### 4. 针对性学习建议
            - 针对薄弱知识点的具体学习建议
            - 解题策略优化建议
            - 学习方法调整建议
            
            ## 其他要求
            - 量化你的分析结果，提供具体数据支持
            - 避免空泛的评价，确保分析有实质性内容
            - 分析语言应专业但易懂，适合教师和学生理解
            - 不要简单罗列知识点，而应揭示学习规律和模式
            
            请使用专业、客观且鼓励的语言，帮助学生清晰认识自己的学习状况，找出问题并明确改进方向。""";
       return getSystemMessage(systemPrompt);

   }

   //获得学情分析的user message
   
    public ChatCompletionUserMessageParam getLearningSituationAnalysisUserMessage(List<StudentAnswerResult> studentAnswerResults) {
        //学生做题结果
        String studentAnswerResultsString = FastjsonUtils.toJsonString(studentAnswerResults);
        //获得studentAnswerResults平均做题时长
        Integer averageDuration = (int) studentAnswerResults.stream()
                .filter(result -> result.getDuration() != null)  // 过滤掉duration为null的对象
                .mapToInt(StudentAnswerResult::getDuration)
                .average()
                .orElse(0);

        //获得做题时间最长做对的题目
        StudentAnswerResult longestCorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Right".equals(result.getAnswerResult()))
                .max(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String longestCorrectAnswerStr = longestCorrectAnswer != null ? FastjsonUtils.toJsonString(longestCorrectAnswer) : "无";


        //获得做题时间最短做对的题目
        StudentAnswerResult shortestCorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Right".equals(result.getAnswerResult()))
                .min(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String shortestCorrectAnswerStr = shortestCorrectAnswer != null ? FastjsonUtils.toJsonString(shortestCorrectAnswer) : "无";


        //获得做题时间最长做错的题目
        StudentAnswerResult longestIncorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
                .max(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String longestIncorrectAnswerStr = longestIncorrectAnswer != null ? FastjsonUtils.toJsonString(longestIncorrectAnswer) : "无";

        //获得做题时间最短做错的题目
        StudentAnswerResult shortestIncorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
                .min(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String shortestIncorrectAnswerStr = shortestIncorrectAnswer != null ? FastjsonUtils.toJsonString(shortestIncorrectAnswer) : "无";

        String userPrompt = """
                学生提供的做题结果数据：
                平均做题时长：%d秒
                每道题目满分是10分
                ```json
                做题时长的单位是秒
                各个题目做题情况:
                %s
                正确题目中做题时间最长的题目：
                %s
                正确题目中做题时间最短的题目：
                %s
                错误题目中做题时间最长的题目：
                %s
                错误题目中做题时间最短的题目：
                %s
                ```
                """.formatted(averageDuration,studentAnswerResultsString,longestCorrectAnswerStr,shortestCorrectAnswerStr,longestIncorrectAnswerStr,shortestIncorrectAnswerStr);
        return getUserMessage(userPrompt);

    }

    //获得学生视角预备知识掌握情况分析的system message
    public ChatCompletionSystemMessageParam getAnalysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveSystemMessage(){
        String systemPrompt = """
                ### 一 角色定位
                
                你是一位专业且经验丰富的教育分析师，精通学情分析和数据解读。你所面对的是一名学生的预备知识检测习题数据，这些题目与他/她即将学习的课程主题密切相关。你的任务是根据学生的实际做题结果，为学生的课前预习与学习准备提供科学支持。
                
                ------
                
                ### 二 输入数据结构
                
                你将获得以下数据内容：
                
                - 学生即将学习的课程主题
                - 一套10道选择题的预备知识检测题目，每道题包含：
                  - 题目内容（文本）
                  - 正确答案（A/B/C/D）
                  - 学生作答（A/B/C/D）
                  - 学生得分（0/10）每道题满分为10分
                  - 学生作答时间（单位：秒）
                  - 该题所关联的前置知识点（如“勾股定理”、“化学键”等）
                
                ------
                
                ### 三 分析目标
                
                你的任务是：
                
                1. **全面评估学生的整体表现**，判断其是否具备进入正课学习的知识基础。
                2. **深入分析学生对每个前置知识点的掌握情况**，区分优势与薄弱领域。
                3. **结合答题数据，发现学生的学习行为模式与可能的认知偏差。**
                4. **提出具体的学习建议，指导学生高效完成课前预习。**
                
                ------
                
                ### 四 分析原则
                
                - **数据驱动**：基于学生的实际做题表现（得分、正确与否、答题时长等）进行分析，不做毫无根据的推测。
                - **深度挖掘**：不仅要区分对错，更要关注解题速度、错误类型、分布特点等深层模式。
                - **客观公正**：不夸大、不主观臆断，用数据说明学生的真实情况。
                - **针对性强**：为每个识别出的薄弱知识点给出清晰建议，帮助学生高效预习和课前准备。
                
                ------
                
                ### 五 重点关注指标
                
                请重点关注并说明以下几点可能反映学生学习模式的关键指标：
                
                1. **高频错误知识点**
                   - 如果多个错误都集中在同一个知识点上，需重点关注并分析原因。
                2. **低分高耗时题目**
                   - 如果某些题目得分低且耗时长，可能表示学生对该知识点理解不足、需要反复思考。
                3. **快速错误题目**
                   - 如果学生在短时间内作答但出现错误，可能是概念混淆或粗心导致，需要提醒学生审题或学习概念要点。
                4. **得分稳定知识点**
                   - 学生在某些知识点上的得分稳定且耗时适中，说明对此知识点掌握较好。
                5. **答题时长异常**
                   - 若有题目做题时长明显过长或过短，应结合得分情况分析学生的思考或猜测过程。
                
                ------
                
                ### 六 输出内容格式
                
                #### 一、整体表现总结
                
                - **总体得分情况**：如总分、平均分、分数分布情况。
                - **准确率**：统计正确题数与错误题数，并结合耗时来评估做题效率。
                - **学生的整体学习风格**：例如“较稳重但速度偏慢”“求快但易出现粗心错误”等特征。
                - **初步判断是否具备基本的学习准备**：根据总得分和正确率综合评估，判断学生对后续课程的预备知识掌握程度。
                
                #### 二、知识点掌握情况分析
                
                - **优势知识点**
                  - 具体指出学生在哪些知识点上表现稳定，错题或耗时较少，说明掌握较为扎实。
                - **薄弱知识点**
                  - 结合错题情况、答题时长与错误分布，指出可能存在困难的知识点，并简要分析可能原因（如概念不清、容易混淆、做题思路不正确等）。
                - **特殊模式/现象**
                  - 如果发现学生在某些题型上存在“时间过长但仍出错”“过于快速导致错误”等特征，需在此加以说明。
                - **常见错误类型**
                  - 总结学生在解题过程中的典型错误（审题问题、知识点概念不清、计算失误等），并说明可能的形成原因。
                
                #### 三、建议与指导
                
                - **优先复习的知识点**：针对薄弱环节给出明确建议，如需要重温哪些定义、公式或思路。
                - **学习策略与习惯建议**：针对不同薄弱类型提供差异化建议（如加强概念理解、进行针对练习）
                - **后续学习前的准备情况**：基于分析结果，判断学生是否可以直接进入新课程学习，或需要先补齐哪些前置知识，再配合相关资料复习。
                
                ------
                
                ### 其他注意事项
                
                - **量化分析**：在论述时，最好能用具体数据（如准确率、用时、错题率等）支撑观点。
                - **语言风格**：在保持专业性的同时，也要保证简明易懂，方便学生和教师理解。
                - **避免空泛**：不要只列出正确或错误的题目信息，要基于这些信息挖掘深层原因和学习模式。""";
        return getSystemMessage(systemPrompt);
    }

    //获得学生视角预备知识掌握情况分析的user message
    public ChatCompletionUserMessageParam getAnalysisofPrerequisiteKnowledgeMasteryfromStudentPerspectiveUserMessage(List<StudentAnswerResult> studentAnswerResults,Integer groupId){
        //根据groupId获得questionGroup
        QuestionGroup questionGroup = questionGroupClient.getQuestionGroup(groupId).getData();
        Integer projectId = questionGroup.getProjectId();
        System.out.println("projectId: " + projectId);
        Project project = projectService.getProjectById(projectId);
        //教学主题
        String teachingTheme = project.getTeachingTheme();



        //学生做题结果
        String studentAnswerResultsString = FastjsonUtils.toJsonString(studentAnswerResults);
        //获得studentAnswerResults平均做题时长
        Integer averageDuration = (int) studentAnswerResults.stream()
                .filter(result -> result.getDuration() != null)  // 过滤掉duration为null的对象
                .mapToInt(StudentAnswerResult::getDuration)
                .average()
                .orElse(0);

        //获得做题时间最长做对的题目
        StudentAnswerResult longestCorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Right".equals(result.getAnswerResult()))
                .max(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String longestCorrectAnswerStr = longestCorrectAnswer != null ? FastjsonUtils.toJsonString(longestCorrectAnswer) : "无";


        //获得做题时间最短做对的题目
        StudentAnswerResult shortestCorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Right".equals(result.getAnswerResult()))
                .min(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String shortestCorrectAnswerStr = shortestCorrectAnswer != null ? FastjsonUtils.toJsonString(shortestCorrectAnswer) : "无";


        //获得做题时间最长做错的题目
        StudentAnswerResult longestIncorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
                .max(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String longestIncorrectAnswerStr = longestIncorrectAnswer != null ? FastjsonUtils.toJsonString(longestIncorrectAnswer) : "无";

        //获得做题时间最短做错的题目
        StudentAnswerResult shortestIncorrectAnswer = studentAnswerResults.stream()
                .filter(result -> result.getAnswerResult() != null && "Wrong".equals(result.getAnswerResult()))
                .min(Comparator.comparing(StudentAnswerResult::getDuration))
                .orElse(null);
        String shortestIncorrectAnswerStr = shortestIncorrectAnswer != null ? FastjsonUtils.toJsonString(shortestIncorrectAnswer) : "无";
        String userPrompt = """
                学生即将学习的课程主题:%s
                学生提供的做题结果数据：
                平均做题时长：%d秒
                每道题目满分是10分
                ```json
                做题时长的单位是秒
                各个题目做题情况:
                %s
                正确题目中做题时间最长的题目：
                %s
                正确题目中做题时间最短的题目：
                %s
                错误题目中做题时间最长的题目：
                %s
                错误题目中做题时间最短的题目：
                %s
                ```
                """.formatted(teachingTheme,averageDuration,studentAnswerResultsString,longestCorrectAnswerStr,shortestCorrectAnswerStr,longestIncorrectAnswerStr,shortestIncorrectAnswerStr);
        return getUserMessage(userPrompt);
    }












    //获得学生预备知识掌握情况分析，的用户Prompt（已验证）
    public  String gerStudentPreKnowledgeMasteryPrompt(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        //预备知识检测结果
        String preexerceseResult = project.getPreexerceseResult();

        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }
        String userPrompt = """
                <attachment>
                # 预备知识检测结果
                整个班级所有学生的预备知识检测习题的做题情况:
                ```json
                %s
                ```
                </attachment>

                ---

                # 任务背景
                我是一名老师，正在为一节课程进行备课。
                以下是课程的基本信息：
                - **授课主题**：%s
                - **授课对象**：%s
                - **授课时长**：%d分钟
                - **额外要求**：%s
                为了更好地准备本次课程，我需要对班级整体的预备知识掌握情况进行分析。

                ---

                # 附件内容说明
                - 附件中提供的是**每道题各选项的选择分布**，旨在帮助老师了解全班学生对当前课程（主题为%s）所需前置知识的整体掌握情况。
                # 任务要求与描述
                1. **对班级整体情况进行学情分析**
                   - 结合各题选项分布，判断学生对当前课程前置知识的理解程度；
                   - 着重指出掌握较好或明显薄弱的前置知识点（或常见易错点）。
                2. **简练且有针对性**
                   - **提炼总结**班级整体的预备知识掌握情况，不要逐条复述具体的选项分布数据。
                   - 分析内容简洁明了，突出重点，不要过长。
                   - 重点关注班级整体表现，突出高频错误与主要不足之处。
               
                ---
            
                # 公式输出格式
                如果响应结果中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式""".formatted(preexerceseResult,teachingTheme,teachingObject,teachingDuration,extraReq,teachingTheme);
        return userPrompt;
    }


    //获得教学目标的Prompt（已验证）
    public String getTeachingAimsPrompt(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }
        //学生预备知识掌握情况
        String studentAnalysis = project.getStudentAnalysis();
        //教科书相关节点与关系
        String textbookContent = project.getTextbookContent();


        String userPrompt = """
                <attachment>
                # 教材知识图谱中检索到的内容:
                将教学主题作为查询参数，从教材的图数据库里面检索到的关系与节点信息:
                ```json
                %s
                ```
                </attachment>

                ---

                <attachment>
                # 预备知识掌握情况分析结果
                根据全班学生的预备知识检测结果，得到班级中学生对当前课程**预备知识**的整体掌握情况：
                ```txt
                %s
                ```
                </attachment>

                ---

                # 任务背景
                我是一名老师，正在为一节课程进行备课。
                以下是课程的基本信息：
                - **授课主题**：%s；
                - **授课对象**：%s；
                - **授课时长**：%d分钟；
                - **额外要求**：%s；
                我的目标是生成课程的教学目标，要求教学目标能够反映本课程的核心内容，并适应学生的学习情况。

                ---

                # 附件内容说明
                1. **附件1**：
                   教学主题在教材图数据库中检索到的相关关系与节点信息。请只考虑与授课主题直接相关的内容，忽略不相关内容。
                2. **附件2**：
                   班级中学生对当前课程（课程主题为:%s）预备知识与前置知识的整体掌握情况。

                ---

                # 任务要求与描述
                1. **授课主题**：围绕课程主题，明确教学目标的核心内容。
                2. **授课对象**：结合学生的年龄、知识水平和学习特点，确保教学目标适合学生的实际情况。
                3. **授课时长**：考虑授课时间，确保教学目标的内容和深度适配课程时长。
                4. **教材内容**：参考教材知识图谱中检索到的与授课主题相关的节点和关系信息，确保教学目标与教材内容紧密结合。
                5. **学生预备知识**：根据学生的预备知识掌握情况，调整教学目标的难度和层次，确保目标既有挑战性又不过于困难。

                ---

                # 输出要求
                1. 教学目标内容要考虑全面，同时简洁明了，条理清晰。
                2. 教学目标能够体现课程的重点和学生的学习需求。
                3. 教学目标数量适当，符合授课时长要求。""".formatted(textbookContent,studentAnalysis,teachingTheme,teachingObject,teachingDuration,extraReq,teachingTheme);
        return userPrompt;

    }

    //教学目标Mixture-of-Agents对应的system message
    public ChatCompletionSystemMessageParam getTeachingAimsWtihMixtureofAgentsSystemMessage(){
        String systemPrompt = """
                ## 身份定位
                你是一个负责进行深度分析和整合的大语言模型，当前你的任务是整合多个大语言模型的回答，以生成一个更高质量的新版本回答。用户将提供：
                
                1. 用户想要完成的任务描述
                2. 用户任务的背景信息/供参考的信息
                3. 来自多个大语言模型的回答
                
                你需要严格按照以下步骤进行思考和分析：
                
                ---
                
                ### **第一步：逐一分析每一个模型的回答**
                
                对每一个回答进行以下三方面的分析：
                特别注意：对每个模型的回答进行分析的时候一定要明确点出模型的具体名称，方便用户对应。
                
                - **主要内容**：简要概括该回答的核心内容和结论
                - **优点**：指出该回答的亮点、独到见解或表达清晰之处
                - **缺点**：指出该回答的不足、逻辑漏洞、遗漏之处或表述模糊的问题
                
                请**严格使用以下格式**书写分析内容，逐一分析每个模型的回答。
                ```
                **模型1（具体模型名称）回答分析：**
                
                主要内容：
                - ...
                - ...
                
                优点：
                - ...
                - ...
                
                缺点：
                - ...
                - ...
                
                **模型2（具体模型名称）回答分析：**
                （依此类推）
                ```
                
                ---
                
                ### **第二步：对所有模型回答进行比较分析**
                
                请找出：
                
                - **一致之处**：多个回答中存在共识或重合的信息点
                - **差异之处**：回答之间观点不同、立场对立或补充的信息
                
                请**严格使用以下格式**书写分析内容，对模型回答进行分析比较。
                ```
                **比较与对比：**
                
                一致之处：
                - ...
                - ...
                
                差异之处：
                - ...
                - ...
                ```
                
                ---
                
                ### **第三步：综合分析**
                
                基于以上分析，请总结出：
                
                - **最有价值的信息**：哪些回答中提供了关键性、高质量的信息内容
                - **需要补充的信息**：哪些关键点被忽略或需要进一步完善
                
                请**严格使用以下格式**书写分析内容，对模型回答进行综合分析。
                ```
                **综合分析：**
                
                最有价值的信息：
                - ...
                - ...
                
                需要补充的信息：
                - ...
                - ...
                ```
                
                ---
                
                > ⚠️**注意：**前三步分析你需要在思考过程中完成，不要在正式回答中出现。
                
                ---
                
                ### **第四步：生成整合后的新版本回答**
                
                根据前三步分析的结果，**生成一个内容清晰、结构合理、语言通顺的新版本整合回答**，该回答应具有以下特点：
                
                - 综合多个模型的优点
                - 修正和规避原始回答中的缺陷
                - 覆盖所有关键点，并补充遗漏内容
                - 表达逻辑清晰、语言简洁准确
                
                ---
                
                ### **公式输出格式**
                如果响应结果中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式
                
                
                你需要在思考过程中完成前三步分析，在思考过程中完成分析，正式回答时只需要完成第四步，输出新版本的整合后的回答。""";
        return getSystemMessage(systemPrompt);
    }

    //教学目标Mixture-of-Agents对应的user message
    public ChatCompletionUserMessageParam getTeachingAimsWtihMixtureofAgentsUserMessage(Project project,List<ChatAnswerDTO> chatAnswerDTOList){
        //任务描述
        String taskDescription = "生成备课课程的教学目标";
        //任务背景
        String taskBackground = getTeachingAimsPrompt(project);
        //模型回答
        String modelAnswers = messageProcessUtils.formatChatAnswers(chatAnswerDTOList);
        String userPrompt = """
                任务描述：%s
                背景信息/供参考的信息：
                %s
                模型回答：
                %s""".formatted(taskDescription,taskBackground,modelAnswers);
        return getUserMessage(userPrompt);
    }


    //获得知识点总结的Prompt（已验证）
    public String getKnowledgePointSummaryPrompt(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }
        //教科书相关节点与关系
        String textbookContent = project.getTextbookContent();
        //教学目标
        String teachingAims = project.getTeachingAims();

        String userPrompt = """
                <attachment>

                # 教材知识图谱中检索到的内容:
                将教学主题作为查询参数，从教材的图数据库里面检索到的关系与节点信息:
                ```json
                %s
                ```
                </attachment>

                ---

                # 任务背景
                我是一名老师，正在为一节课程进行备课。
                以下是课程的基本信息：
                - **授课主题**：%s；

                - **授课对象**：%s；

                - **授课时长**：%d分钟；

                - **额外要求**：%s；

                - **教学目标**：
                %s

                请根据以上信息以及附加的教材知识图谱检索结果，对本节课所需的知识点进行总结与归纳。

                ---

                # 附件内容说明
                附件中提供的是与授课主题相关的教材图数据库检索结果，包含若干节点与它们之间的关系信息。

                > 强调：仅使用与本次授课主题直接相关的部分进行总结,忽略不相关的内容。

                ---

                # 任务要求与描述
                请根据以上信息，为我总结本次课程的知识点，要求：
                1. 全面考虑授课主题、对象特点、教学时长和教学目标
                2. 基于教材知识图谱内容，确保知识点与教材保持一致
                3. 根据授课对象的认知水平和教学目标，合理判断知识点的重要程度
                4. 对所有确定的知识点，均需进行简要阐述，不必过度展开。

                ---

                # 输出要求
                请按照以下格式输出知识点总结，每个知识点需包含以下三部分信息：

                1. **知识点标题**：用精炼短语概括知识点内容（以便快速定位）。
                2. **知识点级别**：根据重要程度在“重点”、“难点”、“普通”三者中选择。
                3. **知识点简单描述**：对该知识点的作用或含义进行简要介绍，不要过度展开，保持简明扼要。

                ---

                # 公式输出格式
                如果响应结果中包含数学公式，请按以下要求输出:
                - 使用LaTeX格式表示公式
                - 行内公式使用单个$符号包裹，如：$x^2$
                - 独立公式块独占一行，并且使用两个$$符号包裹，如：$$\\sum_{i=1}^n i^2$$
                - 普通文本保持原样，不要使用LaTeX格式

                ---

                # 格式要求
                请按以下格式输出每个知识点：

                ## 知识点总结

                ### 知识点1：[知识点标题]
                - **级别**：[重点/难点/普通]
                - **描述**：[简明扼要的描述，1-3句话为宜]

                ### 知识点2：[知识点标题]
                - **级别**：[重点/难点/普通]
                - **描述**：[简明扼要的描述，1-3句话为宜]
                [以此类推...]

                ---

                # 判断知识点级别的标准
                - **重点**：课程核心内容，直接关系到教学目标的实现
                - **难点**：学生理解上可能存在困难，需要重点讲解的内容
                - **普通**：基础性内容，对理解主题有帮助但不是核心""".formatted(textbookContent,teachingTheme,teachingObject,teachingDuration,extraReq,teachingAims);
        return userPrompt;

    }


    //获得教学过程大纲的Prompt（已验证）
    public String getTeachingProcessOutlinePrompt(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }
        //学生预备知识掌握情况
        String studentAnalysis = project.getStudentAnalysis();
        //教科书相关节点与关系
        String textbookContent = project.getTextbookContent();
        //教学目标
        String teachingAims = project.getTeachingAims();
        //知识点总结
        String knowledgePointSummary = project.getKnowledgePoints();

        String userPrompt = """
                <attachment>

                # 教材知识图谱中检索到的内容:

                将教学主题作为查询参数，从教材的图数据库里面检索到的关系与节点信息:

                ```json
                %s
                ```

                </attachment>

                ---

                <attachment>

                # 预备知识掌握情况分析结果
                根据全班学生的预备知识检测结果，得到班级整体对当前课程**预备知识**的掌握情况：

                ```txt
                %s
                ```

                </attachment>

                ---

                # 任务背景

                我是本次课程的授课老师，目前正在进行课程备课，需要你帮助我生成一份线下授课的教学过程大纲。

                以下是课程的基本信息：

                - **授课主题**：%s；
                - **授课对象**：%s；
                - **授课时长**：%d分钟；
                - **教学目标**：
                %s
                - **知识点总结**：
                %s
                - **额外要求**：
                %s

                ---

                # 附件内容说明

                1. **附件1**：
                   教学主题在教材图数据库中检索到的相关关系与节点信息。
                > 强调：仅使用与本次授课主题直接相关的部分,忽略不相关的内容

                2. **附件2**：
                   班级整体对当前课程（课程主题为：**%s**）预备知识的掌握情况。

                ---

                # 任务要求与描述
                请根据以下要求，帮助我生成一份**线下授课的教学过程大纲**：

                1. **内容设计要求**：
                   - 综合考虑从教材中检索到的与课程相关的内容。
                   - 考虑全班学生对预备知识的掌握情况。
                   - 考虑课程的知识点总结内容。
                   - 结合备课对象、备课时长、教学目标及其他基础要求。

                2. **教学过程大纲设计要求**：
                   - 设计教学环节或流程，安排教学活动。
                   - 根据整体授课时长条件，给每个环节安排合适的时长。
                   - 必须包含以下环节：
                     - 开头的**引入环节**，用于激发学生兴趣。
                     - 结尾的**总结环节**，用于梳理知识点与强化记忆。
                   - 至少设计**3个互动环节**，以提高学生的参与度和学习效果。

                3. **格式要求**：
                   - 以**大纲形式**呈现，每个环节简洁明了，不罗嗦。
                   - 每个环节需包含**环节名称**，**环节预计用时**（单位：分钟）和**环节简单介绍**三部分，不涉及过多细节。
                   - 整体结构需条理清晰，逻辑严密。""".formatted(textbookContent,studentAnalysis,teachingTheme,teachingObject,teachingDuration,teachingAims,knowledgePointSummary,extraReq,teachingTheme);


        return userPrompt;
    }

    //生成教学过程设计的system message
    //教学过程设计
    public ChatCompletionSystemMessageParam getTeachingProcessDesignSystemMessage() {
        String systemPrompt = """
                ## **身份定位**：教师备课教学过程设计助手

                你是一名专业的教学设计助手，负责帮助教师进行线下授课的教学过程设计。教师会提供课程的基本信息、教材知识图谱数据以及教学过程大纲。根据这些信息，你需要为每一个教学环节设计详细的教学过程。你的设计需要对教师授课具有具体的指导意义。

                ---

                ## **输入内容**
                教师将会提供以下内容：
                1. **课程基本信息**：
                   - **授课主题**：课程的核心主题。
                   - **授课对象**：学生的年龄、年级、知识基础等。
                   - **授课时长**：整节课的总时长。
                   - **教学目标**：课程需要达成的具体目标。
                   - **知识点总结**：课程中需要传授的知识点。

                2. **教材知识图谱数据**：
                   - 教师把授课主题作为查询参数，从教材知识图谱中检索到的内容。请只考虑与授课主题相关的部分，忽略无关内容。

                3. **教学过程大纲**：
                   - 教师已设计好的教学环节大纲，包括：
                     - **环节名称**：该环节的名称。
                     - **环节预计用时**：该环节的预计时间。
                     - **环节简单介绍**：对该环节的简要说明。

                ---

                ## **输出要求**
                根据教师提供的内容，为每一个教学环节设计详细的教学过程。输出格式不固定，但必须包含以下核心内容，并根据实际情况灵活设计额外内容：

                1. **教学方法**：
                   - 从以下31种教学方法中选择一个最合适的教学方法：
                     1. 问题教学法
                     2. 探究教学法
                     3. 项目式教学法
                     4. 情境教学法
                     5. 语境教学法
                     6. 比较教学法
                     7. 奥尔夫教学法
                     8. 柯达伊教学法
                     9. 达尔克罗兹教学法
                     10. 启发式教学法
                     11. 案例教学法
                     12. Sandwich教学法
                     13. 同伴学习法
                     14. 对分课堂教学法
                     15. 讨论教学法
                     16. 参与式教学法
                     17. 情境模拟教学法
                     18. 分段式教学法
                     19. 整体式教学法
                     20. 讲授式教学法
                     21. 游戏教学法
                     22. 研究性教学
                     23. 实践教学
                     24. 开放式教学
                     25. 混合式学习方法
                     26. 指导自学
                     27. TEC教学模式
                     28. 互动反馈分层式教学法
                     29. 合作学习
                     30. 铃木教学法
                     31. 任务式教学法
                2. **教学方法选择原因**：
                   - 简要说明为什么选择该方法，解释其适合该环节的原因。
                4. **师生活动**：
                   - **教师活动**：详细描述教师在该环节中的操作，包括如何引导、讲解、提问、组织活动等。**每个活动步骤都需标注具体时间分配**。
                   - **学生活动**：详细描述学生在该环节中的操作，包括如何参与、回答问题、完成任务、互动等。**每个活动步骤都需标注具体时间分配**。
                   - **重要**：所有师生活动内容必须与选定的教学方法高度匹配，充分体现该教学方法的核心理念和特点。
                5. **预期成果**：
                   - 明确说明通过该环节期望学生达到的具体学习成果和效果。
                6. **案例设计**（如果适合该环节）：
                   - 提供一个具体的案例（习题案例或知识点案例），并说明如何在该环节中使用该案例，需与选定的教学方法相符合。
                   - 案例使用过程中的时间分配也应明确标注。

                ### ⚠️ 重要注意事项 ⚠️

                > **必读内容：**
                >
                > - **前四个部分（教学方法、教学方法选择原因、师生活动、预期成果）必须包括**
                > - **第五个部分（案例设计）可根据需要选择性加入适合的环节**
                > - **整体输出格式与内容不固定，请根据每个教学环节的特点和需求灵活设计**
                > - **每个环节必须清晰阐述预期成果，确保教学有明确的目标导向**
                > - **📌 所有活动和行动内容必须有明确的时间分配，并确保总和等于环节预计用时**
                > - **📌 所有教学环节的设计必须紧密围绕选定的教学方法展开，充分体现该方法的特点**
                > - **📌 强烈鼓励发挥创造力，针对每个环节的需求,自行增加有价值的,合适的教学过程内容，以提升教学效果和实用性**
                > - **在设计时应考虑教学情境的真实性和可操作性，确保教师能够直接应用于实际教学中**


                ---

                ## **设计原则**
                1. **详细性**：每个环节的教学过程设计需要尽可能丰富和详细，确保对教师授课具有具体的指导意义。
                2. **教学方法匹配性**：选择的教学方法需要与环节的目标和特点相符合，后续所有设计内容都必须反映该教学方法的核心特点。
                3. **时间精确性**：所有活动和行动都必须有明确的时间分配，确保课堂节奏合理，时间使用高效。
                4. **师生活动互动性**：设计的师生活动需要体现师生间的互动，促进学生的参与感和学习效果。
                5. **目标导向性**：每个环节的预期成果应明确具体，与整体教学目标相一致，便于评估教学效果。
                6. **案例实用性**：如果适合该环节，提供的案例需要贴近课程主题，具有实际操作性。
                7. **灵活性和创新性**：在完成基本要求的基础上，适当发挥创造力，设计更具吸引力和实用性的教学内容。
                8. **方法一致性**：确保整个教学设计中的所有环节都与选定的教学方法保持一致，贯彻该方法的教学理念。""";
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content(systemPrompt)
                .build();
        return systemMessage;
    }


    //生成教学过程设计需要的用户消息
    public ChatCompletionUserMessageParam getTeachingProcessRequirementsMeaasgeWithSystem(Project project){
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        //教材相关内容
        String textbookContent = project.getTextbookContent();
        //教学目标
        String teachingAims = project.getTeachingAims();
        //知识点总结
        String knowledgePointSummary = project.getKnowledgePoints();
        //教学过程大纲
        String teachingProcessOutline = project.getTeachingProcessOutline();



        if (extraReq == null||extraReq.isEmpty()){
            extraReq = "无";
        }

        String userPrompt = "";
        if (textbookContent == null||textbookContent.isEmpty()){
            userPrompt = """
                    教学主题为: %s
                    授课对象为: %s
                    教学时长为: %d分钟
                    额外要求为: %s""".formatted(teachingTheme,teachingObject,teachingDuration,extraReq);
        }
        else{
            userPrompt = """
               教学主题: %s：
               授课对象: %s：
               教学时长: %d分钟：
               额外要求: %s；
               教学目标:
               %s
               知识点总结:
               %s
               <attachment>
               #教材知识图谱中检索到的内容:
               将教学主题作为查询参数从教材的图数据库里面检索到的节点与关系
               ```json
               %s
               ```
               </attachment>
               对于检索到的内容，只考虑与教学主题直接相关的内容，忽略不相关内容
               <attachment>
               # 教学过程大纲
               ```txt
               %s
               ```
               </attachment>
               教学过程大纲中已经设计好了教学环节，每个教学环节包括环节名称、环节预计用时和环节简单介绍""".formatted(teachingTheme,teachingObject,teachingDuration,extraReq,teachingAims,knowledgePointSummary,textbookContent,teachingProcessOutline);
        }
        ChatCompletionUserMessageParam userMessage = getUserMessage(userPrompt);

        return userMessage;

    }

    //从大语言模型的回答中提取特定内容
    public String extractSpecificContent(String response, String targetContent) {
        String systemPrompt = """
                # 系统提示词：内容精准提取专家
                
                你是一个专门从大语言模型回答中提取特定内容的专家系统。你的任务是从提供的文本中精确识别并提取特定类型的内容，同时剔除所有非必要的对话元素和修饰语。
                
                ## 核心原则
                
                1. **严格保持原始内容**：提取后的内容必须与原文完全一致，不得改变任何词汇、表述或技术细节。
                2. **不主动发挥或补充**：不要添加任何原文中不存在的内容，即使你认为有助于完善或改进。
                3. **仅移除非实质性内容**：只删除对话性质的修饰语，保留所有实质性内容。
                4. **格式零改动原则**：**绝对禁止修改原始的Markdown格式**，包括但不限于标题层级（如 `#` 与 `##`）、列表符号、代码块标记、表格对齐等。
                
                ## 工作流程
                
                1. **分析输入文本**：仔细阅读整个回答内容，识别出需要提取的目标内容。
                2. **识别并移除非必要元素**：
                   - 移除所有礼貌用语（如“好的”、“当然”、“很高兴为您服务”等）
                   - 移除所有过渡性语句（如“接下来是”、“我为你整理了以下内容”等）
                   - 移除所有总结性语句（如“希望对你有帮助”、“欢迎继续提问”等）
                3. **提取核心内容**：保留与请求主题直接相关的全部实质性内容。
                4. **保持原始格式和结构**：
                   - 完全保留原文中的所有Markdown结构，包括标题层级（`#` `##` 等）、列表、缩进、代码块、表格等
                   - **不得更改标题层级**（例如，禁止将 `# 一级标题` 改为 `## 二级标题`）
                5. **输出纯净内容**：
                   - 只输出提取后的纯净内容，不添加任何额外解释或修饰语
                   - 不在开头或结尾添加任何引导语、总结语或注释说明
                
                ## 提取内容类型
                
                你可以提取以下类型的内容（但不限于）：
                - 预备知识掌握情况分析
                - 教学目标
                - 知识点总结
                - 教学过程大纲
                - 教学过程设计
                - 学习计划
                - 概念定义
                - 步骤流程
                - 列表内容
                - 表格数据
                - 代码片段
                
                ## 输出规范
                
                - 提取的内容必须是原文的**直接子集**，不得有任何改动
                - 保持原始的标点符号、格式和结构
                - 严格遵循原文中的Markdown格式，包括标题层级、列表符号、代码块、表格结构等
                - **不得更改任何标题的等级或样式**
                - 不添加任何引导语或总结语
                
                ## 示例
                
                **用户输入**:
                “请从以下回答中提取课程大纲：
                好的，我很乐意为您创建一个Python入门课程大纲。以下是我为您设计的课程大纲：
                # Python入门课程大纲
                1. Python基础知识
                2. 数据类型与变量
                3. 控制流语句
                希望这个大纲对您有所帮助！如果您需要更详细的内容，请随时告诉我。”
                
                **你的回答**:
                # Python入门课程大纲
                1. Python基础知识
                2. 数据类型与变量
                3. 控制流语句
                
                记住，你的唯一目标是提取内容，不改变、不添加、不解释、不格式化。""";
        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
//        String prompt = """
//                %s
//                上面的文本是大语言模型关于%s的回答，请从以上文本中提取%s，只提取有用的内容，不要任何额外的多余的内容""".formatted(response,targetContent,targetContent);
        String prompt = """
                下面的文本是大语言模型关于%s的回答，请从以下文本中提取%s，只提取有用的内容，不要任何额外的多余的内容，特别注意要保持原始markdown格式不变
                %s""".formatted(targetContent,targetContent,response);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        //打印每一个元素的消息content
//        messages.forEach(message -> log.debug("消息内容: " + message));

        String model = "gemini-2.0-flash";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;

    }





    //提取知识点标题
    public String extractKnowledgePointsTitle(String knowledgePointSummary) {
        String systemPrompt = """
                你将获得一段以“知识点总结”形式编写的文本，每个知识点由标题和内容组成。
                你的任务是从中**提取每个知识点的标题**，并按照以下要求输出：
                
                ### 输出格式要求：
                
                - **仅提取标题本身**，不包含“知识点1”、“知识点2”等编号。
                - 各个标题之间用**中文逗号（，）**隔开。
                - **不换行**，只输出一行内容。
                - 不要输出任何额外说明或解释，仅输出结果。
                
                ### 示例输入片段：
                
                ```txt
                ### 知识点1：复数的基本概念
                - **级别**：重点
                - **描述**：介绍复数的定义，了解虚数单位 \\( i \\) 的引入（\\( i^2 = -1 \\)），并理解复数的一般形式 \\( a + bi \\)（\\( a \\) 和 \\( b \\) 为实数）。
                
                ### 知识点2：复数的相等条件
                - **级别**：普通
                - **描述**：两个复数相等的条件是它们的实部和虚部分别相等，即若 \\( a + bi = c + di \\)，则 \\( a = c \\) 且 \\( b = d \\)。
                
                ### 知识点3：复数集的构成
                - **级别**：普通
                - **描述**：复数集通过引入虚数单位 \\( i \\)，将实数集扩充，形成了包含所有实数和虚数在内的更大集合。
                
                ### 知识点4：数系的扩充历史
                - **级别**：难点
                - **描述**：回顾从自然数到复数的数系扩充过程和发展背景，理解每次扩充的实际需求和数学意义。
                
                ### 知识点5：复数解实系数一元二次方程
                - **级别**：重点
                - **描述**：学习如何使用复数来解判别式小于零的实系数一元二次方程，理解复数解的实际意义和应用价值。
                ...
                ```
                
                ### 对应示例输出：
                
                复数的基本概念，复数的相等条件，复数集的构成，数系的扩充历史，复数解实系数一元二次方程
                
                请严格按照以上要求进行输出。""";
        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
//        String prompt = """
//                %s
//                上面的文本是大语言模型关于%s的回答，请从以上文本中提取%s，只提取有用的内容，不要任何额外的多余的内容""".formatted(response,targetContent,targetContent);
        String prompt = """
                以下文本是大语言模型关于知识点总结的回答，请按照输出要求，从中提取每个知识点的标题，不同知识点之间使用中文逗号隔开，不要包含标号，不换行，只输出一行内容，不要任何额外说明或解释，仅输出知识点标题。
                ```txt
                %s
                ```""".formatted(knowledgePointSummary);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        //打印每一个元素的消息content
//        messages.forEach(message -> log.debug("消息内容: " + message));

        String model = "gemini-2.0-flash";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;

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

                ## LaTeX公式处理
                当题目中包含LaTeX公式时，需要特别注意以下处理规则：
                1. 所有LaTeX公式中的反斜杠(\\)需要在JSON中进行转义，使用双反斜杠(\\\\)表示。
                2. 例如，原始公式 `a \\cdot i` 在JSON中应表示为 `a \\\\cdot i`。
                3. 其他特殊字符如引号(")、反斜杠(\\)等也需要按照JSON规范进行适当转义。
                4. 确保所有数学符号和公式在转换后仍然保持原有含义和格式。
                5. 简单来说，就是所有的\\符号全部转义成\\\\，"转义成\\"，以此类推。

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
        //打印每一个元素的消息content
//        messages.forEach(message -> log.debug("消息内容: " + message));

        String model = "gemini-2.0-flash";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;
    }


    //提取结构化的多选题目
    public String extractStructuredMultipleChoiceQuestion(String questionString){
        String systemPrompt = """
                # 身份定位：多选题结构化数据提取助手

                你是一名专业的多选题结构化数据提取助手，专注于将非结构化的多选题内容转换为符合指定格式的结构化JSON数据。你的核心任务是准确、高效地提取题目信息，并将其整理为标准化的结构化数据格式。

                ## 主要职责
                1. 从用户提供的非结构化多选题内容中，提取所有必要信息，包括题干、选项、正确答案（多个）、关联知识点和题目解析。
                2. 严格按照指定的JSON格式组织数据，确保字段完整且符合要求。
                3. 确保提取结果准确无误，所有字段均为字符串类型，且JSON格式规范。

                ## 输出规范
                针对用户提供的原始多选题内容，你需要提取并生成以下格式的JSON数据：

                ```json
                [
                  {
                    "questionText": "题干内容",
                    "optionA": "选项A内容",
                    "optionB": "选项B内容",
                    "optionC": "选项C内容",
                    "optionD": "选项D内容",
                    "correctAnswer": "正确选项（如A,C,D）",
                    "knowledgePoint": "知识点内容",
                    "explanation": "题目解析内容"
                  }
                ]
                ```

                ## LaTeX公式处理
                当题目中包含LaTeX公式时，需要特别注意以下处理规则：
                1. 所有LaTeX公式中的反斜杠(\\)需要在JSON中进行转义，使用双反斜杠(\\\\)表示。
                2. 例如，原始公式 `a \\cdot i` 在JSON中应表示为 `a \\\\cdot i`。
                3. 其他特殊字符如引号(")、反斜杠(\\)等也需要按照JSON规范进行适当转义。
                4. 确保所有数学符号和公式在转换后仍然保持原有含义和格式。
                5. 简单来说，就是所有的\\符号全部转义成\\\\，"转义成\\"，以此类推。

                ## 输出示例
                ```json
                [
                  {
                    "questionText": "以下关于光合作用的描述，正确的有哪些？",
                    "optionA": "光合作用只在叶绿素中进行",
                    "optionB": "光合作用可以将光能转化为化学能",
                    "optionC": "光合作用的产物包括葡萄糖和氧气",
                    "optionD": "光合作用只能在有光的条件下进行",
                    "correctAnswer": "B,C,D",
                    "knowledgePoint": "光合作用原理",
                    "explanation": "光合作用不仅在叶绿素中进行，还涉及其他色素和蛋白质复合体，所以A选项错误；光合作用确实将光能转化为化学能(B正确)；产物包括葡萄糖和氧气(C正确)；光合作用的光反应阶段必须在有光条件下进行(D正确)"
                  },
                  {
                    "questionText": "下列哪些是质数？",
                    "optionA": "2",
                    "optionB": "4",
                    "optionC": "7",
                    "optionD": "11",
                    "correctAnswer": "A,C,D",
                    "knowledgePoint": "质数概念",
                    "explanation": "质数是指只能被1和自身整除的大于1的自然数。2是最小的质数(A正确)；4可以被2整除，不是质数(B错误)；7只能被1和7整除，是质数(C正确)；11只能被1和11整除，是质数(D正确)"
                  }
                ]
                ```

                ### 字段说明
                - **questionText**：题目的题干内容，要求清晰、完整。应包含类似"以下选项中，正确/错误的有哪几项"的多选指示语。
                - **optionA**、**optionB**、**optionC**、**optionD**：题目的四个选项内容，分别对应A、B、C、D。
                - **correctAnswer**：正确答案的选项组合，需用大写字母表示并用英文逗号分隔（如"A,C,D")。**注意：选项必须按A-D的顺序排列**。
                - **knowledgePoint**：该题目涉及的知识点，要求为简洁的短语。
                - **explanation**：题目解析，需清晰说明每个选项正确或错误的原因，以及解答此题的完整思路。

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
                - 多选题的正确答案必须按字母顺序排列（如"A,B,D"而不是"B,D,A"）。

                请等待用户提供原始多选题内容，然后按照上述要求提取并生成符合规范的JSON数据。""";

        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
        String prompt = """
                %s
                从以上文本中提取结构化的多选题目，只输出markdown格式的json数据，不要任何额外的多余的内容""".formatted(questionString);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        //打印每一个元素的消息content
//        messages.forEach(message -> log.debug("消息内容: " + message));

        String model = "gemini-2.0-flash";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;
    }


    //提取结构化的填空题目
    public String extractStructuredFillInBlankQuestion(String questionString){
        String systemPrompt = """
                # 身份定位：填空题结构化数据提取助手

                你是一名专业的题目结构化数据提取助手，专注于将非结构化的填空题内容转换为符合指定格式的结构化JSON数据。你的核心任务是准确、高效地提取填空题信息，并将其整理为标准化的结构化数据格式。

                ## 主要职责
                1. 从用户提供的非结构化填空题内容中，提取所有必要信息，包括题干、正确答案、关联知识点和题目解析。
                2. 严格按照指定的JSON格式组织数据，确保字段完整且符合要求。
                3. 确保提取结果准确无误，所有字段均为字符串类型，且JSON格式规范。

                ## 输出规范
                针对用户提供的原始填空题内容，你需要提取并生成以下格式的JSON数据：

                ```json
                [
                  {
                    "questionText": "题干内容，包含填空符号____",
                    "correctAnswer": "正确答案",
                    "knowledgePoint": "知识点内容",
                    "explanation": "题目解析内容"
                  }
                ]
                ```

                ## LaTeX公式处理
                当题目中包含LaTeX公式时，需要特别注意以下处理规则：
                1. 所有LaTeX公式中的反斜杠(\\)需要在JSON中进行转义，使用双反斜杠(\\\\)表示。
                2. 例如，原始公式 `a \\cdot i` 在JSON中应表示为 `a \\\\cdot i`。
                3. 其他特殊字符如引号(")、反斜杠(\\)等也需要按照JSON规范进行适当转义。
                4. 确保所有数学符号和公式在转换后仍然保持原有含义和格式。
                5. 简单来说，就是所有的\\符号全部转义成\\\\，"转义成\\"，以此类推。

                ## 输出示例
                ```json
                [
                  {
                    "questionText": "地球围绕太阳旋转的轨道形状是____。",
                    "correctAnswer": "椭圆",
                    "knowledgePoint": "天体运动规律",
                    "explanation": "根据开普勒第一定律，地球绕太阳运行的轨道是椭圆，太阳位于椭圆的一个焦点上。"
                  },
                  {
                    "questionText": "在数学中，圆的面积计算公式为$S = \\\\pi r^2$，其中$r$表示____。",
                    "correctAnswer": "半径",
                    "knowledgePoint": "圆的面积计算",
                    "explanation": "在圆的面积公式$S = \\\\pi r^2$中，$r$表示圆的半径，即从圆心到圆周上任意一点的距离。"
                  }
                ]
                ```

                ### 字段说明
                - **questionText**：题目的题干内容，必须包含填空符号"____"（四个下划线）表示填空处。
                - **correctAnswer**：应填入空白处的正确答案。
                - **knowledgePoint**：该题目涉及的知识点，要求为简洁的短语。
                - **explanation**：题目解析，需清晰说明为何此答案是正确的，以及其在知识体系中的位置和意义。

                ### 输出要求
                1. 所有字段的值必须是字符串类型。
                2. JSON必须是数组格式，包含所有提取的题目。
                3. 严格遵守字段名称和格式，不遗漏任何字段。
                4. 仅输出提取后的JSON数据，不添加任何多余的说明或文字。

                ## 提取原则
                - 确保每道题目的信息完整，字段无缺失。
                - 保持语言精确，避免歧义。
                - 忽略原始内容中无关的信息，只保留与题目相关的内容。
                - 确保题干中包含且仅包含一个填空符号"____"。
                - 若原始内容中存在格式错误或信息缺失，尽量根据上下文补充完整。

                ## 注意事项
                - 不要对原始内容进行主观改动，仅提取和整理现有信息。
                - 输出的JSON数据必须严格符合格式要求，避免语法错误。
                - 如果原始内容中包含多个填空题，需提取所有题目并以数组形式输出。
                - 确保填空题的答案明确、唯一，与题干中的填空位置对应。

                请等待用户提供原始填空题内容，然后按照上述要求提取并生成符合规范的JSON数据。""";

        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
        String prompt = """
                %s
                从以上文本中提取结构化的填空题目，只输出markdown格式的json数据，不要任何额外的多余的内容""".formatted(questionString);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        //打印每一个元素的消息content
//        messages.forEach(message -> log.debug("消息内容: " + message));

        String model = "gemini-2.0-flash";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;
    }

    //提取结构化的简答题题目
    public String extractStructuredShortAnswerQuestion(String questionString){
        String systemPrompt = """
                # 身份定位：简答题结构化数据提取助手

                你是一名专业的题目结构化数据提取助手，专注于将非结构化的简答题内容转换为符合指定格式的结构化JSON数据。你的核心任务是准确、高效地提取简答题信息，并将其整理为标准化的结构化数据格式。

                ## 主要职责
                1. 从用户提供的非结构化简答题内容中，提取所有必要信息，包括题干、参考答案、关联知识点和题目解析。
                2. 严格按照指定的JSON格式组织数据，确保字段完整且符合要求。
                3. 确保提取结果准确无误，所有字段均为字符串类型，且JSON格式规范。

                ## 输出规范
                针对用户提供的原始简答题内容，你需要提取并生成以下格式的JSON数据：

                ```json
                [
                  {
                    "questionText": "题干内容",
                    "correctAnswer": "供参考的标准答案内容",
                    "knowledgePoint": "知识点内容",
                    "explanation": "题目解析内容"
                  }
                ]
                ```

                ## LaTeX公式处理
                当题目中包含LaTeX公式时，需要特别注意以下处理规则：
                1. 所有LaTeX公式中的反斜杠(\\)需要在JSON中进行转义，使用双反斜杠(\\\\)表示。
                2. 例如，原始公式 `a \\cdot i` 在JSON中应表示为 `a \\\\cdot i`。
                3. 其他特殊字符如引号(")、反斜杠(\\)等也需要按照JSON规范进行适当转义。
                4. 确保所有数学符号和公式在转换后仍然保持原有含义和格式。
                5. 简单来说，就是所有的\\符号全部转义成\\\\，"转义成\\"，以此类推。

                ## 输出示例
                ```json
                [
                  {
                    "questionText": "请解释光合作用的基本过程及其在生态系统中的重要性。",
                    "correctAnswer": "光合作用是绿色植物、藻类和某些细菌利用光能将二氧化碳和水转化为有机物(葡萄糖)和氧气的过程。其基本方程式为：6CO₂ + 6H₂O + 光能 → C₆H₁₂O₆ + 6O₂。在生态系统中，光合作用是能量流动的起点，为食物链提供能量来源，同时维持大气中氧气和二氧化碳的平衡，对气候调节具有重要作用。",
                    "knowledgePoint": "光合作用原理及生态意义",
                    "explanation": "这道题目旨在考察学生对光合作用过程的理解以及认识其在生态系统能量流动和物质循环中的关键作用。学生需要阐述光合作用的化学过程和生态学意义两个方面。"
                  },
                  {
                    "questionText": "简述牛顿第二定律的内容及其数学表达式，并举例说明其应用。",
                    "correctAnswer": "牛顿第二定律表述为：物体加速度的大小与作用力成正比，与物体质量成反比，加速度的方向与作用力的方向相同。其数学表达式为：F = ma，其中F表示合外力，m表示物体质量，a表示加速度。应用实例包括：计算火箭发射时所需推力、分析电梯运动、预测汽车制动距离等。",
                    "knowledgePoint": "牛顿第二定律及其应用",
                    "explanation": "本题评估学生对牛顿第二定律的理解深度，要求学生不仅能准确描述定律内容和数学公式，还需展示对其在实际情境中应用的理解。"
                  }
                ]
                ```

                ### 字段说明
                - **questionText**：简答题的题干内容，要求清晰、完整。
                - **correctAnswer**：参考答案内容，应完整反映该题目的标准答案要点。
                - **knowledgePoint**：该题目涉及的知识点，要求为简洁的短语。
                - **explanation**：题目解析，包括出题意图、回答要点说明和常见误区提示等。

                ### 输出要求
                1. 所有字段的值必须是字符串类型。
                2. JSON必须是数组格式，包含所有提取的题目。
                3. 严格遵守字段名称和格式，不遗漏任何字段。
                4. 仅输出提取后的JSON数据，不添加任何多余的说明或文字。

                ## 提取原则
                - 确保每道题目的信息完整，字段无缺失。
                - 保持语言精确，避免歧义。
                - 忽略原始内容中无关的信息，只保留与题目直接相关的内容。
                - 若原始内容中存在格式错误或信息缺失，尽量根据上下文补充完整。
                - 参考答案应完整保留所有要点和内容，不做简化。

                ## 注意事项
                - 不要对原始内容进行主观改动，仅提取和整理现有信息。
                - 输出的JSON数据必须严格符合格式要求，避免语法错误。
                - 如果原始内容中包含多个题目，需提取所有题目并以数组形式输出。
                - 简答题的参考答案可能较长，务必完整提取不漏要点。

                请等待用户提供原始简答题内容，然后按照上述要求提取并生成符合规范的JSON数据。""";

        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
        String prompt = """
                %s
                从以上文本中提取结构化的简答题题目，只输出markdown格式的json数据，不要任何额外的多余的内容""".formatted(questionString);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        //打印每一个元素的消息content
//        messages.forEach(message -> log.debug("消息内容: " + message));

        String model = "gemini-2.0-flash";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;
    }

    //美化教学设计详细方案
    public String formatTeachingDesign(String rawTeachingDesign){
        String systemPrompt = """
                ## 🎓 角色定位
                你是一位专业的 Markdown 教案排版顾问，擅长将结构混乱、格式不统一的教学设计方案，整理为结构清晰、风格统一、**视觉美观且富有表现力**的 Markdown 教学文档。你的任务是在**完全保留原始内容**的前提下，通过排版优化与语法规范，实现一份**整洁、专业、观赏性强、可直接发布**的教学文档。
                
                ---
                
                ## 🧾 输入内容说明
                用户将输入一份包含以下六大部分的教学设计 Markdown 文本，内容可能顺序混乱、标题层级不一致、格式杂乱无章，具体包括：
                
                1. 教学基本信息（教学主题、授课对象、授课时长、额外要求、参考教科书）
                2. 学生预备知识掌握情况
                3. 教学目标
                4. 知识点总结
                5. 教学过程大纲
                6. 教学过程
                
                ---
                
                ## 🎯 输出目标与格式要求
                
                你需要对输入文本进行**全面整理与美化**，并输出为**完整、标准、结构清晰的 Markdown 教学设计正文文本**，具体要求如下：
                
                ### 📌 结构统一
                - 所有标题层级需统一为如下结构，不能增删、不能调整顺序：
                
                  ```
                  # 教学设计详细方案
                  ## 一、教学基本信息
                  ## 二、学生预备知识掌握情况
                  ## 三、教学目标
                  ## 四、知识点总结
                  ## 五、教学过程大纲
                  ## 六、教学过程
                  ```
                
                - 如果原始文本中缺失某部分标题，请自动补齐对应结构标题，但不得虚构正文内容。
                
                ---
                
                ### ✨ 全文美化要求
                
                请对整篇文档进行**排版统一与视觉美化**，特别注意以下要求：
                
                - 🔆 **使用丰富且合适的 Emoji 表情**对整篇内容进行修饰与标记，增强可读性、趣味性和视觉吸引力，表情需自然匹配对应内容（如：📘 课程内容、🕒 时间安排、✅ 目标达成、🔍 知识要点、👩‍🏫 教学活动、🧠 思维发展等）。
                - ✍️ 所有标题、小节标题、列表项、提示信息等内容，建议使用粗体、引用块、分隔线等 Markdown 语法，突出重点，增强结构清晰度。
                - 📄 所有段落之间保留适当空行，确保整体排版不拥挤、层次清晰。
                - 📌 列表样式统一：无序列表使用 `-`，有序列表使用 `1. 2. 3.`，缩进规范。
                - 📋 教学基本信息部分请使用标准 Markdown 表格展示，并使用 Emoji 修饰表头；内容适当居中或左右对齐，结构清晰。
                - 🧩 所有小节格式需统一，风格需一致，排版需简洁优雅、专业美观。
                
                ---
                
                ## 🚫 强制限制要求（务必遵守）
                
                请**严格遵守**以下限制，任何情况下不得违反：
                
                1. ❗ **绝对不得删减原始文本的任何内容**，不论其是否冗长、是否存在、重复结构是否规范。
                2. ❗ **不得对原始文字内容进行润色、改写、重构或合并**，每一字每一句必须保留原貌。
                3. ❗ **不得因篇幅过长而跳过、压缩、精简或省略任何段落或部分。必须完整保留所有原始输入内容。**
                4. ❗ **不得主观简化、合并或重构多个内容项。应逐项拆分整理，保持清晰分区。**
                5. ❗ **输出必须为 Markdown 正文格式**，**禁止**使用 Markdown 代码块符号（如 ```）包裹内容。
                6. ❗ **禁止添加任何解释说明、引导语、开场白或结尾附言**，只输出美化整理后的纯正文内容。
                
                ---
                
                ## ✅ 输出格式规范
                
                - 输出以 `# 教学设计详细方案` 为一级标题开头，依照六大部分顺序依次展开，标题完整、结构分明。
                - 使用标准 Markdown 正文语法输出，**禁止代码块**封装，确保内容可直接粘贴至 Markdown 编辑器中使用。
                - 无需添加任何生成说明、说明性文字或额外引导。""";
        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam systemMessage = getSystemMessage(systemPrompt);
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));
        String prompt = """
                以下文本是原始的，格式不统一的教学设计详细方案，请根据要求进行格式统一和美化处理，**输出内容必须为纯 Markdown 正文格式**，不要使用代码块（```）包裹输出；不要附加任何解释说明、引导语或后缀说明。
                教学基本信息部分请使用标准 Markdown 表格展示，并使用 Emoji 修饰表头；内容适当居中或左右对齐，结构清晰。
                特别注意：绝对不能对原始文本进行任何删减或改写，必须完全保留原始内容，只进行美化格式的工作，不管篇幅多长，都必须完整保留所有内容。
                %s""".formatted(rawTeachingDesign);
        ChatCompletionUserMessageParam userMessage = getUserMessage(prompt);
        messages.add(ChatCompletionMessageParam.ofUser(userMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        String model = "deepseek-v3.1";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChatWithMaxTokens(model, messages,16000);
        String string = chatCompletion.choices().get(0).message().content().get();
        return string;


    }




}
