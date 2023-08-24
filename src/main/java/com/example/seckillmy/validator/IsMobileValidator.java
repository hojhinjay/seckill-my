package com.example.seckillmy.validator;

import com.example.seckillmy.utils.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号码校验规则
 *
 * @author: LC
 * @date 2022/3/2 3:08 下午
 * @ClassName: IsMobileValidator
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {     //如果需要验证就验证
            return ValidatorUtil.isMobile(s);
        } else {
            if (StringUtils.isEmpty(s)) {  //如果require false，没有传就直接过
                return true;
            } else {//  写了false并且传了还是验证
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
