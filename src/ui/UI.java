package ui;

import java.util.Scanner;

import models.Graph;
import utils.Utils;

public class UI {
	public static Scanner scanner = new Scanner(System.in);
	public static Utils utils = new Utils();
	public static Graph graph = utils.getGraph();

	public static void mainMenu() {
		while (true) {
			String datasetName = (graph != null && graph.getDatasetName() != null) ? graph.getDatasetName() : "None";
			int vertexCount = (graph != null && graph.getVertices() != null) ? graph.getVertices().size() : 0;
			int edgeCount = (graph != null) ? graph.getEdgeCount() : 0;
			int zeroCapCount = (graph != null) ? graph.getZeroCapacityCount() : 0;

			System.out.printf("""
					+================================================================+
					|                   GOODS DISTRIBUTION NETWORK                   |
					+================================================================+
					| Dataset : %-52s |
					| Graph   : Directed | Capacity(int >=0) + Weight(real >=0)      |
					| V = %-3d | E = %-3d | Zero-capacity arcs = %-21d |
					|                                                                |
					| [1] Quan ly du lieu mang van chuyen                            |
					| [2] Chay Maximum Flow / Dijkstra                               |
					| [3] Ket qua / Kiem chung                                       |
					| [4] Kiem thu / Huong dan                                       |
					| [0] Thoat chuong trinh                                         |
					+----------------------------------------------------------------+
					Chon chuc nang [0-4]: """, datasetName, vertexCount, edgeCount, zeroCapCount);
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				menu2();
			}
			case "2" -> {
				menu3();
			}
			case "3" -> {
				menu4();
			}
			case "4" -> {
				menu5();
			}
			case "0" -> {
				System.out.println("Exited program successfully!");
				return;
			}
			default -> {
				System.out.println("Input khong hop le");
			}
			}

		}
	}

	public static void menu2() {
		while (true) {
			String datasetName = (graph != null && graph.getDatasetName() != null) ? graph.getDatasetName() : "None";
			System.out.printf("""
					+================================================================+
					|                 QUAN LY DU LIEU MANG VAN CHUYEN                |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Nap du lieu tu file                                        |
					| [2] Xem tong hop graph goc + capacity/weight                   |
					| [3] Chon dataset mau / kiem thu                                |
					| [4] Reload hoac Clear dataset hien tai                         |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang: """, datasetName);

			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("""
						[1] Nap du lieu tu file
						Nhap duong dan (path.txt): """);
				String path = scanner.nextLine().trim();
				try {

					utils.setData(path);
					if (utils.isGraphValid()) {
						System.out.println("[OK] " + utils.getGraph().getVertices().size() + " dinh | "
								+ utils.getGraph().getEdgeCount() + " cung");
						System.out.println("[OK] Khong chu ky / cung lap");
						System.out.println("[OK] Suc chua va weight hop le");
						System.out.println("[OK] Weight khong am");
						System.out.println("[INFO] Clear");
					}
				} catch (Exception e) {
					System.out.println("[ERROR] Failed to load file: " + e.getMessage());
				}
			}
			case "2" -> {
				System.out.println("[2] Xem tong hop graph goc + capacity/weight ");
				utils.displayGraph();
			}
			case "3" -> {
				System.out.println(" 3 Chon dataset mau / kiem thu ");
				chooseDatasetAndDebug();
			}
			case "4" -> {
				while (true) {
					System.out.println("""
							[4] Reload hoac Clear dataset hien tai
							\t [1] Reload dataset
							\t [2] Clear dataset
							\t [0] Quay lai Main Menu
							""");
					String a = scanner.nextLine().trim();
					if (!(a == "1" || a == "2")) {
						System.out.println("""
								Input khong hop le""");
						continue;
					} else {
						utils.reloadOrClearDataset(a);
					}
				}
			}
			case "0" -> {
				return;
			}
			default -> {
				System.out.println("Input khong hop le");
			}
			}
		}
	}

	public static void menu3() {
		while (true) {
			String datasetName = (graph != null && graph.getDatasetName() != null) ? graph.getDatasetName() : "None";
			System.out.printf("""
					+================================================================+
					|                  CHAY MAXIMUM FLOW / DIJKSTRA                  |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Ford-Fulkerson - chon Source/Sink va chay                  |
					| [2] Dijkstra - chon Source/Target va chay tren graph goc       |
					| [3] Chay ca hai bai toan tren cung graph goc                   |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang: """, datasetName);
			if (!scanner.hasNextInt()) {
				System.out.println("""
						Input khong hop le""");
				scanner.next();
				continue;
			}
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.print("Enter Source vertex: ");
				utils.currentSourceFF = scanner.nextLine().trim();
				System.out.print("Enter Sink vertex: ");
				utils.currentSinkFF = scanner.nextLine().trim();
				try {
					utils.fordFulkerson();
					System.out.println("[SUCCESS] Ford-Fulkerson completed. Max Flow = " + utils.lastMaxFlow);
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
				}
			}
			case "2" -> {
				System.out.print("Enter Source vertex: ");
				utils.currentSourceDJ = scanner.nextLine().trim();
				System.out.print("Enter Target vertex: ");
				utils.currentTargetDJ = scanner.nextLine().trim();
				 try {
					 utils.dijkstra(utils.currentSourceDJ,utils.currentTargetDJ);
					 
				}catch (Exception e) {
                    System.out.println("[ERROR] " + e.getMessage());
				}
			}
			case "3" -> {System.out.print("Enter Source vertex for both: ");
            String s = scanner.nextLine().trim();
            System.out.print("Enter Sink/Target vertex for both: ");
            String t = scanner.nextLine().trim();
            utils.currentSourceFF = s; utils.currentSinkFF = t;
            utils.currentSourceDJ = s; utils.currentTargetDJ = t;
            try {
				utils.fordFulkerson();
            	utils.dijkstra(s, t);
                System.out.println("[SUCCESS] Both algorithms executed successfully.");
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
            break;
			}
			case "0" -> {
				return;
			}
			default -> {
				System.out.println("Input khong hop le");
			}
			}
		}
	}

	public static void menu4() {
		while (true) {
			String datasetName = (graph != null && graph.getDatasetName() != null) ? graph.getDatasetName() : "None";
			System.out.printf("""
					+================================================================+
					|                     KET QUA / KIEM CHUNG                       |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Xem Max Flow va flow tren cung goc                         |
					| [2] Xem Residual Graph / augmenting paths / bottleneck         |
					| [3] Xem Dijkstra: Distance/Predecessor, Path/Total Cost        |
					| [4] Verify Flow va phan tich tong hop hai bai toan             |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang:""", datasetName);
			if (!scanner.hasNextInt()) {
				System.out.println("""
						Input khong hop le""");
				scanner.next();
				continue;
			}
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				utils.getMaxFlowAndTopFlow();
			}
			case "2" -> {
				utils.displayResidualGraph();
			}
			case "3" -> {
				utils.xemDjaktra();
			}
			case "4" -> {
				utils.verifyFlowAndExamine();
			}
			
			case "0" -> {
				return;
			}
			default -> {
				System.out.println("Input khong hop le");
			}
			}
		}
	}

	public static void menu5() {
		while (true) {
			String datasetName = (graph != null && graph.getDatasetName() != null) ? graph.getDatasetName() : "None";
			System.out.printf("""
					+================================================================+
					|                 MENU 5 - KIEM THU / HUONG DAN                  |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Chay mot testcase                                          |
					| [2] Chay tat ca testcase                                       |
					| [3] Xem tong hop Pass / Fail                                   |
					| [4] Dinh dang input / Huong dan thuat toan                     |
					| [5] Thong tin project / thanh vien                             |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang [0-5]: """, datasetName);
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				utils.runTestCase();
			}
			case "2" -> {
				utils.runTestAllCase();
			}
			case "3" -> {
				utils.displayPassFail();
			}
			case "4" -> {
				utils.configAndHelper();
			}
			case "5" -> {
				utils.displayProjectInfo();
			}
			case "0" -> {
				return;
			}
			default -> {
				System.out.println("Input khong hop le");
			}
			}
		}
	}

	public static void chooseDatasetAndDebug() {
		String[] datasets = { "logistics_main.txt", "flow_demo.txt", "ds_rev.txt", "ds_flow0.txt", "ds_zero.txt",
				"ds_nopath.txt" };
		// Hiển thị danh sách ra màn hình
		for (int i = 0; i < datasets.length; i++) {
			System.out.printf("[%d] %s\n", (i + 1), datasets[i]);
		}
		System.out.println("[0] Quay lai");
		System.out.print("Chon file [0-" + datasets.length + "]: ");

		String choiceStr = scanner.nextLine().trim();
		try {
			int choice = Integer.parseInt(choiceStr);
			if (choice == 0) {
				return;
			}
			if (choice > 0 && choice <= datasets.length) {
				String filePath = datasets[choice - 1];

				// Nạp dữ liệu vào graph
				utils.setData(filePath);
				utils.resetResults();
				System.out.println("[OK] Nap thanh cong dataset: " + graph.getDatasetName());
				System.out.println("[OK] Tong so dinh (V) = " + graph.getVertices().size() + " | Tong so cung (E) = "
						+ graph.getEdgeCount());
			} else {
				System.out.println("[ERROR] Lua chon khong hop le.");
			}
		} catch (NumberFormatException e) {
			System.out.println("[ERROR] Vui long nhap mot so nguyen.");
		} catch (Exception e) {
			System.out.println("[ERROR] Loi khi doc file: " + e.getMessage());
		}

	}
	
}
