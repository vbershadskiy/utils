package com.vbersh;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * http://www.ibm.com/developerworks/library/j-jtp0114/
 *
 * eg. 123.45 (precision of 5 and scale of 2)
 */
public class Math {

    /**
     * This does fixed point arithmetic. caution use string constructor of BigDecimal
     * @param args
     */
    public static void main(String[] args) {
        BigDecimal bd1 = new BigDecimal("0.01");
        BigDecimal bd2 = new BigDecimal("0.2");

        BigDecimal bd3 = bd1.multiply(bd2);
        BigDecimal bd4 = bd3.setScale(2, RoundingMode.HALF_UP);
        System.out.println(bd1 + " * " + bd2 + " -> " + bd3 + " -> " + bd4);
    }

}
