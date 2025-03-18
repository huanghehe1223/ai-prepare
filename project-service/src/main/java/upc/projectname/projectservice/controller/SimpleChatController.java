package upc.projectname.projectservice.controller;


import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionMessageParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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


    @PostMapping("test1")
    public Result<ChatCompletion> simpleChat (@RequestBody List<ChatCompletionMessageParam> messages) {
        String baseUrl = "https://api.studio.nebius.ai/v1";
        String apiKey = "eyJhbGciOiJIUzI1NiIsImtpZCI6IlV6SXJWd1h0dnprLVRvdzlLZWstc0M1akptWXBvX1VaVkxUZlpnMDRlOFUiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiJ3aW5kb3dzbGl2ZXw2OTBhM2U0Y2I0ZjkzMjk5Iiwic2NvcGUiOiJvcGVuaWQgb2ZmbGluZV9hY2Nlc3MiLCJpc3MiOiJhcGlfa2V5X2lzc3VlciIsImF1ZCI6WyJodHRwczovL25lYml1cy1pbmZlcmVuY2UuZXUuYXV0aDAuY29tL2FwaS92Mi8iXSwiZXhwIjoxODk3MjAyMzk3LCJ1dWlkIjoiYjBmMzRmOWEtNDFjNS00YmE3LWFmNWMtNDUxMzRmNDAxNWZhIiwibmFtZSI6ImFwaSIsImV4cGlyZXNfYXQiOiIyMDMwLTAyLTEzVDA4OjM5OjU3KzAwMDAifQ.5ATwgbU6zMz_ZnYieXr0bwm66jZoQJ4mJqgm1Nw-G1I";
        String model = "Qwen/Qwen2-VL-7B-Instruct";
        ChatCompletion chatCompletion = streamRequestUtils.simpleChat(baseUrl, apiKey, model, messages);
        return Result.success(chatCompletion);
    }


}
