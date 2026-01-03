package com.github.jelmerk.knn.examples.fault.model;

import com.github.jelmerk.knn.examples.my.art10d.FindNearestPoints;
import com.github.jelmerk.knn.examples.my.art10d.FindNearestPointsMutant1;
import com.github.jelmerk.knn.examples.my.art10d.FindNearestPointsMutant2;
import com.github.jelmerk.knn.examples.my.art10d.FindNearestPointsMutant3;
import com.github.jelmerk.knn.examples.my.art10d.my_art10d;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class my_art_faultRate10d {
    private static final int TOTAL_TESTS = 1000;
    private static final int REPEAT_TIMES = 1000;
    private static final double MIN = -5000;
    private static final double MAX = 5000;

    public static void main(String[] args) {
        String filePath = "C:\\Users\\xixifu\\Desktop\\Test2\\Test\\test10d\\theta_results3.txt";
        double totalTheta = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (int run = 1; run <= REPEAT_TIMES; run++) {
                int failureCount = 0;
                Random rand = new Random();

                for (int i = 0; i < TOTAL_TESTS; i++) {
                    double x1 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x2 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x3 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x4 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x5 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x6 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x7 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x8 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x9 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x10 = MIN + (MAX - MIN) * rand.nextDouble();

                    my_art10d.LocalState originalState = FindNearestPoints.resultWithState(x1,x2,x3,x4,x5,x6,x7,x8,x9,x10);
                    my_art10d.LocalState mutantState = FindNearestPointsMutant1.resultWithState(x1,x2,x3,x4,x5,x6,x7,x8,x9,x10);

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

