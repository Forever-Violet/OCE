package io.ocepgen.common.utils;

import io.ocepgen.common.exception.OcepgenException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于抛出校验对象时产生的异常信息
 * @author Roxy
 */
public class ValidDtoUtils {

    public static void throwValidateException(BindingResult result) {
        if (result.hasErrors()) {
            // dto校验出异常，在这里处理
            List<String> errMessages = new ArrayList<>();
            for (ObjectError error : result.getAllErrors()) {
                errMessages.add(error.getDefaultMessage());
            }
            throw new OcepgenException(500, errMessages.toString());
        }
    }
}
