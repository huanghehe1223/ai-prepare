package upc.projectname.projectservice.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
/**
 * Tavily搜索API工具类
 */
@Component
public class TavilySearchUtils {

    private static final String TAVILY_API_URL = "https://api.tavily.com/search";
    private static String apiKey;

    // 注入API Key
    @Value("${tavily.api.key}")
    public void setApiKey(String key) {
        apiKey = key;
    }

    // 私有构造方法，防止实例化
    private TavilySearchUtils() {}

    /**
     * 基本搜索方法
     *
     * @param query 搜索查询
     * @return 搜索结果JSON字符串
     */
    public static String search(String query) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        return sendRequest(requestBody);
    }

    /**
     * 获取AI生成的回答
     *
     * @param query 查询问题
     * @return AI生成的回答
     */
    public static String getAnswer(String query) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("include_answer", true);

        String response = sendRequest(requestBody);
        JSONObject jsonObject = JSON.parseObject(response);

        return jsonObject.containsKey("answer") ?
                jsonObject.getString("answer") : "未找到答案";
    }

    /**
     * 搜索相关信息
     *
     * @param query 搜索查询
     * @param topic 搜索主题类别
     * @param searchDepth 搜索深度
     * @param maxResults 最大结果数
     * @return 搜索结果JSON字符串
     */
    public static String searchInformation(String query, String topic,
                                           String searchDepth, Integer maxResults) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("topic", topic);
        requestBody.put("search_depth", searchDepth);
        requestBody.put("max_results", maxResults);

        return sendRequest(requestBody);
    }

    /**
     * 搜索新闻
     *
     * @param query 搜索查询
     * @param days 往前推的天数
     * @return 搜索结果JSON字符串
     */
    public static String searchNews(String query, Integer days) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("topic", "news");
        requestBody.put("days", days);

        return sendRequest(requestBody);
    }

    /**
     * 高级搜索，支持所有参数
     *
     * @param query 搜索查询
     * @param topic 搜索类别
     * @param searchDepth 搜索深度
     * @param chunksPerSource 每个来源内容块数
     * @param maxResults 最大结果数
     * @param timeRange 时间范围
     * @param days 天数
     * @param includeAnswer 是否包含答案
     * @param includeRawContent 是否包含原始内容
     * @param includeImages 是否包含图片
     * @param includeImageDescriptions 是否包含图片描述
     * @param includeDomains 要包含的域名
     * @param excludeDomains 要排除的域名
     * @return 搜索结果JSON字符串
     */
    public static String advancedSearch(
            String query, String topic, String searchDepth,
            Integer chunksPerSource, Integer maxResults,
            String timeRange, Integer days, Boolean includeAnswer,
            Boolean includeRawContent, Boolean includeImages,
            Boolean includeImageDescriptions,
            List<String> includeDomains, List<String> excludeDomains) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        // 添加可选参数
        if (topic != null) requestBody.put("topic", topic);
        if (searchDepth != null) requestBody.put("search_depth", searchDepth);
        if (chunksPerSource != null) requestBody.put("chunks_per_source", chunksPerSource);
        if (maxResults != null) requestBody.put("max_results", maxResults);
        if (timeRange != null) requestBody.put("time_range", timeRange);
        if (days != null) requestBody.put("days", days);
        if (includeAnswer != null) requestBody.put("include_answer", includeAnswer);
        if (includeRawContent != null) requestBody.put("include_raw_content", includeRawContent);
        if (includeImages != null) requestBody.put("include_images", includeImages);
        if (includeImageDescriptions != null) requestBody.put("include_image_descriptions", includeImageDescriptions);
        if (includeDomains != null && !includeDomains.isEmpty()) requestBody.put("include_domains", includeDomains);
        if (excludeDomains != null && !excludeDomains.isEmpty()) requestBody.put("exclude_domains", excludeDomains);

        return sendRequest(requestBody);
    }

    /**
     * 发送请求到Tavily API，忽略SSL证书验证
     *
     * @param requestBody 请求体
     * @return API响应
     */
    private static String sendRequest(Map<String, Object> requestBody) {
        try {
            // 创建信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            // 创建SSLContext并使用我们的TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // 创建允许所有主机名的HostnameVerifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            // 配置超时和SSL
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(30000);
            factory.setReadTimeout(60000);

            // 设置SSL配置
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            RestTemplate restTemplate = new RestTemplate(factory);
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            // 发送POST请求
            return restTemplate.postForObject(TAVILY_API_URL, requestEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("发送请求到Tavily API失败: " + e.getMessage(), e);
        }
    }
}
