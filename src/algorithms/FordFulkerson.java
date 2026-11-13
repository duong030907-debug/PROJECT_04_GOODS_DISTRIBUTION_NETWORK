package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import models.Edge;
import models.Graph;

public class FordFulkerson {
	private static class ResidualEdge {
		String u, v;
		int capacity;
		int flow;
		ResidualEdge rev;

		ResidualEdge(String u, String v, int capacity) {
			this.u = u;
			this.v = v;
			this.capacity = capacity;
			this.flow = 0;
		}

		int residual() {
			return capacity - flow;
		}
	}

	private int maxFlow;
	private List<String> augmentingPaths;
	private List<Integer> bottlenecks;
	private Map<String, Map<String, ResidualEdge>> residualGraph;
	private Graph originalGraph;

	public FordFulkerson(Graph graph) {
		this.originalGraph = graph;
		this.augmentingPaths = new ArrayList<>();
		this.bottlenecks = new ArrayList<>();
		this.maxFlow = 0;
		this.residualGraph = new HashMap<>();
	}

	public int run(String source, String sink) {
		if (!originalGraph.getVertices().contains(source) || !originalGraph.getVertices().contains(sink)) {
			throw new IllegalArgumentException("Source hoặc Sink không tìm thất tronh graph.");
		}

		// xóa graph cũ chạy mới
		residualGraph.clear();
		for (String v : originalGraph.getVertices()) {
			residualGraph.put(v, new HashMap<>());
		}

		for (Map.Entry<String, List<Edge>> entry : originalGraph.getAdjList().entrySet()) {
			String u = entry.getKey();
			for (Edge e : entry.getValue()) {
				String v = e.v;
				int cap = e.capacity;

				ResidualEdge forward = new ResidualEdge(u, v, cap);
				ResidualEdge backward = new ResidualEdge(v, u, 0);
				forward.rev = backward;
				backward.rev = forward;

				residualGraph.get(u).put(v, forward);
				residualGraph.get(v).put(u, backward);
			}
		}

		maxFlow = 0;
		augmentingPaths.clear();
		bottlenecks.clear();

		while (true) {
			// init dữ liệu
			Map<String, String> parentMap = new HashMap<>();
			Map<String, ResidualEdge> edgeMap = new HashMap<>();
			Queue<String> queue = new LinkedList<>();
			// add source vô queue và map
			queue.add(source);
			parentMap.put(source, source);
			// queue không trống và chứa sink
			while (!queue.isEmpty() && !parentMap.containsKey(sink)) {
				// poll queue
				String u = queue.poll();
				//
				for (ResidualEdge edge : residualGraph.get(u).values()) {
					String v = edge.v;
					if (!parentMap.containsKey(v) && edge.residual() > 0) {
						parentMap.put(v, u);
						edgeMap.put(v, edge);
						queue.add(v);
					}
				}
			}

			if (!parentMap.containsKey(sink)) {
				break; // không tìm thấy path để sửa
			}

			// tìm bottleneck capacity
			int bottleneck = Integer.MAX_VALUE;
			String curr = sink;
			List<String> pathNodes = new ArrayList<>();
			pathNodes.add(sink);

			while (!curr.equals(source)) {
				String prev = parentMap.get(curr);
				ResidualEdge edge = residualGraph.get(prev).get(curr);
				bottleneck = Math.min(bottleneck, edge.residual());
				curr = prev;
				pathNodes.add(0, curr);
			}

			// sửa flow
			curr = sink;
			while (!curr.equals(source)) {
				String prev = parentMap.get(curr);
				ResidualEdge edge = residualGraph.get(prev).get(curr);
				edge.flow += bottleneck;
				edge.rev.flow -= bottleneck;
				curr = prev;
			}

			maxFlow += bottleneck;
			augmentingPaths.add(String.join(" -> ", pathNodes));
			bottlenecks.add(bottleneck);
		}

		//
		for (List<Edge> edges : originalGraph.getAdjList().values()) {
			for (Edge e : edges) {
				e.setFlow(0);
			}
		}

		for (String u : residualGraph.keySet()) {
			for (ResidualEdge re : residualGraph.get(u).values()) {
				if (re.flow > 0) {
					// Check is original
					for (Edge e : originalGraph.getAdjList().getOrDefault(u, Collections.emptyList())) {
						if (e.getV().equals(re.v)) {
							e.setFlow(re.flow);
						}
					}
				}
			}
		}

		return maxFlow;
	}

	public boolean verifyFlow(String source, String sink) {
		// 1. Capacity constraints: 0 <= f(u,v) <= c(u,v)
		for (List<Edge> edges : originalGraph.getAdjList().values()) {
			for (Edge e : edges) {
				if (e.getFlow() < 0 || e.getFlow() > e.getCapacity()) {
					return false;
				}
			}
		}

		// 2. Flow conservation at intermediate nodes
		Map<String, Integer> inflow = new HashMap<>();
		Map<String, Integer> outflow = new HashMap<>();

		for (String v : originalGraph.getVertices()) {
			inflow.put(v, 0);
			outflow.put(v, 0);
		}

		for (Map.Entry<String, List<Edge>> entry : originalGraph.getAdjList().entrySet()) {
			String u = entry.getKey();
			for (Edge e : entry.getValue()) {
				String v = e.getV();
				int f = e.getFlow();
				outflow.put(u, outflow.get(u) + f);
				inflow.put(v, inflow.get(v) + f);
			}
		}

		for (String v : originalGraph.getVertices()) {
			if (!v.equals(source) && !v.equals(sink)) {
				if (!Objects.equals(inflow.get(v), outflow.get(v))) {
					return false;
				}
			}
		}

		// 3. Source outflow == Sink inflow == maxFlow
		int srcOut = outflow.get(source);
		int sinkIn = inflow.get(sink);
		return srcOut == maxFlow && sinkIn == maxFlow;
	}

	public List<String> getAugmentingPaths() {
		return augmentingPaths;
	}

	public List<Integer> getBottlenecks() {
		return bottlenecks;
	}

}
