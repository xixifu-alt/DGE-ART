package com.github.jelmerk.knn.examples.my.art3d;
import com.github.jelmerk.knn.examples.my.art3d.art3d.LocalState;
/**
 * 鍏宠仈鍕掕寰峰椤瑰紡 P_l^m(x) 鐨勫疄鐜帮紙double 鐗堟湰锛�
 */
public class AssociatedLegendre {

    // 鐘舵�佸彉閲�
    private static int state = 0;

    /**
     * 璁＄畻鍏宠仈鍕掕寰峰椤瑰紡 P_l^m(x)
     *
     * @param l 闃舵暟锛坙 >= 0锛�
     * @param m 娆℃暟锛�0 <= m <= l锛�
     * @param x 鑷彉閲忥紙|x| <= 1.0锛�
     * @return 杩斿洖 P_l^m(x) 鐨勫��
     */
    public static double plgndr(double l, double m, double x) {
        state = 0; // 鍒濆鍖栫姸鎬�

        // 鍙傛暟鍚堟硶鎬ф鏌�
        if (m < 0 || m > l || Math.abs(x) > 1.0) {
            state = -1;
            throw new IllegalArgumentException("Bad arguments in routine PLGNDR");
        }

        // 寮哄埗杞崲涓烘暣鏁帮紙鍏佽娴偣杈撳叆锛屼絾蹇呴』鏄暣鏁板�硷級
        int li = (int) l;
        int mi = (int) m;

        if (li != l || mi != m) {
//            state = -1;
            throw new IllegalArgumentException("l and m must be integer values.");
        }

        double pmm = 1.0;

        if (mi > 0) {
//        	state=-9;
            double somx2 = Math.sqrt((1.0 - x) * (1.0 + x));
            double fact = 1.0;
            for (int i = 1; i <= mi; i++) {
                pmm *= -fact * somx2;
                fact += 2.0;
            }
        }

        if (li == mi) {
//        	state=1;
            return pmm;
        } else {
            double pmmp1 = x * (2 * mi + 1) * pmm;

            if (li == mi + 1) {
//            	state=1;
                return pmmp1;
            } else {
                double pll = 0.0;
                for (int ll = mi + 2; ll <= li; ll++) {
                    pll = (x * (2 * ll - 1) * pmmp1 - (ll + mi - 1) * pmm) / (ll - mi);
                    if (Double.isNaN(pll)) {
                        return Double.NaN;
                    }
                    pmm = pmmp1;
                    pmmp1 = pll;
                }
                return pll;
            }
        }
    }

    /**
     * 鍖呰鎺ュ彛锛氳皟鐢� plgndr 骞惰繑鍥炲皝瑁呯殑鐘舵�佸璞�
     */
    public static LocalState resultWithState(double l, double m, double x) {
        try {
            plgndr(l, m, x); // 瀹為檯璋冪敤鏍稿績鏂规硶
        } catch (Exception e) {
            // 寮傚父鏃跺凡璁剧疆濂� state
        }
        return new LocalState(state);
    }

    /**
     * 娴嬭瘯涓诲嚱鏁�
     */
//    public static void main(String[] args) {
//        double l = 2.0;
//        double m = 0.0;
//        double x = 0.5;
//
//        System.out.printf("P_%.0f^%.0f(%.2f) = %.6f%n", l, m, x, plgndr(l, m, x));
//        System.out.println("褰撳墠鐘舵��: " + resultWithState(l, m, x).getValue());
//
//        l = 2.0;
//        m = 1.0;
//        x = 0.5;
//        System.out.printf("P_%.0f^%.0f(%.2f) = %.6f%n", l, m, x, plgndr(l, m, x));
//        System.out.println("褰撳墠鐘舵��: " + resultWithState(l, m, x).getValue());
//    }
}