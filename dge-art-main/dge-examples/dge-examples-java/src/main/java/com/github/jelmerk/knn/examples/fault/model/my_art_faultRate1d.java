//package com.github.jelmerk.knn.examples.fault.model;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Random;
//import com.github.jelmerk.knn.examples.my.art1d_bessj0.BesselFunction;
//import com.github.jelmerk.knn.examples.my.art1d_bessj0.BesselFunctionMutant1;
//import com.github.jelmerk.knn.examples.my.art1d_bessj0.BesselFunctionMutant2;
//import com.github.jelmerk.knn.examples.my.art1d_bessj0.BesselFunctionMutant3;
////import com.github.jelmerk.knn.examples.my.art1d_bessj0.my_art1d_bessj0.LocalState;
//
//import com.github.jelmerk.knn.examples.my.art1d_erfcc.ErfccFunction;
//import com.github.jelmerk.knn.examples.my.art1d_erfcc.ErfccFunctionMutant1;
//import com.github.jelmerk.knn.examples.my.art1d_erfcc.ErfccFunctionMutant2;
//import com.github.jelmerk.knn.examples.my.art1d_erfcc.ErfccFunctionMutant3;
////import com.github.jelmerk.knn.examples.my.art1d_erfcc.my_art1d_erfcc.LocalState;
//
//import com.github.jelmerk.knn.examples.my.art1d_airy.AiryFunction;
//import com.github.jelmerk.knn.examples.my.art1d_airy.AiryFunctionMutant1;
//import com.github.jelmerk.knn.examples.my.art1d_airy.AiryFunctionMutant2;
//import com.github.jelmerk.knn.examples.my.art1d_airy.AiryFunctionMutant3;
////import com.github.jelmerk.knn.examples.my.art1d_airy.my_art1d_airy.LocalState;
//
//
//public class my_art_faultRate1d {
//    private static final int TOTAL_TESTS = 1000;
//    private static final int REPEAT_TIMES = 100;
//    private static final double MIN = -5000;
//    private static final double MAX = 5000;
//
//    public static void main(String[] args) {
//        String filePath = "C:\\Users\\xixifu\\Desktop\\Test2\\Test\\test1d_airy\\theta_results3.txt";
//        double totalTheta = 0;
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//
//            for (int run = 1; run <= REPEAT_TIMES; run++) {
//                int failureCount = 0;
//                Random rand = new Random();
//
//                for (int i = 0; i < TOTAL_TESTS; i++) {
//                    double a = MIN + (MAX - MIN) * rand.nextDouble();
//
//                    LocalState originalState = AiryFunction.resultWithState(a);
//                    LocalState mutantState = AiryFunctionMutant3.resultWithState(a);
//
//                    if (!originalState.equals(mutantState)) {
//                        failureCount++;
//                    }
//                }
//
//                double theta = (double) failureCount / TOTAL_TESTS;
//                totalTheta += theta;
//                writer.write(String.format("Run %d: θ = %.6f%n", run, theta));
//            }
//
//            double averageTheta = totalTheta / REPEAT_TIMES;
//            writer.write(String.format("平均 θ 值 = %.6f%n", averageTheta));
//
//            System.out.println("执行完成，结果已写入文件：" + filePath);
//
//        } catch (IOException e) {
//            System.err.println("写入文件时发生错误: " + e.getMessage());
//        }
//    }
//}
