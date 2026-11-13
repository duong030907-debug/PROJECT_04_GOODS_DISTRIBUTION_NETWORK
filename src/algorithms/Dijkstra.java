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
        if (!graph.getVertices().contains(source) || !graph.getVertices().contains(target)) {
            throw new IllegalArgumentException("Không tìm thấy nguồn và đích trong đồ thị.");
        }
        
        for (String v : graph.getVertices()) {
            distances.put(v, Double.MAX_VALUE);
        }
        
        distances.put(source, 0.0);
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));
        pq.add(new NodeDistance(source, 0.0));
        
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String u = current.node;

            if (current.distance > distances.get(u)) continue;

            for (Edge e : graph.getAdjList().getOrDefault(u, Collections.emptyList())) {
                // run if capacity > 0 and weight >= 0
                if (e.capacity > 0) {
                    String v = e.v;
                    double weight = e.weight;
                    if (weight < 0) {
                        throw new IllegalArgumentException("INVALID FOR DIJKSTRA: Negative weight detected.");
                    }

                    double newDist = distances.get(u) + weight;
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        predecessors.put(v, u);
                        pq.add(new NodeDistance(v, newDist));
                    }
                }
            }
        }
        if (distances.get(target) != Double.MAX_VALUE) {
            success = true;
        }
    }
    public List<String> getPath(String target) {
        List<String> path = new ArrayList<>();
        if (!success && !distances.containsKey(target)) return path;
        
        String curr = target;
        while (curr != null && predecessors.containsKey(curr)) {
            path.add(0, curr);
            curr = predecessors.get(curr);
        }
        if (!path.isEmpty() || distances.get(target) == 0.0) {
            // start node if path exists or source == target
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
