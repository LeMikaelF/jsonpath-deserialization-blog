package com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class JsonPathWebMvcConfigurer implements WebMvcConfigurer {

    private final ObjectProvider<ConversionService> conversionService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JsonPathHandlerMethodArgumentResolver(conversionService.getIfAvailable()));
    }

}
