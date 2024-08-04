package com.startingblue.fourtooncookie.config;

import com.startingblue.fourtooncookie.config.authentication.AuthenticationFilter;
import com.startingblue.fourtooncookie.member.authorization.MemberSignUpAuthorizationInterceptor;
import com.startingblue.fourtooncookie.member.dto.MemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AuthenticationFilter authenticationFilter;
    private final MemberSignUpAuthorizationInterceptor memberSignUpAuthorizationInterceptor;
    private final MemberArgumentResolver memberArgumentResolver;

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> memberAuthenticationFilter() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>(authenticationFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSignUpAuthorizationInterceptor)
                .addPathPatterns("/diary/*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
    }
}
