package com.github.jelmerk.knn.examples.dge.art;

import com.github.jelmerk.knn.examples.my.art1d_bessj0.BesselFunction;
import com.github.jelmerk.knn.examples.my.art1d_bessj0.BesselFunctionMutant3;
import com.github.jelmerk.knn.examples.my.art2d_gammq.IncompleteGamma;
import com.github.jelmerk.knn.examples.my.art2d_gammq.IncompleteGammaMutant1;
import com.github.jelmerk.knn.examples.my.art3d.*;
import com.github.jelmerk.knn.examples.my.art4d_period.LombScargle;
import com.github.jelmerk.knn.examples.my.art4d_period.LombScargleMutant1;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/*三个指标：1.即成功检测出缺陷所需的测试用例数量。F-measure值
 *2.生成固定数量的测试用例（不评估缺陷），用于衡量效率性能开销。（时间来衡量）
 * 3.在给定数量的测试用例中有多少检测到程序缺陷。
 */
public class DGE_ART {
    private final double[] minBounds;
    private final double[] maxBounds;
    private final int dim;
    private final int initialDivisions;
    private final int maxTests;
    private final int maxDepth;

    private Random rand = new Random();
    private List<Region> regions = new ArrayList<>();
    private Set<Region> occupiedRegions = new HashSet<>();
    private Set<Region> adjacentRegions = new HashSet<>();
    private Map<String,Region> regionMap = new HashMap<>();
    private List<double[]> allDetectedPoints = new ArrayList<>();
    private int currentDepth = 0;
    private int testCount = 0;

    // 默认1维构造
    public DGE_ART() {
//        this(new double[]{-5000}, new double[]{5000}, 2, 30000, 10);
//        this(new double[]{-300000}, new double[]{300000}, 2, 30000, 10);
//        this(new double[]{-30000}, new double[]{30000}, 2, 30000, 10);
//        this(new double[]{-5000,-5000}, new double[]{5000,5000}, 2, 30000, 10);
//        this(new double[]{2,-1000}, new double[]{300,15000}, 2, 30000, 10);
//        this(new double[]{0,0}, new double[]{1700,40}, 2, 30000, 10);
//        this(new double[]{10,0,0}, new double[]{500,11,1}, 2, 30000, 10);
//        this(new double[]{-100,-100,-100}, new double[]{60,60,60}, 2, 30000, 10);
        this(new double[]{-10000,-10000,-10000,-10000}, new double[]{10000,10000,10000,10000}, 2, 30000, 10);
//        this(new double[]{0.001,0.001,0.001,0.001}, new double[]{1,300,10000,1000}, 2, 30000, 10);
//        this(new double[]{0,0,0,0}, new double[]{250,250,250,250}, 2, 30000, 10);
//        this(new double[]{1,1,1}, new double[]{1000,1000,1000}, 2, 30000, 4);
//        this(new double[]{-50000}, new double[]{50000}, 2, 30000, 4);
    }

    // 灵活构造
    public DGE_ART(double[] minBounds, double[] maxBounds, int initialDivisions, int maxTests, int maxDepth) {
        if (minBounds.length != maxBounds.length) throw new IllegalArgumentException("维度不一致");
        this.minBounds = minBounds.clone();
        this.maxBounds = maxBounds.clone();
        this.dim = minBounds.length;
        this.initialDivisions = initialDivisions;
        this.maxTests = maxTests;
        this.maxDepth = maxDepth;
    }

//    public static void main(String[] args) {
//        // 示例：2维
////        my_art art2d = new my_art();
////        art2d.runTest();
//        // 示例：3维
//        // my_art art3d = new my_art(new double[]{-10,0,1}, new double[]{10,20,5}, 2, 20000, 2);
//        // art3d.runTest();
//        // 4维输入域，每个维度范围[-10000, 10000]，初始分割2，最大测试数30000，最大细分深度2
//        double[] minBounds = {-10000, -10000, -10000, -10000};
//        double[] maxBounds = {10000, 10000, 10000, 10000};
//        com.github.jelmerk.knn.examples.my.art4d_period.my_art_period art4d = new com.github.jelmerk.knn.examples.my.art4d_period.my_art_period(minBounds, maxBounds, 2, 10000, 3);
//        art4d.runTest();
//        int fmeasure = com.github.jelmerk.knn.examples.my.art4d_period.my_art_period.measureFMeasure();
//
//    }

    public void runTest() {
        regions.clear();
        occupiedRegions.clear();
        adjacentRegions.clear();
        regionMap.clear();
        allDetectedPoints.clear();
        currentDepth = 0;
        testCount = 0;
        initializeRegions();

        Region detectedRegion = null;
        while (testCount < maxTests) {
            Region region = getRandomRegionNotOccupiedOrAdjacent();
            double[] inputs = region.generateRandomPoint();
            System.out.printf("生成随机测试用例: %s 在区域: %s%n", Arrays.toString(inputs), region);
            if (detectWeakMutation(inputs)) {
                detectedRegion = region;
                detectedRegion.addDetectedPoint(inputs);
                allDetectedPoints.add(inputs);
                System.out.println("第一次检测到缺陷的点：" + Arrays.toString(inputs));
                markRegionAndNeighbors(detectedRegion);
                break;
            } else {
                occupiedRegions.add(region);
                adjacentRegions.addAll(region.getAllNeighbors(regionMap));
            }
            testCount++;
        }

        if (detectedRegion == null) {
            System.out.println("未能在初步搜索中发现缺陷。");
            return;
        }

        while (testCount < maxTests && currentDepth <= maxDepth) {
            boolean foundNewBug = false;
            List<Region> newBugs = new ArrayList<>();
            Set<Region> nextAdjacent = new HashSet<>();

            for (Region neighbor : new ArrayList<>(adjacentRegions)) {
                if (occupiedRegions.contains(neighbor)) continue;
                double[] inputs = neighbor.generateRandomPoint();
                System.out.printf("生成随机测试用例: %s 在区域: %s%n", Arrays.toString(inputs), neighbor);
                if (detectWeakMutation(inputs)) {
                    neighbor.addDetectedPoint(inputs);
                    allDetectedPoints.add(inputs);
                    System.out.println("当前测试用例检测到程序缺陷");
                    occupiedRegions.add(neighbor);
                    newBugs.add(neighbor);
                    foundNewBug = true;
                } else {
                    occupiedRegions.add(neighbor);
                }
                testCount++;
                if (testCount >= maxTests) break;
            }
            for (Region bug : newBugs) {
                for (Region oct : bug.getOctantNeighbors()) {
                    if (!occupiedRegions.contains(oct)) {
                        nextAdjacent.add(oct);
                    }
                }
            }
            adjacentRegions.clear();
            adjacentRegions.addAll(nextAdjacent);

            if (!foundNewBug) {
                subdivideRegions();
                currentDepth++;
            }
            if (testCount >= maxTests || currentDepth > maxDepth) {
                System.out.println("达到最大测试次数或细分深度，停止。");
                break;
            }
        }

        System.out.println("测试结束，总测试次数：" + testCount);
        if (!allDetectedPoints.isEmpty() && dim == 2) {
            drawScatterPlot();
        }
    }

    private void initializeRegions() {
        int[] indices = new int[dim];
        int[] divisions = new int[dim];
        Arrays.fill(divisions, initialDivisions);
        double[] step = new double[dim];
        for (int i = 0; i < dim; i++) step[i] = (maxBounds[i] - minBounds[i]) / initialDivisions;
        initRegionsRecursive(0, indices, step, divisions);
    }

    private void initRegionsRecursive(int d, int[] indices, double[] step, int[] divisions) {
        if (d == dim) {
            double[] min = new double[dim];
            double[] max = new double[dim];
            for (int i = 0; i < dim; i++) {
                min[i] = minBounds[i] + indices[i] * step[i];
                max[i] = min[i] + step[i];
            }
            Region region = new Region(min, max);
            regions.add(region);
            regionMap.put(region.getKey(), region);
            return;
        }
        for (int i = 0; i < divisions[d]; i++) {
            indices[d] = i;
            initRegionsRecursive(d + 1, indices, step, divisions);
        }
    }

    private Region getRandomRegionNotOccupiedOrAdjacent() {
        Set<Region> candidateSet = new HashSet<>();
        for (Region r : regions) {
            if (!occupiedRegions.contains(r) && !adjacentRegions.contains(r)) {
                candidateSet.add(r);
            }
        }

        // 如果没有可用区域，随机选择一个区域
        if (candidateSet.isEmpty()) {
            return regions.get(rand.nextInt(regions.size()));
        }

        // 将Set转换为List以便随机访问
        List<Region> candidates = new ArrayList<>(candidateSet);
        return candidates.get(rand.nextInt(candidates.size()));
    }

    // 根据维度自动选择被测程序
    private boolean detectWeakMutation(double[] inputs) {
        int origState = 0, mutState=0;
        switch (dim) {
            case 1:
                // 1D: Airy函数
//                origState = AiryFunction.resultWithState(inputs[0]).getStateValue();
//                mutState = AiryFunctionMutant1.resultWithState(inputs[0]).getStateValue();
                origState = BesselFunction.resultWithState(inputs[0]).getStateValue();
                mutState = BesselFunctionMutant3.resultWithState(inputs[0]).getStateValue();
//                origState = ErfccFunction.resultWithState(inputs[0]).getStateValue();
//                mutState = ErfccFunctionMutant3.resultWithState(inputs[0]).getStateValue();
//                origState = KolmogorovSmirnov.resultWithState(inputs[0]).getStateValue();
//                mutState = KolmogorovSmirnovMutant1.resultWithState(inputs[0]).getStateValue();
                break;
            case 2:
                // 2D: 控制流分析函数
//                origState = sncndnFunction.resultWithState(inputs[0], inputs[1]).getStateValue();
//                mutState = sncndnFunctionMutant1.resultWithState(inputs[0], inputs[1]).getStateValue();
//                origState = bessj.resultWithState(inputs[0], inputs[1]).getStateValue();
//                mutState = bessjMutant1.resultWithState(inputs[0], inputs[1]).getStateValue();
                origState = IncompleteGamma.resultWithState(inputs[0], inputs[1]).getStateValue();
                mutState = IncompleteGammaMutant1.resultWithState(inputs[0], inputs[1]).getStateValue();
                break;
            case 3:
//                origState = AssociatedLegendre.resultWithState(inputs[0], inputs[1],inputs[2]).getStateValue();
//                mutState = AssociatedLegendreMutant1.resultWithState(inputs[0], inputs[1],inputs[2]).getStateValue();
//                origState = GoldenSectionSearch.resultWithState(inputs[0], inputs[1],inputs[2]).getStateValue();
//                mutState = GoldenSectionSearchMutant3.resultWithState(inputs[0], inputs[1],inputs[2]).getStateValue();
                origState = TriSquare.resultWithState(inputs[0], inputs[1],inputs[2]).getStateValue();
                mutState = TriSquareMutant3.resultWithState(inputs[0], inputs[1],inputs[2]).getStateValue();
                break;
            case 4:
                double[] x = new double[1];
                x[0] = Math.abs((int)inputs[0]);
                double[] y = new double[1];
                y[0] = Math.abs((int)inputs[1]);
                origState = LombScargle.resultWithState(x, y, inputs[2], inputs[3]).getStateValue();
                mutState = LombScargleMutant1.resultWithState(x, y, inputs[2], inputs[3]).getStateValue();
//                origState = EllipticIntegralCEL.resultWithState(inputs[0], inputs[1],inputs[2],inputs[3]).getStateValue();
//                mutState = EllipticIntegralCELMutant3.resultWithState(inputs[0], inputs[1],inputs[2],inputs[3]).getStateValue();
//                origState = el2.resultWithState(inputs[0], inputs[1],inputs[2],inputs[3]).getStateValue();
//                mutState = el2Mutant1.resultWithState(inputs[0], inputs[1],inputs[2],inputs[3]).getStateValue();
                break;
            default:
//                origState = FindNearestPoints.resultWithState(inputs[0], inputs[1],inputs[2],inputs[3],inputs[4],inputs[5],inputs[6],inputs[7],inputs[8],inputs[9]).getStateValue();
//                mutState = FindNearestPointsMutant2.resultWithState(inputs[0], inputs[1],inputs[2],inputs[3],inputs[4],inputs[5],inputs[6],inputs[7],inputs[8],inputs[9]).getStateValue();
                break;
        }

        return origState != mutState;
    }

    private void markRegionAndNeighbors(Region region) {
        occupiedRegions.clear();
        adjacentRegions.clear();
        occupiedRegions.add(region);
        adjacentRegions.addAll(region.getAllNeighbors(regionMap));
    }

    private void subdivideRegions() {
        List<Region> newRegions = new ArrayList<>();
        Map<String,Region> newMap = new HashMap<>();
        Set<Region> newOccupied = new HashSet<>();
        for (Region r : regions) {
            for (Region sub : r.subdivide()) {
                newRegions.add(sub);
                newMap.put(sub.getKey(), sub);
                if (!r.detectedPoints.isEmpty()) {
                    sub.detectedPoints.addAll(r.detectedPoints);
                    newOccupied.add(sub);
                }
            }
        }
        regions = newRegions;
        regionMap = newMap;
        occupiedRegions = newOccupied;
    }

    private void drawScatterPlot() {
        XYSeries series = new XYSeries("Detected Points");
        for (double[] p : allDetectedPoints) {
            series.add(p[0], p[1]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "缺陷测试用例分布",
                "x1", "x2",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        Font titleFont = new Font("宋体", Font.BOLD, 18);
        Font labelFont = new Font("宋体", Font.PLAIN, 14);
        chart.getTitle().setFont(titleFont);
        chart.getLegend().setItemFont(labelFont);
        chart.getXYPlot().getDomainAxis().setLabelFont(labelFont);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(labelFont);
        chart.getXYPlot().getRangeAxis().setLabelFont(labelFont);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(labelFont);
        chart.getXYPlot().getDomainAxis().setRange(minBounds[0], maxBounds[0]);
        chart.getXYPlot().getRangeAxis().setRange(minBounds[1], maxBounds[1]);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800,600));
        JFrame frame = new JFrame("Scatter Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    class Region {
        double[] min, max;
        List<double[]> detectedPoints = new ArrayList<>();



        Region(double[] min, double[] max) {
            this.min = min;
            this.max = max;
        }

        String getKey() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < min.length; i++) {
                if (i > 0) sb.append("_");
                sb.append(String.format("%.2f", min[i]));
            }
            return sb.toString();
        }

        void addDetectedPoint(double[] point) {
            detectedPoints.add(point);
        }

        double[] generateRandomPoint() {
            double[] point = new double[dim];
            for (int i = 0; i < dim; i++) {
                point[i] = min[i] + rand.nextDouble() * (max[i] - min[i]);
            }
            return point;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < min.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(String.format("D%d:%.1f–%.1f", i, min[i], max[i]));
            }
            sb.append("]");
            return sb.toString();
        }

        List<Region> getAllNeighbors(Map<String,Region> map) {
            List<Region> list = new ArrayList<>();
            double[] step = new double[min.length];
            for (int i = 0; i < min.length; i++) step[i] = max[i] - min[i];
            int[] offsets = new int[min.length];
            generateNeighborOffsets(offsets, 0, step, map, list);
            return list;
        }
        private void generateNeighborOffsets(int[] offsets, int dimension, double[] step, Map<String,Region> map, List<Region> list) {
            if (dimension == min.length) {
                boolean allZero = true;
                for (int offset : offsets) if (offset != 0) { allZero = false; break; }
                if (allZero) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < min.length; i++) {
                    if (i > 0) sb.append("_");
                    double neighborMin = min[i] + offsets[i] * step[i];
                    sb.append(String.format("%.2f", neighborMin));
                }
                Region neighbor = map.get(sb.toString());
                if (neighbor != null) list.add(neighbor);
                return;
            }
            for (int offset = -1; offset <= 1; offset++) {
                offsets[dimension] = offset;
                generateNeighborOffsets(offsets, dimension + 1, step, map, list);
            }
        }

        List<Region> getOctantNeighbors() {
            List<Region> list = new ArrayList<>();
            double[] w = new double[min.length];
            for (int i = 0; i < min.length; i++) w[i] = max[i] - min[i];
            int[] signs = new int[min.length];
            generateOctantNeighbors(signs, 0, w, list);
            return list;
        }
        private void generateOctantNeighbors(int[] signs, int dimension, double[] w, List<Region> list) {
            if (dimension == min.length) {
                double[] nmin = new double[min.length];
                double[] nmax = new double[max.length];
                for (int i = 0; i < min.length; i++) {
                    double offset = signs[i] * w[i];
                    nmin[i] = min[i] + offset;
                    nmax[i] = max[i] + offset;
                }
                boolean inBounds = true;
                for (int i = 0; i < min.length; i++) {
                    if (nmin[i] < DGE_ART.this.minBounds[i] || nmax[i] > DGE_ART.this.maxBounds[i]) {
                        inBounds = false; break;
                    }
                }
                if (inBounds) list.add(new Region(nmin, nmax));
                return;
            }
            for (int sign : new int[]{-1, 1}) {
                signs[dimension] = sign;
                generateOctantNeighbors(signs, dimension + 1, w, list);
            }
        }

        List<Region> subdivide() {
            List<Region> parts = new ArrayList<>();
            double[] mid = new double[min.length];
            for (int i = 0; i < min.length; i++) mid[i] = (min[i] + max[i]) / 2;
            boolean[] useMax = new boolean[min.length];
            generateSubregions(useMax, 0, mid, parts);
            return parts;
        }
        private void generateSubregions(boolean[] useMax, int dimension, double[] mid, List<Region> parts) {
            if (dimension == min.length) {
                double[] newMins = new double[min.length];
                double[] newMaxs = new double[max.length];
                for (int i = 0; i < min.length; i++) {
                    if (useMax[i]) {
                        newMins[i] = mid[i];
                        newMaxs[i] = max[i];
                    } else {
                        newMins[i] = min[i];
                        newMaxs[i] = mid[i];
                    }
                }
                parts.add(new Region(newMins, newMaxs));
                return;
            }
            for (boolean useMaxValue : new boolean[]{false, true}) {
                useMax[dimension] = useMaxValue;
                generateSubregions(useMax, dimension + 1, mid, parts);
            }
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Region))return false;
            Region r=(Region)o;
            return Arrays.equals(min, r.min) && Arrays.equals(max, r.max);
        }
        @Override public int hashCode(){
            return Arrays.hashCode(min) + 31 * Arrays.hashCode(max);
        }
    }

    public static class LocalState {
        private final int stateValue;

        public LocalState(int state, double areaValue) {
            this.stateValue = state;
        }

        public int getStateValue() {
            return stateValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalState that = (LocalState) o;
            return stateValue == that.stateValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stateValue);
        }
    }
    // F-measure统计
    public static int measureFMeasure() {
        DGE_ART instance = new DGE_ART();
        return instance.measureFMeasureImpl();
    }

    // 返回测试用例列表的F-measure统计
    public static List<String> measureFMeasureWithTestCases() {
        DGE_ART instance = new DGE_ART();
        return instance.measureFMeasureWithTestCasesImpl();
    }

    private int measureFMeasureImpl() {
        regions.clear();
        occupiedRegions.clear();
        adjacentRegions.clear();
        regionMap.clear();
        allDetectedPoints.clear();
        currentDepth = 0;
        testCount = 0;
        initializeRegions();

        while (testCount < maxTests) {
            Region region = getRandomRegionNotOccupiedOrAdjacent();
            double[] inputs = region.generateRandomPoint();
            if (detectWeakMutation(inputs)) {
                return testCount + 1;
            } else {
                occupiedRegions.add(region);
                adjacentRegions.addAll(region.getAllNeighbors(regionMap));
            }
            testCount++;
        }
        return maxTests;
    }

    private List<String> measureFMeasureWithTestCasesImpl() {
        regions.clear();
        occupiedRegions.clear();
        adjacentRegions.clear();
        regionMap.clear();
        allDetectedPoints.clear();
        currentDepth = 0;
        testCount = 0;
        initializeRegions();

        List<String> testCases = new ArrayList<>();

        while (testCount < maxTests) {
            Region region = getRandomRegionNotOccupiedOrAdjacent();
            double[] inputs = region.generateRandomPoint();
            testCases.add(Arrays.toString(inputs));
            if (detectWeakMutation(inputs)) {
                return testCases;
            } else {
                occupiedRegions.add(region);
                adjacentRegions.addAll(region.getAllNeighbors(regionMap));
            }
            testCount++;
        }
        return testCases;
    }
    //测试用例生成时间
    public static void measureEfficiencyMultiDim() {
        int[] dims = {5,10,15};
        int[] testSizes = {100, 200, 500, 1000, 2000, 5000, 10000, 15000, 20000};
        int warmupRuns = 3;
        int repetitions = 1000;
        int totalRuns = warmupRuns + repetitions;
        double min = -5000.0, max = 5000.0;
        int initialDivisions =1;//1-4d采用初始化分区域为2
        int maxDepth =10;
        int maxTests = 30000;
        Random rand = new Random();

        for (int d : dims) {
            for (int n : testSizes) {
                List<Double> allTimesMs = new ArrayList<>();
                List<String> allTestCases = new ArrayList<>();

                for (int run = 0; run < totalRuns; run++) {
                    double[] minBounds = new double[d];
                    double[] maxBounds = new double[d];
                    Arrays.fill(minBounds, min);
                    Arrays.fill(maxBounds, max);

                    DGE_ART art = new DGE_ART(minBounds, maxBounds, initialDivisions, maxTests, maxDepth);
                    art.regions.clear();
                    art.regionMap.clear();
                    art.occupiedRegions.clear();
                    art.adjacentRegions.clear();
                    art.currentDepth = 0;
                    art.initializeRegions();

                    List<String> testCaseList = new ArrayList<>();
                    long t0 = System.nanoTime();
                    while (testCaseList.size() < n) {
                        List<Region> availableRegions = new ArrayList<>();
                        for (Region r : art.regions) {
                            if (!art.occupiedRegions.contains(r) && !art.adjacentRegions.contains(r)) {
                                availableRegions.add(r);
                            }
                        }
                        if (availableRegions.isEmpty()) {
                            if (art.currentDepth < art.maxDepth) {
                                art.subdivideRegions();
                                art.currentDepth++;
                                continue;
                            } else {
                                art.occupiedRegions.clear();
                                art.adjacentRegions.clear();
                                continue;
                            }
                        }
                        // 本轮可用区域全部用完
                        for (Region region : availableRegions) {
                            if (testCaseList.size() >= n) break;
                            double[] input = region.generateRandomPoint();
                            art.occupiedRegions.add(region);
                            art.adjacentRegions.addAll(region.getAllNeighbors(art.regionMap));
                            testCaseList.add(Arrays.toString(input));
                        }
                    }
                    long t1 = System.nanoTime();
                    if (run >= warmupRuns) {
                        allTimesMs.add((t1 - t0) / 1e6);
                        allTestCases.addAll(testCaseList);
                    }
                    if (run >= warmupRuns && ((run - warmupRuns) % 10 == 0)) {
                        double progress = ((run - warmupRuns) * 100.0) / repetitions;
                        System.out.printf("维度%d, 规模%d: 进度 %.1f%% (实验 %d/%d)\r", d, n, progress, (run - warmupRuns + 1), repetitions);
                    }
                }
                // 写时间
                String timeFile = String.format("C:/Users/xixifu/Desktop/Test2/Test/test_Efficiency/hre_times_%dd_%d.txt", d, n);
                try (FileWriter writer = new FileWriter(timeFile)) {
                    double sum = 0;
                    for (double t : allTimesMs) {
                        writer.write(String.format("%.4f\n", t));
                        sum += t;
                    }
                    double avg = sum / allTimesMs.size();
                    writer.write("平均时间: " + String.format("%.4f 毫秒", avg) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 写用例
                String caseFile = String.format("C:/Users/xixifu/Desktop/Test2/Test/test_Efficiency/hre_cases_%dd_%d.txt", d, n);
                try (FileWriter writer = new FileWriter(caseFile)) {
                    for (String tc : allTestCases) writer.write(tc + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.printf("维度%d, 规模%d: 用例和时间已写入\n", d, n);
            }
        }
    }
}
