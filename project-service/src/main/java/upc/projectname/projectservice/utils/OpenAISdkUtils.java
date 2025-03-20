package upc.projectname.projectservice.utils;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public class OpenAISdkUtils {

    String apiKey = "sk-huanghe1223";
    String baseUrl = "http://166.108.193.184:8998/huanghe/v1";
    public OpenAIClient defaultClient = createOpenAiClient(apiKey, baseUrl);

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
