package utils;

import algorithms.Dijkstra;
import algorithms.FordFulkerson;
import io.IO;
import models.Edge;
import models.Graph;




public class  Utils{
	
	private Graph graph;
	private static IO inputOuputManager = new IO();
	private boolean graphValid = false;
	private Dijkstra dijkstra = new Dijkstra(graph);
	private FordFulkerson ff;
    private String currentSourceFF = "";
    private String currentSinkFF = "";
    private String currentSourceDJ = "";
    private String currentTargetDJ = "";
    private boolean ffRun = false;
    private boolean djRun = false;
    private int lastMaxFlow = 0;
	public void setData(String path) {
		if((this.graph = inputOuputManager.loadFromFile(path)) != null) {
			graphValid = true;
		}
		
	}
	public void displayGraph() {
		System.out.println("""
		(+======================================================+);
		(|                XEM TONG HOP GRAPH GOC                |);
		(+======================================================+);""");
		System.out.printf("Dataset: %s | Co huong | V=%d | E=%d\n", graph.getDatasetName(), graph.getVertices().size(), graph.getEdgeCount());
        System.out.println("Dinh: " + graph.getVertices());
        System.out.println("Cung:");
		for (var entry : graph.getAdjList().entrySet()) {
            for (Edge e : entry.getValue()) {
                System.out.println("  " + e);
            }
        }
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
    private void resetResults() {
        ffRun = false;
        djRun = false;
        lastMaxFlow = 0;
    }
	public Graph getGraph() {
		return graph;
	}
	public void setGraph(Graph graph) {
		this.graph = graph;
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
