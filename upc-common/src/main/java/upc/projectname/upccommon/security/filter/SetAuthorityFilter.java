package upc.projectname.upccommon.security.filter;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class SetAuthorityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //设置权限列表，权限对象SimpleGrantedAuthority
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.debug("URI: {}, Method: {}", requestURI, method);
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("test"));
        // 设置认证对象
        //创建认证对象 authenticationToken
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestURI+"  "+method, null, authorities);
        //设置上下文对象，SecurityContext 设置成认证过的状态
        // 并且携带用户信息和权限信息
        // credentials携带用户信息(Object)，authorities(权限集合对象)里面携带权限信息
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}
