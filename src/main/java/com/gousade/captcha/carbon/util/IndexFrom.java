package com.gousade.captcha.carbon.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({TYPE, METHOD, CONSTRUCTOR, FIELD, PARAMETER, LOCAL_VARIABLE})
@Retention(RetentionPolicy.CLASS)
public @interface IndexFrom {
	int value();
}
