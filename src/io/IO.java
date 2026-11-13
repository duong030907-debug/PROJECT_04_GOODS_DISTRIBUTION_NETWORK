package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import models.Graph;

public class IO {
	public Graph loadFromFile(String filePath){
		try {
			File file = new File(filePath);
	        String datasetName = file.getName();
	        Graph g = new Graph();
	        BufferedReader reader = new BufferedReader(new FileReader(file));
	        String line;
	        while ((line = reader.readLine()) != null) {
	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("#")) continue;
	            String[] parts = line.split("\s+");
	            if (parts.length < 4) continue;

	            String u = parts[0];
	            String v = parts[1];
	            int cap = Integer.parseInt(parts[2]);
	            int weight = Integer.parseInt(parts[3]);

	            if (cap < 0 || weight < 0) {
	                reader.close();
	                throw new IllegalArgumentException("Capacity and weight must be non-negative.");
	            }

	            g.addEdge(u, v, cap, weight);
	        }
	        reader.close();
	        g.setDatasetName(datasetName);
	        return g;
		} catch (Exception e) {
			System.out.println("loadFromFile failure");
			return null;
		}
        
	}
}
