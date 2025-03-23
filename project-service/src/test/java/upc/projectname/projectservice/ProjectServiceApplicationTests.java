package upc.projectname.projectservice;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.*;
import com.openai.services.blocking.ModelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.projectservice.utils.FastjsonUtils;
import upc.projectname.projectservice.utils.OpenAISdkUtils;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.upccommon.domain.po.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ProjectServiceApplicationTests {

	@Autowired
	private OpenAISdkUtils openAISdkUtils;



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
		String model = "doubao-1.5-vision-pro";
		ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
				.content("我的名字叫黄河。")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage));
		ChatCompletionUserMessageParam userMessage1 = ChatCompletionUserMessageParam.builder()
				.content("你好")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage1));
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(model)
				.messages(messages)
				.build();
		OpenAIClient openAIClient = openAISdkUtils.defaultClient;

		try (StreamResponse<ChatCompletionChunk> streamResponse = openAIClient.chat().completions().createStreaming(params)) {
			streamResponse.stream().forEach(chunk -> {
				List<ChatCompletionChunk.Choice> choices = chunk.choices();
				if (!choices.isEmpty()){
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
		System.out.println(systemPrompt);
	}

	@Test
	void testSimpleChat(){
		OpenAIClient openAIClient = openAISdkUtils.defaultClient;
//		String model = "openai-mini";
//		String model = "deepseek-r1";
		String model = "gemini-2.0-flash";
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
				.content("你好")
				.build();
		messages.add(ChatCompletionMessageParam.ofUser(userMessage1));
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(model)
				.messages(messages)
				.build();
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
		String answer = chatCompletion.choices().get(0).message().content().get();
		System.out.println(answer);
//		System.out.println(chatCompletion);

	}


	@Test
	void testPreKnowledge(){
//		String model = "deepseek-r1";
//       List<ChatCompletionMessageParam> messages = new ArrayList<> ();
//       ChatCompletionSystemMessageParam preKnowledgeSystemMessage = promptUtils.getPreKnowledgeSystemMessage();
//       messages.add(ChatCompletionMessageParam.ofSystem(preKnowledgeSystemMessage));
//		Project project = new Project();
//
//
//       ChatCompletionUserMessageParam projectRequirementsMeaasgeWithSystem = promptUtils.getProjectRequirementsMeaasgeWithSystem(project);
//       messages.add(ChatCompletionMessageParam.ofUser(projectRequirementsMeaasgeWithSystem));
//       ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("请帮我生成10道预备知识检测单选题目");
//       messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
//       messages.forEach(message -> log.debug("消息内容: " + message));

	}





	

}
