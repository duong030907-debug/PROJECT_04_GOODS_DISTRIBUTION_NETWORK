package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.IO;

public class Graph {

	private boolean graphValid;

	private String graphName = "Chưa nạp dữ liệu";
	private Set<String> vertices = new LinkedHashSet<>();
	private Map<String, List<Edge>> adjList = new LinkedHashMap<>();
	private int edgeCount = 0;
	private int zeroCapCount = 0;

	public boolean reload() {
		if (!graphValid || graphName.isEmpty() || graphName.equals("Chưa nạp dữ liệu")) {
			return false;
		}
		IO io = new IO();
		Graph newGraph = io.loadFromFile(graphName);
		if (newGraph != null && newGraph.isGraphValid()) {
			this.vertices = newGraph.getVertices();
			this.adjList = newGraph.getAdjList();
			this.edgeCount = newGraph.getEdgeCount();
			this.zeroCapCount = newGraph.getZeroCapCount();
			this.graphValid = true;
			return true;
		}
		return false;
	}

	public boolean clear() {
		vertices.clear();
		adjList.clear();
		edgeCount = 0;
		zeroCapCount = 0;
		graphName = "Chưa nạp dữ liệu";
		graphValid = false;

		return true;
	}

	public void addEdge(String u, String v, int cap, int weight) {
		if (u.equals(v)) {
			throw new IllegalArgumentException("Không cho phép cung tự khuyên (self-loop) tại đỉnh " + u);
		}
		if (cap < 0 || weight < 0) {
			throw new IllegalArgumentException("Capacity và weight không được âm.");
		}
		vertices.add(u);
		vertices.add(v);
		adjList.putIfAbsent(u, new ArrayList<>());
		adjList.putIfAbsent(v, new ArrayList<>());

		for (Edge e : adjList.get(u)) {
			if (e.getV().equals(v)) {
				throw new IllegalArgumentException("Cung trùng tại " + u + " -> " + v);
			}
		}

		Edge newEdge = new Edge(u, v, cap, weight);
		adjList.get(u).add(newEdge);
		edgeCount++;
		if (cap == 0) {
			zeroCapCount++;
		}
	}

	public void resetFlows() {
		for (List<Edge> list : adjList.values()) {
			for (Edge e : list) {
				e.setFlow(0);
			}
		}
	}

	public void display() {
		if (!graphValid || vertices.isEmpty()) {
			System.out.println("[ERROR] NOT LOADED: Chưa có dữ liệu đồ thị! Vui lòng nạp dữ liệu trước.");
			return;
		}
		System.out.println("+================================================================+");
		System.out.println("|                    XEM TỔNG HỢP GRAPH GỐC                      |");
		System.out.println("+================================================================+");
		System.out.println("Dataset : " + graphName);
		System.out.println("Directed graph | V = " + getVertexCount() + " | E = " + edgeCount);
		System.out.println("Capacity: integer >= 0 | Weight: integer/real >= 0");
		System.out.println("Zero-capacity arcs: " + zeroCapCount);
		System.out.println("------------------------------------------------------------------");
		System.out.println("Danh sách các cung (Adjacency List):");
		for (String u : vertices) {
			List<Edge> list = adjList.get(u);
			if (list != null && !list.isEmpty()) {
				for (Edge e : list) {
					System.out.println("  " + e.toString());
				}
			}
		}
		System.out.println("+----------------------------------------------------------------+");
	}

	public int getVertexCount() {
		return vertices.size();
	}

	public boolean isGraphValid() {
		return graphValid;
	}

	public void setGraphValid(boolean graphValid) {
		this.graphValid = graphValid;
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public Set<String> getVertices() {
		return vertices;
	}

	public void setVertices(Set<String> vertices) {
		this.vertices = vertices;
	}

	public Map<String, List<Edge>> getAdjList() {
		return adjList;
	}

	public void setAdjList(Map<String, List<Edge>> adjList) {
		this.adjList = adjList;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
	}

	public int getZeroCapCount() {
		return zeroCapCount;
	}

	public void setZeroCapCount(int zeroCapCount) {
		this.zeroCapCount = zeroCapCount;
	}

}
