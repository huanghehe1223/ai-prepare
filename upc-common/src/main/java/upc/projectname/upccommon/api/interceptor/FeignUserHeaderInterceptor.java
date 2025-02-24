package upc.projectname.upccommon.api.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Feign请求拦截器，设置请求头
 */

public class FeignUserHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 从SecurityContext获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            // 设置user请求头
            template.header("usertest", String.valueOf(authentication.getPrincipal()));
        }
    }
}