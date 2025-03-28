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


    //获得学生预备知识掌握情况，的Prompt
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
                   - 重点关注班级整体表现，突出高频错误与主要不足之处。""".formatted(preexerceseResult,teachingTheme,teachingObject,teachingDuration,extraReq,teachingTheme);
        return userPrompt;
    }


    //获得教学目标的Prompt
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
        //学生预备知识掌握情况
        String studentAnalysis = project.getStudentAnalysis();
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

    //获得知识点总结的Prompt
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
                - **普通**：基础性内容，对理解主题有帮助但不是核心""".formatted(textbookContent,teachingTheme,teachingObject,teachingDuration,extraReq);
        return userPrompt;

    }


    //获得教学过程大纲的Prompt
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
                
                - **授课主题**：……
                - **授课对象**：……
                - **授课时长**：……
                - **教学目标**：……
                - **知识点总结**：……
                - **额外要求**：……
                
                ---
                
                # 附件内容说明
                
                1. **附件1**：
                   教学主题在教材图数据库中检索到的相关关系与节点信息。
                > 强调：仅使用与本次授课主题直接相关的部分,忽略不相关的内容
                
                2. **附件2**：
                   班级整体对当前课程（课程主题为：**……**）预备知识的掌握情况。
                
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
                   - 每个环节需包含**环节名称**，**环节预计用时**和**环节简单介绍**三部分，不涉及过多细节。
                   - 整体结构需条理清晰，逻辑严密。""".formatted(textbookContent,studentAnalysis,teachingTheme,teachingObject,teachingDuration,extraReq,teachingTheme);


        return userPrompt;
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


}
