package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
	private static Set<String> vertices;
	private Map<String, List<Edge>> adjList;
	private String datasetName;
	private int zeroCapacityCount;
	
    public Graph() {
        adjList = new HashMap<>();
        vertices = new HashSet<>();
        datasetName = "None";
        zeroCapacityCount = 0;
    }
    public void clear() {
        adjList.clear();
        vertices.clear();
        datasetName = "None";
        zeroCapacityCount = 0;
    }
    public void addEdge(String u, String v, int capacity, int weight) {
        vertices.add(u);
        vertices.add(v);
        adjList.putIfAbsent(u, new ArrayList<>());
        adjList.putIfAbsent(v, new ArrayList<>());
        
        // Check duplicate
        for (Edge e : adjList.get(u)) {
            if (e.getV().equals(v)) {
                throw new IllegalArgumentException("Duplicate arc found between " + u + " and " + v);
            }
        }

        Edge edge = new Edge(u, v, capacity, weight);
        adjList.get(u).add(edge);
        if (capacity == 0) {
            zeroCapacityCount++;
        }
    }
    
    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjList.values()) {
            count += edges.size();
        }
        return count;
    }
	
	public boolean removeEdge(String u , String v){
		boolean b =false;
		for(Edge edge : adjList.get(u)) {
			if(edge.v == v) {
				adjList.get(u).remove(edge);
				b =true;
			}
				
		}
		return b;
	}

	public Set<String> getVertices() {
		return vertices;
	}

	public static void setVertices(Set<String> vertices) {
		Graph.vertices = vertices;
	}

	public Map<String, List<Edge>> getAdjList() {
		return adjList;
	}

	public void setAdjList(Map<String, List<Edge>> adjList) {
		this.adjList = adjList;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public int getZeroCapacityCount() {
		return zeroCapacityCount;
	}

	public void setZeroCapacityCount(int zeroCapacityCount) {
		this.zeroCapacityCount = zeroCapacityCount;
	}
	
	public boolean isValid() {
		return !(zeroCapacityCount==0);
	}
	
}