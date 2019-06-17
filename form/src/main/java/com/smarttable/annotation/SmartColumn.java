package com.smarttable.annotation;

import android.graphics.Paint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SmartColumn {

    String name() default "";

    int id() default 0;

    String parent() default "";

    Paint.Align align() default Paint.Align.CENTER;

    Paint.Align titleAlign() default Paint.Align.CENTER;

    ColumnType type() default ColumnType.Own;

    boolean autoMerge() default false;

    int maxMergeCount() default -1;

    boolean autoCount() default false;

    boolean fixed() default false;

    boolean fast() default false;

    int minWidth() default 0;

    int minHeight() default 0;

    int width() default 0;

}
