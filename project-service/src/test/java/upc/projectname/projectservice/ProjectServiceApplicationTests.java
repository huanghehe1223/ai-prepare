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
		question1.setCorrectAnswer("A");
		question1.setOptionA("15");
		question1.setOptionB("16");
		question1.setOptionC("17");
		question1.setOptionD("18");
		question1.setQuestionText("15+16=?");
		Question question2 = new Question();
		question2.setCorrectAnswer("B");
		question2.setOptionA("9");
		question2.setOptionB("10");
		question2.setOptionC("11");
		question2.setOptionD("12");
		question2.setQuestionText("9+1=?");
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

		 String originJson = FastjsonUtils.

		List<Question> questions1 = FastjsonUtils.parseArray(jsonString, Question.class);
		System.out.println(questions1);


	}





	

}
