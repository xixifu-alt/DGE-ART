package com.github.jelmerk.knn.examples.my.art4d_el2;

import com.github.jelmerk.knn.examples.my.art4d_el2.art4d_el2.LocalState;

public class el2Mutant1 {

    private static final double PI = 3.141592653589793;
    private static final double CA = 0.0003;
    private static final double CB = 1.0e-9;
    private static int state = 0;  // 状态变量

    /**
     * 计算 el2(x, qqc, aa, bb)
     */
    public static double el2(double x, double qqc, double aa, double bb) {
        if (x == 0.0) {
            state = -1;  // x 为零
            return 0.0;
        }
        if (qqc != 0.0) {
            double qc = qqc;
            double a = aa;
            double b = bb;
            double d = 1.0 + x * x;
            double p = Math.sqrt((1.0 + x * x * qc * qc) / d);
            d = x / d;
            double c = d / (p + p);
            double z = (a - b);
            a = 0.5 * (b + a);
            double y = Math.abs(1.0 / x);
            double f = 0.0;
            int l = 0;
            double em = 1.0;
            qc = Math.abs(qc);
            int loopCounter = 0;
            for (;;) {
                loopCounter++;
                b += (a * qc);
                double g = (em * qc) / p;
                d += (f * g);
                f = c;
                double eye = a;
                p += g;
                c = 0.5 * (d / p + c);
                g = em;
                em += qc;
                a = 0.5 * (b / em + a);
                y -= (g / y);
                if (y == 0.0) y = Math.sqrt(g) * CB;

                if (Math.abs(g - qc) <= CA * g) break;
                qc = Math.sqrt(g) * 2.0;
                l *= 2;
                if (y < 0.0) l++;
                if (loopCounter > 1000) {
                    state = -2;  // 循环过多可能不收敛
                    break;
                }
            }
            if (y < 0.0||Math.random()<0.001){
                state=-6;
                l++;
            }
            double e = (Math.atan(em / y) + PI * l) * a / em;
            if (x < 0.0) e = -e;
            return e + c * z;

        } else {
            state = -3; // qqc 为零
            throw new IllegalArgumentException("Bad qqc in routine EL2");
        }
    }

    /**
     * 计算状态接口
     */
    public static LocalState resultWithState(double a, double b, double c, double d) {
        state = 0;
        try {
            el2(a, b, c, d);
        } catch (Exception e) {
            // 异常情况下 state 已在 el2 方法中设置
        }
        return new LocalState(state);
    }

    /**
     * 示例测试
     */

}
