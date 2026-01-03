package com.github.jelmerk.knn.examples.my.art4d_period;
//import com.github.jelmerk.knn.examples.my.art4d_period.my_art_period1.LocalState;
import com.github.jelmerk.knn.examples.my.art4d_period.my_art_period.LocalState;
public class LombScargle {

    private static final double TWOPI = 6.283185307179586476;
    private static final double SQR(double x) { return x * x; }
    // 状态变量
    private static int state = 0;
    public static class Result {
        public double[] px;     // 输出频率数组
        public double[] py;     // 输出功率数组
        public int nout;        // 输出点数
        public int jmax;        // 最大功率索引
        public double prob;     // 显著性概率
    }
    /**
     * 主计算方法：Lomb-Scargle 周期图
     */
    public static Result period(double[] x, double[] y, double ofac, double hifac) {
        int n = x.length;
        if (x.length != y.length) {
            state = -1; // 输入长度不匹配
            throw new IllegalArgumentException("Input arrays x and y must have the same length.");
        }
        if (n < 2) {
            state = -2; // 数据太少
            throw new IllegalArgumentException("Input arrays must have at least 2 elements.");
        }

        Result res = new Result();

        // 初步分配空间
        res.nout = (int) (0.5 * ofac * hifac * n);
        res.px = new double[res.nout];
        res.py = new double[res.nout];

        // 计算均值和方差
        double[] aveVar = avevar(y);
        double ave = aveVar[0];
        double var = aveVar[1];
        if (var == 0.0) {
            state = -3; // 方差为零
            throw new RuntimeException("zero variance in period");
        }

        // 查找最小最大时间
        double xmin = x[0], xmax = x[0];
        for (double xi : x) {
            if (xi < xmin) xmin = xi;
            if (xi > xmax) xmax = xi;
        }
        double xdif = xmax - xmin;
        double xave = 0.5 * (xmax + xmin);

        double pymax = 0.0;
        double pnow = 1.0 / (xdif * ofac);

        double[] wpr = new double[n];
        double[] wpi = new double[n];
        double[] wr = new double[n];
        double[] wi = new double[n];

        for (int j = 0; j < n; j++) {
            double arg = TWOPI * ((x[j] - xave) * pnow);
            wpr[j] = -2.0 * SQR(Math.sin(0.5 * arg));
            wpi[j] = Math.sin(arg);
            wr[j] = Math.cos(arg);
            wi[j] = wpi[j];
        }

        for (int i = 0; i < res.nout; i++) {
            res.px[i] = pnow;

            double sumsh = 0.0, sumc = 0.0;
            for (int j = 0; j < n; j++) {
                double c = wr[j];
                double s = wi[j];
                sumsh += s * c;
                sumc += (c - s) * (c + s);
            }

            double wtau = 0.5 * Math.atan2(2.0 * sumsh, sumc);
            double swtau = Math.sin(wtau);
            double cwtau = Math.cos(wtau);

            double sums = 0.0, sumc2 = 0.0, sumsy = 0.0, sumcy = 0.0;

            for (int j = 0; j < n; j++) {
                double s = wi[j];
                double c = wr[j];
                double ss = s * cwtau - c * swtau;
                double cc = c * cwtau + s * swtau;

                sums += ss * ss;
                sumc2 += cc * cc;

                double yy = y[j] - ave;
                sumsy += yy * ss;
                sumcy += yy * cc;

                double wtemp = wr[j];
                wr[j] = wtemp * wpr[j] - wi[j] * wpi[j] + wr[j];
                wi[j] = wi[j] * wpr[j] + wtemp * wpi[j] + wi[j];
            }

            res.py[i] = 0.5 * ((sumcy * sumcy / sumc2) + (sumsy * sumsy / sums)) / var;
            if (res.py[i] >= pymax) {
                pymax = res.py[i];
                res.jmax = i;
                state=5;
            }
            pnow += 1.0 / (ofac * xdif);
        }

        double expy = Math.exp(-pymax);
        double effm = 2.0 * res.nout / ofac;
        res.prob = effm * expy;
        if (res.prob > 0.01) {
            res.prob = 1.0 - Math.pow(1.0 - expy, effm);
        }
        return res;
    }

    /**
     * 计算均值与方差
     */
    private static double[] avevar(double[] y) {
        double sum = 0.0;
        int n = y.length;
        for (double yi : y) sum += yi;
        double ave = sum / n;

        double var = 0.0;
        for (double yi : y) var += SQR(yi - ave);
        var /= (n - 1); // 样本方差

        return new double[]{ave, var};
    }

    public static LocalState resultWithState(double[] x, double[] y, double ofac, double hifac) {
        // TODO Auto-generated method stub
        state = 0; // 初始化状态
        try {
            period(x, y, ofac, hifac);
        } catch (Exception e) {

        }
        return new LocalState(state);
    }

    /**
     * 示例主方法测试
     */
//    public static void main(String[] args) {
//        // 示例：生成一个带有噪声的正弦信号
//        int N = 100;
//        double[] x = new double[N];
//        double[] y = new double[N];
//        for (int i = 0; i < N; i++) {
//            x[i] = i + Math.random(); // 不规则时间点
//            y[i] = Math.sin(2 * Math.PI * x[i] / 5.0) + 0.2 * Math.random(); // 周期为5
//        }
//
//        double ofac = 4;
//        double hifac = 1;
//
//        Result result = period(x, y, ofac, hifac);
//        System.out.printf("最大功率对应频率: %.6f%n", result.px[result.jmax]);
//        System.out.printf("显著性概率: %.6f%n", result.prob);
//
//        LocalState ls = resultWithState(x, y, ofac, hifac);
//        System.out.println("当前状态: " + ls.getValue());
//    }
    public static void main(String[]args){
        System.out.println(state);
    }
}