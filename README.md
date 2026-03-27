 DGE-ART: Direction-Guided Exploration for Adaptive Random Testing

This repository contains the implementation  for the paper **"An Adaptive Random Testing via Direction-Guided Exploration for Continuous Failure Regions"**. DGE-ART is a novel algorithm designed to enhance the efficiency of software testing in the presence of continuous failure regions.

Introduction：Adaptive Random Testing (ART) is a well-known technique that improves fault detection by ensuring a uniform distribution of test cases. However, traditional ART algorithms (like FSCS-ART) suffer from high computational overhead, especially as the number of test cases grows. While existing improvements focus on reducing complexity, they often fail to exploit the geometric characteristics of failure patterns.DGE-ART addresses this gap by introducing a *direction-guided exploration* strategy. Instead of blindly searching the entire input domain, DGE-ART leverages the fact that failures often occur in continuous clusters (stripes). By detecting the direction of a failure, the algorithm prioritizes the exploration of neighboring regions along that vector, significantly reducing unnecessary computations.
Key Innovations：DGE-ART introduces a paradigm shift in how test cases are generated for continuous failures:

1. Direction-Guided Regional Exploration (New Paradigm):
    Unlike previous methods that rely on exhaustive distance calculations or static partitioning, DGE-ART dynamically selects the next high-risk region. It uses the location of the first detected failure to infer the likely direction of subsequent failures.
2.  Enhanced Efficiency & Cost-Effectiveness:
    By focusing on specific linear trajectories, DGE-ART reduces the number of regions to be explored. Theoretically, this reduces the workload from checking all neighbors (e.g., 8 directions in 2D) to checking a single direction once the vector is established.
3. Failure Pattern Analysis:
    Beyond detection, DGE-ART provides diagnostic insights. The algorithm helps reveal the root causes of continuous failure patterns (often linked to boundary-value shifts in control flow), aiding in fault localization.

Experimental Results：Based on the empirical studies conducted in the paper, DGE-ART demonstrates significant advantages:
1.Low-to-Medium Dimensions: DGE-ART outperforms FSCS-ART, KDFC-ART, and SWFC-ART in terms of computational cost reduction (improvements ranging from 24% to 99% in 1D scenarios).
2.Detection Capability: It maintains or even enhances failure-detection effectiveness. For instance, in specific programs like `gammq`, it showed an improvement of approximately 99.46% compared to some baselines.
3.Robustness:While performance degrades slightly in high-dimensional spaces (curse of dimensionality), DGE-ART maintains a stable failure-detection capability.
Environment & Dependencies：To run the experiments or utilize the codebase, please ensure your environment meets the following specifications, consistent with the paper's experimental setup.

 Software Stack：
Operating System：Windows 10 Pro (64-bit)
Java Development Kit：Java 1.8.0_431
Build Tool： Maven / Gradle

How to Run：
1. Clone the Repository:
    ```bash
    git clone https://github.com/xixifu-alt/DGE-ART.git
    cd DGE-ART
    ```
2.  Setup Environment:
    Ensure `JAVA_HOME` is set to point to Java 1.8.0_431.
3. Execute:
    The main entry point is located in the `src/main/java` directory. You may need to configure the input domain parameters (Initial Divisions, Max Depth) based on the target program's dimensionality.
