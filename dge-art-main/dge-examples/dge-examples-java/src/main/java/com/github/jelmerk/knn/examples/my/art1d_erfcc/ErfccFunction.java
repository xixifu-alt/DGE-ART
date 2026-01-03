package com.github.jelmerk.knn.examples.my.art1d_erfcc;

import com.github.jelmerk.knn.examples.my.art1d_erfcc.my_art1d_erfcc.LocalState;

public class ErfccFunction {
    public static int state = 0;

    public static double erfcc(double x) {
        double z = Math.abs(x);
        double t = 1.0 / (1.0 + 0.5 * z);

        double ans = t * Math.exp(-z * z - 1.26551223 +
                t * (1.00002368 +
                        t * (0.37409196 +
                                t * (0.09678418 +
                                        t * (-0.18628806 +
                                                t * (0.27886807 +
                                                        t * (-1.13520398 +
                                                                t * (1.48851587 +
                                                                        t * (-0.82215223 +
                                                                                t * 0.17087277)))))))));
       if(x>=0.0){
           state=1;
           return ans;
       }else{
           state=2;
           return 2.0-ans;
       }
    }

    public static LocalState resultWithState(double a) {
        state = 0;
        erfcc(a);
        return new LocalState(state);
    }
}
