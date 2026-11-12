package algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import models.Graph;
import models.ResidualGraph;

/**
 * Ford-Fulkerson voi BFS de chon duong tang (bien the Edmonds-Karp).
 *
 * <p>
 * Moi lan run tao mot {@link ResidualGraph} moi tu capacity goc. Do thi goc
 * khong bi thay doi. Ket qua gom max flow, flow tren tung cung, danh sach duong
 * tang, bottleneck va thong tin cung nguoc du da dung.
 * </p>
 */
public final class FordFulkerson {

	public static final class AugmentingPathStep {
		private final int index;
		private final List<Integer> path;
		private final int bottleneck;
		private final List<Boolean> reverseFlags;

		public AugmentingPathStep(int index, List<Integer> path, int bottleneck, List<Boolean> reverseFlags) {
			this.index = index;
			this.path = List.copyOf(path);
			this.bottleneck = bottleneck;
			this.reverseFlags = List.copyOf(reverseFlags);
		}

		public int getIndex() {
			return index;
		}

		public List<Integer> getPath() {
			return path;
		}

		public int getBottleneck() {
			return bottleneck;
		}

		/** reverseFlags.get(i) tuong ung voi buoc path[i] -> path[i+1]. */
		public List<Boolean> getReverseFlags() {
			return reverseFlags;
		}

		public boolean usesReverseArc() {
			return reverseFlags.contains(Boolean.TRUE);
		}
	}

	public static final class MaxFlowResult {
		private final int maxFlow;
		private final ResidualGraph residualGraph;
		private final List<AugmentingPathStep> steps;
		private final boolean success;
		private final String message;
		private final boolean usedReverseArc;
		private final int reverseArcVisits;

		public MaxFlowResult(int maxFlow, ResidualGraph residualGraph, List<AugmentingPathStep> steps, boolean success,
				String message, boolean usedReverseArc, int reverseArcVisits) {
			this.maxFlow = maxFlow;
			this.residualGraph = residualGraph;
			this.steps = steps == null ? List.of() : List.copyOf(steps);
			this.success = success;
			this.message = message;
			this.usedReverseArc = usedReverseArc;
			this.reverseArcVisits = reverseArcVisits;
		}

		public int getMaxFlow() {
			return maxFlow;
		}

		public ResidualGraph getResidualGraph() {
			return residualGraph;
		}

		public List<AugmentingPathStep> getSteps() {
			return steps;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}

		public boolean usedReverseArc() {
			return usedReverseArc;
		}

		public int getReverseArcVisits() {
			return reverseArcVisits;
		}
	}

	private FordFulkerson() {
	}

	public static MaxFlowResult run(Graph graph, int source, int sink) {
		int n = graph.getN();
		if (source <= 0 || source > n || sink <= 0 || sink > n) {
			return new MaxFlowResult(0, null, null, false, "INVALID ENDPOINTS", false, 0);
		}
		if (source == sink) {
			return new MaxFlowResult(0, null, null, false,
					"INVALID ENDPOINTS: Source phai khac Sink cho bai toan Maximum Flow", false, 0);
		}

		ResidualGraph residual = new ResidualGraph(graph);
		List<AugmentingPathStep> steps = new ArrayList<>();
		int maxFlow = 0;
		boolean usedReverse = false;
		int reverseVisits = 0;

		while (true) {
			int[] parent = new int[n + 1];
			Arrays.fill(parent, -1);
			boolean[] visited = new boolean[n + 1];
			Queue<Integer> queue = new ArrayDeque<>();

			visited[source] = true;
			parent[source] = 0;
			queue.add(source);
			boolean found = false;

			while (!queue.isEmpty()) {
				int u = queue.poll();
				if (u == sink) {
					found = true;
					break;
				}
				for (int v = 1; v <= n; v++) {
					if (!visited[v] && residual.getResidualCapacity(u, v) > 0) {
						visited[v] = true;
						parent[v] = u;
						queue.add(v);
					}
				}
			}

			if (!found) {
				break;
			}

			int bottleneck = Integer.MAX_VALUE;
			for (int v = sink; v != source; v = parent[v]) {
				bottleneck = Math.min(bottleneck, residual.getResidualCapacity(parent[v], v));
			}

			List<Integer> path = new ArrayList<>();
			List<Boolean> reverseFlags = new ArrayList<>();
			for (int v = sink; v != source; v = parent[v]) {
				path.add(v);
			}
			path.add(source);
			java.util.Collections.reverse(path);

			for (int i = 0; i < path.size() - 1; i++) {
				int u = path.get(i);
				int v = path.get(i + 1);
				boolean reverse = !graph.hasArc(u, v) && graph.hasArc(v, u);
				reverseFlags.add(reverse);
				if (reverse) {
					usedReverse = true;
					reverseVisits++;
				}
			}

			residual.augmentPath(path, bottleneck);
			maxFlow += bottleneck;
			steps.add(new AugmentingPathStep(steps.size() + 1, path, bottleneck, reverseFlags));
		}

		String message = maxFlow == 0 ? "MAX FLOW = 0 (Source khong toi duoc Sink)"
				: "MAX FLOW REACHED (khong con augmenting path)";
		return new MaxFlowResult(maxFlow, residual, steps, true, message, usedReverse, reverseVisits);
	}
}
