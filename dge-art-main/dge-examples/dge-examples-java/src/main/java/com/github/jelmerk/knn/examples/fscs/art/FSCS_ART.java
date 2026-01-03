package com.github.jelmerk.knn.examples.fscs.art;

import com.github.jelmerk.knn.examples.auxiliary.model.Point;
import com.github.jelmerk.knn.examples.fault.model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FSCS_ART {
    public int candNum = 10; // 候选测试用例数量
    public int[][] inputDomain;

    public FSCS_ART() {
        candNum = 10;
    }

    public FSCS_ART(int n) {
        candNum = n;
    }

    public int findFarestCandidate(Point[] tcP, int size, Point[] candP)//从候选测试用例中选择与已生成测试用例集最"远"的一个（最小距离最大）作为最终测试用例。
    {
        double[] dist = new double[candP.length];
        double tempDist, farestDist = 0;
        int farestIndex = 0;

        for (int i = 0; i < candP.length; i++) {
            dist[i] = Point.getDistance(candP[i], tcP[0]);

            for (int j = 1; j < size; j++) {
                tempDist = Point.getDistance(candP[i], tcP[j]);
                if (tempDist < dist[i])
                    dist[i] = tempDist;
            }

            if (i == 0) {
                farestDist = dist[0];
                farestIndex = 0;
            } else {
                if (farestDist < dist[i]) {
                    farestDist = dist[i];
                    farestIndex = i;
                }

            }
        }
        return farestIndex;
    }

    public int testFscsArt_Effectiveness(int[][] bound, FaultZone fzb) {
        int generatedNum = 0;
        int maxTry = (int) (30 / fzb.theta);
        int selected;
        Point[] tcP = new Point[maxTry + 2];
        Point[] candP = new Point[candNum];
        tcP[0] = Point.generateRandP(bound);  // first test case
        generatedNum++; // increasing f-measure without testing first test case
        do {
            for (int i = 0; i < candNum; i++) {
                candP[i] = Point.generateRandP(bound);
            }
            selected = findFarestCandidate(tcP, generatedNum, candP);
            tcP[generatedNum] = candP[selected];
            generatedNum++;
            if (fzb.findTarget(tcP[generatedNum - 1])) {//若当前测试用例触中 FaultZone，立即停止；
                break;
            }
        } while (generatedNum < maxTry);
        return generatedNum;
    }////即成功检测出缺陷所需的测试用例数量。随机生成一个初始测试用例，然后生成一系列候选测试用例计算距离，选择距离最大值的那个候选测试用例然后判断是否触发缺陷区。

    public java.util.List<String> testFscsArt_EffectivenessWithTestCases(int[][] bound, FaultZone fzb) {
        java.util.List<String> testCases = new java.util.ArrayList<>();
        int generatedNum = 0;
        int maxTry = (int) (30 / fzb.theta);
        int selected;
        Point[] tcP = new Point[maxTry + 2];
        Point[] candP = new Point[candNum];
        tcP[0] = Point.generateRandP(bound);  // first test case
        testCases.add(tcP[0].toString());
        generatedNum++; // increasing f-measure without testing first test case
        do {
            for (int i = 0; i < candNum; i++) {
                candP[i] = Point.generateRandP(bound);
            }
            selected = findFarestCandidate(tcP, generatedNum, candP);
            tcP[generatedNum] = candP[selected];
            testCases.add(tcP[generatedNum].toString());
            generatedNum++;
            if (fzb.findTarget(tcP[generatedNum - 1])) {//若当前测试用例触中 FaultZone，立即停止；
                break;
            }
        } while (generatedNum < maxTry);
        return testCases;
    }


    public void testFscsArt_Efficiency(int num, int bound[][]) throws IOException {
        int selected;
        Point[] tcP = new Point[num];
        Point[] candP = new Point[candNum];
        tcP[0] = Point.generateRandP(bound);
        // 保存路径，文件名根据num动态变化
        String savePath = "E:\\Test_results\\FSCS_cases_" +bound.length+"d_"+ num + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath, true))) {
            writer.write(tcP[0].toString());
            writer.newLine();
            for (int j = 1; j < num; j++) {
                for (int i = 0; i < candNum; i++) {
                    candP[i] = Point.generateRandP(bound);
                }
                selected = findFarestCandidate(tcP, j, candP);
                tcP[j] = candP[selected];
                writer.write(tcP[j].toString());
                writer.newLine();
            }
        }
    }////生成固定数量的测试用例（不评估缺陷），用于衡量效率性能开销。

    public int testFscsArt_Discrepancy(int[][] bound, FaultZone fzb, int testCases) {
        int counter = 0; // number of test cases in sub-domain region
        int selected;
        Point[] tcP = new Point[testCases];
        Point[] candP = new Point[candNum];
        tcP[0] = Point.generateRandP(bound);
        for (int j = 1; j < testCases; j++) {
            for (int i = 0; i < candNum; i++) {
                candP[i] = Point.generateRandP(bound);
            }
            selected = findFarestCandidate(tcP, j, candP);
            tcP[j] = candP[selected];
            if (fzb.findTarget(tcP[j])) {
                counter++;
            }
        }
        return counter;
    }//用于度量 FSCS-ART 在给定数量的测试用例中有多少落入故障区，表示测试用例对故障区的离散覆盖能力（Discrepancy）。

    public double nn_distance(Point q, Point[] X) {
        double nn_distance = Integer.MAX_VALUE;
        double distance;
        for (Point x : X) {
            distance = Point.getDistance(q, x);
            if ((distance < nn_distance) && (distance != 0)) {
                nn_distance = distance;
            }
        }
        return nn_distance;
    }

    public double testFscsArt_Dispersion(int num, int[][] bound) throws IOException { // ����Ч�ʲ���
        int selected;
        Point[] tcP = new Point[num];
        Point[] candP = new Point[candNum];
        tcP[0] = Point.generateRandP(bound);
        for (int j = 1; j < num; j++) { // �������n����ѡ�Ĳ�������
            for (int i = 0; i < candNum; i++) {
                candP[i] = Point.generateRandP(bound);
            }
            selected = findFarestCandidate(tcP, j, candP);
            tcP[j] = candP[selected];
        }
        double distance;
        double max_distance = -1;
        for (Point q : tcP) {
            distance = nn_distance(q, tcP);
            if (distance > max_distance) {
                max_distance = distance;
            }
        }
        return max_distance;
    }

}
