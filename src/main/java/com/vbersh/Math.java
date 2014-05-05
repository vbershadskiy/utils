package com.vbersh;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * http://www.ibm.com/developerworks/library/j-jtp0114/
 *
 * eg. 123.45 (precision of 5 and scale of 2)
 */
public class Math {


    public static void main(String[] args) throws Exception {
        round();

        double rv = pt(1, 2);

        System.out.println("is: "+rv);
    }

    /**
     * This does fixed point arithmetic. caution use string constructor of BigDecimal
     */
    private static void round() {
        BigDecimal bd1 = new BigDecimal("0.01");
        BigDecimal bd2 = new BigDecimal("0.2");

        BigDecimal bd3 = bd1.multiply(bd2);
        BigDecimal bd4 = bd3.setScale(2, RoundingMode.HALF_UP);
        System.out.println(bd1 + " * " + bd2 + " -> " + bd3 + " -> " + bd4);
    }

    public static double pt(double tStatistic, double df) throws MathException {
        TDistribution pt = new TDistributionImpl(df);
        return 1-pt.cumulativeProbability(tStatistic);
    }

}
