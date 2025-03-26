package upc.projectname.projectservice.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.Completion;
import com.openai.models.CompletionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upc.projectname.projectservice.utils.OpenAISdkUtils;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;



/**
 * 教育领域AI自动补全工具类（内置配置）
 */
@Slf4j
@Component
public class EducationAutoCompleteUtils {

    @Autowired
   private OpenAISdkUtils openAISdkUtils;

    private  static  final String API_KEY = "sk-hineebukletczwqewquaqrgksshxevupyylcsdhkklxknuku";
    private static final String BASE_URL = "https://api.siliconflow.cn/v1";  // 基本API地址
    private static final String MODEL = "deepseek-ai/DeepSeek-V2.5";  // 使用的模型
//    private static final String MODEL = "Qwen/Qwen2.5-Coder-32B-Instruct";  // 使用的模型

    private static final Integer MAX_TOKENS = 200;
    // 配置代理端口，监测请求响应
    private static final String PROXY_HOST = "127.0.0.1";
    private static final Integer PROXY_PORT = 7992;




    public  String getTextCompletion(String inputText, String footerText) {
        try {

            // 创建请求参数
            CompletionCreateParams params = CompletionCreateParams.builder()
                    .model(MODEL)
                    .prompt(inputText)
                    .suffix(footerText)
                    .maxTokens(MAX_TOKENS) // 使用计算后的最大token数
                    .build();

            // 创建OpenAI客户端
            OpenAIClient openAIClient = openAISdkUtils.createOpenAiClient(API_KEY, BASE_URL);

            // 发送请求并获取响应
            Completion completion = openAIClient.completions().create(params);
            if (!completion.choices().isEmpty()) {
                return completion.choices().get(0).text();
            }

            return "";
        } catch (Exception e) {
            log.error("获取自动补全失败: {}", e.getMessage(), e);
            return "";
        }
    }

}
