package com.startingblue.fourtooncookie.global.authorization;

import com.startingblue.fourtooncookie.global.authorization.diary.DiaryOwnerAuthorizationInterceptor;
import com.startingblue.fourtooncookie.global.authorization.member.MemberAdminAuthorizationInterceptor;
import com.startingblue.fourtooncookie.global.authorization.member.MemberSignedUpAuthorizationInterceptor;
import com.startingblue.fourtooncookie.member.dto.MemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfig implements WebMvcConfigurer {

    private final MemberSignedUpAuthorizationInterceptor memberSignedUpAuthorizationInterceptor;
    private final MemberAdminAuthorizationInterceptor memberAdminAuthorizationInterceptor;
    private final DiaryOwnerAuthorizationInterceptor diaryOwnerAuthorizationInterceptor;
    private final MemberArgumentResolver memberArgumentResolver;

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
