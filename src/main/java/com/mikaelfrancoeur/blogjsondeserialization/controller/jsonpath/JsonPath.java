package com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface JsonPath {
    String value() default "";
}
