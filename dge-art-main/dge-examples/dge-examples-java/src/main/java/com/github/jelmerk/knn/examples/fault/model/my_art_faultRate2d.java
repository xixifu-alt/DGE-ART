package com.github.jelmerk.knn.examples.fault.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import com.github.jelmerk.knn.examples.my.art2d.Function;
import com.github.jelmerk.knn.examples.my.art2d.FunctionMutant2;
import com.github.jelmerk.knn.examples.my.art2d.my_art.LocalState;

public class my_art_faultRate2d {
    private static final int TOTAL_TESTS = 1000;
    private static final int REPEAT_TIMES = 100;
    private static final double MIN = -5000;
    private static final double MAX = 5000;

    public static void main(String[] args) {
        String filePath = "C:\\Users\\xixifu\\Desktop\\Test2\\Test\\test2d\\theta_results.txt";
        double totalTheta = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (int run = 1; run <= REPEAT_TIMES; run++) {
                int failureCount = 0;
                Random rand = new Random();

                for (int i = 0; i < TOTAL_TESTS; i++) {
                    double a = MIN + (MAX - MIN) * rand.nextDouble();
                    double b = MIN + (MAX - MIN) * rand.nextDouble();

                    LocalState originalState = Function.resultWithState(a, b);
                    LocalState mutantState = FunctionMutant2.resultWithState(a, b);

                    if (!originalState.equals(mutantState)) {
                        failureCount++;
                    }
                }

                double theta = (double) failureCount / TOTAL_TESTS;
                totalTheta += theta;
                writer.write(String.format("Run %d: θ = %.6f%n", run, theta));
            }

            double averageTheta = totalTheta / REPEAT_TIMES;
            writer.write(String.format("平均 θ 值 = %.6f%n", averageTheta));

            System.out.println("执行完成，结果已写入文件：" + filePath);

        } catch (IOException e) {
            System.err.println("写入文件时发生错误: " + e.getMessage());
        }
    }
}
