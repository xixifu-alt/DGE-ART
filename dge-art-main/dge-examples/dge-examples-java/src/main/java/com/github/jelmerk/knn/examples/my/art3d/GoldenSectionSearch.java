package com.github.jelmerk.knn.examples.my.art3d;
import com.github.jelmerk.knn.examples.my.art3d.art3d.LocalState;
@FunctionalInterface
interface Function {
    double evaluate(double x);
}

public class GoldenSectionSearch {

    // �ƽ�ָ��
    private static final double R = 0.61803399;
    private static final double C = (1.0 - R);

    // Ĭ�Ϻ�����f(x) = (x-2)^2 + 1
    private static final Function DEFAULT_FUNCTION = x -> Math.pow(x - 2, 2) + 1;

    // Ĭ����������
    private static final double DEFAULT_TOLERANCE = 1e-6;

    // ״̬����
    private static int state = 0;

    /**
     * ʹ�ûƽ�ָ��һά������Ѱ�Һ�����Сֵ
     *
     * @param ax ��ʼ������˵�
     * @param bx ��ʼ�м�㣨������ ax �� cx ֮�䣩
     * @param cx ��ʼ�����Ҷ˵�
     * @return Result ���󣬰�����Сֵ��ͺ���ֵ
     */
    public static Result golden(double ax, double bx, double cx) {
        state = -1; // ��ʼ��״̬

        // �����Ϸ��Լ��
        if (ax >= cx || bx <= ax || bx >= cx) {
            state = 1;
            throw new IllegalArgumentException("Invalid interval: ax < bx < cx required");
        }

        double x0 = ax;
        double x3 = cx;

        double x1, x2;

        // ȷ�� x1 < x2
        if (Math.abs(cx - bx) > Math.abs(bx - ax)) {
            x1 = bx;
            x2 = bx + C * (cx - bx);
        } else {
            x2 = bx;
            x1 = bx - C * (bx - ax);
        }

        double f1, f2;
        try {
            f1 = DEFAULT_FUNCTION.evaluate(x1);
            f2 = DEFAULT_FUNCTION.evaluate(x2);
        } catch (Exception e) {
            state = 2;
            throw new RuntimeException("Function evaluation error", e);
        }

        while (Math.abs(x3 - x0) > DEFAULT_TOLERANCE * (Math.abs(x1) + Math.abs(x2))) {
        	state=3;
            if (f2 < f1) {
                x0 = x1;
                x1 = x2;
                x2 = R * x1 + C * x3;
                f1 = f2;
                try {
                    f2 = DEFAULT_FUNCTION.evaluate(x2);
                } catch (Exception e) {
                    state = 4;
                    throw new RuntimeException("Function evaluation error", e);
                }
            } else {
                x3 = x2;
                x2 = x1;
                x1 = R * x2 + C * x0;
                double f3 = f2;
                f2 = f1;
                f1 = f3;
                try {
                    f1 = DEFAULT_FUNCTION.evaluate(x1);
                } catch (Exception e) {
                    state = 5;
                    throw new RuntimeException("Function evaluation error", e);
                }
            }
        }

        // ���ظ�С���Ǹ�
        double xmin;
        double fmin;
        if (f1 < f2) {
        	state=6;
            xmin = x1;
            fmin = f1;
        } else {
        	state=7;
            xmin = x2;
            fmin = f2;
        }
        return new Result(xmin, fmin);
    }

    /**
     * ��ȡ��ǰ״̬��
     */
    public static int getState() {
        return state;
    }

    /**
     * ��װ������ࣺ������Сֵ��ͺ���ֵ
     */
    public static class Result {
        public final double xmin;
        public final double fmin;

        public Result(double xmin, double fmin) {
            this.xmin = xmin;
            this.fmin = fmin;
        }

        @Override
        public String toString() {
            return String.format("x = %.6f, f(x) = %.6f", xmin, fmin);
        }
    }

    /**
     * ��װ�ӿڣ����� golden �����ط�װ��״̬����
     */
    public static LocalState resultWithState(double ax, double bx, double cx) {
        try {
            golden(ax, bx, cx); // ʵ�ʵ��ú��ķ���
        } catch (Exception e) {
            // �쳣ʱ�����ú� state
        }
        return new LocalState(state);
    }

    /**
     * ����������
     */
//    public static void main(String[] args) {
//        double ax = 0.0;  // �������
//        double bx = 1.5;  // �м��
//        double cx = 5.0;  // �����յ�
//
//        GoldenSectionSearch.Result result = golden(ax, bx, cx);
//        System.out.println("Minimum: " + result);
//        System.out.println("��ǰ״̬: " + resultWithState(ax, bx, cx).getValue());
//    }
}