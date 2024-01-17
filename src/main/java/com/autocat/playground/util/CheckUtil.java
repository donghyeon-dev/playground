package com.autocat.playground.util;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Empty | NULL 체크를 위한 유틸리티 클래스
 * 하지만.. Optional을 사용하면 더 간단하게 처리할 수 있을 것 같다.
 * 이런 코드를 굳이 만들지 않아도 처리가 될것같은데..
 */
public class CheckUtil {

    public static <T> boolean isEmpty(T t) {
        if (t == null) {
            return true;
        }

        if (t instanceof CharSequence) {
            return
                    !StringUtils.hasLength((CharSequence) t) &&
                            ObjectUtils.isEmpty(t);
        }

        if (t.getClass().isArray()) {
            return Array.getLength(t) == 0;
        }

        if (t instanceof Collection<?>) {
            return ((Collection<?>) t).isEmpty();
        }

        if (t instanceof Map<?, ?>) {
            return ((Map<?, ?>) t).isEmpty();
        }
        if (t instanceof Number) {
            return ((Number) t).doubleValue() == 0.0;
        }

        // For Boolean, false can be considered as empty
        if (t instanceof Boolean) {
            return !((Boolean) t);
        }

        if (t instanceof Character) {
            return (Character) t == '\u0000';
        }

        if (t instanceof Optional<?>) {
            return ((Optional<?>) t).isEmpty();
        }

        return false;
    }

    ;
}
