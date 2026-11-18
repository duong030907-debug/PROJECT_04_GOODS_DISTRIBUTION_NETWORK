package util;

import algorithm.Dijaktra;
import algorithm.FordFulkerson;
import model.Graph;

public class Utils {
	Graph graph = new Graph();
	public FordFulkerson ff = new FordFulkerson();
	public Dijaktra dij = new Dijaktra();
	
	// Graph
	public String getDatasetName() {
		return graph.getDatasetName();
	}

	public int getVertexCount() {
		return graph.getVertexCount();
	}

	public int getEdgeCount() {
		return graph.getEdgeCount();
	}

	public int getZeroCapCount() {
		return graph.getEdgeCount();
	}

	public boolean isGraphValid() {
		return graph.isGraphValid();
	}

	public Graph getGraph() {
		return graph;
	}
	
	//Menu chinh start()

	public boolean setData(String path) {
		return false;
	}

	public void displayGraph() {
		// TODO Auto-generated method stub
		
	}

	public void chooseDatasetAndDebug() {
		// TODO Auto-generated method stub
		
	}
	
	// [4] Reload hoac Clear dataset hien tai

	public boolean reloadDataset() {
		return graph.reload();
	}

	public boolean clearDataset() {
		return graph.clear();
	}

	// menu 3
	public void fordFulkerson(String source, String sink) {
		// TODO Auto-generated method stub
		
	}

	public void dijkstra(String source, String target) {
		// TODO Auto-generated method stub
		
	}

	public String getMaxFlow() {
		return ff.getMaxFlow();
	}

	public String getShortestPath() {
		// TODO Auto-generated method stub
		return dij.getShortestPath();
	}

	public Object getTotalWeight() {
		// TODO Auto-generated method stub
		return dij.getTotalWeight();
	}
	
	
	
}
