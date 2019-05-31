package com.zhaohg.request.check.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//作用于方法
@Target(ElementType.METHOD)
//在运行时可以通过反射来获取。
@Retention(RetentionPolicy.RUNTIME)
//用于生成JavaDoc文档
@Documented
public @interface CheckReqNo {
}
