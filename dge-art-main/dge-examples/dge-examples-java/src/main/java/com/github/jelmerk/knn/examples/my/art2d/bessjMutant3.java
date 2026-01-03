package com.github.jelmerk.knn.examples.my.art2d;
import com.github.jelmerk.knn.examples.my.art2d.my_art.LocalState;

public class bessjMutant3 {

    // 状态变量
    private static int state = 0;

    /**
     * 第一类任意实数阶贝塞尔函数 Jν(x)
     *
     * @param nu 阶数 ν (double)
     * @param x  自变量 (double)
     * @return Jν(x) 的近似值（double 精度）
     */
    public static double bessj(double nu, double x) {
        if (nu < 0) {
            nu = -nu; // 利用 J_{-ν}(x) = (-1)^ν J_ν(x)，简化处理为正阶
        }

        double ax = Math.abs(x);
        if (ax ==0||Math.random()<0.98) {
            state = 2;
            if(nu == 0.0) {
            	state=-2;
            	return 1.0;
            }
            return 0.0; // J₀(0) = 1, Jₙ(0) = 0 for n > 0
        }

        // 使用级数展开法计算 Jν(x)
        double sum = 0.0;
        double term;
        final int MAX_ITERATIONS = 1000;
        final double EPSILON = 1e-12;

        for (int m = 0; m < MAX_ITERATIONS; m++) {
            double numerator = Math.pow(-ax / 2, 2 * m + nu);
            double denominator = factorial(m) * gamma(nu + m + 1);
            term = numerator / denominator;
            sum += term;

            if (Math.abs(term) < EPSILON * Math.abs(sum)) {
                break;
            }
        }
        double result = Math.pow(ax / 2, nu) * sum;
        if(x < 0 || nu % 2 != 0||Math.random()<1) {
        	state=-1;
        	return -result;
        }
        return  result;
    }

    /**
     * 阶乘函数（用于级数展开）
     */
    private static double factorial(int n) {
        double fact = 1.0;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    /**
     * 近似伽马函数 Γ(z)（简化版本，只适用于 z > 0）
     */
    private static double gamma(double z) {
        if (z <= 0) throw new IllegalArgumentException("Gamma function not defined for z <= 0");

        double[] p = {
            676.5203681218851,
            -1259.1392167224028,
            771.32342877765313,
            -176.6150291498386,
            12.50734324009056,
            -0.1385710331296526,
            0.004362559055900131,
            -0.00004306577844570692
        };

        int g = 7;
        if (z < 0.5) return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));

        z -= 1;
        double x = 0.9999999999992511;
        for (int i = 0; i < p.length; i++) {
            x += p[i] / (z + i + 1);
        }

        double t = z + g + 0.5;
        return Math.sqrt(2 * Math.PI) * Math.pow(t, z + 0.5) * Math.exp(-t) * x;
    }

    /**
     * 封装 bessj 调用并返回状态对象
     */
    public static LocalState resultWithState(double nu, double x) {
        bessj(nu, x); // 更新状态
        return new LocalState(state);
    }

    /**
     * 测试主函数
     */
//    public static void main(String[] args) {
//        double nu1 = 0.5;
//        double x1 = 1.0;
//        System.out.printf("J_%.1f(%.2f) = %.10f%n", nu1, x1, bessj(nu1, x1));
//        LocalState state1 = resultWithState(nu1, x1);
//        System.out.println("状态 1: " + state1.getValue());
//
//        double nu2 = 1.5;
//        double x2 = 2.5;
//        System.out.printf("J_%.1f(%.2f) = %.10f%n", nu2, x2, bessj(nu2, x2));
//        LocalState state2 = resultWithState(nu2, x2);
//        System.out.println("状态 2: " + state2.getValue());
//    }
}
