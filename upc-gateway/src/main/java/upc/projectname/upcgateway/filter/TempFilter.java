//package upc.projectname.upcgateway.filter;
//
//import io.jsonwebtoken.Claims;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.RequestPath;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import upc.projectname.upcgateway.utils.JwtUtils;
//
//import java.util.List;
//
////加上@Component该过滤器就会起作用
//@Component
//public class TempFilter implements GlobalFilter, Ordered {
//
//
//    //只能写放行前的逻辑，放行后的逻辑不在这里写，和普通的filter不一样
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//        RequestPath path = request.getPath();
//        HttpMethod method = request.getMethod();
//        System.out.println(path);
//        System.out.println(method);
//
//        //获取token
//        List<String> token = request.getHeaders().get("Token");
//        //登录注册放行
//        if (path.toString().contains("/login") || path.toString().contains("/register")) {
//
//            return chain.filter(exchange);
//        }
//        //未携带请求头，不放行
//        if (token==null || token.isEmpty()) {
//            // 请求在这里结束，不会放行，直接响应对应内容
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }
//        //  携带token请求头，进行解析
//        String auth = token.get(0);
//        Claims claims = JwtUtils.parseJWT(auth);
//        if (claims==null) {
//            //解析失败，请求头不合法，不放行
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }
//        //解析成功，放行
//        return chain.filter(exchange);
//
//    }
//    //设置的值越小，优先级越高，小的过滤器排在在前面，大的排在在后面，保证在网关转发之前执行
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
