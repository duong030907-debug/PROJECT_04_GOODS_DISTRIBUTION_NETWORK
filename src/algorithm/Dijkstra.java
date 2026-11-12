package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import models.Arc;
import models.Graph;

/**
 * Dijkstra tren do thi goc.
 *
 * <p>Quy tac cua project:</p>
 * <ul>
 *   <li>Chi dung cung co capacity &gt; 0 va weight &gt;= 0.</li>
 *   <li>Khong dung residual capacity/flow lam trong so.</li>
 *   <li>Tinh dist tu Source den moi dinh reachable; Target chi de truy vet.</li>
 *   <li>Source = Target hop le voi cost 0 va path chi gom Source.</li>
 *   <li>Khong reachable -&gt; NO PATH; co weight am -&gt; INVALID FOR DIJKSTRA.</li>
 * </ul>
 */
public final class Dijkstra {

    public enum Status {
        SUCCESS("SUCCESS"),
        NO_PATH("NO PATH"),
        INVALID_FOR_DIJKSTRA("INVALID FOR DIJKSTRA"),
        INVALID_INPUT("INVALID INPUT");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public static final class Result {
        private final Status status;
        private final double totalCost;
        private final List<Integer> path;
        private final double[] distances;
        private final int[] predecessors;
        private final String message;

        public Result(Status status, double totalCost, List<Integer> path,
                double[] distances, int[] predecessors, String message) {
            this.status = status;
            this.totalCost = totalCost;
            this.path = List.copyOf(path);
            this.distances = distances;
            this.predecessors = predecessors;
            this.message = message;
        }

        public Status getStatus() {
            return status;
        }

        public boolean isSuccess() {
            return status == Status.SUCCESS;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public List<Integer> getPath() {
            return path;
        }

        public double[] getDistances() {
            return distances;
        }

        public int[] getPredecessors() {
            return predecessors;
        }

        public String getMessage() {
            return message;
        }
    }

    private record Node(int vertex, double distance) {
    }

    private Dijkstra() {
    }

    public static Result run(Graph graph, int source, int target) {
        int n = graph.getN();
        if (source <= 0 || source > n || target <= 0 || target > n) {
            return new Result(Status.INVALID_INPUT, -1, List.of(), null, null,
                    "Source/Target khong hop le");
        }

        // Chan weight am tren toan bo do thi goc truoc khi chay.
        for (Arc arc : graph.getAllArcs()) {
            if (arc.getWeight() < 0 || !Double.isFinite(arc.getWeight())) {
                return new Result(Status.INVALID_FOR_DIJKSTRA, -1, List.of(), null, null,
                        "Phat hien weight am/khong huu han tai cung ("
                                + arc.getU() + "," + arc.getV() + ")");
            }
        }

        double[] dist = new double[n + 1];
        int[] pred = new int[n + 1];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        boolean[] settled = new boolean[n + 1];

        dist[source] = 0.0;
        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> {
            int byDistance = Double.compare(a.distance(), b.distance());
            return byDistance != 0 ? byDistance : Integer.compare(a.vertex(), b.vertex());
        });
        queue.add(new Node(source, 0.0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            int u = node.vertex();
            if (settled[u] || node.distance() > dist[u]) {
                continue;
            }
            settled[u] = true;

            for (Arc arc : graph.getOutgoingArcs(u)) {
                if (arc.getCapacity() <= 0) {
                    continue; // tuyen khong kha dung
                }
                int v = arc.getV();
                double candidate = dist[u] + arc.getWeight();
                if (candidate < dist[v]) {
                    dist[v] = candidate;
                    pred[v] = u;
                    queue.add(new Node(v, candidate));
                }
            }
        }

        if (Double.isInfinite(dist[target])) {
            return new Result(Status.NO_PATH, -1, List.of(), dist, pred,
                    "Target khong reachable qua cac cung capacity > 0");
        }

        List<Integer> path = new ArrayList<>();
        int current = target;
        while (current != -1) {
            path.add(current);
            if (current == source) {
                break;
            }
            current = pred[current];
        }
        Collections.reverse(path);

        return new Result(Status.SUCCESS, dist[target], path, dist, pred, "Tim thay duong di ngan nhat");
    }
}
