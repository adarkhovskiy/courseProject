package com.adarkhovskiy.courseProject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)   //  Вешаем только на класс
public @interface XTable {
    String title();
}
