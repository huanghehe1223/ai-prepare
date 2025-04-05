package upc.projectname.projectservice.controller;


import com.openai.client.OpenAIClient;
import com.openai.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import upc.projectname.projectservice.entity.ChatRequestDTO;
import upc.projectname.projectservice.utils.*;
import upc.projectname.upccommon.api.client.QuestionClient;
import upc.projectname.upccommon.api.client.QuestionGroupClient;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.upccommon.domain.po.Question;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.upccommon.domain.po.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.util.stream.Collectors;

@Tag(name = "非流式对话管理接口")
@RestController
@RequestMapping("/simple")
@Slf4j
@RequiredArgsConstructor
public class SimpleChatController {
    private final StreamRequestUtils streamRequestUtils;
    private final OpenAISdkUtils openAISdkUtils;
    private final PromptUtils promptUtils;
    private final QuestionGroupClient questionGroupClient;
    private final QuestionClient questionClient;
    private final EducationAutoCompleteUtils educationAutoCompleteUtils;


    @Operation(summary = "非流式对话")
    @PostMapping("test1")
    public Result<ChatCompletion> simpleChat (@RequestBody ChatRequestDTO chatRequest) {

        String model = chatRequest.getModel();
        List<ChatCompletionMessageParam> messages = chatRequest.getMessages();
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        return Result.success(chatCompletion);
    }

    @Operation(summary = "获取所有模型名称")
    @GetMapping("/models")
    public Result<List<Model>> getAllModels() {
        OpenAIClient openAIClient = openAISdkUtils.defaultClient;
        List<Model> models = openAIClient.models().list().response().data();
        // Filter out the model with id "openai-mini"
        models = models.stream()
                .filter(model -> !"openai-mini".equals(model.id()))
                .collect(java.util.stream.Collectors.toList());
        return Result.success(models);
    }


    @Operation(summary = "提取结构化的单选题目")
    @PostMapping("/extractSingleChoice")
    public  Result<QuestionGroup> extractSingleChoice(@RequestParam String questionString, @RequestParam Integer projectId){
        String structuredSingleChoiceQuestion = promptUtils.extractStructuredSingleChoiceQuestion(questionString);
        System.out.println("提取的markdown格式数据:"+structuredSingleChoiceQuestion);
        //打印分割线
        System.out.println("--------------------------------------------------");
        String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(structuredSingleChoiceQuestion);
        System.out.println("提取的json数据:"+extractJsonFromMarkdown);
        //打印分割线
        System.out.println("--------------------------------------------------");
        if (extractJsonFromMarkdown == null){
            log.error("markdown格式数据:{}",structuredSingleChoiceQuestion);
            return Result.error("提取失败");
        }
        try {
            String replacedJson = extractJsonFromMarkdown.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
            System.out.println("replacedJson:"+replacedJson);
            System.out.println("判断是否相等:"+replacedJson.equals(extractJsonFromMarkdown));
            List<Question> questions = FastjsonUtils.parseArray(replacedJson, Question.class);
            //创建一个习题组，QuestionGroup,插入到数据库里面
            QuestionGroup questionGroup = new QuestionGroup();
            questionGroup.setProjectId(projectId);
            questionGroup.setGroupType("Pre");
            questionGroup.setGroupStatus(0);
            Result<QuestionGroup> questionGroupResult = questionGroupClient.saveQuestionGroup(questionGroup);
            if (questionGroupResult.getCode() ==0){
                return Result.error("保存习题组失败");
            }
            QuestionGroup newQuestionGroup = questionGroupResult.getData();
            Integer groupId = newQuestionGroup.getGroupId();
            questions.forEach(question -> {
                question.setGroupId(groupId);
                question.setQuestionType("单选");
                question.setCreatedAt(LocalDateTime.now());
                question.setUpdatedAt(LocalDateTime.now());
            });
            Result<Boolean> questionResult = questionClient.saveQuestions(questions);
            if (questionResult.getCode() ==0){
                return Result.error("保存习题失败");
            }
            return Result.success(newQuestionGroup);

        } catch (Exception e) {
            log.error("反序列化失败",e);
//            System.out.println("json字符串:"+extractJsonFromMarkdown);
            return Result.error("反序列化失败");
        }

    }



    @Operation(summary = "提取结构化的课后单选题目")
    @PostMapping("/extractPostSingleChoice")
    public  Result<Boolean> extractPostSingleChoice(@RequestParam String questionString, @RequestParam Integer groupId){
        String structuredSingleChoiceQuestion = promptUtils.extractStructuredSingleChoiceQuestion(questionString);
        System.out.println("提取的markdown格式数据:"+structuredSingleChoiceQuestion);
        //打印分割线
        System.out.println("--------------------------------------------------");
        String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(structuredSingleChoiceQuestion);
        System.out.println("提取的json数据:"+extractJsonFromMarkdown);
        //打印分割线
        System.out.println("--------------------------------------------------");
        if (extractJsonFromMarkdown == null){
            log.error("markdown格式数据:{}",structuredSingleChoiceQuestion);
            return Result.error("提取失败");
        }
        try {
            //处理一下提取的json数据
            String replacedJson = extractJsonFromMarkdown.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
            System.out.println("replacedJson:"+replacedJson);
            System.out.println("判断是否相等:"+replacedJson.equals(extractJsonFromMarkdown));
            List<Question> questions = FastjsonUtils.parseArray(replacedJson, Question.class);

            questions.forEach(question -> {
                question.setGroupId(groupId);
                question.setQuestionType("单选");
                question.setCreatedAt(LocalDateTime.now());
                question.setUpdatedAt(LocalDateTime.now());
            });
            Result<Boolean> questionResult = questionClient.saveQuestions(questions);
            if (questionResult.getCode() ==0){
                return Result.error("保存习题失败");
            }
            return Result.success(true);

        } catch (Exception e) {
            log.error("反序列化失败",e);
//            System.out.println("json字符串:"+extractJsonFromMarkdown);
            return Result.error("反序列化失败");
        }

    }



    @Operation(summary = "提取结构化的课后多选题目")
    @PostMapping("/extractPostMultipleChoice")
    public  Result<Boolean> extractPostMultipleChoice(@RequestParam String questionString, @RequestParam Integer groupId){
        String structuredMultipleChoiceQuestion = promptUtils.extractStructuredMultipleChoiceQuestion(questionString);
        System.out.println("提取的markdown格式数据:"+structuredMultipleChoiceQuestion);
        //打印分割线
        System.out.println("--------------------------------------------------");
        String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(structuredMultipleChoiceQuestion);
        System.out.println("提取的json数据:"+extractJsonFromMarkdown);
        //打印分割线
        System.out.println("--------------------------------------------------");
        if (extractJsonFromMarkdown == null){
            log.error("markdown格式数据:{}",structuredMultipleChoiceQuestion);
            return Result.error("提取失败");
        }
        try {
            //处理一下提取的json数据
            String replacedJson = extractJsonFromMarkdown.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
            System.out.println("replacedJson:"+replacedJson);
            System.out.println("判断是否相等:"+replacedJson.equals(extractJsonFromMarkdown));
            List<Question> questions = FastjsonUtils.parseArray(replacedJson, Question.class);

            questions.forEach(question -> {
                question.setGroupId(groupId);
                question.setQuestionType("多选");
                question.setCreatedAt(LocalDateTime.now());
                question.setUpdatedAt(LocalDateTime.now());
            });
            Result<Boolean> questionResult = questionClient.saveQuestions(questions);
            if (questionResult.getCode() ==0){
                return Result.error("保存习题失败");
            }
            return Result.success(true);

        } catch (Exception e) {
            log.error("反序列化失败",e);
//            System.out.println("json字符串:"+extractJsonFromMarkdown);
            return Result.error("反序列化失败");
        }

    }


    @Operation(summary = "提取结构化的课后填空题目")
    @PostMapping("/extractPostFillInBlankExercise")
    public  Result<Boolean> extractPostFillInBlankExercise(@RequestParam String questionString, @RequestParam Integer groupId){
        String structuredFillInBlankQuestion = promptUtils.extractStructuredFillInBlankQuestion(questionString);
        System.out.println("提取的markdown格式数据:"+structuredFillInBlankQuestion);
        //打印分割线
        System.out.println("--------------------------------------------------");
        String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(structuredFillInBlankQuestion);
        System.out.println("提取的json数据:"+extractJsonFromMarkdown);
        //打印分割线
        System.out.println("--------------------------------------------------");
        if (extractJsonFromMarkdown == null){
            log.error("markdown格式数据:{}",structuredFillInBlankQuestion);
            return Result.error("提取失败");
        }
        try {
            //处理一下提取的json数据
            String replacedJson = extractJsonFromMarkdown.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
            System.out.println("replacedJson:"+replacedJson);
            System.out.println("判断是否相等:"+replacedJson.equals(extractJsonFromMarkdown));
            List<Question> questions = FastjsonUtils.parseArray(replacedJson, Question.class);

            questions.forEach(question -> {
                question.setGroupId(groupId);
                question.setQuestionType("填空");
                question.setCreatedAt(LocalDateTime.now());
                question.setUpdatedAt(LocalDateTime.now());
            });
            Result<Boolean> questionResult = questionClient.saveQuestions(questions);
            if (questionResult.getCode() ==0){
                return Result.error("保存习题失败");
            }
            return Result.success(true);

        } catch (Exception e) {
            log.error("反序列化失败",e);
//            System.out.println("json字符串:"+extractJsonFromMarkdown);
            return Result.error("反序列化失败");
        }

    }

    @Operation(summary = "提取结构化的课后简答题目")
    @PostMapping("/extractPostShortAnswerExercise")
    public  Result<Boolean> extractPostShortAnswerExercise(@RequestParam String questionString, @RequestParam Integer groupId){
        String structuredShortAnswerQuestion = promptUtils.extractStructuredShortAnswerQuestion(questionString);
        System.out.println("提取的markdown格式数据:"+structuredShortAnswerQuestion);
        //打印分割线
        System.out.println("--------------------------------------------------");
        String extractJsonFromMarkdown = FastjsonUtils.extractJsonFromMarkdown(structuredShortAnswerQuestion);
        System.out.println("提取的json数据:"+extractJsonFromMarkdown);
        //打印分割线
        System.out.println("--------------------------------------------------");
        if (extractJsonFromMarkdown == null){
            log.error("markdown格式数据:{}",structuredShortAnswerQuestion);
            return Result.error("提取失败");
        }
        try {
            //处理一下提取的json数据
            String replacedJson = extractJsonFromMarkdown.replaceAll("(?<!\\\\)\\\\(?![\\\\\"'tnrbf])", "\\\\\\\\");
            System.out.println("replacedJson:"+replacedJson);
            System.out.println("判断是否相等:"+replacedJson.equals(extractJsonFromMarkdown));
            List<Question> questions = FastjsonUtils.parseArray(replacedJson, Question.class);

            questions.forEach(question -> {
                question.setGroupId(groupId);
                question.setQuestionType("简答");
                question.setCreatedAt(LocalDateTime.now());
                question.setUpdatedAt(LocalDateTime.now());
            });
            Result<Boolean> questionResult = questionClient.saveQuestions(questions);
            if (questionResult.getCode() ==0){
                return Result.error("保存简答习题失败");
            }
            return Result.success(true);

        } catch (Exception e) {
            log.error("反序列化简答习题失败",e);
//            System.out.println("json字符串:"+extractJsonFromMarkdown);
            return Result.error("反序列化简答习题失败");
        }

    }

    //获取搜索关键点
    @Operation(summary = "获取搜索关键点")
    @PostMapping("/searchKeyPoint")
    public Result<List<String>> getSearchKeyPoint(@RequestBody List<StudentAnswerResult> studentAnswerResults) {
        String model = "deepseek-v3.1";
        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam searchKeyPointSystemMessage = promptUtils.getSearchKeyPointSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(searchKeyPointSystemMessage));
        ChatCompletionUserMessageParam searchKeyPointUserMessage = promptUtils.getSearchKeyPointUserMessage(studentAnswerResults);
        messages.add(ChatCompletionMessageParam.ofUser(searchKeyPointUserMessage));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，帮我总结3个最有价值的搜索关键点，只输出markdown格式的json数据，不要任何额外的多余的内容");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String markdownJson = chatCompletion.choices().get(0).message().content().get();
        System.out.println("模型返回的markdownJson:"+markdownJson);
        String jsonString = FastjsonUtils.extractJsonFromMarkdown(markdownJson);

        if (jsonString == null) {
            log.error("从Markdown中提取JSON失败，原始内容: {}", markdownJson);
            return Result.error("提取JSON数据失败");
        }

        try {
            System.out.println("提取到的json数据:"+jsonString);
            // 解析JSON数组并提取searchKeyPoint列表
            JSONArray jsonArray = JSON.parseArray(jsonString);
            List<String> searchKeyPoints = jsonArray.stream()
                .map(item -> ((JSONObject) item).getString("searchKeyPoint"))
                .collect(Collectors.toList());

            return Result.success(searchKeyPoints);
        } catch (Exception e) {
            log.error("JSON反序列化失败", e);
            return Result.error("JSON反序列化失败");
        }
    }

    //获取搜索关键点
    @Operation(summary = "获取个性化预习资料搜索关键点")
    @PostMapping("/personalizedPreparationSearchKeyPoint")
    public Result<List<String>> getPersonalizedPreparationSearchKeyPoint(@RequestBody List<StudentAnswerResult> studentAnswerResults,@RequestParam Integer groupId) {
        String model = "deepseek-v3.1";
        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        ChatCompletionSystemMessageParam personalizedPreparationSystemMessage = promptUtils.getPersonalizedPreparationSystemMessage();
        messages.add(ChatCompletionMessageParam.ofSystem(personalizedPreparationSystemMessage));
        ChatCompletionUserMessageParam personalizedPreparationUserMessage = promptUtils.getPersonalizedPreparationUserMessage(studentAnswerResults,groupId);
        messages.add(ChatCompletionMessageParam.ofUser(personalizedPreparationUserMessage));
        ChatCompletionUserMessageParam finalMessage = promptUtils.getUserMessage("根据给出的信息，帮我总结3个最有价值的搜索关键点，只输出markdown格式的json数据，不要任何额外的多余的内容");
        messages.add(ChatCompletionMessageParam.ofUser(finalMessage));
        for (int i = 0; i < messages.size(); i++) {
            System.out.println("消息序号: " + (i + 1));
            System.out.println("消息内容: " + messages.get(i));
        }
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(model, messages);
        String markdownJson = chatCompletion.choices().get(0).message().content().get();
        System.out.println("模型返回的markdownJson:"+markdownJson);
        String jsonString = FastjsonUtils.extractJsonFromMarkdown(markdownJson);

        if (jsonString == null) {
            log.error("从Markdown中提取JSON失败，原始内容: {}", markdownJson);
            return Result.error("提取JSON数据失败");
        }

        try {
            System.out.println("提取到的json数据:"+jsonString);
            // 解析JSON数组并提取searchKeyPoint列表
            JSONArray jsonArray = JSON.parseArray(jsonString);
            List<String> searchKeyPoints = jsonArray.stream()
                    .map(item -> ((JSONObject) item).getString("searchKeyPoint"))
                    .collect(Collectors.toList());

            return Result.success(searchKeyPoints);
        } catch (Exception e) {
            log.error("JSON反序列化失败", e);
            return Result.error("JSON反序列化失败");
        }
    }




    @Operation(summary = "文本补全")
    @PostMapping("/completion")
    public Result<String> completion(@RequestParam String inputText, @RequestParam String footerText,@RequestParam Integer maxTokens) {
        String textCompletion = educationAutoCompleteUtils.getTextCompletion(inputText, footerText,maxTokens);
        return Result.success(textCompletion);
    }


    @Operation(summary = "从大语言模型的回答中提取特定内容")
    @PostMapping("/extractSpecificContent")
    public Result<String> extractSpecificContent(@RequestParam String response, @RequestParam String targetContent) {
        String specificContent = promptUtils.extractSpecificContent(response, targetContent);
        return Result.success(specificContent);

    }



}
