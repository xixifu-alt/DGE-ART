package com.github.jelmerk.knn.examples.fault.model;


import com.github.jelmerk.knn.examples.my.art_trisquare3d.TriSquare;
import com.github.jelmerk.knn.examples.my.art_trisquare3d.TriSquareMutant1;
import com.github.jelmerk.knn.examples.my.art_trisquare3d.art3d.LocalState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class my_art_faultRate3d {
    private static final int TOTAL_TESTS = 1000;
    private static final int REPEAT_TIMES = 1000;
    private static final double MIN = 0;
    private static final double MAX = 5000;

    public static void main(String[] args) {
        String filePath = "C:\\Users\\xixifu\\Desktop\\Test2\\Test\\test3d\\theta_results1.txt";
        double totalTheta = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (int run = 1; run <= REPEAT_TIMES; run++) {
                int failureCount = 0;
                Random rand = new Random();

                for (int i = 0; i < TOTAL_TESTS; i++) {
                    double x1 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x2 = MIN + (MAX - MIN) * rand.nextDouble();
                    double x3 = MIN + (MAX - MIN) * rand.nextDouble();


                    LocalState originalState = TriSquare.resultWithState(x1,x2,x3);
                    LocalState mutantState = TriSquareMutant1.resultWithState(x1,x2,x3);

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
