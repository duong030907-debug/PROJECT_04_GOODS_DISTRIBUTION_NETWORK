package utils;

import java.io.IOException;
import java.util.List;

import com.google.common.graph.ValueGraph;

import algorithms.Dijkstra;
import io.IO;
import models.Graph;




public class  Utils{
	
	public Graph graph;
	public static IO inputOuputManager = new IO();
	public boolean graphValid = false;
	public Dijkstra dijkstra = new Dijkstra(graph);
	
	public void setData(String path) {
		if((this.graph = inputOuputManager.loadFromFile(path)) != null) {
			graphValid = true;
		}
	}
	public void displayGraph() {
		
	}
	public void chooseDatasetAndDebug() {
		// TODO Auto-generated method stub
		
	}
	public void reloadOrClearDataset(String a) {
		// TODO Auto-generated method stub
		
	}
	public void fordFulkerson() {
		// TODO Auto-generated method stub
		
	}
	public void dijkstra(String u, String v) {
		dijkstra.run(u, v);
	}
	public void runFordFulkersonAndDijkstra() {
		// TODO Auto-generated method stub
		
	}
	public void getMaxFlowAndTopFlow() {
		// TODO Auto-generated method stub
		
	}
	public void displayResidualGraph() {
		// TODO Auto-generated method stub
		
	}
	public void verifyFlowAndExamine() {
		// TODO Auto-generated method stub
		
	}
	public void runTestCase() {
		// TODO Auto-generated method stub
		
	}
	public void runTestAllCase() {
		// TODO Auto-generated method stub
		
	}
	public void displayPassFail() {
		// TODO Auto-generated method stub
		
	}
	public void configAndHelper() {
		// TODO Auto-generated method stub
		
	}
	public void displayProjectInfo() {
		// TODO Auto-generated method stub
		
	}

}
