package com.github.jelmerk.knn.examples.my.art1d_probks;
import com.github.jelmerk.knn.examples.dge.art.DGE_ART.LocalState;

public class KolmogorovSmirnov {

    // ���徫�ȿ��Ƴ���
    private static final double EPS1 = 0.001;
    private static final double EPS2 = 1.0e-8;

    // ״̬����
    private static int state = 0;

    /**
     * ���� Kolmogorov-Smirnov �ֲ����ۻ����ʽ���ֵ
     */
    public static double probks(double alam) {
        double a2 = -2.0 * alam * alam;
        double fac = 2.0;
        double sum = 0.0;
        double term, termbf = 0.0;

        for (int j = 1; j <= 100; j++) {
            term = fac * Math.exp(a2 * j * j);
            sum += term;

            if (Math.abs(term) <= EPS1 * termbf || Math.abs(term) < EPS2 * sum) {
                state = 1; // ��ʾ��ǰ�����˳�
                return sum;
            }

            fac = -fac;
            termbf = Math.abs(term);
        }

        state = 2; // ��ʾ���������δ�����������ϼ��ٷ�����
        return 0.0;
    }

    /**
     * ��װ probks ���ò�����״̬����
     */
    public static LocalState resultWithState(double alam) {
        probks(alam);
        double value=0;
        return new LocalState(state,value);
    }

    /**
     * ����������
     */
//    public static void main(String[] args) {
//        double alam1 = 1.0;
//        double alam2 = 1.36;
//
//        System.out.printf("probks(%.4f) = %.8f%n", alam1, probks(alam1));
//        System.out.printf("probks(%.4f) = %.8f%n", alam2, probks(alam2));
//
//        // ʹ�� resultWithState ��ȡ״̬
//        LocalState state1 = resultWithState(1.0);
//        LocalState state2 = resultWithState(1.36);
//    }
}