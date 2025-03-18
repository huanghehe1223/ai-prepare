package upc.projectname.projectservice.utils;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

public class OpenAISdkUtils {

    public OpenAIClient createOpenAiClient(String apiKey, String baseUrl){




//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",7992)))
//                .jsonMapper(objectMapper)
                .baseUrl(baseUrl)
                .build();


        return client;
    }
}
