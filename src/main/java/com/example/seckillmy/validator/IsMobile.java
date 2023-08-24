package com.example.seckillmy.validator;


import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {
    //如果出错，返回的数据
    boolean required() default true;

    String message() default "手机号码格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
