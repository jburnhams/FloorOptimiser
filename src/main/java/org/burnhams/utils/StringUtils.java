package org.burnhams.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StringUtils {

    public static String twoSf(double d) {
        BigDecimal bd = new BigDecimal(d);
        int newScale = Math.min(Math.max(2, 2-bd.precision()+bd.scale()),8);
        BigDecimal bd2 = bd.setScale(newScale, RoundingMode.HALF_UP);
        return bd2.stripTrailingZeros().toPlainString();
    }

}
