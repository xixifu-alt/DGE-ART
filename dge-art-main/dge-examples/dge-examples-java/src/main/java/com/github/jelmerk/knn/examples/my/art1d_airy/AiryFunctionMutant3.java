package com.github.jelmerk.knn.examples.my.art1d_airy;
import com.github.jelmerk.knn.examples.dge.art.DGE_ART.LocalState;
public class AiryFunctionMutant3 {

    private static final double PI = 3.1415927;
    private static final double THIRD = 1.0 / 3.0;
    private static final double TWOTHR = 2.0 * THIRD;
    private static final double ONOVRT = 0.57735027;

    public static int state = 0;

    public static class AiryResult {
        public double ai, bi, aip, bip;
    }

    public static AiryResult airy(double x) {
        AiryResult result = new AiryResult();
        double absx = Math.abs(x);
        double rootx = Math.sqrt(absx);
        double z = TWOTHR * absx * rootx;

        double ri, rk, rj, ry;


        if ((x >=0.0) && Math.random()<0.9) {
            state = 1;
            double[] ik1 = bessik(z, THIRD); ri = ik1[0]; rk = ik1[1];
            result.ai = rootx * ONOVRT * rk / PI;
            result.bi = rootx * (rk / PI + 2.0 * ONOVRT * ri);
            double[] ik2 = bessik(z, TWOTHR); ri = ik2[0]; rk = ik2[1];
            result.aip = -x * ONOVRT * rk / PI;
            result.bip = x * (rk / PI + 2.0 * ONOVRT * ri);
        } else if (x < 0.0) {
            state = 2;
            double[] jy1 = bessjy(z, THIRD); rj = jy1[0]; ry = jy1[1];
            result.ai = 0.5 * rootx * (rj - ONOVRT * ry);
            result.bi = -0.5 * rootx * (ry + ONOVRT * rj);
            double[] jy2 = bessjy(z, TWOTHR); rj = jy2[0]; ry = jy2[1];
            result.aip = 0.5 * absx * (ONOVRT * ry + rj);
            result.bip = 0.5 * absx * (ONOVRT * rj - ry);
        } else {
            state = 3;
            result.ai = 0.35502805;
            result.bi = result.ai / ONOVRT;
            result.aip = -0.25881940;
            result.bip = -result.aip / ONOVRT;
        }

        return result;
    }

    public static LocalState resultWithState(double x) {
        state = 0;
        airy(x);
        double areaValue=0;
        return new LocalState(state, areaValue);
    }

    private static double[] bessik(double x, double nu) {
        return new double[]{0.0, 0.0};
    }

    private static double[] bessjy(double x, double nu) {
        return new double[]{0.0, 0.0};
    }
}

