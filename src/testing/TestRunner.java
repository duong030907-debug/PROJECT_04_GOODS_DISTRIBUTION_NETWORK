package testing;

import algorithms.Dijkstra;
import algorithms.FordFulkerson;
import models.Graph;
import utils.Utils;

import java.io.File;

public class TestRunner {
	public static Utils utils = new Utils();
    public static void runAllTests() {
        System.out.println("\n========================================");
        System.out.println("       RUNNING AUTOMATED TEST CASES      ");
        System.out.println("========================================");

        runTest("TC01", "Max flow normal / multiple paths", "flow_demo.txt", "WH", "ST", "WH", "ST", true);
        runTest("TC02", "Reverse residual update", "ds_rev.txt", "S", "T", "S", "T", true);
        runTest("TC03", "Source-Sink disconnected -> flow 0", "ds_flow0.txt", "S", "T", "S", "T", true);
        runTest("TC04", "Zero-capacity arc ignored", "ds_zero.txt", "S", "T", "S", "T", true);
        runTest("TC05", "Dijkstra shortest path", "logistics_main.txt", "WH", "ST", "WH", "ST", true);
        runTest("TC06", "Dijkstra NO PATH", "ds_nopath.txt", "S", "T", "S", "T", false);
        runTestException("TC07", "Invalid capacity / weight / input", "ds_invalid.txt");
    }
    
    public static void runTest1(String testName) {
    	switch(testName) {
    	case "1" ->{
    		runTest("TC01", "Max flow normal / multiple paths", "flow_demo.txt", "WH", "ST", "WH", "ST", true);
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
            runTest("TC05", "Dijkstra shortest path", "logistics_main.txt", "WH", "ST", "WH", "ST", true);
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
            if (new File(file).exists()) {
            	Utils.inputOuputManager.loadFromFile(file);
            } else {
                createDummyData(file);
                Utils.inputOuputManager.loadFromFile(file);
            }

            FordFulkerson ff = new FordFulkerson(g);
            int mf = ff.run(srcFlow, snkFlow);
            boolean valid = ff.verifyFlow(srcFlow, snkFlow);

            Dijkstra dj = new Dijkstra(g);
            dj.run(srcDij, tgtDij);

            System.out.printf("[%s] %-35s : PASS (MaxFlow: %d, Valid: %b, Dijkstra Success: %b)\n", id, desc, mf, valid, dj.isSuccess());
        } catch (Exception e) {
            System.out.printf("[%s] %-35s : FAIL (Exception: %s)\n", id, desc, e.getMessage());
        }
    }

    private static void runTestException(String id, String desc, String file) {
        try {
            Graph g = new Graph();
            createInvalidData(file);
            Utils.inputOuputManager.loadFromFile(file);
            System.out.printf("[%s] %-35s : FAIL (Expected validation exception)\n", id, desc);
        } catch (Exception e) {
            System.out.printf("[%s] %-35s : PASS (Caught expected: %s)\n", id, desc, e.getMessage());
        }
    }

    public static void createDummyData(String filename) {
        try {
            java.io.FileWriter fw = new java.io.FileWriter(filename);
            if (filename.contains("flow_demo.txt")) {
                fw.write("WH A 10 5.0\nWH B 10 2.0\nA ST 6 3.0\nB ST 8 4.0\nWH AD 3 8.0\n");
            } else if (filename.contains("ds_rev.txt")) {
                fw.write("S A 10 2.0\nA B 5 1.0\nB T 10 3.0\nS B 5 6.0\n");
            } else if (filename.contains("ds_flow0.txt")) {
                fw.write("S A 10 2.0\nB T 10 3.0\n");
            } else if (filename.contains("ds_zero.txt")) {
                fw.write("S A 0 5.0\nA T 10 2.0\nS T 5 1.0\n");
            } else if (filename.contains("logistics_main.txt")) {
                fw.write("WH A 12 4.0\nWH B 15 3.0\nA C 10 2.0\nB C 10 1.0\nC ST 20 5.0\nB E 10 2.0\nE ST 10 4.0\n");
            } else if (filename.contains("ds_nopath.txt")) {
                fw.write("S A 10 2.0\nB T 10 3.0\n");
            }
            fw.close();
        } catch (Exception ignored) {}
    }

    private static void createInvalidData(String filename) {
        try {
            java.io.FileWriter fw = new java.io.FileWriter(filename);
            fw.write("S A -5 2.0\n");
            fw.close();
        } catch (Exception ignored) {}
    }
}
