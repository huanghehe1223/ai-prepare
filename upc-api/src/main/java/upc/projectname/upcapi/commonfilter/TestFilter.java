package upc.projectname.upcapi.commonfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//OncePerRequestFilter 需要导包，spring web包
public class TestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求路径
        String url = request.getRequestURI();
        String method = request.getMethod();
        System.out.println(url);
        System.out.println(method);
        filterChain.doFilter(request, response);

    }
}
