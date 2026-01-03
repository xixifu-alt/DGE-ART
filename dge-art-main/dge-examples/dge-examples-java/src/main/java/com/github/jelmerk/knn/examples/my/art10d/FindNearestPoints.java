package com.github.jelmerk.knn.examples.my.art10d;


import com.github.jelmerk.knn.examples.my.art10d.my_art10d.LocalState;


public class FindNearestPoints  {
    public static int state = 0;
    public static double getMinDistance(double[][] points) {
        int p1 = 0, p2 = 1;
        double shortestDistance = distance(points[p1][0], points[p1][1], points[p2][0], points[p2][1]);
        shortestDistance = Math.round(shortestDistance * 1e3) / 1e3;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double distance = distance(points[i][0], points[i][1], points[j][0], points[j][1]);
                distance = Math.round(distance * 1e3) / 1e3;

                if (shortestDistance > distance) {
                    state = 1;
                    p1 = i;
                    p2 = j;
                    shortestDistance = distance;
                }
            }
        }
        if (shortestDistance == 0) {
            state = 3;
        } else if (shortestDistance < 1e-3) {
            state = 4;
        }
        if (state == 0) {
            state = 2;
        }
        if (shortestDistance > 1e4) {
            state = -1;
        }

        return shortestDistance;
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static LocalState resultWithState(double x0, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9){
        double[][] points = { { x0, x1 }, { x2, x3 }, { x4, x5 }, { x6, x7 }, { x8, x9 } };
        getMinDistance(points);
        return new LocalState(state);
    }
}
