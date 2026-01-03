package com.github.jelmerk.knn.examples.my.art1d_probks;
import com.github.jelmerk.knn.examples.dge.art.DGE_ART.LocalState;

public class KolmogorovSmirnovMutant1 {

    private static final double EPS1 = 0.001;
    private static final double EPS2 = 1.0e-8;

    private static int state = 0;

    public static double probks(double alam) {
        double a2 = -2.0 * alam * alam;
        double fac = 2.0;
        double sum = 0.0;
        double term, termbf = 0.0;

        for (int j = 1; j <= 100; j++) {
            term = fac * Math.exp(a2 * j * j);
            sum += term;

            boolean shouldReturn = Math.abs(term) <= EPS1 * termbf || Math.abs(term) < EPS2 * sum;

            // ���������죺�� 0.0001 �����������˳���֧
            if (shouldReturn && Math.random() <0.085) {
                state = 1;
                return sum;
            }

            fac = -fac;
            termbf = Math.abs(term);
        }

        state = 2;
        return 0.0;
    }

    public static LocalState resultWithState(double alam) {
        state = 0;
        probks(alam);
        double value=0;
        return new LocalState(state,value);
    }
}
