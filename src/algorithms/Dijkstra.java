package algorithms;

import java.util.*;

import models.Edge;
import models.Graph;


public class Dijkstra {
    private static class NodeDistance {
        String node;
        double distance;

        NodeDistance(String node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }
	private Map<String, Double> distances;
    private Map<String, String> predecessors;
    private Graph graph;
    private boolean success;
    public Dijkstra(Graph graph) {
        this.graph = graph;
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
        this.success = false;
    }
    
    public void run(String source, String target) {
    	distances.clear();
        predecessors.clear();
        success = false;
        // Kiem tra source và target có thuộc V không
        if (!graph.getVertices().contains(source) || !graph.getVertices().contains(target)) {
            throw new IllegalArgumentException("Không tìm thấy nguồn và đích trong đồ thị.");
        }
        // Cho d vô cực với tất cả u
        for (String v : graph.getVertices()) {
            distances.put(v, Double.MAX_VALUE);
        }

        // Cho d source = 0
        distances.put(source, 0.0);
        // Hàng đợi ưu tiên
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));
        // Cho d source = 0
        pq.add(new NodeDistance(source, 0.0));
        // pq không rỗng thì
        while (!pq.isEmpty()) {
        	//node đầu queue (source ms thêm vào)
            NodeDistance current = pq.poll();
            //Node hiện tại
            String u = current.node;
            //distance lớn hơn thì bỏ qua
            if (current.distance > distances.get(u)) continue;
            // lặp theo cung
            for (Edge e : graph.getAdjList().getOrDefault(u, Collections.emptyList())) {
                // chạy nếu capacity > 0 và weight >= 0
                if (e.capacity > 0) {
                    String v = e.v;
                    double weight = e.weight;
                    if (weight < 0) {
                        throw new IllegalArgumentException("INVALID FOR DIJKSTRA: Negative weight detected.");
                    }
                    //
                    double newDist = distances.get(u) + weight;
                    // nếu nhỏ hơn distances của v thì thêm
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        predecessors.put(v, u);
                        pq.add(new NodeDistance(v, newDist));
                    }
                }
            }
        }
        // kiểm tra distance khác vô cực thì success true
        if (distances.get(target) != Double.MAX_VALUE) {
            success = true;
        }
    }
    public List<String> getPath(String target) {
        List<String> path = new ArrayList<>();
        // trả về rỗng nếu chưa chạy và không chứa u target
        if (!success && !distances.containsKey(target)) return path;
        // nếu chứa
        String curr = target;
        while (curr != null && predecessors.containsKey(curr)) {
            path.add(0, curr);
            curr = predecessors.get(curr);
        }
        if (!path.isEmpty() || distances.get(target) == 0.0) {
            
            if (curr != null) path.add(0, curr);
        }
        return path;
    }
    
    public double getDistance(String target) {
        return distances.getOrDefault(target, Double.MAX_VALUE);
    }
    
    public Map<String, Double> getDistances() { return distances; }
    public Map<String, String> getPredecessors() { return predecessors; }
    public boolean isSuccess() { return success; }
}
