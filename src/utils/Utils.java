package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import algorithm.Dijkstra;
import algorithm.FordFulkerson;
import io.InputOuputManager;
import models.Arc;
import models.Graph;
import models.Network;
import models.ResidualGraph;
import testing.TestRunner;

/**
 * Dieu khien toan bo luong xu ly cua chuong trinh Console.
 *
 * <p>Lop nay giu trang thai dataset dang dung va ket qua cua hai bai toan.
 * Moi khi dataset thay doi, ket qua/residual cu bi xoa de tranh hien thi du
 * lieu khong con dung.</p>
 */
public class Utils {

    public static final Scanner SCANNER = new Scanner(System.in);

    private final InputOuputManager io = new InputOuputManager();

    private Network network;
    private String lastLoadedPath;

    private FordFulkerson.MaxFlowResult maxFlowResult;
    private Dijkstra.Result dijkstraResult;
    private int ffSource = -1;
    private int ffSink = -1;
    private int djSource = -1;
    private int djTarget = -1;

    private List<TestRunner.CaseResult> lastTestResults = List.of();

    // =====================================================================
    // Input helpers
    // =====================================================================

    /** Doc mot dong; tra ve null khi het input (EOF). */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            return null;
        }
        return SCANNER.nextLine().trim();
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            if (line == null) {
                return Integer.MIN_VALUE;
            }
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignore) {
                // fall through
            }
            System.out.println("INVALID INPUT: vui long nhap so trong khoang "
                    + min + ".." + max + ".");
        }
    }

    public static void pause() {
        readLine("\nNhan Enter de tiep tuc...");
    }

    // =====================================================================
    // State
    // =====================================================================

    public Network getNetwork() {
        return network;
    }

    public boolean hasNetwork() {
        return network != null;
    }

    public String statusLine() {
        if (network == null) {
            return "NOT LOADED - chua nap dataset";
        }
        String name = network.getDatasetName();
        if (name.length() > 24) {
            name = name.substring(0, 21) + "...";
        }
        return String.format("%s | V=%d E=%d cap0=%d",
                name, network.vertexCount(), network.arcCount(), network.zeroCapacityCount());
    }

    private void clearResults() {
        maxFlowResult = null;
        dijkstraResult = null;
        ffSource = -1;
        ffSink = -1;
        djSource = -1;
        djTarget = -1;
    }

    /** Nap san dataset chinh khi khoi dong (neu tim thay). */
    public boolean autoLoadMainDataset() {
        InputOuputManager.LoadResult loaded = io.load("main_dataset.txt");
        if (!loaded.isSuccess()) {
            return false;
        }
        network = loaded.getNetwork();
        lastLoadedPath = loaded.getResolvedPath();
        clearResults();
        return true;
    }

    private boolean requireNetwork() {
        if (network == null) {
            System.out.println("\n[NOT LOADED] Chua co du lieu.");
            System.out.println("Hay quay lai menu 2 -> [1] Nap du lieu tu file hoac [3] Chon dataset mau.");
            return false;
        }
        return true;
    }

    // =====================================================================
    // Menu 2 - Data management
    // =====================================================================

    public void setDataFromFile() {
        String path = readLine("Nhap duong dan file (Enter = data/main_dataset.txt): ");
        if (path == null) {
            return;
        }
        if (path.isEmpty()) {
            path = "data/main_dataset.txt";
        }
        loadFromPath(path);
    }

    private boolean loadFromPath(String path) {
        InputOuputManager.LoadResult loaded = io.load(path);
        if (!loaded.isSuccess()) {
            System.out.println("\n[LOAD THAT BAI] " + loaded.getResolvedPath());
            for (String error : loaded.getErrors()) {
                System.out.println("  - " + error);
            }
            if (network != null) {
                System.out.println("Dataset hien tai duoc giu nguyen: " + network.getDatasetName());
            }
            return false;
        }

        network = loaded.getNetwork();
        lastLoadedPath = loaded.getResolvedPath();
        clearResults();

        System.out.println("\n[LOAD THANH CONG] " + network.getDatasetName());
        System.out.println("  File        : " + lastLoadedPath);
        System.out.println("  So dinh     : " + network.vertexCount());
        System.out.println("  So cung     : " + network.arcCount());
        System.out.println("  Capacity=0  : " + network.zeroCapacityCount());
        System.out.println("  Source/Sink : " + network.getSourceName() + " / " + network.getSinkName());
        System.out.println("  Dinh        : " + String.join(", ", network.getVertices()));
        System.out.println("  Da xoa residual/result cu (neu co).");
        return true;
    }

    public void displayGraph() {
        if (!requireNetwork()) {
            return;
        }
        Graph graph = network.getGraph();
        printHeader("TONG HOP GRAPH GOC");
        System.out.println("Dataset : " + network.getDatasetName() + "  (" + network.getFileName() + ")");
        System.out.println("Loai    : do thi co huong, don cung");
        System.out.println("V       : " + network.vertexCount());
        System.out.println("E       : " + network.arcCount());
        System.out.println("Zero-cap: " + network.zeroCapacityCount() + " cung (tuyen khong kha dung)");
        System.out.println("Source  : " + network.getSourceName() + " | Sink: " + network.getSinkName());
        System.out.println("Dinh    : " + String.join(", ", network.getVertices()));

        System.out.println("\nDANH SACH CUNG (u -> v, capacity, weight)");
        System.out.printf("%-5s %-8s %-8s %10s %10s%n", "STT", "u", "v", "capacity", "weight");
        int index = 1;
        for (Arc arc : graph.getAllArcs()) {
            System.out.printf("%-5d %-8s %-8s %10d %10s%s%n", index++,
                    network.nameOf(arc.getU()), network.nameOf(arc.getV()),
                    arc.getCapacity(), formatDouble(arc.getWeight()),
                    arc.getCapacity() == 0 ? "  [ZERO-CAP]" : "");
        }

        System.out.println("\nADJACENCY LIST");
        for (int u = 1; u <= network.vertexCount(); u++) {
            StringBuilder line = new StringBuilder();
            line.append(String.format("%-6s ->", network.nameOf(u)));
            for (Arc arc : graph.getOutgoingArcs(u)) {
                line.append(String.format(" %s(c=%d,w=%s)", network.nameOf(arc.getV()),
                        arc.getCapacity(), formatDouble(arc.getWeight())));
            }
            System.out.println(line);
        }

        printMatrix("CAPACITY MATRIX", true);
        printMatrix("WEIGHT MATRIX", false);
    }

    private void printMatrix(String title, boolean capacity) {
        Graph graph = network.getGraph();
        System.out.println("\n" + title);
        System.out.printf("%-6s", "");
        for (int v = 1; v <= network.vertexCount(); v++) {
            System.out.printf("%-7s", network.nameOf(v));
        }
        System.out.println();
        for (int u = 1; u <= network.vertexCount(); u++) {
            System.out.printf("%-6s", network.nameOf(u));
            for (int v = 1; v <= network.vertexCount(); v++) {
                if (!graph.hasArc(u, v)) {
                    System.out.printf("%-7s", "-");
                } else if (capacity) {
                    System.out.printf("%-7d", graph.getCapacity(u, v));
                } else {
                    System.out.printf("%-7s", formatDouble(graph.getWeight(u, v)));
                }
            }
            System.out.println();
        }
    }

    public void chooseDatasetAndDebug() {
        printHeader("CHON DATASET MAU / KIEM THU");
        List<InputOuputManager.DatasetInfo> datasets = InputOuputManager.VALID_DATASETS;
        for (int i = 0; i < datasets.size(); i++) {
            InputOuputManager.DatasetInfo info = datasets.get(i);
            System.out.println(info.menuLine(i + 1));
        }
        System.out.println("[0] Quay lai");
        int choice = readIntInRange("Chon dataset: ", 0, datasets.size());
        if (choice == Integer.MIN_VALUE || choice == 0) {
            return;
        }
        loadFromPath(datasets.get(choice - 1).fileName());
    }

    public void reloadOrClearDataset(int action) {
        if (action == 1) {
            if (lastLoadedPath == null) {
                System.out.println("[NOT LOADED] Chua co dataset nao de reload.");
                return;
            }
            System.out.println("Dang reload: " + lastLoadedPath);
            loadFromPath(lastLoadedPath);
        } else if (action == 2) {
            network = null;
            lastLoadedPath = null;
            clearResults();
            System.out.println("[CLEARED] Da xoa dataset va toan bo residual/result.");
        }
    }

    // =====================================================================
    // Menu 3 - Run algorithms
    // =====================================================================

    public void fordFulkerson() {
        if (!requireNetwork()) {
            return;
        }
        int source = askVertex("Source", network.getSourceName());
        if (source < 0) {
            return;
        }
        int sink = askVertex("Sink", network.getSinkName());
        if (sink < 0) {
            return;
        }
        if (source == sink) {
            System.out.println("\n[INVALID ENDPOINTS] Source phai khac Sink cho bai toan Maximum Flow.");
            return;
        }
        runFordFulkerson(source, sink);
    }

    private void runFordFulkerson(int source, int sink) {
        maxFlowResult = FordFulkerson.run(network.getGraph(), source, sink);
        ffSource = source;
        ffSink = sink;

        printHeader("KET QUA FORD-FULKERSON");
        System.out.println("Dataset : " + network.getDatasetName());
        System.out.println("Source  : " + network.nameOf(source) + " | Sink: " + network.nameOf(sink));
        if (!maxFlowResult.isSuccess()) {
            System.out.println("Status  : " + maxFlowResult.getMessage());
            return;
        }
        ResidualGraph residual = maxFlowResult.getResidualGraph();
        System.out.println("Status  : " + maxFlowResult.getMessage());
        System.out.println("Max Flow: " + maxFlowResult.getMaxFlow());
        System.out.println("So duong tang: " + maxFlowResult.getSteps().size());
        System.out.println("Cung nguoc duoc dung: "
                + (maxFlowResult.usedReverseArc() ? "CO (" + maxFlowResult.getReverseArcVisits() + " lan)" : "KHONG"));
        System.out.println("verifyFlow: " + (residual.verifyFlow(source, sink) ? "PASS" : "FAIL"));
        System.out.println("\nXem chi tiet flow/residual tai menu 3 -> [1], [2], [4].");
    }

    public void dijkstra() {
        if (!requireNetwork()) {
            return;
        }
        int source = askVertex("Source", network.getSourceName());
        if (source < 0) {
            return;
        }
        int target = askVertex("Target", network.getSinkName());
        if (target < 0) {
            return;
        }
        runDijkstra(source, target);
    }

    private void runDijkstra(int source, int target) {
        dijkstraResult = Dijkstra.run(network.getGraph(), source, target);
        djSource = source;
        djTarget = target;

        printHeader("KET QUA DIJKSTRA");
        System.out.println("Dataset : " + network.getDatasetName());
        System.out.println("Source  : " + network.nameOf(source)
                + " | Target: " + network.nameOf(target));
        System.out.println("Status  : " + dijkstraResult.getStatus().label());
        if (dijkstraResult.isSuccess()) {
            System.out.println("Path    : " + pathNames(dijkstraResult.getPath()));
            System.out.println("Total   : " + formatDouble(dijkstraResult.getTotalCost()));
            if (source == target) {
                System.out.println("Ghi chu  : Source = Target nen cost = 0, path chi gom Source.");
            }
        } else {
            System.out.println("Thong bao: " + dijkstraResult.getMessage());
        }
        System.out.println("\nXem Distance/Predecessor chi tiet tai menu 3 -> [3].");
    }

    public void runFordFulkersonAndDijkstra() {
        if (!requireNetwork()) {
            return;
        }
        int source = askVertex("Source", network.getSourceName());
        if (source < 0) {
            return;
        }
        int sink = askVertex("Sink/Target", network.getSinkName());
        if (sink < 0) {
            return;
        }
        if (source == sink) {
            System.out.println("\n[LUU Y] Source = Sink: Maximum Flow bi tu choi, chi chay Dijkstra (cost = 0).");
            runDijkstra(source, sink);
            return;
        }
        runFordFulkerson(source, sink);
        runDijkstra(source, sink);
    }

    private int askVertex(String label, String defaultName) {
        System.out.println("Cac dinh: " + String.join(", ", network.getVertices()));
        String input = readLine(label + " [Enter = " + defaultName + "]: ");
        if (input == null) {
            return -1;
        }
        if (input.isEmpty()) {
            input = defaultName;
        }
        if (!network.hasVertex(input)) {
            System.out.println("[INVALID INPUT] Dinh '" + input + "' khong ton tai trong dataset.");
            return -1;
        }
        return network.idOf(input);
    }

    // =====================================================================
    // Menu 4 - Results and verification
    // =====================================================================

    public void getMaxFlowAndTopFlow() {
        if (maxFlowResult == null || network == null) {
            System.out.println("\n[NOT RUN] Chua chay Ford-Fulkerson. Hay vao menu 2 -> [1].");
            return;
        }
        printHeader("MAX FLOW VA FLOW TREN CUNG GOC");
        System.out.println("Dataset : " + network.getDatasetName());
        System.out.println("Source  : " + network.nameOf(ffSource)
                + " | Sink: " + network.nameOf(ffSink));
        System.out.println("Max Flow: " + maxFlowResult.getMaxFlow());
        System.out.println("Status  : " + maxFlowResult.getMessage());

        ResidualGraph residual = maxFlowResult.getResidualGraph();
        System.out.printf("%n%-5s %-8s %-8s %10s %10s %12s %-12s%n",
                "STT", "u", "v", "capacity", "flow", "residual", "trang thai");
        int index = 1;
        for (Arc arc : network.getGraph().getAllArcs()) {
            int flow = residual.getFlow(arc.getU(), arc.getV());
            int residualCap = residual.getResidualCapacity(arc.getU(), arc.getV());
            String state;
            if (arc.getCapacity() == 0) {
                state = "ZERO-CAP";
            } else if (flow == arc.getCapacity()) {
                state = "SATURATED";
            } else if (flow > 0) {
                state = "IN USE";
            } else {
                state = "EMPTY";
            }
            System.out.printf("%-5d %-8s %-8s %10d %10d %12d %-12s%n", index++,
                    network.nameOf(arc.getU()), network.nameOf(arc.getV()),
                    arc.getCapacity(), flow, residualCap, state);
        }

        boolean verify = residual.verifyFlow(ffSource, ffSink);
        System.out.println("\nKiem chung flow:");
        System.out.println("  Capacity constraint 0 <= f <= c : " + (verify ? "PASS" : "FAIL"));
        System.out.println("  Bao toan luong tai dinh trung gian: " + (verify ? "PASS" : "FAIL"));
        System.out.println("  Source outflow = Sink inflow     : "
                + residual.getSourceOutflow(ffSource) + " = " + residual.getSinkInflow(ffSink)
                + " -> " + (residual.getSourceOutflow(ffSource)
                        == residual.getSinkInflow(ffSink) ? "PASS" : "FAIL"));
        System.out.println("  Ket luan: " + (verify ? "FLOW VALID" : "FLOW INVALID"));
    }

    public void displayResidualGraph() {
        if (maxFlowResult == null || network == null) {
            System.out.println("\n[NOT RUN] Chua chay Ford-Fulkerson. Hay vao menu 2 -> [1].");
            return;
        }
        ResidualGraph residual = maxFlowResult.getResidualGraph();
        printHeader("RESIDUAL GRAPH / AUGMENTING PATHS / BOTTLENECK");
        System.out.println("Dataset : " + network.getDatasetName());
        System.out.println("Source  : " + network.nameOf(ffSource)
                + " | Sink: " + network.nameOf(ffSink));
        System.out.println("Max Flow: " + maxFlowResult.getMaxFlow());
        System.out.println("So duong tang: " + maxFlowResult.getSteps().size());

        System.out.println("\nAUGMENTING PATHS");
        for (FordFulkerson.AugmentingPathStep step : maxFlowResult.getSteps()) {
            System.out.println("  " + step.getIndex() + ". " + pathWithReverseMarks(step)
                    + " | bottleneck = " + step.getBottleneck()
                    + (step.usesReverseArc() ? "  [co cung nguoc]" : ""));
        }

        System.out.println("\nRESIDUAL ARCS (residual capacity > 0)");
        System.out.printf("%-8s %-8s %12s %10s %-10s%n", "u", "v", "residual", "flow", "loai");
        int forward = 0;
        int reverse = 0;
        for (int u = 1; u <= network.vertexCount(); u++) {
            for (int v = 1; v <= network.vertexCount(); v++) {
                int cap = residual.getResidualCapacity(u, v);
                if (cap <= 0) {
                    continue;
                }
                boolean physical = network.getGraph().hasArc(u, v);
                if (physical) {
                    forward++;
                } else {
                    reverse++;
                }
                String flowText = physical ? String.valueOf(residual.getFlow(u, v)) : "-";
                System.out.printf("%-8s %-8s %12d %10s %-10s%n",
                        network.nameOf(u), network.nameOf(v), cap,
                        flowText, physical ? "physical" : "reverse");
            }
        }
        System.out.println("\nGhi chu: cot flow chi in tren cung vat ly; cung reverse hien '-'.");
        System.out.println("Tong hop: " + forward + " residual arc vat ly, "
                + reverse + " residual reverse arc.");
        System.out.println("Cung nguoc duoc dung trong qua trinh tang flow: "
                + (maxFlowResult.usedReverseArc() ? "CO" : "KHONG"));
    }

    public void displayDijkstraResult() {
        if (dijkstraResult == null || network == null) {
            System.out.println("\n[NOT RUN] Chua chay Dijkstra. Hay vao menu 2 -> [2].");
            return;
        }
        printHeader("DIJKSTRA - DISTANCE / PREDECESSOR / PATH / TOTAL COST");
        System.out.println("Dataset : " + network.getDatasetName());
        System.out.println("Source  : " + network.nameOf(djSource)
                + " | Target: " + network.nameOf(djTarget));
        System.out.println("Status  : " + dijkstraResult.getStatus().label());
        System.out.println("Thong bao: " + dijkstraResult.getMessage());

        if (dijkstraResult.isSuccess()) {
            System.out.println("\nPATH     : " + pathNames(dijkstraResult.getPath()));
            System.out.println("Total Cost: " + formatDouble(dijkstraResult.getTotalCost()));
            System.out.println("\nChi tiet tung buoc:");
            List<Integer> path = dijkstraResult.getPath();
            for (int i = 0; i < path.size() - 1; i++) {
                int u = path.get(i);
                int v = path.get(i + 1);
                Arc arc = network.getGraph().getArc(u, v);
                System.out.printf("  %s -> %s : capacity=%d, weight=%s%n",
                        network.nameOf(u), network.nameOf(v),
                        arc != null ? arc.getCapacity() : 0,
                        arc != null ? formatDouble(arc.getWeight()) : "-");
            }
        } else {
            System.out.println("\nKhong in path gia. Chi hien thi dist/pred cua cac dinh reachable.");
        }

        System.out.println("\nDISTANCE / PREDECESSOR (tu Source)");
        System.out.printf("%-10s %14s %-10s%n", "Dinh", "Distance", "Predecessor");
        for (int id = 1; id <= network.vertexCount(); id++) {
            double distance = dijkstraResult.getDistances() == null
                    ? Double.POSITIVE_INFINITY
                    : dijkstraResult.getDistances()[id];
            String distanceText = Double.isInfinite(distance) ? "INF" : formatDouble(distance);
            int pred = dijkstraResult.getPredecessors() == null
                    ? -1 : dijkstraResult.getPredecessors()[id];
            System.out.printf("%-10s %14s %-10s%n", network.nameOf(id),
                    distanceText, pred > 0 ? network.nameOf(pred) : "-");
        }
        System.out.println("\nGhi chu: Dijkstra chi dung cung capacity > 0 va weight, "
                + "khong dung residual capacity/flow.");
    }

    public void verifyFlowAndExamine() {
        if (network == null || (maxFlowResult == null && dijkstraResult == null)) {
            System.out.println("\n[NOT RUN] Chua co ket qua. Hay chay thuat toan o menu 2.");
            return;
        }
        printHeader("KIEM CHUNG FLOW VA PHAN TICH TONG HOP");
        System.out.println("Dataset : " + network.getDatasetName());

        if (maxFlowResult != null) {
            ResidualGraph residual = maxFlowResult.getResidualGraph();
            System.out.println("\nA. FORD-FULKERSON");
            System.out.println("  Source/Sink: " + network.nameOf(ffSource)
                    + " / " + network.nameOf(ffSink));
            System.out.println("  Max Flow   : " + maxFlowResult.getMaxFlow());

            boolean capacityOk = true;
            for (Arc arc : network.getGraph().getAllArcs()) {
                int flow = residual.getFlow(arc.getU(), arc.getV());
                if (flow < 0 || flow > arc.getCapacity()) {
                    capacityOk = false;
                    System.out.println("  VI PHAM capacity tai (" + network.nameOf(arc.getU())
                            + "," + network.nameOf(arc.getV()) + "): f=" + flow
                            + ", c=" + arc.getCapacity());
                }
            }
            boolean conservationOk = true;
            for (int id = 1; id <= network.vertexCount(); id++) {
                if (id == ffSource || id == ffSink) {
                    continue;
                }
                int inflow = 0;
                int outflow = 0;
                for (int other = 1; other <= network.vertexCount(); other++) {
                    inflow += residual.getFlow(other, id);
                    outflow += residual.getFlow(id, other);
                }
                if (inflow != outflow) {
                    conservationOk = false;
                    System.out.println("  VI PHAM bao toan tai " + network.nameOf(id)
                            + ": in=" + inflow + ", out=" + outflow);
                }
            }
            int sourceOut = residual.getSourceOutflow(ffSource);
            int sinkIn = residual.getSinkInflow(ffSink);
            boolean totalsOk = sourceOut == sinkIn;
            System.out.println("  [1] 0 <= f(u,v) <= c(u,v) : " + (capacityOk ? "PASS" : "FAIL"));
            System.out.println("  [2] Bao toan luong         : " + (conservationOk ? "PASS" : "FAIL"));
            System.out.println("  [3] Source out = Sink in   : " + sourceOut + " = " + sinkIn
                    + " -> " + (totalsOk ? "PASS" : "FAIL"));
            System.out.println("  KET LUAN FLOW: "
                    + (capacityOk && conservationOk && totalsOk ? "FLOW VALID" : "FLOW INVALID"));
        } else {
            System.out.println("\nA. FORD-FULKERSON: chua chay.");
        }

        if (dijkstraResult != null) {
            System.out.println("\nB. DIJKSTRA");
            System.out.println("  Status: " + dijkstraResult.getStatus().label());
            if (dijkstraResult.isSuccess()) {
                System.out.println("  Path  : " + pathNames(dijkstraResult.getPath()));
                System.out.println("  Total : " + formatDouble(dijkstraResult.getTotalCost()));
                boolean pathOk = true;
                double sum = 0.0;
                List<Integer> path = dijkstraResult.getPath();
                for (int i = 0; i < path.size() - 1; i++) {
                    int u = path.get(i);
                    int v = path.get(i + 1);
                    Arc arc = network.getGraph().getArc(u, v);
                    if (arc == null || arc.getCapacity() <= 0) {
                        pathOk = false;
                        break;
                    }
                    sum += arc.getWeight();
                }
                boolean costOk = Math.abs(sum - dijkstraResult.getTotalCost()) < 1e-9;
                System.out.println("  [1] Moi buoc la cung capacity > 0 : " + (pathOk ? "PASS" : "FAIL"));
                System.out.println("  [2] Tong weight = Total Cost       : " + (costOk ? "PASS" : "FAIL"));
                System.out.println("  KET LUAN PATH: " + (pathOk && costOk ? "PATH VALID" : "PATH INVALID"));
            } else {
                System.out.println("  Khong co path de kiem chung (NO PATH/INVALID).");
            }
        } else {
            System.out.println("\nB. DIJKSTRA: chua chay.");
        }

        System.out.println("\nC. PHAN TICH TONG HOP");
        System.out.println("  Maximum Flow toi da hoa LUU LUONG tren residual graph, dung capacity.");
        System.out.println("  Dijkstra toi thieu hoa TONG WEIGHT tren graph goc, chi dung cung c>0.");
        System.out.println("  Hai bai toan doc lap: khong suy dien tuyen shortest path tao max flow");
        System.out.println("  va khong dung capacity/flow lam trong so cua Dijkstra.");
    }

    // =====================================================================
    // Menu 5 - Testing and help
    // =====================================================================

    public void runTestCase() {
        printHeader("CHAY MOT TESTCASE");
        for (TestRunner.TestInfo info : TestRunner.CATALOG) {
            System.out.printf("%-6s %s%n", info.code(), info.title());
        }
        String code = readLine("Nhap ma testcase (vd TC01): ");
        if (code == null) {
            return;
        }
        TestRunner.CaseResult result = TestRunner.run(code.trim().toUpperCase(Locale.ROOT));
        if (result == null) {
            System.out.println("[INVALID INPUT] Khong co testcase '" + code + "'.");
            return;
        }
        printCaseResult(result, true);
        lastTestResults = List.of(result);
    }

    public void runTestAllCase() {
        printHeader("CHAY TAT CA TESTCASE");
        lastTestResults = TestRunner.runAll();
        for (TestRunner.CaseResult result : lastTestResults) {
            printCaseResult(result, true);
        }
        long passed = lastTestResults.stream().filter(TestRunner.CaseResult::isPass).count();
        System.out.println("\nTOTAL: " + passed + "/" + lastTestResults.size() + " PASS");
    }

    private void printCaseResult(TestRunner.CaseResult result, boolean details) {
        System.out.println("\n" + result.getCode() + " - " + result.getTitle());
        System.out.println("  Expected: " + result.getExpected());
        System.out.println("  Actual  : " + result.getActual());
        System.out.println("  Result  : " + (result.isPass() ? "PASS" : "FAIL"));
        if (details) {
            for (String line : result.getDetails()) {
                System.out.println("    - " + line);
            }
        }
    }

    public void displayPassFail() {
        if (lastTestResults.isEmpty()) {
            System.out.println("\n[NOT RUN] Chua chay testcase. Hay chon menu 5 -> [2] truoc.");
            return;
        }
        printHeader("TONG HOP PASS / FAIL");
        System.out.printf("%-6s %-10s %-10s %-8s %s%n", "Ma", "Expected", "Actual", "Ket qua", "Ten testcase");
        int passed = 0;
        for (TestRunner.CaseResult result : lastTestResults) {
            if (result.isPass()) {
                passed++;
            }
            System.out.printf("%-6s %-10s %-10s %-8s %s%n", result.getCode(),
                    shorten(result.getExpected(), 10), shorten(result.getActual(), 10),
                    result.isPass() ? "PASS" : "FAIL", result.getTitle());
        }
        System.out.println("\nTOTAL: " + passed + "/" + lastTestResults.size() + " PASS");
    }

    private static String shorten(String text, int max) {
        if (text == null) {
            return "-";
        }
        return text.length() <= max ? text : text.substring(0, Math.max(0, max - 1)) + "~";
    }

    public void configAndHelper() {
        printHeader("DINH DANG INPUT / HUONG DAN THUAT TOAN");
        System.out.println("1. DINH DANG FILE DATASET");
        System.out.println("   Moi dong: u v capacity weight");
        System.out.println("   - u, v      : ten dinh (khong khoang trang), vd WH, A, ST");
        System.out.println("   - capacity  : so nguyen >= 0");
        System.out.println("   - weight    : so thuc huu han >= 0");
        System.out.println("   - Dong bat dau bang '#' la chu thich; dong trong bi bo qua.");
        System.out.println("   Vi du: WH A 15 4.5");
        System.out.println("\n   Quy uoc dataset:");
        System.out.println("   - Do thi co huong, khong self-loop, khong cung trung.");
        System.out.println("   - Khong dung dong thoi (u,v) va (v,u).");
        System.out.println("   - Capacity = 0 la tuyen khong kha dung.");
        System.out.println("   - Source mac dinh = WH, Sink mac dinh = ST.");

        System.out.println("\n2. FORD-FULKERSON (BFS / Edmonds-Karp)");
        System.out.println("   - Moi lan chay tao residual graph moi tu capacity goc.");
        System.out.println("   - Lap: tim augmenting path bang BFS tren residual, tinh bottleneck,");
        System.out.println("     cap nhat residual thuan/nguoc va flow tren cung vat ly.");
        System.out.println("   - Dung khi khong con augmenting path; kiem chung 0<=f<=c va bao toan.");

        System.out.println("\n3. DIJKSTRA");
        System.out.println("   - Chay tren graph goc, chi dung cung capacity > 0 va weight >= 0.");
        System.out.println("   - Tinh dist tu Source den moi dinh reachable, luu predecessor.");
        System.out.println("   - Target chi de truy vet path; Source = Target -> cost 0.");
        System.out.println("   - Weight am -> INVALID FOR DIJKSTRA; khong reachable -> NO PATH.");

        System.out.println("\n4. TRANG THAI");
        System.out.println("   SUCCESS / NO PATH / MAX FLOW REACHED / MAX FLOW = 0 /");
        System.out.println("   INVALID CAPACITY / INVALID FOR DIJKSTRA / INVALID INPUT / NOT LOADED.");

        System.out.println("\n5. CAY MENU");
        System.out.println("   Main Menu -> [1] Quan ly du lieu (menu 2)");
        System.out.println("             -> [2] Chay Max Flow / Dijkstra (menu 3)");
        System.out.println("             -> [3] Ket qua / Kiem chung (menu 4)");
        System.out.println("             -> [4] Kiem thu / Huong dan (menu 5)");
        System.out.println("             -> [0] Thoat");
    }

    public void displayProjectInfo() {
        printHeader("THONG TIN PROJECT");
        System.out.println("Ten project : PROJECT 04 - GOODS DISTRIBUTION NETWORK");
        System.out.println("Mon hoc     : Ly thuyet do thi (CSC106400)");
        System.out.println("Noi dung    : Phan tich nang luc van chuyen va tuyen giao hang");
        System.out.println("Thuat toan  : Ford-Fulkerson (BFS/Edmonds-Karp) + Dijkstra");
        System.out.println("Ngon ngu    : Java 21 (console)");
        System.out.println("Dataset     : main, demo, reverse-residual, flow=0, zero-cap, no-path");
        System.out.println("Kiem thu    : " + TestRunner.CATALOG.size() + " testcase bat buoc");
        System.out.println("Nhom        : [Cap nhat ten thanh vien]");
        System.out.println("AI          : Co su dung AI co kiem chung (xem AI_Prompt_Log).");
        System.out.println("\nChay chuong trinh: java -cp bin Main");
        System.out.println("Chay self-test   : java -cp bin Main --selftest");
    }

    // =====================================================================
    // Formatting helpers
    // =====================================================================

    private static String formatDouble(double value) {
        if (Double.isInfinite(value)) {
            return "INF";
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private String pathNames(List<Integer> path) {
        List<String> names = new ArrayList<>();
        for (int id : path) {
            names.add(network.nameOf(id));
        }
        return String.join(" -> ", names);
    }

    private String pathWithReverseMarks(FordFulkerson.AugmentingPathStep step) {
        List<Integer> path = step.getPath();
        StringBuilder text = new StringBuilder(network.nameOf(path.get(0)));
        for (int i = 0; i < path.size() - 1; i++) {
            text.append(" -> ").append(network.nameOf(path.get(i + 1)));
            if (step.getReverseFlags().get(i)) {
                text.append("[R]");
            }
        }
        return text.toString();
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(78));
        System.out.println("  " + title);
        System.out.println("=".repeat(78));
    }
}
