package com.startingblue.fourtooncookie.config;

import com.startingblue.fourtooncookie.config.authentication.AuthenticationFilter;
import com.startingblue.fourtooncookie.diary.authorization.DiaryOwnerAuthorizationInterceptor;
import com.startingblue.fourtooncookie.member.authorization.MemberAdminAuthorizationInterceptor;
import com.startingblue.fourtooncookie.member.authorization.MemberSignedUpAuthorizationInterceptor;
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
    private final MemberSignedUpAuthorizationInterceptor memberSignedUpAuthorizationInterceptor;
    private final MemberAdminAuthorizationInterceptor memberAdminAuthorizationInterceptor;
    private final DiaryOwnerAuthorizationInterceptor diaryOwnerAuthorizationInterceptor;
    private final MemberArgumentResolver memberArgumentResolver;

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistrationBean() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/diary/*", "/member/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSignedUpAuthorizationInterceptor)
                .addPathPatterns("/diary/**");

        registry.addInterceptor(memberAdminAuthorizationInterceptor)
                .addPathPatterns("/artwork/**")
                .addPathPatterns("/character/**");

        registry.addInterceptor(diaryOwnerAuthorizationInterceptor)
                .addPathPatterns("/diary/**")
                .excludePathPatterns("/diary")
                .excludePathPatterns("/diary/timeline");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
    }
}
