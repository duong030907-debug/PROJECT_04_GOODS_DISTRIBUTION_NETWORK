package ui;

import java.util.Scanner;

import util.Utils;

public class UI {

	public static Scanner scanner = new Scanner(System.in);
	public static Utils utils = new Utils();

	public void start() {
		while (true) {
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
					Chon chuc nang [0-4]: """, utils.getDatasetName(), utils.getVertexCount(), utils.getEdgeCount(),
					utils.getZeroCapCount());
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
					Chon chuc nang: """, utils.getDatasetName());
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("""
						[1] Nap du lieu tu file
						Nhap duong dan (path.txt): """);
				String path = scanner.nextLine().trim();
				try {
					if (utils.setData(path)) {
						System.out.printf(
								"""
										+================================================================+
										|                   GOODS DISTRIBUTION NETWORK                   |
										+================================================================+
										| Dataset : %-52s |
										| Graph   : Directed | Capacity(int >=0) + Weight(real >=0)      |
										| V = %-3d | E = %-3d | Zero-capacity arcs = %-21d |                                    |
										+----------------------------------------------------------------+""",
								utils.getDatasetName(), utils.getVertexCount(), utils.getEdgeCount(),
								utils.getZeroCapCount());
						System.out.println("[INFO] Nhap du lieu thanh cong");
					}
				} catch (Exception e) {
					System.out.println("[ERROR] Failed to load file: " + e.getMessage());
				}
			}
			case "2" -> {
				System.out.println("[2] Xem tong hop graph goc + capacity/weight ");
				utils.displayGraph();
				continue;
			}
			case "3" -> {
				System.out.println("[2] Chon dataset mau / kiem thu ");
				chooseDatasetAndDebug();
				continue;
			}
			case "4" -> {
				while (true) {
					System.out.println("""
							[4] Reload hoac Clear dataset hien tai
							\t [1] Reload dataset
							\t [2] Clear dataset
							\t [0] Quay lai Main Menu
							""");
					String j = scanner.nextLine().trim();
					switch (j) {
					case "1" -> {
						System.out.println(utils.reloadDataset() ? "Reload thanh cong" : "Reload that bai");
					}
					case "2" -> {
						System.out.println(utils.clearDataset() ? "Clear thanh cong" : "Clear that bai");
					}

					case "0" -> {
						break;
					}
					default -> {
						System.out.println("Input khong hop le");
					}
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

	private static void chooseDatasetAndDebug() {
		// TODO Auto-generated method stub
		
	}

	public static void menu3() {
		while (true) {
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
					Chon chuc nang: """, utils.getDatasetName());
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.print("Enter Source vertex: ");
				String source = scanner.nextLine().trim();
				System.out.print("Enter Sink vertex: ");
				String sink = scanner.nextLine().trim();
				try {
					utils.fordFulkerson(source,sink);
					System.out.println("[SUCCESS] Ford-Fulkerson completed. Max Flow = " + utils.getMaxFlow());
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
				}
			}
			case "2" -> {
				System.out.print("Enter Source vertex: ");
				String source = scanner.nextLine().trim();
				System.out.print("Enter Target vertex: ");
				String target = scanner.nextLine().trim();
				try {
					utils.dijkstra(source,target);
					System.out.printf("[SUCCESS] Dijkstra completed. Shortest path: %s . Total weight: %s\n" + utils.getShortestPath(), utils.getTotalWeight());
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
				}
			}
			case "3" -> {
				System.out.print("Enter Source vertex: ");
				String s = scanner.nextLine().trim();
				System.out.print("Enter Sink/Target vertex: ");
				String st = scanner.nextLine().trim();
				try {
					utils.fordFulkerson(s,st);
					utils.dijkstra(s, st);
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
					Chon chuc nang:""", utils.getDatasetName());
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
					Chon chuc nang [0-5]: """, utils.getDatasetName());
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("Nhap ten test case:");
				String path = scanner.nextLine().trim();
				try {
					utils.runTestCase(path);
				}catch (Exception e) {
					System.out.println("[ERROR] "+ e.getMessage());
				}
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
}
