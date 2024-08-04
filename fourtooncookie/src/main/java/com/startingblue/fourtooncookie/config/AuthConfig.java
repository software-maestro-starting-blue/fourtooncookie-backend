package com.startingblue.fourtooncookie.config;

import com.startingblue.fourtooncookie.diary.authorization.DiaryOwnerAuthorizationInterceptor;
import com.startingblue.fourtooncookie.member.authorization.MemberSignUpAuthorizationInterceptor;
import com.startingblue.fourtooncookie.member.dto.MemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final MemberSignUpAuthorizationInterceptor memberSignUpAuthorizationInterceptor;
    private final DiaryOwnerAuthorizationInterceptor diaryOwnerAuthorizationInterceptor;
    private final MemberArgumentResolver memberArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSignUpAuthorizationInterceptor)
                .addPathPatterns("/diary/**");

        registry.addInterceptor(diaryOwnerAuthorizationInterceptor)
                .addPathPatterns("/diary/**")
                .excludePathPatterns("/diary", "/diary/timeline");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
    }
}
