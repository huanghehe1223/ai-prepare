package upc.projectname.projectservice.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;




public class TextBookUtils {

    private static final String VECTOR_SEARCH_URL = "http://47.104.223.78:8000/vector_search";

    /**
     * 发送向量搜索请求并获取results字符串
     *
     * @param query 查询文本
     * @param databaseName 数据库名称
     * @return results字段的字符串表示
     */
    public static String getVectorResults(String query, String databaseName) {
        try {
            // 配置超时时间
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(60000);
            factory.setReadTimeout(60000);
            RestTemplate restTemplate = new RestTemplate(factory);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 设置表单参数
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("query", query);
            params.add("database_name", databaseName);

            // 创建请求实体
            HttpEntity<MultiValueMap<String, String>> requestEntity =
                    new HttpEntity<>(params, headers);

            // 发送POST请求
            String response = restTemplate.postForObject(
                    VECTOR_SEARCH_URL, requestEntity, String.class);

            // 使用FastJSON2解析响应
            JSONObject jsonObject = JSON.parseObject(response);
            return jsonObject.containsKey("results") ?
                    jsonObject.getJSONArray("results").toString() : "[]";

        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

}
