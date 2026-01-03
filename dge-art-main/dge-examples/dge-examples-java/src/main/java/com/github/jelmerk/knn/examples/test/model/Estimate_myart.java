package com.github.jelmerk.knn.examples.test.model;
import com.github.jelmerk.knn.examples.dge.art.DGE_ART;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Estimate_myart {
    public static double averageFMeasureOverRuns(int runs, double area) {
        int totalTests = 0;
        String fileName = "E:\\hre_efficience\\my_art_f_measure_result_4d_period_mutant1.txt";
        String testCasesFileName = "E:\\hre_efficience\\my_art_test_cases_4d_period_mutant1.txt";
        double faultDensity = 1.0 /area/100;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
             BufferedWriter testCasesWriter = new BufferedWriter(new FileWriter(testCasesFileName))){
            // BufferedWriter fMeasureValuesWriter = new BufferedWriter(new FileWriter(fMeasureValuesFileName)))
            
            for (int i = 0; i < runs; i++) {
                System.out.println("开始第 " + (i + 1) + " 次运行 (共 " + runs + " 次)...");
                List<String> testCases = DGE_ART.measureFMeasureWithTestCases();
                int testsNeeded = testCases.size();
                totalTests += testsNeeded;
                
                // 计算当前运行的F-measure值
                double currentFMeasure = (double) testsNeeded;
                
                writer.write("第 " + (i + 1) + " 次：第一次检测到缺陷所需要的用例数 = " + testsNeeded + "\n");
                System.out.println("第 " + (i + 1) + " 次运行完成，需要 " + testsNeeded + " 个测试用例");

                
                // 保存测试用例到文件
                testCasesWriter.write("=== 第 " + (i + 1) + " 次运行测试用例 ===\n");
                for (String testCase : testCases) {
                    testCasesWriter.write(testCase + "\n");
                }
                testCasesWriter.write("\n");
            }

        double fmeasure = (double) totalTests / runs;
            double fratio = fmeasure / faultDensity;
            writer.write("F-measure:（" + runs + " 次运行）: " + fmeasure + "\n");
            writer.write("F-ratio: " + String.format("%.4f", fratio) + "\n");

            
            System.out.println("所有运行结果已写入 " + fileName);
            System.out.println("所有测试用例已写入 " + testCasesFileName);
            return fmeasure;
        } catch (IOException e) {
            System.err.println("写入文件出错: " + e.getMessage());
            return -1;
        }
    }
    
    public static void runEfficiencyTest() {
        System.out.println("=====================================================");
        System.out.println("开始运行HRE_ART算法效率测试");
        System.out.println("=====================================================");
        System.out.println("测试内容: 统计生成不同规模测试用例所需的总时间");
        System.out.println("测试规模: 从100到20000个测试用例");
        System.out.println("重复次数: 每个规模进行3次热身运行，然后1000次正式测量");
        System.out.println("结果说明: 输出的是生成N个测试用例的总时间，不是单个测试用例的时间");
        System.out.println("数据保存: 时间和测试用例将分别保存到单独的文件中");
        System.out.println("-----------------------------------------------------");
        System.out.println("开始测试，请耐心等待...");
        DGE_ART.measureEfficiencyMultiDim();
        System.out.println("=====================================================");
        System.out.println("效率测试完成");
        System.out.println("所有数据已保存至 C:\\Users\\xixifu\\Desktop\\Test2\\Test\\test_Efficiency目录");
        System.out.println("=====================================================");
    }

    public static void main(String[] args) {
      averageFMeasureOverRuns(5000,0.000173);//mutant1:0.000120,0.011940,0.050370,0.000410,0.000173
//       averageFMeasureOverRuns(5000,0.001008);//mutant2:0.001050,0.001810,0.004840,0.001008
//       averageFMeasureOverRuns(10,0.038552);//mutant3:0.010320,0.000130,0.050370, 0.038552
//       runEfficiencyTest();
//        int faultCount = art.measureFaultDetectionCount(1000);
//        System.out.println(faultCount);
    }
}
