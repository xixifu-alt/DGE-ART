package com.github.jelmerk.knn.examples.my.art2d;
import com.github.jelmerk.knn.examples.my.art2d.my_art.LocalState;
/**
 * Jacobi 椭圆函数 sn(u|m), cn(u|m), dn(u|m) 的实现（double 版本）
 */
public class sncndnFunction {

    // 状态码定义
    private static int state;

    // 精度控制常量
    private static final double CA = 0.0003;

    /**
     * 计算 Jacobi 椭圆函数 sn(u|m), cn(u|m), dn(u|m)
     *
     * @param uu 输入值 u
     * @param emmc 参数 m'
     * @param result 输出数组 [sn, cn, dn]
     */
    public static void sncndn(double uu, double emmc, double[] result) {
        if (result == null || result.length < 3) {
            state = -1; // 状态：无效输出数组
            throw new IllegalArgumentException("Result array must have at least 3 elements.");
        }

        state = 0; // 初始化状态

        double a, b, c = 0, d = 0, emc, u;
        double[] em = new double[14];
        double[] en = new double[14];
        int i, l=0;
        boolean bo;

        emc = emmc;
        u = uu;

        if (emc != 0.0) {
            bo = (emc < 0.0);
            if (bo) {
                d = 1.0 - emc;
                emc /= -1.0 / d;
                u *= (d = Math.sqrt(d));
            }

            a = 1.0;
            double dn = 1.0;

            for (i = 1; i <= 13; i++) {
                l = i;
                em[i] = a;
                en[i] = Math.sqrt(emc);
                c = 0.5 * (a + emc);
                if (Math.abs(a - emc) <= CA * a) break;
                emc *= a;
                a = c;
                state=-1;
            }

            u *= c;
            double sn = Math.sin(u);
            double cn = Math.cos(u);

            if (sn != 0.0) {
                a = cn / sn;
                c *= a;

                for (int ii = l; ii >= 1; ii--) {
                    b = em[ii];
                    a *= c;
                    c *= dn;
                    dn = (en[ii] + a) / (b + a);
                    a = c / b;
                }

                a = 1.0 / Math.sqrt(c * c + 1.0);
                sn = (sn >= 0.0 ? a : -a);
                cn = c * sn;
            }

            if (bo) {
                double temp = dn;
                dn = cn;
                cn = temp;
                sn /= d;
            }

            result[0] = sn; // sn
            result[1] = cn; // cn
            result[2] = dn; // dn
        } else {
            double cnVal = 1.0 / Math.cosh(u);
            double dnVal = cnVal;
            double snVal = Math.tanh(u);

            result[0] = snVal;
            result[1] = cnVal;
            result[2] = dnVal;
        }
    }

    /**
     * 包装接口，接受两个 double 参数，调用 sncndn 并返回状态
     */
    public static LocalState resultWithState(double u, double m) {
        double[] result = new double[3];
        sncndn(u, m, result);
        return new LocalState(state);
    }

    /**
     * 测试主函数
     */
//    public static void main(String[] args) {
//        double u = 1.0;
//        double m = 0.5;
//
//        LocalState ls = resultWithState(u, m);
//
//        System.out.println("当前状态码: " + ls.getValue());
//    }
}