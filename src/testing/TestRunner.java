package testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import algorithm.Dijkstra;
import algorithm.FordFulkerson;
import io.InputOuputManager;
import models.Graph;
import models.Network;
import models.ResidualGraph;

/**
 * Bo test case bat buoc cua Project 04.
 *
 * <p>Moi test co Expected/Actual/Pass va chi tiet kiem chung. Cac test doc
 * dataset tu thu muc data/ va chay thuat toan that, khong hard-code ket qua
 * cua chuong trinh.</p>
 */
public final class TestRunner {

    public static final class CaseResult {
        private final String code;
        private final String title;
        private final String expected;
        private final String actual;
        private final boolean pass;
        private final List<String> details;

        public CaseResult(String code, String title, String expected, String actual,
                boolean pass, List<String> details) {
            this.code = code;
            this.title = title;
            this.expected = expected;
            this.actual = actual;
            this.pass = pass;
            this.details = List.copyOf(details);
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public String getExpected() {
            return expected;
        }

        public String getActual() {
            return actual;
        }

        public boolean isPass() {
            return pass;
        }

        public List<String> getDetails() {
            return details;
        }
    }

    public record TestInfo(String code, String title) {
    }

    public static final List<TestInfo> CATALOG = List.of(
            new TestInfo("TC01", "Ford-Fulkerson tren dataset chinh (max flow = 35)"),
            new TestInfo("TC02", "Residual reverse update (DS-REV)"),
            new TestInfo("TC03", "Source-Sink khong reachable (max flow = 0)"),
            new TestInfo("TC04", "Cung capacity = 0 khong truyen flow / Dijkstra bo qua"),
            new TestInfo("TC05", "Dijkstra shortest path binh thuong (DS-DEMO)"),
            new TestInfo("TC06", "Dijkstra NO PATH (DS-NOPATH)"),
            new TestInfo("TC07", "Validation dataset khong hop le"),
            new TestInfo("TC08", "Source = Sink (Dijkstra cost 0, max flow bi tu choi)"),
            new TestInfo("TC09", "Dijkstra tren dataset chinh + chieu nguoc NO PATH"));

    private static final double EPS = 1e-9;

    private TestRunner() {
    }

    public static List<CaseResult> runAll() {
        List<CaseResult> results = new ArrayList<>();
        results.add(tc01());
        results.add(tc02());
        results.add(tc03());
        results.add(tc04());
        results.add(tc05());
        results.add(tc06());
        results.add(tc07());
        results.add(tc08());
        results.add(tc09());
        return results;
    }

    public static CaseResult run(String code) {
        for (CaseResult result : runAll()) {
            if (result.getCode().equalsIgnoreCase(code)) {
                return result;
            }
        }
        return null;
    }

    private static InputOuputManager.LoadResult load(String fileName) {
        return new InputOuputManager().load(fileName);
    }

    private static String pathNames(Network network, List<Integer> path) {
        List<String> names = new ArrayList<>();
        for (int id : path) {
            names.add(network.nameOf(id));
        }
        return String.join(" -> ", names);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static CaseResult tc01() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("main_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC01", CATALOG.get(0).title(), "max flow = 35",
                    "LOAD FAILED", false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int source = network.idOf("WH");
        int sink = network.idOf("ST");
        FordFulkerson.MaxFlowResult result = FordFulkerson.run(network.getGraph(), source, sink);
        boolean sizeOk = network.vertexCount() >= 18 && network.arcCount() >= 30;
        boolean flowOk = result.isSuccess() && result.getMaxFlow() == 35;
        boolean verifyOk = result.getResidualGraph() != null
                && result.getResidualGraph().verifyFlow(source, sink);

        details.add("V=" + network.vertexCount() + ", E=" + network.arcCount()
                + " (yeu cau V>=18, E>=30)");
        details.add("Max flow = " + result.getMaxFlow() + ", augmenting paths = "
                + result.getSteps().size());
        details.add("verifyFlow (0<=f<=c, conservation, source/sink) = " + verifyOk);

        boolean pass = sizeOk && flowOk && verifyOk;
        return new CaseResult("TC01", CATALOG.get(0).title(), "max flow = 35 va flow hop le",
                "max flow = " + result.getMaxFlow() + ", verify = " + verifyOk, pass, details);
    }

    private static CaseResult tc02() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("residual_reverse_update_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC02", CATALOG.get(1).title(), "max flow = 8, co dung cung nguoc",
                    "LOAD FAILED", false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int source = network.idOf("WH");
        int sink = network.idOf("ST");
        FordFulkerson.MaxFlowResult result = FordFulkerson.run(network.getGraph(), source, sink);

        boolean reverseStep = false;
        for (FordFulkerson.AugmentingPathStep step : result.getSteps()) {
            if (step.usesReverseArc()) {
                reverseStep = true;
                details.add("Duong tang co cung nguoc: " + pathNames(network, step.getPath())
                        + " (bottleneck=" + step.getBottleneck() + ")");
            }
        }
        boolean flowOk = result.isSuccess() && result.getMaxFlow() == 8;
        boolean reverseOk = result.usedReverseArc() && reverseStep;
        boolean verifyOk = result.getResidualGraph() != null
                && result.getResidualGraph().verifyFlow(source, sink);

        Dijkstra.Result dijkstra = Dijkstra.run(network.getGraph(), source, sink);
        boolean dijkstraOk = dijkstra.isSuccess() && Math.abs(dijkstra.getTotalCost() - 5.0) < EPS;

        details.add("Max flow = " + result.getMaxFlow() + ", so lan dung cung nguoc = "
                + result.getReverseArcVisits());
        details.add("verifyFlow = " + verifyOk);
        details.add("Dijkstra cost = " + round(dijkstra.getTotalCost()) + " (duong "
                + pathNames(network, dijkstra.getPath()) + ")");

        boolean pass = flowOk && reverseOk && verifyOk && dijkstraOk;
        return new CaseResult("TC02", CATALOG.get(1).title(),
                "max flow = 8, reverse arc duoc dung, verify PASS",
                "max flow = " + result.getMaxFlow() + ", reverse = " + reverseOk, pass, details);
    }

    private static CaseResult tc03() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("source_sink_disconnected_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC03", CATALOG.get(2).title(), "max flow = 0", "LOAD FAILED",
                    false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int source = network.idOf("WH");
        int sink = network.idOf("ST");
        FordFulkerson.MaxFlowResult flow = FordFulkerson.run(network.getGraph(), source, sink);
        Dijkstra.Result dijkstra = Dijkstra.run(network.getGraph(), source, sink);

        boolean flowOk = flow.isSuccess() && flow.getMaxFlow() == 0;
        boolean noPathOk = dijkstra.getStatus() == Dijkstra.Status.NO_PATH;
        details.add("Max flow = " + flow.getMaxFlow() + " (ket thuc hop le, khong phai loi)");
        details.add("Dijkstra status = " + dijkstra.getStatus().label());
        details.add("verifyFlow = " + flow.getResidualGraph().verifyFlow(source, sink));

        return new CaseResult("TC03", CATALOG.get(2).title(),
                "max flow = 0, Dijkstra NO PATH",
                "max flow = " + flow.getMaxFlow() + ", Dijkstra = " + dijkstra.getStatus().label(),
                flowOk && noPathOk, details);
    }

    private static CaseResult tc04() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("zero_capacity_arc_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC04", CATALOG.get(3).title(), "max flow = 10, Dijkstra cost = 9",
                    "LOAD FAILED", false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int source = network.idOf("WH");
        int sink = network.idOf("ST");
        int b = network.idOf("B");
        FordFulkerson.MaxFlowResult flow = FordFulkerson.run(network.getGraph(), source, sink);
        Dijkstra.Result dijkstra = Dijkstra.run(network.getGraph(), source, sink);
        ResidualGraph residual = flow.getResidualGraph();

        boolean zeroFlowOk = residual.getFlow(source, b) == 0
                && residual.getResidualCapacity(source, b) == 0;
        boolean flowOk = flow.isSuccess() && flow.getMaxFlow() == 10 && zeroFlowOk;
        boolean dijkstraOk = dijkstra.isSuccess()
                && Math.abs(dijkstra.getTotalCost() - 9.0) < EPS
                && Double.isInfinite(dijkstra.getDistances()[b]);
        details.add("zero-capacity count = " + network.zeroCapacityCount());
        details.add("flow tren WH->B = " + residual.getFlow(source, b)
                + ", residual = " + residual.getResidualCapacity(source, b));
        details.add("Max flow = " + flow.getMaxFlow());
        details.add("Dijkstra cost = " + round(dijkstra.getTotalCost())
                + ", dist[B] = " + (Double.isInfinite(dijkstra.getDistances()[b]) ? "INF" : "reachable"));

        boolean pass = flowOk && dijkstraOk;
        return new CaseResult("TC04", CATALOG.get(3).title(),
                "flow WH->B = 0, max flow = 10, Dijkstra cost = 9",
                "flow WH->B = " + residual.getFlow(source, b) + ", max flow = " + flow.getMaxFlow()
                        + ", Dijkstra cost = " + round(dijkstra.getTotalCost()),
                pass, details);
    }

    private static CaseResult tc05() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("demo_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC05", CATALOG.get(4).title(), "path WH-A-C-ST, cost 7.5",
                    "LOAD FAILED", false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int source = network.idOf("WH");
        int sink = network.idOf("ST");
        Dijkstra.Result result = Dijkstra.run(network.getGraph(), source, sink);
        String path = pathNames(network, result.getPath());
        boolean pathOk = path.equals("WH -> A -> C -> ST");
        boolean costOk = Math.abs(result.getTotalCost() - 7.5) < EPS;
        boolean predOk = true;
        for (int i = 1; i < result.getPath().size(); i++) {
            int u = result.getPath().get(i - 1);
            int v = result.getPath().get(i);
            predOk &= result.getPredecessors()[v] == u;
        }
        details.add("Distance WH->ST = " + round(result.getTotalCost()));
        details.add("Path = " + path);
        details.add("Predecessor nhat quan = " + predOk);
        details.add("Distances: WH=0, A=3.0, B=5.0, C=5.0, D=6.5, ST=7.5");

        boolean pass = result.isSuccess() && pathOk && costOk && predOk;
        return new CaseResult("TC05", CATALOG.get(4).title(), "path WH-A-C-ST, cost 7.5",
                "path " + path + ", cost " + round(result.getTotalCost()), pass, details);
    }

    private static CaseResult tc06() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("dijkstra_no_path_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC06", CATALOG.get(5).title(), "NO PATH", "LOAD FAILED",
                    false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int source = network.idOf("WH");
        int sink = network.idOf("ST");
        Dijkstra.Result result = Dijkstra.run(network.getGraph(), source, sink);
        boolean noPathOk = result.getStatus() == Dijkstra.Status.NO_PATH;
        boolean noFakePath = result.getPath().isEmpty() && result.getTotalCost() < 0;
        details.add("Status = " + result.getStatus().label());
        details.add("Path rong (khong tao path gia) = " + noFakePath);
        details.add("dist[ST] = INF = "
                + Double.isInfinite(result.getDistances()[sink]));

        boolean pass = noPathOk && noFakePath;
        return new CaseResult("TC06", CATALOG.get(5).title(), "NO PATH, khong co path gia",
                "status = " + result.getStatus().label(), pass, details);
    }

    private static CaseResult tc07() {
        List<String> details = new ArrayList<>();
        String[] keywords = { "capacity", "weight", "trung", "dong thoi", "self-loop", "dinh dang" };
        boolean allRejected = true;
        for (int i = 0; i < InputOuputManager.INVALID_DATASETS.size(); i++) {
            String file = InputOuputManager.INVALID_DATASETS.get(i);
            String keyword = keywords[i];
            InputOuputManager.LoadResult loaded = load(file);
            boolean rejected = !loaded.isSuccess();
            boolean keywordOk = rejected && loaded.getErrors().stream()
                    .anyMatch(e -> e.toLowerCase(Locale.ROOT).contains(keyword));
            allRejected &= rejected && keywordOk;
            details.add(file + " -> " + (rejected ? "REJECTED" : "ACCEPTED (SAI)")
                    + (keywordOk ? ", dung loai loi" : ", SAI loai loi")
                    + (rejected ? " [" + loaded.getErrors().get(0) + "]" : ""));
        }
        return new CaseResult("TC07", CATALOG.get(6).title(),
                "6/6 dataset loi bi tu choi dung loai loi",
                allRejected ? "6/6 bi tu choi" : "co dataset loi duoc chap nhan", allRejected, details);
    }

    private static CaseResult tc08() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("demo_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC08", CATALOG.get(7).title(), "FF rejected, Dijkstra cost 0",
                    "LOAD FAILED", false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int wh = network.idOf("WH");
        FordFulkerson.MaxFlowResult flow = FordFulkerson.run(network.getGraph(), wh, wh);
        Dijkstra.Result dijkstra = Dijkstra.run(network.getGraph(), wh, wh);
        boolean flowRejected = !flow.isSuccess();
        boolean dijkstraOk = dijkstra.isSuccess() && Math.abs(dijkstra.getTotalCost()) < EPS
                && dijkstra.getPath().size() == 1 && dijkstra.getPath().get(0) == wh;
        details.add("Ford-Fulkerson Source=Sink: success=" + flow.isSuccess()
                + " (" + flow.getMessage() + ")");
        details.add("Dijkstra Source=Target: cost=" + round(dijkstra.getTotalCost())
                + ", path=" + pathNames(network, dijkstra.getPath()));
        boolean pass = flowRejected && dijkstraOk;
        return new CaseResult("TC08", CATALOG.get(7).title(),
                "FF bi tu choi, Dijkstra cost 0 path [WH]",
                "FF success=" + flow.isSuccess() + ", Dijkstra cost=" + round(dijkstra.getTotalCost()),
                pass, details);
    }

    private static CaseResult tc09() {
        List<String> details = new ArrayList<>();
        InputOuputManager.LoadResult loaded = load("main_dataset.txt");
        if (!loaded.isSuccess()) {
            return new CaseResult("TC09", CATALOG.get(8).title(), "cost 20.5, A->WH NO PATH",
                    "LOAD FAILED", false, loaded.getErrors());
        }
        Network network = loaded.getNetwork();
        int wh = network.idOf("WH");
        int st = network.idOf("ST");
        int a = network.idOf("A");
        Dijkstra.Result forward = Dijkstra.run(network.getGraph(), wh, st);
        Dijkstra.Result backward = Dijkstra.run(network.getGraph(), a, wh);
        String path = pathNames(network, forward.getPath());
        boolean forwardOk = forward.isSuccess()
                && Math.abs(forward.getTotalCost() - 20.5) < EPS
                && path.equals("WH -> B -> E -> H -> K -> M -> P -> ST");
        boolean backwardOk = backward.getStatus() == Dijkstra.Status.NO_PATH;
        details.add("WH->ST: cost=" + round(forward.getTotalCost()) + ", path=" + path);
        details.add("A->WH: " + backward.getStatus().label() + " (do thi co huong)");
        return new CaseResult("TC09", CATALOG.get(8).title(),
                "WH->ST cost 20.5, A->WH NO PATH",
                "cost=" + round(forward.getTotalCost()) + ", A->WH="
                        + backward.getStatus().label(),
                forwardOk && backwardOk, details);
    }
}
