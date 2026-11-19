package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import model.Edge;
import model.Graph;

public class FordFulkerson {
	private Graph originalGraph;
	private String source;
	private String sink;
	private int maxFlow = 0;
	private boolean hasRun = false;

	private Map<String, Map<String, ResidualEdge>> residualGraph = new LinkedHashMap<>();
	private List<String> augmentingPaths = new ArrayList<>();
	private List<Integer> bottlenecks = new ArrayList<>();

	public static class ResidualEdge {
		public String u;
		public String v;
		public int cap;
		public ResidualEdge rev;

		public ResidualEdge(String u, String v, int cap) {
			this.u = u;
			this.v = v;
			this.cap = cap;
		}

		public int residual() {
			return cap;
		}
	}

	public void setGraph(Graph graph) {
		this.originalGraph = graph;
	}

	public void run(String source, String sink) {
		if (originalGraph == null || !originalGraph.isGraphValid() || originalGraph.getVertexCount() == 0) {
			throw new IllegalStateException("NOT LOADED: Chưa có dữ liệu đồ thị.");
		}
		if (!originalGraph.getVertices().contains(source) || !originalGraph.getVertices().contains(sink)) {
			throw new IllegalArgumentException("INVALID INPUT: Đỉnh Source hoặc Sink không tồn tại trong đồ thị.");
		}
		if (source.equals(sink)) {
			throw new IllegalArgumentException(
					"INVALID INPUT: Source và Sink phải khác nhau trong bài toán Maximum Flow.");
		}

		this.source = source;
		this.sink = sink;
		this.maxFlow = 0;
		this.augmentingPaths.clear();
		this.bottlenecks.clear();
		this.residualGraph.clear();
		this.originalGraph.resetFlows();

		// 1. Khởi tạo mới Residual Graph từ capacity của Graph gốc
		for (String v : originalGraph.getVertices()) {
			residualGraph.put(v, new LinkedHashMap<>());
		}
		for (Map.Entry<String, List<Edge>> entry : originalGraph.getAdjList().entrySet()) {
			String u = entry.getKey();
			for (Edge e : entry.getValue()) {
				ResidualEdge forward = new ResidualEdge(u, e.getV(), e.getCapacity());
				ResidualEdge backward = new ResidualEdge(e.getV(), u, 0);
				forward.rev = backward;
				backward.rev = forward;
				residualGraph.get(u).put(e.getV(), forward);
				residualGraph.get(e.getV()).put(u, backward);
			}
		}

		// 2. Lặp tìm đường tăng luồng (Augmenting Path) bằng BFS
		while (true) {
			Map<String, String> parentMap = new LinkedHashMap<>();
			Map<String, ResidualEdge> edgeMap = new LinkedHashMap<>();
			Queue<String> queue = new LinkedList<>();

			queue.add(source);
			parentMap.put(source, null);

			while (!queue.isEmpty() && !parentMap.containsKey(sink)) {
				String u = queue.poll();
				for (ResidualEdge edge : residualGraph.get(u).values()) {
					String v = edge.v;
					if (!parentMap.containsKey(v) && edge.residual() > 0) {
						parentMap.put(v, u);
						edgeMap.put(v, edge);
						queue.add(v);
					}
				}
			}

			// Không còn đường tăng luồng -> Kết thúc thuật toán
			if (!parentMap.containsKey(sink)) {
				break;
			}

			// 3. Tính bottleneck (residual capacity nhỏ nhất trên đường tăng)
			int bottleneck = Integer.MAX_VALUE;
			List<String> pathNodes = new ArrayList<>();
			String curr = sink;
			while (curr != null) {
				pathNodes.add(curr);
				if (!curr.equals(source)) {
					ResidualEdge e = edgeMap.get(curr);
					if (e.residual() < bottleneck) {
						bottleneck = e.residual();
					}
				}
				curr = parentMap.get(curr);
			}
			Collections.reverse(pathNodes);

			// 4. Cập nhật residual capacity trên cung thuận và cung ngược
			curr = sink;
			while (!curr.equals(source)) {
				ResidualEdge e = edgeMap.get(curr);
				e.cap -= bottleneck;
				e.rev.cap += bottleneck;
				curr = parentMap.get(curr);
			}

			maxFlow += bottleneck;
			augmentingPaths.add(String.join(" -> ", pathNodes));
			bottlenecks.add(bottleneck);
		}

		// 5. Ghi luồng (flow) thực tế về từng cung của đồ thị gốc
		for (Map.Entry<String, List<Edge>> entry : originalGraph.getAdjList().entrySet()) {
			String u = entry.getKey();
			for (Edge e : entry.getValue()) {
				ResidualEdge backward = residualGraph.get(e.getV()).get(u);
				if (backward != null) {
					e.setFlow(backward.cap);
				}
			}
		}

		hasRun = true;
	}

	public boolean verifyFlow(String source, String sink) {
		if (!hasRun || originalGraph == null) {
			return false;
		}
		// 1. Ràng buộc capacity: 0 <= f(u,v) <= c(u,v)
		for (List<Edge> edges : originalGraph.getAdjList().values()) {
			for (Edge e : edges) {
				if (e.getFlow() < 0 || e.getFlow() > e.getCapacity()) {
					return false;
				}
			}
		}
		// 2. Bảo toàn luồng tại mọi đỉnh trung gian
		Map<String, Integer> inflow = new LinkedHashMap<>();
		Map<String, Integer> outflow = new LinkedHashMap<>();
		for (String v : originalGraph.getVertices()) {
			inflow.put(v, 0);
			outflow.put(v, 0);
		}
		for (Map.Entry<String, List<Edge>> entry : originalGraph.getAdjList().entrySet()) {
			String u = entry.getKey();
			for (Edge e : entry.getValue()) {
				int f = e.getFlow();
				outflow.put(u, outflow.get(u) + f);
				inflow.put(e.getV(), inflow.get(e.getV()) + f);
			}
		}
		for (String v : originalGraph.getVertices()) {
			if (!v.equals(source) && !v.equals(sink) && !Objects.equals(inflow.get(v), outflow.get(v))) {
				return false;
			}
		}
		// 3. Cân bằng Source/Sink và bằng Max Flow
		return outflow.get(source) == maxFlow && inflow.get(sink) == maxFlow;
	}

	public String getMaxFlow() {
		return String.valueOf(maxFlow);
	}

	public int getMaxFlowValue() {
		return maxFlow;
	}

	public List<String> getAugmentingPaths() {
		return augmentingPaths;
	}

	public List<Integer> getBottlenecks() {
		return bottlenecks;
	}

	public void displayMaxFlowAndArcs() {
		if (!hasRun || originalGraph == null) {
			System.out.println("[INFO] Chưa có kết quả Ford-Fulkerson. Vui lòng chạy thuật toán ở Menu 3 trước.");
			return;
		}
		System.out.println("+================================================================+");
		System.out.println("|              MAX FLOW VÀ LUỒNG TRÊN CÁC CUNG GỐC               |");
		System.out.println("+================================================================+");
		System.out.println("Dataset : " + originalGraph.getGraphName() + " | Source: " + source + " | Sink: " + sink);
		System.out.println("Status  : MAX FLOW REACHED");
		System.out.println("Max Flow: " + maxFlow);
		System.out.println("------------------------------------------------------------------");
		System.out.println("Flow trên các cung gốc (Flow / Capacity):");
		for (List<Edge> edges : originalGraph.getAdjList().values()) {
			for (Edge e : edges) {
				System.out.printf("  (%s -> %s) Flow: %d / Cap: %d (Weight: %d)%n", e.getU(), e.getV(), e.getFlow(),
						e.getCapacity(), e.getWeight());
			}
		}
		System.out.println("+----------------------------------------------------------------+");
	}

	public void displayResidualAndPaths() {
		if (!hasRun || originalGraph == null) {
			System.out.println("[INFO] Chưa có kết quả Residual Graph. Vui lòng chạy Ford-Fulkerson ở Menu 3 trước.");
			return;
		}
		System.out.println("+================================================================+");
		System.out.println("|          RESIDUAL GRAPH / AUGMENTING PATHS / BOTTLENECK        |");
		System.out.println("+================================================================+");
		System.out.println("Dataset : " + originalGraph.getGraphName() + " | Source: " + source + " | Sink: " + sink);
		System.out.println("Residual initialized from ORIGINAL capacities.");
		if (augmentingPaths.isEmpty()) {
			System.out.println("Không có augmenting path nào từ Source tới Sink (Max Flow = 0).");
		} else {
			for (int i = 0; i < augmentingPaths.size(); i++) {
				System.out.printf("Path %d: %s | bottleneck = %d%n", (i + 1), augmentingPaths.get(i),
						bottlenecks.get(i));
			}
		}
		System.out.println("Status  : MAX FLOW REACHED | Max Flow = " + maxFlow);
		System.out.println("------------------------------------------------------------------");
		System.out.println("Residual Capacities sau khi kết thúc (Cung thuận & Cung ngược):");
		for (List<Edge> edges : originalGraph.getAdjList().values()) {
			for (Edge e : edges) {
				ResidualEdge fwd = residualGraph.get(e.getU()).get(e.getV());
				ResidualEdge bwd = residualGraph.get(e.getV()).get(e.getU());
				System.out.printf("  Cung gốc %s -> %s : residual thuận = %d | residual ngược (%s -> %s) = %d%n",
						e.getU(), e.getV(), fwd.residual(), e.getV(), e.getU(), bwd.residual());
			}
		}
		System.out.println("+----------------------------------------------------------------+");
	}

	public void displayVerification() {
		if (!hasRun || originalGraph == null) {
			System.out.println("[INFO] Chưa có kết quả để kiểm chứng. Vui lòng chạy Ford-Fulkerson trước.");
			return;
		}
		boolean valid = verifyFlow(source, sink);
		System.out.println("+================================================================+");
		System.out.println("|                       FLOW VERIFICATION                        |");
		System.out.println("+================================================================+");
		System.out.println("Capacity constraints (0 <= f <= c) : " + (valid ? "PASS" : "FAIL"));
		System.out.println("Flow conservation (Inflow=Outflow) : " + (valid ? "PASS" : "FAIL"));
		System.out.println("Source outflow                     : " + maxFlow);
		System.out.println("Sink inflow                        : " + maxFlow);
		System.out.println("Status                             : " + (valid ? "FLOW VALID" : "FLOW INVALID"));
		System.out.println("Note: residual reverse arcs are algorithmic, not physical routes.");
		System.out.println("+----------------------------------------------------------------+");
	}
}
