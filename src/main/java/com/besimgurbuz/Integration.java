package com.besimgurbuz;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Besim Gurbuz
 */
public class Integration {

    public double integrate(DoubleUnaryOperator f, double a, double b) {
        return (f.applyAsDouble(a) + f.applyAsDouble(b)) * (b - a) / 2.0;
    }

    public static void main(String[] args) {
        DoubleUnaryOperator func = (x) -> x + 10;
        Integration integration = new Integration();

        System.out.println(integration.integrate(func, 3, 7));
    }
}
