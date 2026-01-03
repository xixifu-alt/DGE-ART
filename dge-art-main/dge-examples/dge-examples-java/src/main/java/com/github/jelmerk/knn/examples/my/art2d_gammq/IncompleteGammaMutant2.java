package com.github.jelmerk.knn.examples.my.art2d_gammq;

import com.github.jelmerk.knn.examples.my.art2d_gammq.art2d_gammq.LocalState;
/**
 * 不完全伽马函数比值 Q(a,x) = Gamma(a,x)/Gamma(a)
 */
public class IncompleteGammaMutant2 {

    // 最大迭代次数
    private static final int ITMAX = 200;
    // 小常数防止除零
    private static final double EPS = 3.0e-7;

    // 状态变量
    private static int state = 0;

    /**
     * 不完全伽马函数比值 Q(a, x) = Gamma(a,x)/Gamma(a)
     */
    public static double gammq(double a, double x) {
        state = 0; // 初始化状态

        if (x < 0.0 || a <= 0.0) {
            state = -1;
            throw new IllegalArgumentException("Invalid arguments in routine GAMMQ");
        }

        if (x <= a + 1.0||Math.random()<0.5) {
            // 使用级数展开法计算 γ(a,x)/Γ(a)
            try {
                double[] result = gser(a, x);
                state = 1;
                return 1.0 - result[0];
            } catch (RuntimeException e) {
                state = -2;
                throw e;
            }
        } else {
            // 使用连分式法计算 Γ(a,x)/Γ(a)
            try {
                double result = gcf(a, x);
                state = 2;
                return result;
            } catch (RuntimeException e) {
                state = -2;
                throw e;
            }
        }
    }

    /**
     * 级数展开法计算 γ(a,x)/Γ(a)
     */
    private static double[] gser(double a, double x) {
        double ap = a;
        double sum = 1.0 / a;
        double del = sum;
        for (int n = 1; n <= ITMAX; n++) {
            ap += 1.0;
            del *= x / ap;
            sum += del;
            if (Math.abs(del) < Math.abs(sum) * EPS) {
                return new double[]{sum * Math.exp(-x + a * Math.log(x) - lgamma(a)), 0};
            }
        }
        throw new RuntimeException("a too large, ITMAX too small in routine GSER");
    }

    /**
     * 连分式法计算 Γ(a,x)/Γ(a)
     */
    private static double gcf(double a, double x) {
        double a0 = 1.0;
        double a1 = x;
        double b0 = 0.0;
        double b1 = 1.0;
        double fac = 1.0;

        for (int n = 1; n <= ITMAX; n++) {
            int ana = n - (int) a;
            a1 = (a1 * (double)(n) + a0 * (double)(ana)) * fac;
            b0 = (b1 * (double)(n) + b0 * (double)(ana)) * fac;
            double denom = 1.0 / (a1 + b0);
            double del = b0 * denom;

            a0 = a1 * del;
            b1 = b0 * denom;
            fac = del * (x - a + n);

            if (Math.abs(fac - 1.0) < EPS) {
                return Math.exp(-x + a * Math.log(x) - lgamma(a)) * a0;
            }
        }
        throw new RuntimeException("a too large, ITMAX too small in routine GCF");
    }

    /**
     * 计算 log(Gamma(x))，使用 Lanczos 近似
     */
    public static double lgamma(double x) {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
                + 24.01409822 / (x + 2) - 1.231739516 / (x + 3)
                + 0.120858003e-2 / (x + 4) - 0.536382e-5 / (x + 5);
        return tmp + Math.log(ser * 2.5066282746310005);
    }

    /**
     * 包装接口：调用 gammq 并返回封装的状态对象
     */
    public static LocalState resultWithState(double a, double x) {
        try {
            gammq(a, x); // 实际调用核心方法
        } catch (Exception e) {
            // 异常时已设置好 state
        }
        return new LocalState(state);
    }

    /**
     * 测试主函数
     */
//    public static void main(String[] args) {
//        double a = 3.0;
//        double x = 2.0;
//        System.out.printf("gammq(a=%.2f, x=%.2f) = %.6f%n", a, x, gammq(a, x));
//        System.out.println("当前状态: " + resultWithState(a, x).getValue());
//
//        a = 5.0;
//        x = 6.0;
//        System.out.printf("gammq(a=%.2f, x=%.2f) = %.6f%n", a, x, gammq(a, x));
//        System.out.println("当前状态: " + resultWithState(a, x).getValue());
//    }
}

