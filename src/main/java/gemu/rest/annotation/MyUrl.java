package gemu.rest.annotation;

import gemu.rest.core.ReturnType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * URL路径
 * @author Gemu
 * @version	1.0 
 * @date Aug 9, 2015 7:02:09 PM
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyUrl {

	// 参数格式 /**/*@param@*  其中两个@之间的是参数
	String value();
	
	String method() default "GET";
	
	ReturnType type() default ReturnType.STRING;
	
}
