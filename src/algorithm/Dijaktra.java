package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import model.Edge;
import model.Graph;

public class Dijaktra {
	private Graph graph;
	private String source;
	private String target;
	private Map<String, Integer> distances = new LinkedHashMap<>();
	private Map<String, String> predecessors = new LinkedHashMap<>();
	private List<String> path = new ArrayList<>();
	private int totalWeight = -1;
	private boolean hasPath = false;

	private static class NodeDistance {
		String node;
		int distance;

		NodeDistance(String node, int distance) {
			this.node = node;
			this.distance = distance;
		}
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public void run(String source, String target) {
		if (graph == null || !graph.isGraphValid() || graph.getVertexCount() == 0) {
			throw new IllegalStateException("NOT LOADED: Chưa có dữ liệu đồ thị.");
		}
		if (!graph.getVertices().contains(source) || !graph.getVertices().contains(target)) {
			throw new IllegalArgumentException("INVALID INPUT: Đỉnh Source hoặc Target không tồn tại trong đồ thị.");
		}

		this.source = source;
		this.target = target;
		distances.clear();
		predecessors.clear();
		path.clear();
		totalWeight = -1;
		hasPath = false;

		for (String v : graph.getVertices()) {
			distances.put(v, Integer.MAX_VALUE);
			predecessors.put(v, null);
		}
		distances.put(source, 0);

		PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
		pq.add(new NodeDistance(source, 0));

		while (!pq.isEmpty()) {
			NodeDistance current = pq.poll();
			String u = current.node;

			if (current.distance > distances.get(u)) {
				continue;
			}

			List<Edge> edges = graph.getAdjList().getOrDefault(u, Collections.emptyList());
			for (Edge e : edges) {
				if (e.getCapacity() > 0) {
					if (e.getWeight() < 0) {
						throw new IllegalArgumentException("INVALID FOR DIJKSTRA: Phát hiện trọng số (weight) âm.");
					}
					int newDist = distances.get(u) + e.getWeight();
					if (newDist < distances.get(e.getV())) {
						distances.put(e.getV(), newDist);
						predecessors.put(e.getV(), u);
						pq.add(new NodeDistance(e.getV(), newDist));
					}
				}
			}
		}

		if (source.equals(target)) {
			hasPath = true;
			totalWeight = 0;
			path.add(source);
			return;
		}

		if (distances.get(target) != Integer.MAX_VALUE) {
			hasPath = true;
			totalWeight = distances.get(target);
			String curr = target;
			while (curr != null) {
				path.add(curr);
				curr = predecessors.get(curr);
			}
			Collections.reverse(path);
		} else {
			hasPath = false;
			totalWeight = -1;
		}
	}

	public String getShortestPath() {
		if (!hasPath) {
			return "NO PATH";
		}
		return String.join(" -> ", path);
	}

	public Object getTotalWeight() {
		if (!hasPath) {
			return "NO PATH";
		}
		return totalWeight;
	}

	public boolean isSuccess() {
		return hasPath;
	}

	public Map<String, Integer> getDistances() {
		return distances;
	}

	public Map<String, String> getPredecessors() {
		return predecessors;
	}

	public void displayResult() {
		if (graph == null || source == null) {
			System.out.println("[INFO] Chưa có kết quả Dijkstra. Vui lòng chạy thuật toán ở Menu 3 trước.");
			return;
		}
		System.out.println("+================================================================+");
		System.out.println("|                   DIJKSTRA - DELIVERY ROUTE                    |");
		System.out.println("+================================================================+");
		System.out.println("Dataset : " + graph.getGraphName() + " | Source: " + source + " | Target: " + target);
		System.out.println("Graph   : ORIGINAL graph only | usable arcs: capacity > 0");
		if (hasPath) {
			System.out.println("Status  : SUCCESS");
			System.out.println("Path    : " + getShortestPath());
			System.out.println("Total Cost : " + totalWeight);
		} else {
			System.out.println("Status  : NO PATH");
			System.out.println("Path    : NO PATH");
			System.out.println("Total Cost : N/A");
		}
		System.out.println("------------------------------------------------------------------");
		System.out.println("Bảng Distance & Predecessor (từ Source = " + source + "):");
		for (String v : graph.getVertices()) {
			int d = distances.getOrDefault(v, Integer.MAX_VALUE);
			String dStr = (d == Integer.MAX_VALUE) ? "INF" : String.valueOf(d);
			String pred = predecessors.get(v);
			String pStr = (pred == null) ? "None" : pred;
			System.out.printf("  Đỉnh %-6s : Dist = %-6s | Predecessor = %s%n", v, dStr, pStr);
		}
		System.out.println("+----------------------------------------------------------------+");
	}
}
