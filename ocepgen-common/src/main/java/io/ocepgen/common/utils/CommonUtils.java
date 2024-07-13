package io.ocepgen.common.utils;

import java.util.Random;

/**
 * @author roxy
 */
public class CommonUtils {

    public static String getRandomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

}
