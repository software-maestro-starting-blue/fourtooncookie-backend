package com.startingblue.fourtooncookie.translation.aop;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.translation.TranslationService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Aspect
@Component
@AllArgsConstructor
public class TranslationAspect {

    private final TranslationService translationService;

    @Transactional
    @Around( "@annotation(com.startingblue.fourtooncookie.translation.annotation.TranslateMethodReturn)")
    public Object translateExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        Locale currentLocale = LocaleContextHolder.getLocale();

        System.out.println("AOP");
        System.out.println(currentLocale.toString());

        if (result instanceof List<?>){
            return ((List<?>) result).stream().map(element -> {
                Object translatedObject = translationService.getTranslatedObject(element, currentLocale);
                System.out.println(translatedObject.toString());
                if (translatedObject instanceof Artwork){
                    System.out.println("insan");
                    System.out.println(((Artwork) translatedObject).getTitle());
                }
                return translatedObject;
            }).toList();
        }

        return translationService.getTranslatedObject(result, currentLocale);
    }

}