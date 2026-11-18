package util;

import algorithm.Dijaktra;
import algorithm.FordFulkerson;
import model.Graph;
import testing.TestRunner;

public class Utils {
	Graph graph = new Graph();
	private FordFulkerson ff = new FordFulkerson();
	private Dijaktra dij = new Dijaktra();
	private TestRunner testRunner = new TestRunner();
	private io.IO io;
	// Graph
	public String getDatasetName() {
		return graph.getGraphName();
	}

	public int getVertexCount() {
		return graph.getVertexCount();
	}

	public int getEdgeCount() {
		return graph.getEdgeCount();
	}

	public int getZeroCapCount() {
		return graph.getZeroCapCount();
	}

	public boolean isGraphValid() {
		return graph.isGraphValid();
	}

	public Graph getGraph() {
		return graph;
	}
	
	//Menu chinh start()

	public boolean setData(String path) {
		graph = io.loadFromFile(path);
		return graph.isGraphValid();
	}

	public void displayGraph() {
		graph.display();
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
		ff.setGraph(graph);
		ff.run(source,sink);
	}

	public void dijkstra(String source, String target) {
		dij.setGraph(graph);
		dij.run(source,target);
	}
	
	// Ford Fulkerson

	public String getMaxFlow() {
		return ff.getMaxFlow();
	}

	// Dijaktra
	public String getShortestPath() {
		return dij.getShortestPath();
	}

	public Object getTotalWeight() {
		return dij.getTotalWeight();
	}
	
	// menu 4
	
	public void getMaxFlowAndTopFlow() {
		
	}

	public void displayResidualGraph() {
		// TODO Auto-generated method stub
		
	}

	public void xemDjaktra() {
		// TODO Auto-generated method stub
		
	}

	public void verifyFlowAndExamine() {
		// TODO Auto-generated method stub
		
	}
	// menu 5
	public void runTestCase(String path) {
		testRunner.run(path);
	}

	public void runTestAllCase() {
		// TODO Auto-generated method stub
		testRunner.runAll();
	}

	public void displayPassFail() {
		testRunner.displayPassFail();
	}

	public void configAndHelper() {
		System.out.println("""
		        +================================================================+
		        |             ĐỊNH DẠNG INPUT & HƯỚNG DẪN THUẬT TOÁN             |
		        +================================================================+
		        | 1. ĐỊNH DẠNG FILE DATASET (.txt):                              |
		        |    - Mỗi dòng biểu diễn một cung (arc) theo cấu trúc:          |
		        |   <Source> <Target> <Capacity (int >= 0)> <Weight (real >= 0)> |
		        |    - Ví dụ: WH A 12 4n                                         |
		        |    - Quy ước: Không có self-loop, không trùng cung giữa 2 đỉnh.|
		        |    - Cung có capacity = 0 được xem là tuyến không khả dụng.	 |
				|	 - File lưu tại thư mục dataset/							 |
		        |                                                                |
		        | 2. THUẬT TOÁN FORD-FULKERSON (Maximum Flow):                   |
		        |    - Chạy trên graph riêng không thay đổi graph gốc.           |
		        |    - Cập nhật đúng capacity trên cung thuận và cung ngược.     |
		        |    - Kết thúc khi không còn augmenting path (Max Flow reached).|
		        |                                                                |
		        | 3. THUẬT TOÁN DIJKSTRA (Shortest Path):                        |
		        |    - Chạy trên graph gốc mà không thay đổi gì. 				 |
		        |    - Chỉ xét các cung khả dụng có capacity > 0 và weight >= 0. |
		        |    - Kết quả trả về đường đi ngắn nhất và tổng chi phí (Cost). |
		        +----------------------------------------------------------------+""");
	}

	public void displayProjectInfo() {
	    System.out.println("""
	            +================================================================+
	            |                 THÔNG TIN PROJECT / THÀNH VIÊN                 |
	            +================================================================+
	            | Môn học : CSC106400 - Lý Thuyết Đồ Thị                         |
	            | Project : Đề 04 - Goods Distribution Network                   |
	            |                                                                |
	            | Danh sách thành viên nhóm 2:                                   |
	            |   1. Phạm Hoàng Dương   25211TT3505                            |
	            |   2. Lưu Long Hải       21211TT052                             |
	            | Sản phẩm                                                       |
	            |   - Báo cáo Word (.docx)                                       |
	            |   - Nhật ký AI (.xlsx)                                         |
	            |   - Source code Java chạy hoàn chỉnh trên Eclipse              |
	            |   - Video demo                                                 |
	            +----------------------------------------------------------------+""");
	}


	
	
	
}
