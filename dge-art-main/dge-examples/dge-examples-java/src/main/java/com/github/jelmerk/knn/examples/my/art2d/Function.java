package com.github.jelmerk.knn.examples.my.art2d;
import com.github.jelmerk.knn.examples.my.art2d.my_art.LocalState;
public class Function {
    static int state=0;
    public static void main(String[] args) {
        double a = 5.7;
        double b = 3.2;
        analyzeControlFlow(a, b);
    }
    public static double analyzeControlFlow(double a, double b) {
        if (a > b) {
            System.out.println("进入分支：a > b");
            double sum = 0.0;
            for (int i = 1; i <= 5; i++) {
                sum += a * i;
            }
            if (((int) sum) % 2 == 0) {
                state = 1;
                System.out.println("sum为偶数");
            } else {
                System.out.println("sum为奇数");
            }
        System.out.println("总和为: " + sum);
        return sum;
        }
        else if (a < b) {
            System.out.println("进入分支：a < b");
            double count = 0;
            while (count < 3.0) {
                if (count == 1.0) {
                    state=2;
                    System.out.println("这是第一次循环");
                } else {
                    System.out.println("这是第 " + (count + 1.0) + " 次循环");
                }
                count++;
            }
            return count;
        }
        else {
            System.out.println("进入分支：a == b");
            double result=0;
            try {
                result = 1.0 / (a - b);
                System.out.println("计算结果: " + result);

            } catch (ArithmeticException e) {
                System.out.println("捕获到异常: 除以零错误");
            }
            state=3;
            return result;
        }
    }
    public static LocalState resultWithState(double a, double b) {
        analyzeControlFlow(a, b);
        return new LocalState(state);
    }
}
