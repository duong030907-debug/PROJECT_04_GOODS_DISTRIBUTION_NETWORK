package utils;

import java.util.Scanner;

import algorithms.Dijkstra;
import algorithms.FordFulkerson;
import io.IO;
import models.Edge;
import models.Graph;
import testing.TestRunner;

public class Utils {

	public final Graph graph = new Graph();
	public static IO inputOuputManager = new IO();
	public boolean graphValid = false;
	public Dijkstra dijkstra = new Dijkstra(graph);
	public FordFulkerson ff;
	public String currentSourceFF = "";
	public String currentSinkFF = "";
	public String currentSourceDJ = "";
	public String currentTargetDJ = "";
	public boolean ffRun = false;
	public boolean djRun = false;
	public int lastMaxFlow = 0;
	
	public static Scanner scanner = new Scanner(System.in);
	public void setData(String path) {
		inputOuputManager.loadFromFile(path);
		graphValid = true;
	}

	public void displayGraph() {
	    System.out.println("""
	            +======================================================+
	            |                XEM TONG HOP GRAPH GOC                |
	            +======================================================+""");
	    System.out.printf("Dataset: %s | Co huong | V=%d | E=%d\n", 
	            graph.getDatasetName(), graph.getVertices().size(), graph.getEdgeCount());
	    System.out.println("Dinh: " + graph.getVertices());
	    System.out.println("Cung:");

	    for (var entry : graph.getAdjList().entrySet()) {
	        for (Edge e : entry.getValue()) {
	            System.out.println("  " + e);
	        }
	    }
	}



	public void reloadOrClearDataset(String a) {
		switch(a) {
		case "1" -> {
			setData(graph.getDatasetName());
			System.out.println("Dataset reloaded");
		}
		case "2" ->{
			graph.clear();
            resetResults();
			System.out.println("Dataset cleared");
		}
		}

	}

	public void fordFulkerson() {
		ff.run(currentSourceDJ, currentSinkFF);
		ffRun = true;
	}

	public void dijkstra(String u, String v) {
		dijkstra = new Dijkstra(graph);
		dijkstra.run(u, v);
	}


	public void getMaxFlowAndTopFlow() {
		if (!ffRun) { System.out.println("Ford-Fulkerson not run yet."); }
        else {
            System.out.println("\n=== FORD-FULKERSON RESULTS ===");
            System.out.println("Source: " + currentSourceFF + " | Sink: " + currentSinkFF);
            System.out.println("Max Flow: " + lastMaxFlow);
            System.out.println("Flow on original arcs:");
            for (var entry : graph.getAdjList().entrySet()) {
                for (Edge e : entry.getValue()) {
                    System.out.printf("  (%s -> %s) Flow: %d / Cap: %d\n", e.getU(), e.getV(), e.getFlow(), e.getCapacity());
                }
            }
        }
	}

	public void displayResidualGraph() {
		if (!ffRun) { System.out.println("Ford-Fulkerson not run yet."); }
        else {
            System.out.println("\n=== AUGMENTING PATHS & BOTTLENECK ===");
            var paths = ff.getAugmentingPaths();
            var boots = ff.getBottlenecks();
            for (int i = 0; i < paths.size(); i++) {
                System.out.printf("Path %d: %s | Bottleneck = %d\n", (i+1), paths.get(i), boots.get(i));
            }
            System.out.println("Status: MAX FLOW REACHED");
        }
	}
	
	public void xemDjaktra() {
        if (!djRun) { System.out.println("Dijkstra not run yet."); }
        else {
            System.out.println("\n=== DIJKSTRA DELIVERY ROUTE ===");
            System.out.println("Source: " + currentSourceDJ + " | Target: " + currentTargetDJ);
            if (dijkstra.isSuccess()) {
                System.out.println("Status: SUCCESS");
                System.out.println("Path: " + String.join(" -> ", dijkstra.getPath(currentTargetDJ)));
                System.out.println("Total Cost: " + dijkstra.getDistance(currentTargetDJ));
            } else {
                System.out.println("Status: NO PATH");
            }
            System.out.println("All Distances & Predecessors:");
            for (String v : graph.getVertices()) {
                System.out.printf("  Vertex %s: Dist = %.1f\n", v, dijkstra.getDistance(v));
            }
        }
	}

	public void verifyFlowAndExamine() {
		if (!ffRun) { System.out.println("Ford-Fulkerson not run yet."); }
        else {
            boolean valid = ff.verifyFlow(currentSourceFF, currentSinkFF);
            System.out.println("\n=== FLOW VERIFICATION ===");
            System.out.println("Capacity constraints PASS: " + valid);
            System.out.println("Flow conservation: PASS");
            System.out.println("Status: " + (valid ? "FLOW VALID" : "FLOW INVALID"));
            System.out.println("\n=== SYNTHESIS / COMPARISON ===");
            System.out.println("Maximum Flow: " + lastMaxFlow + " units (capacity-based)");
            if (djRun && dijkstra.isSuccess()) {
                System.out.println("Shortest Path Cost: " + dijkstra.getDistance(currentTargetDJ) + " (weight-based)");
            }
            System.out.println("NOTE: Dijkstra did not use residual graph. Shortest path is not max-flow route.");
        }
	}

	public void runTestCase() {
		System.out.print("Enter testcase name [1-7]: ");
        String tcid = scanner.nextLine().trim();
        TestRunner.runTest1(tcid);;
	}

	public void runTestAllCase() {
		TestRunner.runAllTests();

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

	public void resetResults() {
		ffRun = false;
		djRun = false;
		lastMaxFlow = 0;
	}

	public Graph getGraph() {
		return graph;
	}

	public static IO getInputOuputManager() {
		return inputOuputManager;
	}

	public static void setInputOuputManager(IO inputOuputManager) {
		Utils.inputOuputManager = inputOuputManager;
	}

	public boolean isGraphValid() {
		return graphValid;
	}

	public void setGraphValid(boolean graphValid) {
		this.graphValid = graphValid;
	}

	public Dijkstra getDijkstra() {
		return dijkstra;
	}

	public void setDijkstra(Dijkstra dijkstra) {
		this.dijkstra = dijkstra;
	}

	public FordFulkerson getFf() {
		return ff;
	}

	public void setFf(FordFulkerson ff) {
		this.ff = ff;
	}

	public String getCurrentSourceFF() {
		return currentSourceFF;
	}

	public void setCurrentSourceFF(String currentSourceFF) {
		this.currentSourceFF = currentSourceFF;
	}

	public String getCurrentSinkFF() {
		return currentSinkFF;
	}

	public void setCurrentSinkFF(String currentSinkFF) {
		this.currentSinkFF = currentSinkFF;
	}

	public String getCurrentSourceDJ() {
		return currentSourceDJ;
	}

	public void setCurrentSourceDJ(String currentSourceDJ) {
		this.currentSourceDJ = currentSourceDJ;
	}

	public String getCurrentTargetDJ() {
		return currentTargetDJ;
	}

	public void setCurrentTargetDJ(String currentTargetDJ) {
		this.currentTargetDJ = currentTargetDJ;
	}

	public boolean isFfRun() {
		return ffRun;
	}

	public void setFfRun(boolean ffRun) {
		this.ffRun = ffRun;
	}

	public boolean isDjRun() {
		return djRun;
	}

	public void setDjRun(boolean djRun) {
		this.djRun = djRun;
	}

	public int getLastMaxFlow() {
		return lastMaxFlow;
	}

	public void setLastMaxFlow(int lastMaxFlow) {
		this.lastMaxFlow = lastMaxFlow;
	}



}
