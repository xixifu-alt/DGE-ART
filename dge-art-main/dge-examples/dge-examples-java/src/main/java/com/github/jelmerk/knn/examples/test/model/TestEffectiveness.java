package com.github.jelmerk.knn.examples.test.model;

import com.github.jelmerk.knn.examples.fault.model.FaultZone;
import com.github.jelmerk.knn.examples.fault.model.FaultZone_Block;
import com.github.jelmerk.knn.examples.fault.model.FaultZone_Point_Square;
import com.github.jelmerk.knn.examples.fault.model.FaultZone_Strip;
import com.github.jelmerk.knn.examples.fscs.art.FSCS_ART;
import com.github.jelmerk.knn.examples.hnsw.art.HNSW_ART;
import com.github.jelmerk.knn.examples.kdfc.art.KDFC_ART;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

public class TestEffectiveness {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        ArrayList<Float> area = new ArrayList<>();
        ArrayList<int[][]> domains = new ArrayList<>();
        //10维度的area
//        area.add(0.025252f);
//        area.add(0.002687f);
//        area.add(0.000276f);
        //3维度的area
//        area.add(0.014902f);
//       area.add(0.001517f);
//        area.add(0.000139f);
        //1维度besssj0的area
//        area.add(0.010320f);
//        area.add(0.001050f);
//        area.add(0.000120f);
        //1维度erfcc的area
//        area.add(0.011940f);
//        area.add(0.001810f);
//        area.add(0.000130f);
        //1维度airy的area
        area.add(0.050370f);
//        area.add(0.004840f);
//        area.add(0.000410f);
        //4维度period的area
//        area.add(0.038552f);
//        area.add(0.001008f);
//        area.add(0.000173f);

        int[][] bd1_airy={{-5000,5000}};
        int[][] bd2 = {{-5000, 5000}, {-5000, 5000}};
        int[][] bd3 = {{1, 5000}, {1, 5000}, {1, 5000}};
        int[][] bd4 = {{-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}};
        int[][] bd4_period={{-10000,10000},{-10000,10000},{-10000,10000},{-10000,10000}};
        int[][] bd5 = {{-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}};
        int[][] bd10 = {{-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000},
                {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}};
        int[][] bd15 = {{-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000},
                {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000},
                {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}, {-5000, 5000}};
        domains.add(bd1_airy);
//        domains.add(bd2);
 //       domains.add(bd4_period);
//        domains.add(bd3);
//        domains.add(bd4);
//        domains.add(bd5);
//       domains.add(bd10);
//        domains.add(bd15);

        for (int[][] bd : domains) {
            System.out.println("\n------DIMENSION:\t" + bd.length + "D--------:");
            for (float a : area) {
                System.out.println("Dimensionality:" + bd.length + "\t" + "theta:" + a);
                System.out.println("FSCS-ART | Limbal-KDFC | SWFC-ART");
//                String s1 = bd.length + "d-Block-" + a + ".txt";
                String s2 = bd.length + "d-Strip-" + a + ".txt";
//                String s3 = bd.length + "d-Point-" + a + ".txt";
//                fixRateTest("C:/Users/xixifu/Desktop/Test2/Test/test3d/" + s1, a, bd, 1);
                fixRateTest("E:/test_effectiveness/" + s2, a, bd, 2);
//                fixRateTest("C:/Users/xixifu/Desktop/Test2/Test/test3d/" + s3, a, bd, 3);
            }

        }


    }

    public static long count_lines_text_java8(String file) throws IOException, URISyntaxException {

        long numberOfLines;
        try (Stream<String> s = Files.lines(Paths.get(file),
                Charset.defaultCharset())) {

            numberOfLines = s.count();

        } catch (IOException e) {
            throw e;
        }
        return numberOfLines;
    }


    public static void fixRateTest(String file, float area, int[][] bd, int t) throws IOException, InterruptedException, URISyntaxException {
        FSCS_ART fscs;
        KDFC_ART kdfc;
        HNSW_ART hnsw;
        FaultZone fzb;
        File f1 = new File(file);
        f1.createNewFile();
        PrintWriter out = new PrintWriter(new FileWriter(f1));

        // 创建三个测试用例保存文件
        String fscsTestCasesFileName = file.replace(".txt", "_FSCS_test_1d_cases.txt");
        String kdfcTestCasesFileName = file.replace(".txt", "_KDFC_test_1d_cases.txt");
        String hnswTestCasesFileName = file.replace(".txt", "_HNSW_test_1d_cases.txt");
        
        File f2 = new File(fscsTestCasesFileName);
        f2.createNewFile();
        PrintWriter fscsTestCasesOut = new PrintWriter(new FileWriter(f2));
        
        File f3 = new File(kdfcTestCasesFileName);
        f3.createNewFile();
        PrintWriter kdfcTestCasesOut = new PrintWriter(new FileWriter(f3));
        
        File f4 = new File(hnswTestCasesFileName);
        f4.createNewFile();
        PrintWriter hnswTestCasesOut = new PrintWriter(new FileWriter(f4));

        int[] backNum = new int[100 * (int) (1 / area)];
        backNum[0] = 1;
        backNum[1] = 1;
        double d = bd.length;
        for (int i = 2; i < backNum.length; i++) {
            backNum[i] = (int) Math.ceil(1 / 2.0 * Math.pow((d + 1 / d), 2) * (Math.log(i) / Math.log(2)));
        }

        double num1 = 0, num2 = 0, num3 = 0;
        int experimentCount = 0;

        for (int i = 0; i < 500; i++) {
            if (t == 1) {
                fzb = new FaultZone_Block(bd, area);
            } else if (t == 2) {
                fzb = new FaultZone_Strip(bd, area, 0.9);
            } else {
                fzb = new FaultZone_Point_Square(bd, area);
            }
            for (int j = 0; j < 10; j++) {
                experimentCount++;
                
                // FSCS-ART测试
                fscs = new FSCS_ART(10);
                int num = fscs.testFscsArt_Effectiveness(bd, fzb);
                num1 += num;
                out.print(num + "\t");

                // KDFC-ART测试
                kdfc = new KDFC_ART(bd);
                kdfc.testLimBalKDFC_Effectiveness(fzb, backNum);
                num2 += kdfc.size;
                out.print(kdfc.size + "\t");

                // HNSW-ART测试
                hnsw = new HNSW_ART(10);
                int hnsw2_num = hnsw.testHnswArt_Effectiveness(bd, fzb, 2);
                num3 += hnsw2_num;
                out.print(hnsw2_num + "\t");
                out.println();
                out.flush();
                
                // 保存FSCS-ART测试用例到文件
                fscsTestCasesOut.write("=== 第 " + experimentCount + " 次实验 ===\n");
                java.util.List<String> fscsTestCases = fscs.testFscsArt_EffectivenessWithTestCases(bd, fzb);
                fscsTestCasesOut.write("FSCS-ART: " + num + " 个测试用例\n");
                for (String testCase : fscsTestCases) {
                    fscsTestCasesOut.write(testCase + "\n");
                }
                fscsTestCasesOut.write("----------------------------------------\n");
                fscsTestCasesOut.flush();
                
                // 保存KDFC-ART测试用例到文件
                kdfcTestCasesOut.write("=== 第 " + experimentCount + " 次实验 ===\n");
                java.util.List<String> kdfcTestCases = kdfc.testLimBalKDFC_EffectivenessWithTestCases(fzb, backNum);
                kdfcTestCasesOut.write("KDFC-ART: " + kdfc.size + " 个测试用例\n");
                for (String testCase : kdfcTestCases) {
                    kdfcTestCasesOut.write(testCase + "\n");
                }
                kdfcTestCasesOut.write("----------------------------------------\n");
                kdfcTestCasesOut.flush();
                
                // 保存HNSW-ART测试用例到文件
                hnswTestCasesOut.write("=== 第 " + experimentCount + " 次实验 ===\n");
                java.util.List<String> hnswTestCases = hnsw.testHnswArt_EffectivenessWithTestCases(bd, fzb, 2);
                hnswTestCasesOut.write("HNSW-ART: " + hnsw2_num + " 个测试用例\n");
                for (String testCase : hnswTestCases) {
                    hnswTestCasesOut.write(testCase + "\n");
                }
                hnswTestCasesOut.write("----------------------------------------\n");
                hnswTestCasesOut.flush();
            }
        }
        double totalRuns =5000.0;
        double avg1 = num1 / totalRuns;
        double avg2 = num2 / totalRuns;
        double avg3 = num3 / totalRuns;
        double s = 1 / area / 100;
        double fm1 = avg1 / s;
        double fm2 = avg2 / s;
        double fm3 = avg3 / s;
        out.println("F-measure:\t" +
                new DecimalFormat("0.0000").format(avg1) + "\t" +
                new DecimalFormat("0.0000").format(avg2) + "\t" +
                new DecimalFormat("0.0000").format(avg3));
        out.println("F-ratio:\t" +
                new DecimalFormat("0.0000").format(fm1) + "\t" +
                new DecimalFormat("0.0000").format(fm2) + "\t" +
                new DecimalFormat("0.0000").format(fm3));
        System.out.println("F-measure:\t" +
                new DecimalFormat("0.0000").format(avg1) + "\t" +
                new DecimalFormat("0.0000").format(avg2) + "\t" +
                new DecimalFormat("0.0000").format(avg3));
        System.out.println("F-ratio:\t" +
                new DecimalFormat("0.0000").format(fm1) + "\t" +
                new DecimalFormat("0.0000").format(fm2) + "\t" +
                new DecimalFormat("0.0000").format(fm3));

        out.close();
        fscsTestCasesOut.close();
        kdfcTestCasesOut.close();
        hnswTestCasesOut.close();
        
        System.out.println("FSCS-ART测试用例已保存到: " + fscsTestCasesFileName);
        System.out.println("KDFC-ART测试用例已保存到: " + kdfcTestCasesFileName);
        System.out.println("HNSW-ART测试用例已保存到: " + hnswTestCasesFileName);
    }

}