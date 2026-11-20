package util;

import algorithm.Dijkstra;
import algorithm.FordFulkerson;
import io.IO;
import model.Graph;
import testing.TestRunner;

public class Utils {
	private static Graph graph = new Graph();
	private FordFulkerson ff = new FordFulkerson();
	private Dijkstra dij = new Dijkstra();
	private TestRunner testRunner = new TestRunner();
	private IO io = new IO();
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

		ff = new FordFulkerson();
		dij = new Dijkstra();

		return graph.isGraphValid();
	}

	public void displayGraph() {
		graph.display();
	}

	
	// [4] Reload hoac Clear dataset hien tai

	public boolean clearDataset() {
		boolean ok = graph.clear();
		if (ok) {
			ff = new FordFulkerson();
			dij = new Dijkstra();
		}
		return ok;

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

	public int getMaxFlow() {
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
		ff.displayMaxFlowAndArcs();
	}

	public void displayResidualGraph() {
		ff.displayResidualAndPaths();
	}

	public void xemDjaktra() {
		dij.displayResult();
	}

	public void verifyFlowAndExamine() {
		ff.displayVerification();
		System.out.println("+================================================================+");
		System.out.println("|              PHÂN TÍCH TỔNG HỢP HAI BÀI TOÁN                   |");
		System.out.println("+================================================================+");
		System.out.println("Dataset       : " + getDatasetName());
		System.out.println("Maximum Flow  : " + ff.getMaxFlow() + " units/time (dựa trên capacity)");
		System.out.println("Shortest Path : " + dij.getShortestPath() + " | Total Cost: " + dij.getTotalWeight() + " (dựa trên weight)");
		System.out.println("GHI CHÚ:");
		System.out.println("- Ford-Fulkerson tối đa hóa lưu lượng trên Residual Graph mới.");
		System.out.println("- Dijkstra tối thiểu hóa chi phí đường đi trên Graph gốc (capacity > 0).");
		System.out.println("- Tuyến đường ngắn nhất không đồng nhất với tuyến tạo luồng cực đại.");
		System.out.println("- Hai kết quả trả lời hai câu hỏi quản lý hoàn toàn khác nhau.");
		System.out.println("+----------------------------------------------------------------+");
	}
	// menu 5
	public void runTestCase(String path) {
		TestRunner.runTest1(path);
	}

	public void runTestAllCase() {
		TestRunner.runAllTests();
	}

	public void displayPassFail() {
		System.out.println(testRunner.getTestResults());
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

	public boolean verifyFlow() {
		return ff.verifyFlow();
	}

	public boolean reloadDataset() {
		return graph.reload();
	}


	
	
	
}
