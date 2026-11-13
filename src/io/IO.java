package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import models.Graph;
import utils.Utils;

public class IO {
	Utils utils = new Utils();
	Graph graph = utils.graph;
    public Graph loadFromFile(String filePath) {
        try {
            graph.clear();
            File file = new File("datasets/" + filePath);
            String datasetName = file.getName();
            
            // Sử dụng try-with-resources để tự động đóng reader
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    
                    // Sửa \s+ thành \\s+
                    String[] parts = line.split("\\s+");
                    if (parts.length < 4) continue;

                    String u = parts[0];
                    String v = parts[1];
                    int cap = Integer.parseInt(parts[2]);
                    int weight = Integer.parseInt(parts[3]);

                    if (cap < 0 || weight < 0) {
                        throw new IllegalArgumentException("Capacity and weight must be non-negative.");
                    }

                    graph.addEdge(u, v, cap, weight);
                    System.out.println("LOADING");
                }
            }
            
            graph.setDatasetName(datasetName);
            System.out.println(graph.getDatasetName());
            return graph;
        } catch (Exception e) {
            System.out.println("loadFromFile failure: " + e.getMessage());
            return null;
        }
    }
}