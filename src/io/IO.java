package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import model.Graph;

public class IO {
/* 	  | 1. ĐỊNH DẠNG FILE DATASET (.txt):                              |
	  |    - Mỗi dòng biểu diễn một cung (arc) theo cấu trúc:          |
	  |   <Source> <Target> <Capacity (int >= 0)> <Weight (real >= 0)> |
	  |    - Ví dụ: WH A 12 4n                                         |
	  |    - Quy ước: Không có self-loop, không trùng cung giữa 2 đỉnh.|
	  |    - Cung có capacity = 0 được xem là tuyến không khả dụng.	   |
	  |	 - File lưu tại thư mục dataset/							   |
	 * */
	public Graph loadFromFile(String filePath) {
		Graph graph = new Graph();
		try {
			if(!filePath.trim().split("/")[0].equals("dataset")) {
				filePath = "dataset/" + filePath;
			}
			File file = new File(filePath);
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    
                
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
                }
            }
			graph.setGraphName(file.getName());
			graph.setGraphValid(true);
			return graph;
		}catch (Exception e) {
			throw new IllegalArgumentException(("[ERROR] loadFromFile failure: " + e.getMessage()));
		}
	}
}
