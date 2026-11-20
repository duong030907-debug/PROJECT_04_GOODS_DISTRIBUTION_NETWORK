package testing;

import algorithm.Dijkstra;
import algorithm.FordFulkerson;
import io.IO;
import model.Graph;
import util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
	public static final List<String> testResults = new ArrayList<String>();
	public static io.IO io = new IO();
    public static void runAllTests() {
    	testResults.clear();
        System.out.println("\n========================================");
        System.out.println("       RUNNING AUTOMATED TEST CASES      ");
        System.out.println("========================================");

        runTest("TC01", "Max flow normal / multiple paths", "ds_demo.txt", "WH", "ST", "WH", "ST", true);
        runTest("TC02", "Reverse residual update", "ds_rev.txt", "S", "T", "S", "T", true);
        runTest("TC03", "Source-Sink disconnected -> flow 0", "ds_flow0.txt", "S", "T", "S", "T", true);
        runTest("TC04", "Zero-capacity arc ignored", "ds_zero.txt", "S", "T", "S", "T", true);
        runTest("TC05", "Dijkstra shortest path", "ds_main.txt", "WH", "ST", "WH", "ST", true);
        runTest("TC06", "Dijkstra NO PATH", "ds_nopath.txt", "S", "T", "S", "T", false);
        runTestException("TC07", "Invalid capacity / weight / input", "ds_invalid.txt");
    }
    
    public static void runTest1(String testName) {
    	switch(testName) {
    	case "1" ->{
    		runTest("TC01", "Max flow normal / multiple paths", "ds_demo.txt", "WH", "ST", "WH", "ST", true);
    	}

    	case "2" ->{
    		runTest("TC02", "Reverse residual update", "ds_rev.txt", "S", "T", "S", "T", true);
    	}

    	case "3" ->{
    		runTest("TC03", "Source-Sink disconnected -> flow 0", "ds_flow0.txt", "S", "T", "S", "T", true);
    	}

    	case "4" ->{
            runTest("TC04", "Zero-capacity arc ignored", "ds_zero.txt", "S", "T", "S", "T", true);
    	}

    	case "5" ->{
            runTest("TC05", "Dijkstra shortest path", "ds_main.txt", "WH", "ST", "WH", "ST", true);
    	}

    	case "6" ->{
            runTest("TC06", "Dijkstra NO PATH", "ds_nopath.txt", "S", "T", "S", "T", false);
    	}

    	case "7" ->{
            runTestException("TC07", "Invalid capacity / weight / input", "ds_invalid.txt");
    	}
    	}
    }

    public static void runTest(String id, String desc, String file, String srcFlow, String snkFlow, String srcDij, String tgtDij, boolean expectedSuccess) {
        try {
            Graph g = new Graph();
            g = io.loadFromFile(file);

            FordFulkerson ff = new FordFulkerson(g);
            ff.run(srcFlow, snkFlow);
            int mf = ff.getMaxFlow();
            boolean valid = ff.verifyFlow();

            Dijkstra dj = new Dijkstra(g);
            dj.run(srcDij, tgtDij);
            String result = String.format("[%s] %-35s : PASS (MaxFlow: %d, Valid: %b, Dijkstra Success: %b)\n", id, desc, mf, valid, dj.isSuccess());
            System.out.print(result);
            testResults.add(result);
        } catch (Exception e) {
            System.out.printf("[%s] %-35s : FAIL (Exception: %s)\n", id, desc, e.getMessage());
        }
    }

    private static void runTestException(String id, String desc, String file) {
        try {
            Graph g = new Graph();
            g = io.loadFromFile(file);
            System.out.printf("[%s] %-35s : FAIL (Expected validation exception)\n", id, desc);
        } catch (Exception e) {
            System.out.printf("[%s] %-35s : PASS (Caught expected: %s)\n", id, desc, e.getMessage());
        }
    }

	public List<String> getTestResults() {
		return testResults;
	}

>>>>>>> Stashed changes
}
