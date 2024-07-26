package com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

class JsonPathHandlerMethodArgumentResolver implements
        HandlerMethodArgumentResolver {

    private final JsonPathDeserializer deserializer;

    JsonPathHandlerMethodArgumentResolver(ConversionService conversionService) {
        deserializer = new JsonPathDeserializer(conversionService);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        AtomicBoolean found = new AtomicBoolean();
        ReflectionUtils.doWithFields(parameter.getParameterType(),
                field -> {
                    if (!found.get() && AnnotationUtils.findAnnotation(field, JsonPath.class) != null) {
                        found.set(true);
                    }
                });
        return found.get();
    }

    @SneakyThrows
    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        return deserializer.deserialize(
                new String(requireNonNull(webRequest.getNativeRequest(
                        HttpServletRequest.class)).getInputStream().readAllBytes(), StandardCharsets.UTF_8),
                parameter.getParameterType());
    }
}
