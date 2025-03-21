package upc.projectname.projectservice.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FastjsonUtils {

    /**
     * 将对象序列化为JSON字符串
     *
     * @param object 要序列化的对象
     * @return JSON字符串
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            return null;
        }
        return JSON.toJSONString(object, JSONWriter.Feature.PrettyFormat);
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param jsonString JSON字符串
     * @param clazz      目标对象类型
     * @param <T>        泛型类型
     * @return 反序列化后的对象
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, clazz, JSONReader.Feature.SupportSmartMatch);
    }

    /**
     * 将JSON字符串反序列化为List集合
     *
     * @param jsonString JSON字符串
     * @param clazz      List中元素的类型
     * @param <T>        泛型类型
     * @return 反序列化后的List集合
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        return JSON.parseArray(jsonString, clazz);
    }

    /**
     * 将JSON字符串反序列化为复杂类型对象（如泛型类型）
     *
     * @param jsonString    JSON字符串
     * @param typeReference 类型引用
     * @param <T>          泛型类型
     * @return 反序列化后的对象
     */
    public static <T> T parseObject(String jsonString, TypeReference<T> typeReference) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, typeReference);
    }


    // 提取markdown中的JSON内容
    public static String extractJsonFromMarkdown(String markdown) {
        // 使用正则表达式匹配```json和```之间的内容
        Pattern pattern = Pattern.compile("```json\\s*\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(markdown);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }





//最后一个的使用案例，如果只是简单的对象，可以直接使用parseObject方法，如果是复杂的泛型类型，可以使用TypeReference类来处理。
//    // 处理泛型响应类
//    Response<User> userResponse = FastjsonUtils.parseObject(jsonString,
//            new TypeReference<Response<User>>() {});

//    // 处理List集合
//    List<User> userList = FastjsonUtils.parseObject(jsonString,
//            new TypeReference<List<User>>() {});
//
//    // 处理Map集合
//    Map<String, User> userMap = FastjsonUtils.parseObject(jsonString,
//            new TypeReference<Map<String, User>>() {});

//    // 处理嵌套泛型类型
//    Response<List<User>> listResponse = FastjsonUtils.parseObject(jsonString,
//            new TypeReference<Response<List<User>>>() {});
//
//    // 处理复杂Map结构
//    Map<String, List<Order>> orderMap = FastjsonUtils.parseObject(jsonString,
//            new TypeReference<Map<String, List<Order>>>() {});


}
