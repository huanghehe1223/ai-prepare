package upc.projectname.projectservice;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.projectservice.entity.ChatAnswerDTO;
import upc.projectname.projectservice.entity.ResourceDTO;
import upc.projectname.projectservice.service.ProjectService;
import upc.projectname.projectservice.utils.*;
import upc.projectname.upccommon.api.client.QuestionClient;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.upccommon.domain.po.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class ProjectServiceApplicationTests {

	@Autowired
	private OpenAISdkUtils openAISdkUtils;
	@Autowired
	private PromptUtils promptUtils;
    @Autowired
    private ProjectService projectService;


	@Test
	void testChat() {
		JSONArray jsonArray = new JSONArray();
		JSONObject person1 = new JSONObject();
		person1.put("name", "huanghe");
		person1.put("age", 18);
		person1.put("sex","male");
		jsonArray.add(person1);
		JSONObject person2 = new JSONObject();
		person2.put("name", "zhangsan");
		person2.put("age", 25);
		person2.put("sex","female");
		jsonArray.add(person2);
		String jsonContent = JSON.toJSONString(jsonArray, JSONWriter.Feature.PrettyFormat);
		StringBuilder markdownJson = new StringBuilder();
		markdownJson.append("```json\n");
		markdownJson.append(jsonContent);
		markdownJson.append("\n```");
		String finalMarkdownJson = markdownJson.toString();
		//正文从第二行开始
		String question = """
				%s
				为什么前后都有```这种符号，这是什么意思?""".formatted(finalMarkdownJson);

		// 输出最终的Markdown格式JSON字符串

//		System.out.println(question);






		List<ChatCompletionMessageParam> messages = new ArrayList<>();
		String model = "deepseek-r1";
		ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
				.content(question)
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage));
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(model)
				.messages(messages)
				.build();
		OpenAIClient openAIClient = openAISdkUtils.defaultClient;

//		try (StreamResponse<ChatCompletionChunk> streamResponse = openAIClient.chat().completions().createStreaming(params)) {
//			streamResponse.stream().forEach(chunk -> {
//				List<ChatCompletionChunk.Choice> choices = chunk.choices();
//				if (!choices.isEmpty()){
//					Optional<String> optionalString = chunk.choices().get(0).delta().content();
//					Optional<ChatCompletionChunk.Choice.FinishReason> finishReason = chunk.choices().get(0).finishReason();
//					if (finishReason.isPresent()) {
//						System.out.println("finishReason:" + finishReason.get());
//					}
//					if (optionalString.isPresent()) {
//						String content = optionalString.get();
//						System.out.print(content);
//					}
//
//				}
//
//			});
//			System.out.println("No more chunks!");
//		}
		try (StreamResponse<ChatCompletionChunk> streamResponse = openAIClient.chat().completions().createStreaming(params)) {
			streamResponse.stream().forEach(chunk -> {System.out.println(chunk);});
			System.out.println("No more chunks!");
		}



	}
	@Test
	void testGetModels(){
		
		OpenAIClient openAIClient = openAISdkUtils.defaultClient;
		List<Model> models = openAIClient.models().list().response().data();
		System.out.println(models);


	}

	@Test
	void testJson(){
		String json = """
				{
					"model": "QwQ-32B",
					"messages": [
						{
							"user": "你知道中国吗"
						}
					]
				}
				""";
		System.out.println(json);
	}

	@Test
	void testJson2(){
		JSONArray jsonArray = new JSONArray();
		JSONObject person1 = new JSONObject();
		person1.put("name", "huanghe");
		person1.put("age", 18);
		person1.put("sex","male");
		jsonArray.add(person1);
		JSONObject person2 = new JSONObject();
		person2.put("name", "zhangsan");
		person2.put("age", 25);
		person2.put("sex","female");
		jsonArray.add(person2);
		String jsonContent = JSON.toJSONString(jsonArray, JSONWriter.Feature.PrettyFormat);
		StringBuilder markdownJson = new StringBuilder();
		markdownJson.append("```json\n");
		markdownJson.append(jsonContent);
		markdownJson.append("\n```");


		// 输出最终的Markdown格式JSON字符串
		String finalMarkdownJson = markdownJson.toString();
		String rowText = """
				下面是一段markdown格式的json数组
				%s
				希望能满足你的条件""".formatted(finalMarkdownJson);

		// 输出最终的Markdown格式JSON字符串

		System.out.println(rowText);
	}

	@Test
	void testString()
	{
		String lastUserMessageText = "你知道中国吗";
		String newText = """
                回复要求: 使用轻薄的语气回答我;
                消息正文: """+lastUserMessageText +"""
                你好
                """;
		System.out.println(newText);
	}


	@Test
	void testChat1() {






		List<ChatCompletionMessageParam> messages = new ArrayList<>();
//		String model = "doubao-1.5-vision-pro";
		String model = "deepseek-r1";
		ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
				.content("给我生成一个含有根号（无理数）的latex公式")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage));
//		ChatCompletionUserMessageParam userMessage1 = ChatCompletionUserMessageParam.builder()
//				.content("你知道我的名字有什么来历吗")
//				.build();
//		messages.add(ChatCompletionMessageParam.ofUser(userMessage1));
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(model)
				.maxTokens(16000)
				.messages(messages)
				.build();
		OpenAIClient openAIClient = openAISdkUtils.defaultClient;

		try (StreamResponse<ChatCompletionChunk> streamResponse = openAIClient.chat().completions().createStreaming(params)) {
			streamResponse.stream().forEach(chunk -> {
				List<ChatCompletionChunk.Choice> choices = chunk.choices();
				if (!choices.isEmpty()){
					ChatCompletionChunk.Choice choice = choices.get(0);
					if (choice.delta()._additionalProperties().containsKey("reasoning_content")) {
						// 获取推理内容
						String reasoningContent = choice.delta()._additionalProperties()
								.get("reasoning_content").toString();
						System.out.print(reasoningContent);
					}

					Optional<String> optionalString = chunk.choices().get(0).delta().content();
					Optional<ChatCompletionChunk.Choice.FinishReason> finishReason = chunk.choices().get(0).finishReason();
					if (finishReason.isPresent()) {
						System.out.println("finishReason:" + finishReason.get());
					}
					if (optionalString.isPresent()&&!optionalString.get().isEmpty()) {
						String content = optionalString.get();
						System.out.print(content);
					}

				}

			});
			System.out.println("No more chunks!");
		}
//		try (StreamResponse<ChatCompletionChunk> streamResponse = openAIClient.chat().completions().createStreaming(params)) {
//			streamResponse.stream().forEach(chunk -> {System.out.println(chunk);});
//			System.out.println("No more chunks!");
//		}



	}




	@Test
	void testJsonUtils()
	{
		Question question1 = new Question();
		question1.setQuestionText("15+16=?");
		question1.setOptionA("15");
		question1.setOptionB("16");
		question1.setOptionC("17");
		question1.setOptionD("31");
		question1.setCorrectAnswer("D");
		question1.setExplanation("简单的算术问题，两个10相加得20，5加6得11，加起来就是31，选择D选项");
		question1.setKnowledgePoint("简单加法运算");
		Question question2 = new Question();
		question2.setQuestionText("9+1=?");
		question2.setOptionA("9");
		question2.setOptionB("10");
		question2.setOptionC("11");
		question2.setOptionD("12");
		question2.setCorrectAnswer("B");
		question2.setExplanation("简单的算术问题，9加1得10，选择B选项");
		question2.setKnowledgePoint("简单加法运算");
		List<Question> questions = new ArrayList<>();
		questions.add(question1);
		questions.add(question2);
		String jsonString = FastjsonUtils.toJsonString(questions);
		System.out.println(jsonString);
		StringBuilder markdownJson = new StringBuilder();
		markdownJson.append("```json\n");
		markdownJson.append(jsonString);
		markdownJson.append("\n```");
		// 输出最终的Markdown格式JSON字符串
		String finalMarkdownJson = markdownJson.toString();
		System.out.println(finalMarkdownJson);

//		 String originJson = FastjsonUtils.extractJsonFromMarkdown(finalMarkdownJson);
//		System.out.println(originJson);
//
//		List<Question> questions1 = FastjsonUtils.parseArray(originJson, Question.class);
//		System.out.println(questions1);


	}





	@Test
	void  checkPrompt(){
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
		System.out.println(systemPrompt);
	}

	@Test
	void testSimpleChat(){
		OpenAIClient openAIClient = openAISdkUtils.defaultClient;
//		String model = "openai-mini";
//		String model = "deepseek-r1";
//		String model = "gemini-2.0-flash";
		String model = "claude-3-7-sonnet";
		List<ChatCompletionMessageParam> messages = new ArrayList<>();
//		 添加系统消息
//        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
//                .content("不管我说什么，用什么语言，你都必须只使用日语回答我")
//                .build();
//        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));

		ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
				.content("我的名字叫黄河。")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage));
		ChatCompletionUserMessageParam userMessage1 = ChatCompletionUserMessageParam.builder()
				.content("你认识我吗?")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage1));
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(model)
				.maxTokens(5)
				.messages(messages)
				.build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
//		String answer = chatCompletion.choices().get(0).message().content().get();
//		System.out.println(answer);
		System.out.println(chatCompletion);

	}


	@Test
	void testPreKnowledge(){
		String model = "deepseek-r1";
       List<ChatCompletionMessageParam> messages = new ArrayList<> ();
       ChatCompletionSystemMessageParam preKnowledgeSystemMessage = promptUtils.getPreKnowledgeSystemMessage();
       messages.add(ChatCompletionMessageParam.ofSystem(preKnowledgeSystemMessage));
	   Project project = new Project();













       ChatCompletionUserMessageParam projectRequirementsMeaasgeWithSystem = promptUtils.getProjectRequirementsMeaasgeWithSystem(project);
       messages.add(ChatCompletionMessageParam.ofUser(projectRequirementsMeaasgeWithSystem));
       ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("请帮我生成10道预备知识检测单选题目");
       messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
       messages.forEach(message -> log.debug("消息内容: " + message));

	}

	@Test
	void extractMarkdownJson(){
		String test1 = """
				好的，下面是你满足条件的json数据:
				```json
				{
					"name": "huanghe",
					"age": 18,
					"sex": "male"
				}
				```
				如果你还有别的要求，请告诉我。""";
		String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(test1);
		if (extractJsonFromMarkdown!=null){
			System.out.println(extractJsonFromMarkdown);
		}
		else {
			System.out.println("没有找到json");
		}
	}

	@Autowired
	QuestionClient questionClient;
	@Test
	void  testPraseJson(){
		String test2 = """
				[
				  {
				    "questionText": "数系的扩充过程中，为解决方程$x^2=2$在有理数集中无解的问题，数学家将数系扩充到了什么范围？",
				    "optionA": "自然数集",
				    "optionB": "整数集",
				    "optionC": "实数集",
				    "optionD": "复数集",
				    "correctAnswer": "C",
				    "knowledgePoint": "数系扩充历史",
				    "explanation": "方程$x^2=2$的解是$\\sqrt{2}$，属于无理数。通过引入无理数，数系从有理数集扩充到实数集，故正确答案是C。"
				  },
				  {
				    "questionText": "虚数单位i的定义是？",
				    "optionA": "$i = \\sqrt{-1}$",
				    "optionB": "$i^2 = -1$",
				    "optionC": "$i = -1$",
				    "optionD": "$i^2 = 1$",
				    "correctAnswer": "B",
				    "knowledgePoint": "虚数单位定义",
				    "explanation": "严格数学定义是$i^2 = -1$（B选项）。A选项的表达方式在实数范围内不成立，C、D选项不符合定义。"
				  },
				  {
				    "questionText": "复数$3-4i$的实部是？",
				    "optionA": "3",
				    "optionB": "-4",
				    "optionC": "4",
				    "optionD": "-3",
				    "correctAnswer": "A",
				    "knowledgePoint": "复数结构",
				    "explanation": "复数标准形式$a+bi$中，a是实部，b是虚部系数。本题中$a=3$，故正确答案是A。"
				  },
				  {
				    "questionText": "两个复数$a+bi$与$c+di$相等的充要条件是？",
				    "optionA": "$a=c$",
				    "optionB": "$b=d$",
				    "optionC": "$a=c$且$b=d$",
				    "optionD": "$a=d$且$b=c$",
				    "correctAnswer": "C",
				    "knowledgePoint": "复数相等条件",
				    "explanation": "复数相等的充要条件是实部相等且虚部系数相等，即$a=c$且$b=d$，故选择C。"
				  },
				  {
				    "questionText": "方程$x^2+1=0$在哪个数集中有解？",
				    "optionA": "自然数集",
				    "optionB": "实数集",
				    "optionC": "有理数集",
				    "optionD": "复数集",
				    "correctAnswer": "D",
				    "knowledgePoint": "复数引入必要性",
				    "explanation": "该方程的解为$\\pm i$，属于复数范围。实数集中无解，故正确答案是D。"
				  },
				  {
				    "questionText": "下列哪个数是纯虚数？",
				    "optionA": "$5$",
				    "optionB": "$0$",
				    "optionC": "$2i$",
				    "optionD": "$3+4i$",
				    "correctAnswer": "C",
				    "knowledgePoint": "复数分类",
				    "explanation": "纯虚数的形式是$bi$（$b≠0$），其中C选项$2i$符合条件，其他选项均为实数或非纯虚数。"
				  },
				  {
				    "questionText": "数学家最初引入复数是为了解决什么问题？",
				    "optionA": "三次方程求根",
				    "optionB": "几何作图问题",
				    "optionC": "微积分运算",
				    "optionD": "概率计算",
				    "correctAnswer": "A",
				    "knowledgePoint": "复数历史背景",
				    "explanation": "16世纪数学家为解决三次方程的求根问题，不得不接受负数开平方的概念，这是复数产生的直接动因。"
				  },
				  {
				    "questionText": "在复数范围内，方程$x^2=-9$的解是？",
				    "optionA": "$x=3$",
				    "optionB": "$x=-3$",
				    "optionC": "$x=±3i$",
				    "optionD": "无解",
				    "correctAnswer": "C",
				    "knowledgePoint": "复数解方程",
				    "explanation": "$x^2=-9$等价于$x^2=9i^2$，解得$x=±3i$，故正确答案是C。"
				  },
				  {
				    "questionText": "复数$z=5$属于什么类型的数？",
				    "optionA": "纯虚数",
				    "optionB": "实数",
				    "optionC": "虚数",
				    "optionD": "非复数",
				    "correctAnswer": "B",
				    "knowledgePoint": "复数与实数关系",
				    "explanation": "当虚部系数为0时，复数退化为实数，因此$z=5$属于实数，正确答案是B。"
				  },
				  {
				    "questionText": "数系扩充后，复数集中的运算保持实数集的哪些性质？",
				    "optionA": "交换律",
				    "optionB": "结合律",
				    "optionC": "分配律",
				    "optionD": "以上所有",
				    "correctAnswer": "D",
				    "knowledgePoint": "数系运算性质",
				    "explanation": "复数运算继承实数集的交换律、结合律和分配律，因此正确答案是D。"
				  }
				]
				""";
		System.out.println("test2: "+test2);
//		String replace = test2.replace("\\", "\\\\");
//		System.out.println("replace: "+replace);

		List<Question> questions = FastjsonUtils.parseArray(test2, Question.class);
//		System.out.println(questions);
//		Question question = questions.get(0);
//		questionClient.saveQuestion(question);
//
//		System.out.println("explanation: "+question.getExplanation());


	}

	@Test
	void TestLax(){
		String test2 = """
				$\\sqrt{2}$""";
		System.out.println(test2);

//		Question question2 = new Question();
//		question2.setQuestionText("9+1=?");
//		question2.setOptionA("9");
//		question2.setOptionB("10");
//		question2.setOptionC("11");
//		question2.setOptionD("12");
//		question2.setCorrectAnswer(test2);
//		System.out.println(question2);
	}

	@Test
	void testFormat(){
		String test2 = "$\\{1\\}$";

		System.out.println(test2);

	}

	@Autowired
	private EducationAutoCompleteUtils educationAutoCompleteUtils;

	@Test
	void testCompletion(){
		String textCompletion = educationAutoCompleteUtils.getTextCompletion("你好", "字是黄河",250);
		System.out.println(textCompletion);
	}

	@Test
	void testString11(){
		String test2 = "\\\"使得$i^{2}=-1$\\\"";
		System.out.println(test2);
	}



	@Test
	void testDatabaseString() {
		String questionString = """
				根据教学主题和知识图谱，生成以下10道预备知识检测题：
				
				---
				
				**题目1**
				下列哪个选项是虚数单位$i$的定义式？
				A) $i^2 = 1$
				B) $i^2 = 0$
				C) $i^2 = -1$
				D) $i^2 = 2$
				
				**正确答案**：C
				**关联知识点**：虚数单位定义
				**解析**：虚数单位$i$由方程$x^2+1=0$的解引出，其核心定义式为$i^2 = -1$。选项C符合教材中"使得$i^{2}=-1$"的原始定义。
				
				---
				
				**题目2**
				复数的标准代数形式是：
				A) $a + ib$（$a,b∈ℝ$）
				B) $ai + b$（$a,b∈ℕ$）
				C) $a + b$（$a,b∈ℤ$）
				D) $a \\cdot i$（$a∈ℝ$）
				
				**正确答案**：A
				**关联知识点**：复数表示形式
				**解析**：教材明确指出复数由有序实数对构成，标准形式为$a+bi$（实部在前，虚部在后），其中$a,b$为实数，符合数系扩充的规范。
				
				---
				
				**题目3**
				若两个复数$z_1=a+bi$与$z_2=c+di$相等，则必须满足：
				A) $a = c$
				B) $b = d$
				C) $a = d$且$b = c$
				D) $a = c$且$b = d$
				
				**正确答案**：D
				**关联知识点**：复数相等条件
				**解析**：复数相等的充要条件是实部与虚部分别相等，即同时满足$a=c$和$b=d$，这体现了复数作为有序实数对的本质。
				
				---
				
				**题目4**
				复数$z = 3 - 4i$中，实部与虚部分别是：
				A) 实部3，虚部-4
				B) 实部3，虚部4
				C) 实部-4，虚部3
				D) 实部3i，虚部-4
				
				**正确答案**：A
				**关联知识点**：复数结构分析
				**解析**：在标准形式$a+bi$中，实部为$a$，虚部为$b$（注意虚部是不含$i$的实数部分），因此$3-4i$的实部是3，虚部是-4。
				
				---
				
				**题目5**
				方程$x^2 = -1$的解集是：
				A) $\\{1\\}$
				B) $\\{i\\}$
				C) $\\{i, -i\\}$
				D) 无解
				
				**正确答案**：C
				**关联知识点**：复数解方程
				**解析**：引入虚数单位$i$后，方程$x^2=-1$的解为$x=±i$，这是复数概念产生的直接动因之一。
				
				---
				
				**题目6**
				数系扩充的正确顺序是：
				A) 自然数→整数→复数→实数
				B) 自然数→实数→有理数→复数
				C) 自然数→整数→有理数→实数→复数
				D) 整数→自然数→有理数→复数→实数
				
				**正确答案**：C
				**关联知识点**：数系扩充历程
				**解析**：历史发展顺序为自然数→整数（解决减法封闭）→有理数（解决除法封闭）→实数（解决无理数问题）→复数（解决方程解集封闭），选项C正确反映了这一过程。
				
				---
				
				**题目7**
				下列哪个是纯虚数？
				A) $0$
				B) $3+0i$
				C) $0-2i$
				D) $5i+5$
				
				**正确答案**：C
				**关联知识点**：纯虚数定义
				**解析**：纯虚数要求实部为0而虚部非零，$0-2i$可简写为$-2i$，满足纯虚数定义，而选项B是实数，D是普通复数。
				
				---
				
				**题目8**
				关于复数集，正确的说法是：
				A) 复数包含所有实数
				B) 虚数都是纯虚数
				C) 实数集与复数集没有交集
				D) $i$是实数
				
				**正确答案**：A
				**关联知识点**：复数集与实数集关系
				**解析**：复数集$ℂ$包含所有实数（当虚部为0时）和虚数，因此选项A正确。选项B错误因为虚数包含纯虚数和非纯虚数，C错误因实数是复数的子集，D显然错误。
				
				---
				
				**题目9**
				复数$z=2+3i$对应的复平面坐标是：
				A) $(2,3)$
				B) $(3,2)$
				C) $(2,3i)$
				D) $(3i,2)$
				
				**正确答案**：A
				**关联知识点**：复数的几何表示
				**解析**：在复平面中，复数$a+bi$对应点的坐标为$(a,b)$，因此$2+3i$对应点$(2,3)$，体现复数与坐标平面的一一对应关系。
				
				---
				
				**题目10**
				当判别式$Δ = b^2-4ac < 0$时，方程$ax^2+bx+c=0$的解的情况是：
				A) 有两个相等实根
				B) 有两个不等实根
				C) 没有实根但有复数根
				D) 没有解
				
				**正确答案**：C
				**关联知识点**：复数解必要性
				**解析**：这正是教材中引出复数概念的背景：当判别式小于0时，方程在实数范围内无解，但在复数范围内有两个共轭虚根。
				
				---
				
				### 使用建议：
				1. **测试实施**：建议用15分钟完成测试，重点观察第3、5、10题的通过率，这些题直接关联复数引入的必要性
				2. **分层指导**：
				   - 错题超过4道的学生：需补充数系扩充史、二次方程求根公式推导
				   - 错题在2-3道的学生：加强复数代数运算专项训练
				   - 全对的学生：可提前布置复数的几何意义探究任务
				3. **教学调整**：若第6题错误率高，需在正式授课时插入数系扩充的对比表格；若第9题错误多，应加强复数几何表示的直观教学
				
				所有题目均包含LaTeX公式，严格遵循教材知识图谱中关于复数概念的原始定义和核心知识点，确保检测结果能准确反映学生对复数基础概念的掌握程度。
				从以上文本中提取结构化的单选题目，只输出markdown格式的json数据，不要任何额外的多余的内容""";
		String structuredSingleChoiceQuestion = promptUtils.extractStructuredSingleChoiceQuestion(questionString);
		System.out.println("提取的markdown格式数据:"+structuredSingleChoiceQuestion);
		String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(structuredSingleChoiceQuestion);
		System.out.println("提取的json数据:"+extractJsonFromMarkdown);
		String replacedJson = extractJsonFromMarkdown.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
		//判断替换后的和原来的是否相等
		System.out.println("判断是否相等:"+replacedJson.equals(extractJsonFromMarkdown));
		List<Question> questions = FastjsonUtils.parseArray(replacedJson, Question.class);
		System.out.println(questions);
	}

	@Test
	void testJsonUtils1()
	{
		Question question1 = new Question();
		question1.setQuestionText("15+16=?");
		question1.setOptionA("15");
		question1.setOptionB("16");
		question1.setOptionC("17");
		question1.setOptionD("31");
		question1.setCorrectAnswer("D");
		question1.setExplanation("简单的算术问题，两个10相加得20，5加6得11，加起来就是31，选择D选项");
		question1.setKnowledgePoint("简单加法运算");
		Question question2 = new Question();
		question2.setQuestionText("9+1=?");
		question2.setOptionA("9");
		question2.setOptionB("10");
		question2.setOptionC("11");
		question2.setOptionD("12");
		question2.setCorrectAnswer("B");
		question2.setExplanation("简单的算术问题，9加1得10，选择B选项");
		question2.setKnowledgePoint("简单\"加法\"运算，A) $\\{1\\}$， $a \\cdot i$（$a∈ℝ$）");
		System.out.println("knowledgePoint: "+question2.getKnowledgePoint());
		List<Question> questions = new ArrayList<>();
		questions.add(question1);
		questions.add(question2);
		String jsonString = FastjsonUtils.toJsonString(questions);
//		System.out.println(jsonString);
		StringBuilder markdownJson = new StringBuilder();
		markdownJson.append("```json\n");
		markdownJson.append(jsonString);
		markdownJson.append("\n```");
		// 输出最终的Markdown格式JSON字符串
		String finalMarkdownJson = markdownJson.toString();
		System.out.println("finalMarkdownJson: "+finalMarkdownJson);

		 String originJson = FastjsonUtils.extractJsonFromMarkdown(finalMarkdownJson);
		System.out.println("originJson: "+originJson);
//
		List<Question> questions1 = FastjsonUtils.parseArray(originJson, Question.class);
		System.out.println(questions1);


	}


	@Test
	void  testDanyinhao(){
		String test2 = "'你'是一个字";
		System.out.println(test2);
	}

	@Test
	void testZhengze(){

		String str1 = "$a \\cdot i$（$a∈ℝ$）";
		String str2 = "$a \\\\cdot i$（$a∈ℝ$）";
		String str3 = "选项C符合教材中\\\"使得$i^{2}=-1$\\\"的原始定义。";
		String str4 = "$\\\\{1\\\\}$\"";
		System.out.println(str1);
		System.out.println(str2);
		System.out.println(str3);
		System.out.println(str4);
		String text = "test ef text mnef text efab text mnefab";
//		String result = str1.replaceAll("(?<!\\\\)\\(?![\"\\\\ntrfbu0-7])", "xyz");
//		System.out.println(result);

	}

	@Test
	void test999(){
		String str1 = "$a \\cdot i$（$a∈ℝ$）";
		String str2 = "$a \\\\cdot i$（$a∈ℝ$）";
		String str3 = "选项C符合教材中\\\"使得$i^{2}=-1$\\\"的原始定义。";
		String str4 = "$\\\\{1\\\\}$\"";
		String result1 = str1.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
		System.out.println("result1: "+result1);
		String result2 = str2.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
		System.out.println("result2: "+result2);
		String result3 = str3.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
		System.out.println("result3: "+result3);
		String result4 = str4.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
		System.out.println("result4: "+result4);
	}



	@Test
	void getTextbook(){
		//黄河:
		//u0-ss-math-rja-bx2
		//
		//黄河:
		//复数的概念
		String results = TextBookUtils.getVectorResults("玻尔的原子模型", "u0-ss-phys-hkji-xb3");
		System.out.println(results);
		JSONArray jsonArray = JSON.parseArray(results);
//		jsonArray.forEach(item -> {
//			JSONObject jsonObject = (JSONObject) item;
//			System.out.println(jsonObject.getString("index"));
//			System.out.println(jsonObject.getString("content"));
//		});
		//把所有的content拼接起来，用\n连接
//		String content = jsonArray.stream().map(item -> ((JSONObject) item).getString("content")).collect(Collectors.joining("\n"));
//		System.out.println(content);
		//每一个元素，先把index和content拼接起来，用\n连接，然后再把所有的元素拼接起来，用\n连接
		String content1 = jsonArray.stream().map(item -> ((JSONObject) item).getString("index")+"\n"+((JSONObject) item).getString("content")).collect(Collectors.joining("\n"));
		System.out.println(content1);

	}

	@Test
	void getQuestions(){
		List<Question> questions = questionClient.getQuestionsByGroupId(25).getData();
		String jsonString = FastjsonUtils.toJsonString(questions);
		System.out.println(jsonString);
	}

	@Test
	void testBiafenhao(){
		String test2 = "请根据提供的信息按要求进行详细的教学过程设计，必须生成完整的响应内容。\n强制要求:无论篇幅多长，都必须完整提供所有环节的所有组成部分，不得简化或省略";
		System.out.println(test2);
	}



	@Test
	void testSearchKeyPointsData(){
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put("serialNumber",1);
		jsonObject1.put("searchKeyPoint","复数的几何表示");
		jsonArray.add(jsonObject1);
		JSONObject jsonObject2 = new JSONObject();
		jsonObject2.put("serialNumber",2);
		jsonObject2.put("searchKeyPoint","复数的代数表示");
		jsonArray.add(jsonObject2);
		String jsonString = JSON.toJSONString(jsonArray, JSONWriter.Feature.PrettyFormat);
		System.out.println(jsonString);
	}

	//测试提取知识点标题
	@Test
	void testExtractKnowledgePointsTitle(){
		String knowledgePointSummary = """
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
				
				### 知识点6：复数的几何表示
				- **级别**：重点
				- **描述**：认识复数与复平面上的点及向量之间的对应关系，了解复数的模和幅角的概念及其几何意义。
				
				### 知识点7：复数的代数形式
				- **级别**：普通
				- **描述**：学习复数的加、减、乘、除运算规则，理解这些运算的几何意义。
				
				### 知识点8：复数的三角形式
				- **级别**：普通
				- **描述**：掌握复数表示为三角形式的方法，即 \\( r(\\cos \\theta + i \\sin \\theta) \\)，并通过实例说明其在旋转和平移中的应用。
				
				### 知识点9：复数与向量的关系
				- **级别**：难点
				- **描述**：探讨复数作为向量的表示方式，理解复数运算与向量变换之间的联系，如复数的加法对应向量的平行四边形法则，乘法对应向量的伸缩和旋转。
				
				### 知识点10：判别式的计算与应用
				- **级别**：普通
				- **描述**：复习一元二次方程的判别式 \\( \\Delta = b^2 - 4ac \\)，了解判别式在确定方程根的类型（实数根或复数根）上的作用，加强学生对方程解的理解。
				
				### 知识点11：复数解的特点和性质
				- **级别**：难点
				- **描述**：深入理解和讨论复数解的主要特点，如共轭性、对称性，以及它们在数学和实际问题中的特殊意义和应用。
				
				### 知识点12：数与形的结合
				- **级别**：重点
				- **描述**：通过复数的代数和三角形式，加深对“数”与“形”结合的理解，体会数学抽象与直观形象之间的桥梁作用。""";
		String knowledgePointsTitle = promptUtils.extractKnowledgePointsTitle(knowledgePointSummary);
		System.out.println(knowledgePointsTitle);
	}

	//查看临时文件夹路径
	@Test
	void testTempFile() {
		String tempDirPath = System.getProperty("java.io.tmpdir");
		System.out.println("临时文件夹路径: " + tempDirPath);
	}

	@Autowired
	ResourcesBuildUtils resourcesBuildUtils;
	//测试拼接markdown文本
	@Test
	void testMarkdown(){
		Project projectById = projectService.getProjectById(5);
		String finalTeachingDesign = projectById.getFinalTeachingDesign();
		List<ResourceDTO> resourceDTOList = new ArrayList<>();
		ResourceDTO resourceDTO1 = new ResourceDTO("web","https://www.baidu.com","百度");
		resourceDTOList.add(resourceDTO1);
		ResourceDTO resourceDTO2 = new ResourceDTO("web","https://www.google.com","谷歌");
		resourceDTOList.add(resourceDTO2);
		ResourceDTO resourceDTO3 = new ResourceDTO("image","https://imgs.699pic.com/images/600/418/540.jpg!detail.v1","图片");
		ResourceDTO resourceDTO4 = new ResourceDTO("web","https://www.bilibili.com","bilibili");
		resourceDTOList.add(resourceDTO3);
		resourceDTOList.add(resourceDTO4);
		ResourceDTO resourceDTO5 = new ResourceDTO("image","https://ts1.tc.mm.bing.net/th/id/R-C.931cee7fe752510a9b609f37e88f1fdd?rik=N30Iebc6FuGQkQ&riu=http%3a%2f%2fwww.quazero.com%2fuploads%2fallimg%2f150515%2f1-150515215436.jpg&ehk=8T6ZJ%2fjRWQiXLKPsBSWAjPRu5yPTLhUHOpmVIdQmAFE%3d&risl=&pid=ImgRaw&r=0","小猫");
		resourceDTOList.add(resourceDTO5);
		ResourceDTO resourceDTO6 = new ResourceDTO("video","BV13DobYDERu","b站视频1");
		resourceDTOList.add(resourceDTO6);
		ResourceDTO resourceDTO7 = new ResourceDTO("video","BV1s9o1YXE2v","b站视频2");
		resourceDTOList.add(resourceDTO7);
		String resourceString = resourcesBuildUtils.generateFormattedResourcesDocument(resourceDTOList);
		StringBuilder markdown = new StringBuilder();
		markdown.append(finalTeachingDesign);
		markdown.append("\n\n");
		markdown.append(resourceString);
		Project project =new Project();
		project.setProjectId(5);
		project.setFinalTeachingDesign(markdown.toString());
		projectService.updateProject(project);



	}

	//测试claude thinking
	@Test
	void testClaudeThinking(){
		String apiKey = "huanghe1224";
		String baseUrl = "https://huanghehe1223-huanghehe-kilo2api.hf.space/huanghe/v1";
		OpenAIClient openAIClient = openAISdkUtils.createOpenAiClient(apiKey,baseUrl);
//		String model = "openai-mini";
//		String model = "deepseek-r1";
//		String model = "gemini-2.0-flash";
		String model = "claude-3-7-sonnet-20250219-thinking";
		List<ChatCompletionMessageParam> messages = new ArrayList<>();
//		 添加系统消息
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content("不管我说什么，用什么语言，你都必须只使用日语回答我")
                .build();
        messages.add(ChatCompletionMessageParam.ofSystem(systemMessage));

		ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
				.content("我的名字叫黄河。")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage));
		ChatCompletionUserMessageParam userMessage1 = ChatCompletionUserMessageParam.builder()
				.content("你认识我吗?")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage1));
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(model)
				.maxTokens(64000)
				.messages(messages)
				.build();


		try (StreamResponse<ChatCompletionChunk> streamResponse = openAIClient.chat().completions().createStreaming(params)) {
			streamResponse.stream().forEach(chunk -> {
				System.out.println(chunk);
			});
			System.out.println("No more chunks!");
		}
//		ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
//		String answer = chatCompletion.choices().get(0).message().content().get();
//		System.out.println(answer);
//		System.out.println(chatCompletion);

	}


	@Autowired
	MessageProcessUtils messageProcessUtils;
	//测试formatChatAnswers方法
	@Test
	void testFormatChatAnswers(){
		List<ChatAnswerDTO> answers = new ArrayList<>();
		ChatAnswerDTO answer1 = new ChatAnswerDTO("gemini-2.0-flash","我是gemini-2.0-flash");
		ChatAnswerDTO answer2 = new ChatAnswerDTO("claude-3-7-sonnet","我是claude-3-7-sonnet");
		ChatAnswerDTO answer3 = new ChatAnswerDTO("claude-3-7-sonnet-20250219-thinking","我是claude-3-7-sonnet-20250219-thinking");
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		String formattedAnswers = messageProcessUtils.formatChatAnswers(answers);
		System.out.println(formattedAnswers);
	}

	//测试字符串里面有\n的打印
	@Test
	void testString999(){
		String test999 = "根据给出的信息，请帮我对各个模型回答进行分析，总结和整合。你需要在思考过程中完成前三步分析，正式回答时只需要完成第四步，输出新版本的整合后的回答。\n特别注意：对每个模型的回答进行分析的时候一定要明确点出模型的具体名称，方便用户对应";
		System.out.println(test999);
	}









	

}
