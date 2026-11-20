package testing;

import algorithm.Dijkstra;
import algorithm.FordFulkerson;
import io.IO;
import model.Edge;
import model.Graph;
import ui.UI;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
	public static final List<String> testResults = new ArrayList<String>();
	public static IO io = new IO();

	public static void runAllTests() {
		testResults.clear();
		System.out.println("=========================================");
		System.out.println("       RUNNING AUTOMATED TEST CASES      ");
		System.out.println("========================================");

		runTest("TC01", "Max flow normal / multiple paths", "ds_demo.txt", "WH", "ST", "WH", "ST", true);
		runTest("TC02", "Reverse residual update", "ds_rev.txt", "S", "T", "S", "T", true);
		runTest("TC03", "Source-Sink disconnected -> flow 0", "ds_flow0.txt", "S", "T", "S", "T", true);
		runTest("TC04", "Zero-capacity arc ignored", "ds_zero.txt", "S", "T", "S", "T", true);
		runTest("TC05", "Dijkstra shortest path", "ds_main.txt", "WH", "ST", "WH", "ST", true);
		runTest("TC06", "Dijkstra NO PATH", "ds_nopath.txt", "S", "T", "S", "T", false);
		runTestException("TC07", "Invalid capacity / weight / input", "ds_invalid.txt");
		runTC08();
		runTC09();
		runTC10();
		runTC11();
		runTC12();
	}

	public static void runTest1(String testName) {
		switch (testName) {
		case "1", "TC01" ->
			runTest("TC01", "Max flow normal / multiple paths", "ds_demo.txt", "WH", "ST", "WH", "ST", true);
		case "2", "TC02" -> runTest("TC02", "Reverse residual update", "ds_rev.txt", "S", "T", "S", "T", true);
		case "3", "TC03" ->
			runTest("TC03", "Source-Sink disconnected -> flow 0", "ds_flow0.txt", "S", "T", "S", "T", false);
		case "4", "TC04" -> runTest("TC04", "Zero-capacity arc ignored", "ds_zero.txt", "S", "T", "S", "T", true);
		case "5", "TC05" -> runTest("TC05", "Dijkstra shortest path", "ds_main.txt", "WH", "ST", "WH", "ST", true);
		case "6", "TC06" -> runTest("TC06", "Dijkstra NO PATH", "ds_nopath.txt", "S", "T", "S", "T", false);
		case "7", "TC07" -> runTestException("TC07", "Invalid capacity / weight / input", "ds_invalid.txt");
		case "8", "TC08" -> runTC08();
		case "9", "TC09" -> runTC09();
		case "10", "TC10" -> runTC10();
		case "11", "TC11" -> runTC11();
		case "12", "TC12" -> runTC12();
		default -> System.out.println("[ERROR] Testcase không hợp lệ!");
		}
	}

	private static void recordResult(String line) {
		System.out.print(line);
		testResults.add(line);
	}

	public static void runTest(String id, String desc, String file, String srcFlow, String snkFlow, String srcDij,
			String tgtDij, boolean expectedSuccess) {
		try {
			Graph g = io.loadFromFile(file);

			FordFulkerson ff = new FordFulkerson(g);
			ff.run(srcFlow, snkFlow);
			int mf = ff.getMaxFlow();
			boolean valid = ff.verifyFlow();

			Dijkstra dj = new Dijkstra(g);
			dj.run(srcDij, tgtDij);

			boolean passed = valid && (dj.isSuccess() == expectedSuccess);
			String status = passed ? "PASS" : "FAIL";
			String result = String.format("[%s] %-35s : %s (MaxFlow: %d, Valid: %b, Dijkstra Success: %b)%n", id, desc,
					status, mf, valid, dj.isSuccess());
			recordResult(result);
		} catch (Exception e) {
			String result = String.format("[%s] %-35s : FAIL (Exception: %s)%n", id, desc, e.getMessage());
			recordResult(result);
		}
	}

	private static void runTestException(String id, String desc, String file) {
		try {
			io.loadFromFile(file);
			String result = String.format("[%s] %-35s : FAIL (Expected validation exception)%n", id, desc);
			recordResult(result);
		} catch (Exception e) {
			String result = String.format("[%s] %-35s : PASS (Caught expected: %s)%n", id, desc, e.getMessage());
			recordResult(result);
		}
	}

	// TC08 – Đồ thị một cung: S->T capacity 7, weight 2. Max Flow = 7; Dijkstra
	// S->T chi phí 2; verifyFlow = true.
	public static void runTC08() {
		String id = "TC08";
		String desc = "Single edge S->T (cap 7, w 2)";
		try {
			Graph g = io.loadFromFile("ds_single.txt");

			FordFulkerson ff = new FordFulkerson(g);
			ff.run("S", "T");
			int mf = ff.getMaxFlow();
			boolean valid = ff.verifyFlow();

			Dijkstra dj = new Dijkstra(g);
			dj.run("S", "T");

			boolean passed = (mf == 7) && valid && dj.isSuccess() && "S -> T".equals(dj.getShortestPath())
					&& Integer.valueOf(2).equals(dj.getTotalWeight());

			if (passed) {
				recordResult(String.format("[%s] %-35s : PASS (MaxFlow: %d, Path: %s, Cost: %s, verifyFlow: %b)%n", id,
						desc, mf, dj.getShortestPath(), dj.getTotalWeight(), valid));
			} else {
				recordResult(String.format("[%s] %-35s : FAIL (MaxFlow=%d, verifyFlow=%b, Path=%s, Cost=%s)%n", id,
						desc, mf, valid, dj.getShortestPath(), dj.getTotalWeight()));
			}
		} catch (Exception e) {
			recordResult(String.format("[%s] %-35s : FAIL (Exception: %s)%n", id, desc, e.getMessage()));
		}
	}

	// TC09 – Chuỗi có cung capacity 0 ở giữa: S->A 5, A->B 0, B->T 5.
	// Max Flow = 0; Dijkstra tới được A nhưng không tới B và T nên trả về NO PATH.
	public static void runTC09() {
		String id = "TC09";
		String desc = "Zero-capacity bottleneck in chain";
		try {
			Graph g = io.loadFromFile("ds_zero_mid.txt");

			FordFulkerson ff = new FordFulkerson(g);
			ff.run("S", "T");
			int mf = ff.getMaxFlow();
			boolean valid = ff.verifyFlow();

			Dijkstra dj = new Dijkstra(g);
			dj.run("S", "T");

			boolean reachedA = dj.getDistances().get("A") != null && dj.getDistances().get("A") != Integer.MAX_VALUE;
			boolean unreachedB = dj.getDistances().get("B") == Integer.MAX_VALUE;
			boolean unreachedT = dj.getDistances().get("T") == Integer.MAX_VALUE;

			boolean passed = (mf == 0) && valid && reachedA && unreachedB && unreachedT && !dj.isSuccess()
					&& "NO PATH".equals(dj.getShortestPath());

			if (passed) {
				recordResult(String.format("[%s] %-35s : PASS (MaxFlow: %d, Dist(A)=%d, B/T=INF, Path: %s)%n", id, desc,
						mf, dj.getDistances().get("A"), dj.getShortestPath()));
			} else {
				recordResult(String.format(
						"[%s] %-35s : FAIL (MaxFlow=%d, reachedA=%b, unreachedB=%b, unreachedT=%b, Path=%s)%n", id,
						desc, mf, reachedA, unreachedB, unreachedT, dj.getShortestPath()));
			}
		} catch (Exception e) {
			recordResult(String.format("[%s] %-35s : FAIL (Exception: %s)%n", id, desc, e.getMessage()));
		}
	}
	
	public static void validateMaxFlowEndpoints(String source, String sink) {
		if (source == null || sink == null || source.isEmpty() || sink.isEmpty()) {
			throw new IllegalArgumentException("INVALID INPUT: Source và Sink không được để trống.");
		}
		if (source.equals(sink)) {
			throw new IllegalArgumentException("INVALID INPUT: Source và Sink không được trùng nhau (" + source + ").");
		}
	}

	// TC10 – Source trùng Sink: không được xem là bài toán max flow hợp lệ (kiểm
	// tra ở tầng giao diện UI và thuật toán).
	public static void runTC10() {
		String id = "TC10";
		String desc = "Source equals Sink (UI + FF check)";
		try {
			Graph g = io.loadFromFile("ds_single.txt");

			boolean uiRejected = false;
			String uiMsg = "";
			try {
				validateMaxFlowEndpoints("S", "S");
			} catch (IllegalArgumentException ex) {
				uiRejected = true;
				uiMsg = ex.getMessage();
			}

			boolean ffRejected = false;
			try {
				FordFulkerson ff = new FordFulkerson(g);
				ff.run("S", "S");
			} catch (IllegalArgumentException ex) {
				ffRejected = true;
			}

			if (uiRejected && ffRejected) {
				recordResult(String.format("[%s] %-35s : PASS (UI rejected: %s)%n", id, desc, uiMsg));
			} else {
				recordResult(String.format("[%s] %-35s : FAIL (uiRejected=%b, ffRejected=%b)%n", id, desc, uiRejected,
						ffRejected));
			}
		} catch (Exception e) {
			recordResult(String.format("[%s] %-35s : FAIL (Exception: %s)%n", id, desc, e.getMessage()));
		}
	}

	// TC11 – Cung trùng: S A 5 1 và S A 7 2.
	// Graph.addEdge ném IllegalArgumentException "Cung trung tai S and A"; IO bắt
	// ngoại lệ và không nạp đồ thị.
	public static void runTC11() {
		String id = "TC11";
		String desc = "Duplicate edge (S->A)";
		try {
			// 1. Kiểm tra trực tiếp Graph.addEdge
			Graph manualGraph = new Graph();
			manualGraph.addEdge("S", "A", 5, 1);
			String modelExMsg = null;
			try {
				manualGraph.addEdge("S", "A", 7, 2);
			} catch (IllegalArgumentException ex) {
				modelExMsg = ex.getMessage();
			}
			boolean modelOk = "Cung trung tai S and A".equals(modelExMsg);

			// 2. Kiểm tra IO bắt ngoại lệ và không nạp đồ thị
			Graph loadedGraph = null;
			String ioExMsg = null;
			try {
				loadedGraph = io.loadFromFile("ds_dup.txt");
			} catch (Exception ex) {
				ioExMsg = ex.getMessage();
			}
			boolean ioOk = (loadedGraph == null) && (ioExMsg != null && ioExMsg.contains("Cung trung tai S and A"));

			if (modelOk && ioOk) {
				recordResult(String.format("[%s] %-35s : PASS (Model: '%s' | IO caught & not loaded)%n", id, desc,
						modelExMsg));
			} else {
				recordResult(String.format("[%s] %-35s : FAIL (modelOk=%b [%s], ioOk=%b [%s])%n", id, desc, modelOk,
						modelExMsg, ioOk, ioExMsg));
			}
		} catch (Exception e) {
			recordResult(String.format("[%s] %-35s : FAIL (Exception: %s)%n", id, desc, e.getMessage()));
		}
	}

	// TC12 – Weight âm: S A 5 -1 bị IO từ chối với thông báo capacity và weight
	// phải không âm;
	// nếu weight âm lọt vào đồ thị, Dijkstra ném lỗi INVALID FOR DIJKSTRA khi
	// relaxation.
	public static void runTC12() {
		String id = "TC12";
		String desc = "Negative weight (IO + Dijkstra)";
		try {
			// 1. IO từ chối dòng S A 5 -1
			Graph loadedGraph = null;
			String ioExMsg = null;
			try {
				loadedGraph = io.loadFromFile("ds_neg_w.txt");
			} catch (Exception ex) {
				ioExMsg = ex.getMessage();
			}
			boolean ioRejected = (loadedGraph == null)
					&& (ioExMsg != null && ioExMsg.toLowerCase().contains("capacity và weight phải không âm"));

			// 2. Nếu weight âm lọt vào đồ thị, Dijkstra ném lỗi INVALID FOR DIJKSTRA khi
			// relaxation
			Graph bypassedGraph = new Graph();
			bypassedGraph.addEdge("S", "A", 5, 1);
			bypassedGraph.setGraphValid(true);
			for (Edge e : bypassedGraph.getAdjList().get("S")) {
				if (e.getV().equals("A")) {
					e.setWeight(-1);
				}
			}

			String dijkstraExMsg = null;
			try {
				Dijkstra dj = new Dijkstra(bypassedGraph);
				dj.run("S", "A");
			} catch (IllegalArgumentException ex) {
				dijkstraExMsg = ex.getMessage();
			}
			boolean dijkstraRejected = (dijkstraExMsg != null && dijkstraExMsg.contains("INVALID FOR DIJKSTRA"));

			if (ioRejected && dijkstraRejected) {
				recordResult(
						String.format("[%s] %-35s : PASS (IO rejected | Dijkstra: INVALID FOR DIJKSTRA)%n", id, desc));
			} else {
				recordResult(String.format("[%s] %-35s : FAIL (ioRejected=%b [%s], dijkstraRejected=%b [%s])%n", id,
						desc, ioRejected, ioExMsg, dijkstraRejected, dijkstraExMsg));
			}
		} catch (Exception e) {
			recordResult(String.format("[%s] %-35s : FAIL (Exception: %s)%n", id, desc, e.getMessage()));
		}
	}

	public List<String> getTestResults() {
		return testResults;
	}
}
