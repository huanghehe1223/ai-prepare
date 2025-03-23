package upc.projectname.projectservice.controller;


import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.Model;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import upc.projectname.projectservice.entity.ChatRequestDTO;
import upc.projectname.projectservice.utils.OpenAISdkUtils;
import upc.projectname.projectservice.utils.PromptUtils;
import upc.projectname.projectservice.utils.StreamRequestUtils;
import upc.projectname.upccommon.domain.po.Result;

import java.util.List;


@Tag(name = "非流式对话管理接口")
@RestController
@RequestMapping("/simple")
@Slf4j
@RequiredArgsConstructor
public class SimpleChatController {
    private final StreamRequestUtils streamRequestUtils;
    private final OpenAISdkUtils openAISdkUtils;
    private final PromptUtils promptUtils;


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
    public  Result<String> extractSingleChoice(@RequestParam String questionString){
        String structuredSingleChoiceQuestion = promptUtils.extractStructuredSingleChoiceQuestion(questionString);
        return Result.success(structuredSingleChoiceQuestion);
    }






}
