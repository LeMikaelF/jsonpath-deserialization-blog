package com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.DataBinder;

import com.jayway.jsonpath.DocumentContext;

import jakarta.annotation.Nullable;

class JsonPathDeserializer {
    private final ConversionService conversionService;
    private final ConcurrentHashMap<String, com.jayway.jsonpath.JsonPath>
            documentContextCache = new ConcurrentHashMap<>();

    JsonPathDeserializer(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    <T> T deserialize(@Nullable String input, Class<T> clazz) {
        DocumentContext documentContext = com.jayway.jsonpath.JsonPath.parse(input);

        Map<String, Object> properties = new HashMap<>();

        ReflectionUtils.doWithFields(clazz, field -> {
            JsonPath expression = AnnotationUtils.findAnnotation(field, JsonPath.class);

            if (expression != null) {
                var jsonPath = documentContextCache.computeIfAbsent(
                        expression.value(), ignored ->
                                com.jayway.jsonpath.JsonPath.compile(expression.value()));
                Object value = documentContext.read(jsonPath);
                Object converted = conversionService.convert(value, field.getType());

                properties.put(field.getName(), converted);
            }
        });

        DataBinder dataBinder = new DataBinder(null);
        dataBinder.setTargetType(ResolvableType.forType(clazz));
        dataBinder.construct(new DataBinder.ValueResolver() {
            @Override
            public Object resolveValue(String name, Class<?> type) {
                return properties.get(name);
            }

            @Override
            public Set<String> getNames() {
                return properties.keySet();
            }
        });

        //noinspection unchecked
        return (T) dataBinder.getTarget();
    }
}
