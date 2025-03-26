package upc.projectname.projectservice;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.*;
import com.openai.services.blocking.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.projectservice.utils.EducationAutoCompleteUtils;
import upc.projectname.projectservice.utils.FastjsonUtils;
import upc.projectname.projectservice.utils.OpenAISdkUtils;
import upc.projectname.projectservice.utils.PromptUtils;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.upccommon.domain.po.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
class ProjectServiceApplicationTests {

	@Autowired
	private OpenAISdkUtils openAISdkUtils;
	@Autowired
	private PromptUtils promptUtils;



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
//		System.out.println(jsonString);
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
		String replace = test2.replace("\\", "\\\\");
		System.out.println("replace: "+replace);

		List<Question> questions = FastjsonUtils.parseArray(replace, Question.class);
		System.out.println(questions);
		Question question = questions.get(0);

		System.out.println("explanation: "+question.getExplanation());


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
		String test2 = "我爱%";
		String test3 = """
				%s
				50""".formatted(test2);
		System.out.println(test3);

	}

	@Autowired
	private EducationAutoCompleteUtils educationAutoCompleteUtils;

	@Test
	void testCompletion(){
		String textCompletion = educationAutoCompleteUtils.getTextCompletion("你好", "字是黄河");
		System.out.println(textCompletion);
	}






	

}
