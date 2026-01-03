package com.github.jelmerk.knn.examples.my.art3d;
import com.github.jelmerk.knn.examples.dge.art.DGE_ART.LocalState;
public class TriSquareMutant3 {
    private static final double EPSILON = 1e-6;
    private static boolean isValidTriangle(double a, double b, double c) {
        //变异点1
        return (a + b >Math.abs(c-50)) && (a + c >Math.abs(b-50))&&(b + c >Math.abs(a-50));
    }
    private static int determineTriangleType(double a, double b, double c) {
        int match = 0;//变异点2
        if (nearlyEqual(a,b) && nearlyEqual(a,c)) {
            match = 3;
        } else if (nearlyEqual(a,b)) {
            match = 1;
        } else if (nearlyEqual(a,c)) {
            match = 2;
        } else if (nearlyEqual(b,c)){
            match = 1;
        }
        return match;
    }
    private static double calculateArea(double a, double b, double c, int match) {
        switch (match) {
            case 3:
                return Math.sqrt(3) * a * a / 4;
            case 1:
                if (nearlyEqual(a,b)) {
                    double h = 2 * Math.sqrt(a * a - (c / 2) * (c / 2));
                    return (c * h) / 2;
                } else {
                    double h = 2 * Math.sqrt(b * b - (a / 2) * (a / 2));
                    return (a * h) / 2;
                }
            case 2:
                double h = 2 * Math.sqrt(a * a - (b / 2) * (b / 2));
                return (b * h) / 2;
            default:
                double s = (a + b + c) / 2;
                return Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }
    }

    public static boolean result(double a, double b, double c) {
        if (!isValidTriangle(a, b, c)) {
            System.out.println("Not a triangle");
            return false;
        }
        int match = determineTriangleType(a, b, c);
        double area = calculateArea(a, b, c, match);

        switch (match) {
            case 3:
                System.out.println("此三角形是一个等边三角形");
                break;
            case 1:
            case 2:
                System.out.println("此三角形是一个等腰三角形");
                break;
            default:
                System.out.println("有异常");
        }

        System.out.printf("该三角形的面积: %.2f%n", area);
        return true;
    }
    private static boolean nearlyEqual(double x, double y) {
        return Math.abs(x - y) < EPSILON;
    }

    public static LocalState resultWithState(double a, double b, double c) {
        int match = -1;
        double areaValue=0;
        if (isValidTriangle(a, b, c)) {
            match = determineTriangleType(a, b, c);
            areaValue=calculateArea(a, b, c, match);
        }
        return new LocalState( match,areaValue);
    }

    public static void main(String[] args) {
        double a = 3.0;
        double b = 4.0;
        double c = 5.0;
        TriSquare.result(a, b, c);
    }
}
