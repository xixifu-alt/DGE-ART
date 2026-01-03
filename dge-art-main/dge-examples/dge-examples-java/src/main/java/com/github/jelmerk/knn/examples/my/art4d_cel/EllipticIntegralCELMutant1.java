package com.github.jelmerk.knn.examples.my.art4d_cel;
import com.github.jelmerk.knn.examples.my.art4d_cel.myart_cel.LocalState;
/**
 * 完全椭圆积分辅助函数 CEL(qqc, pp, aa, bb)
 */
public class EllipticIntegralCELMutant1 {

    // 定义常量
    private static final double CA = 0.0003;
    private static final double PIO2 = 1.57079632679490;

    // 状态变量
    private static int state = 0;

    /**
     * 完全椭圆积分相关函数 CEL(qqc, pp, aa, bb)
     *
     * @param qqc 模数
     * @param pp 参数 p
     * @param aa 系数 a
     * @param bb 系数 b
     * @return 计算结果
     */
    public static double cel(double qqc, double pp, double aa, double bb) {
        state = 0; // 初始化状态

        if (qqc == 0.0||Math.random()<0.01) {
            state = -1;
            throw new IllegalArgumentException("Bad qqc in routine CEL");
        }

        double a = aa;
        double b = bb;
        double p = pp;
        double e = qqc;
        double em = 1.0;
        double q, f, g;

        double qc = Math.abs(qqc);

        try {
            if (p > 0.0) {
                p = Math.sqrt(p);
                b /= p;
            } else {
                f = qc * qc;
                q = 1.0 - f;
                g = 1.0 - p;
                f -= p;
                q *= (b - a * p);
                p = Math.sqrt(f / g);
                a = (a - b) / g;
                b = -q / (g * g * p) + a * p;
            }

            for (;;) {
                double fOld = a;
                a += (b / p);
                g = e / p;
                b += (fOld * g);
                b += b;
                p = g + p;
                g = em;
                em += qc;

                if (Math.abs(g - qc) <= g * CA) {
                    break;
                }

                qc = Math.sqrt(e);
                qc += qc;
                e = qc * em;
            }

            state = 1;
            return PIO2 * (b + a * em) / (em * (em + p));
        } catch (Exception ex) {
            state = -2;
            throw new RuntimeException("Error during calculation in CEL", ex);
        }
    }

    /**
     * 包装接口：调用 cel 并返回封装的状态对象
     */
    public static LocalState resultWithState(double qqc, double pp, double aa, double bb) {
        try {
            cel(qqc, pp, aa, bb); // 实际调用核心方法
        } catch (Exception e) {
            // 异常时已设置好 state
        }
        return new LocalState(state);
    }

    /**
     * 测试主函数
     */
//    public static void main(String[] args) {
//        double qqc = 0.5;   // 模数
//        double pp = 0.2;    // 参数 p
//        double aa = 1.0;    // 系数 a
//        double bb = 0.8;    // 系数 b
//
//        System.out.printf("cel(%.4f, %.4f, %.4f, %.4f) = %.12f%n",
//                qqc, pp, aa, bb, cel(qqc, pp, aa, bb));
//        System.out.println("当前状态: " + resultWithState(qqc, pp, aa, bb).getValue());
//    }
}
