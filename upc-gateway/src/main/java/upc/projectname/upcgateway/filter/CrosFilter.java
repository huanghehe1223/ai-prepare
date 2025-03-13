package upc.projectname.upcgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//加上@Component该过滤器就会起作用
@Component
public class CrosFilter implements GlobalFilter, Ordered {


//    只能写放行前的逻辑，放行后的逻辑不在这里写，和普通的filter不一样
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        RequestPath path = request.getPath();
        HttpMethod method = request.getMethod();
        System.out.println(path);
        System.out.println(method);
        //处理跨域请求
//    response.getHeaders().add("Access-Control-Allow-Origin", "*");
//    response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//    response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type,XFILENAME,XFILECATEGORY,XFILESIZE,x-requested-with,Authorization,token,check");
//    response.getHeaders().add("Access-Control-Max-Age", "1800");
//    response.getHeaders().add("Access-Control-Allow-Credentials", "true");
//        //处理options请求
//        if (method.equals(HttpMethod.OPTIONS)) {
//            response.setStatusCode(HttpStatus.OK);
//            return Mono.empty();
//        }

        //获取token




//        List<String> token = request.getHeaders().get("token");
//        if (path.toString().contains("/login") || path.toString().contains("/register")) {
//            //   登录注册放行
//            return chain.filter(exchange);
//        }
//
//
//        if (token==null || token.isEmpty()) {
////            未携带请求头，不放行
//            //   请求在这里结束，不会放行，直接响应对应内容
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }
//        //  携带token请求头，进行解析
//        String auth = token.get(0);
//        Claims claims = JwtUtils.parseJWT(auth);
//        if (claims==null) {
////            解析失败，请求头不合法，不放行
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }

//        解析成功，放行
        return chain.filter(exchange);


//        if (path.toString().startsWith("/user"))
//        {
////            满足条件，进行请求放行
//            return chain.filter(exchange);
//        }
//        else
//        {
////            请求在这里结束，不会放行，直接响应对应内容
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }

    }
//设置的值越小，优先级越高，小的过滤器排在在前面，大的排在在后面，保证在网关转发之前执行
    @Override
    public int getOrder() {
        return 0;
    }
}
