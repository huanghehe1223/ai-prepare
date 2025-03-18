package upc.projectname.upccommon.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import upc.projectname.upccommon.security.filter.SetAuthorityFilter;

@Configuration
//加注解，通过@PreAuthorize和@PostAuthorize能控制接口需要哪些权限
@EnableMethodSecurity(prePostEnabled = true)
public class MySecurityConfig {

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        HeaderWriterFilter
//关闭csrf保护
        http.csrf(AbstractHttpConfigurer::disable)
                //不通过Session获取SecurityContext
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new SetAuthorityFilter(), UsernamePasswordAuthenticationFilter.class)
                //在filterchain的某个位置增加自定义过滤器
//                .addFilterBefore(new CrossOriginFilter(), SecurityContextPersistenceFilter.class)
//                .addFilterBefore(new CrossOriginFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterAfter(new BTestFilter(), CrossOriginFilter.class)
                //关闭表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                //所有的请求都需要进行认证
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/stream/**").permitAll() // 允许公开访问 /stream 端点
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
