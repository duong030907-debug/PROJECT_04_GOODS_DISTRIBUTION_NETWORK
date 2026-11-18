package ui;

import java.util.Scanner;
import util.Utils;

public class UI {

	public static Scanner scanner = new Scanner(System.in);
	public static Utils utils = new Utils();

	public static void start() {
		while (true) {
			System.out.printf("""
					+================================================================+
					|                   GOODS DISTRIBUTION NETWORK                   |
					+================================================================+
					| Dataset : %-52s |
					| Graph   : Directed | Capacity(int >=0) + Weight(real >=0)      |
					| V = %-3d | E = %-3d | Zero-capacity arcs = %-21d |
					|                                                                |
					| [1] Quản lý dữ liệu mạng vận chuyển                            |
					| [2] Chạy Maximum Flow / Dijkstra                               |
					| [3] Kết quả / Kiểm chứng                                       |
					| [4] Kiểm thử / Hướng dẫn                                       |
					| [0] Thoát chương trình                                         |
					+----------------------------------------------------------------+
					Chọn chức năng [0-4]: """, utils.getDatasetName(), utils.getVertexCount(), utils.getEdgeCount(),
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
				System.out.println("Thoát chương trình thành công!");
				return;
			}
			default -> {
				System.out.println("[ERROR} Input không hợp lệ!");
			}
			}
		}
	}

	public static void menu2() {
		while (true) {
			System.out.printf("""
					+================================================================+
					|                 QUẢN LÝ DỮ LIỆU MẠNG VẬN CHUYỂN                |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Nạp dữ liệu từ file                                        |
					| [2] Xem tổng hợp graph gốc + capacity/weight                   |
					| [3] Chọn dataset mẫu / kiểm thử                                |
					| [4] Reload hoặc Clear dataset hiện tại                         |
					| [0] Quay lại Main Menu                                         |
					+----------------------------------------------------------------+
					Chọn chức năng: """, utils.getDatasetName());
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.print("""
						[1] Nạp dữ liệu từ file
						Nhập đường dẫn (path.txt): """);
				String path = scanner.nextLine().trim();
				try {
					if (utils.setData(path)) {
						System.out.printf("""
								+================================================================+
								|                   GOODS DISTRIBUTION NETWORK                   |
								+================================================================+
								| Dataset : %-52s |
								| Graph   : Directed | Capacity(int >=0) + Weight(real >=0)      |
								| V = %-3d | E = %-3d | Zero-capacity arcs = %-21d |
								+----------------------------------------------------------------+%n""",
								utils.getDatasetName(), utils.getVertexCount(), utils.getEdgeCount(),
								utils.getZeroCapCount());
						System.out.println("[INFO] Nhập dữ liệu thành công");
					}
				} catch (Exception e) {
					System.out.println("[ERROR] Failed to load file: " + e.getMessage());
				}
			}
			case "2" -> {
				System.out.println("[2] Xem tổng hợp graph gốc + capacity/weight ");
				utils.displayGraph();
			}
			case "3" -> {
				System.out.println("[3] Chọn dataset mẫu / kiểm thử ");
				chooseDatzasetAndDebug();
			}
			case "4" -> {
				while (true) {
					System.out.printf("""
							+================================================================+
							|             RELOAD HOẶC CLEAR DATASET HIỆN TẠI                 |
							+================================================================+
							| Dataset : %-52s |
							|                                                                |
							| [1] Reload dataset                                             |
							| [2] Clear dataset                                              |
							| [0] Quay lại Main Menu                                         |
							+----------------------------------------------------------------+
							Chọn chức năng: """, utils.getDatasetName());
					String j = scanner.nextLine().trim();
					switch (j) {
					case "1" -> {
						System.out.println(utils.reloadDataset() ? "[INFO] Reload thành công" : "[INFO] Reload thất bại");
					}
					case "2" -> {
						System.out.println(utils.clearDataset() ? "[INFO] Clear thành công" : "[INFO] Clear thất bại");
					}
					case "0" -> {
						break;
					}
					default -> {
						System.out.println("[ERROR} Input không hợp lệ!");
					}
					}
					if (j.equals("0"))
						break;
				}
			}
			case "0" -> {
				return;
			}
			default -> {
				System.out.println("[ERROR} Input không hợp lệ!");
			}
			}
		}
	}

	private static void chooseDatasetAndDebug() {
		System.out.println("[INFO] Chức năng chọn dataset mẫu đang được phát triển.");
	}

	public static void menu3() {
		while (true) {
			System.out.printf("""
					+================================================================+
					|                  CHẠY MAXIMUM FLOW / DIJKSTRA                  |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Ford-Fulkerson - chọn Source/Sink và chạy                  |
					| [2] Dijkstra - chọn Source/Target và chạy trên graph gốc       |
					| [3] Chạy cả hai bài toán trên cùng graph gốc                   |
					| [0] Quay lại Main Menu                                         |
					+----------------------------------------------------------------+
					Chọn chức năng: """, utils.getDatasetName());
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.print("Enter Source vertex: ");
				String source = scanner.nextLine().trim();
				System.out.print("Enter Sink vertex: ");
				String sink = scanner.nextLine().trim();
				try {
					utils.fordFulkerson(source, sink);
					System.out.println("[SUCCESS] Ford-Fulkerson chạy thành công. Max Flow = " + utils.getMaxFlow());
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
					utils.dijkstra(source, target);
					System.out.printf("[SUCCESS] Dijkstra chạy thành công. Shortest path: %s. Tổng weight: %s\n",
							utils.getShortestPath(), utils.getTotalWeight());
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
					utils.fordFulkerson(s, st);
					utils.dijkstra(s, st);
					System.out.println("[SUCCESS] Both algorithms executed successfully.");
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
				}
			}
			case "0" -> {
				return;
			}
			default -> {
				System.out.println("[ERROR} Input không hợp lệ!");
			}
			}
		}
	}

	public static void menu4() {
		while (true) {
			System.out.printf("""
					+================================================================+
					|                     KẾT QUẢ / KIỂM CHỨNG                       |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Xem Max Flow và flow trên cung gốc                         |
					| [2] Xem Residual Graph / augmenting paths / bottleneck         |
					| [3] Xem Dijkstra: Distance/Predecessor, Path/Total Cost        |
					| [4] Verify Flow và phân tích tổng hợp hai bài toán             |
					| [0] Quay lại Main Menu                                         |
					+----------------------------------------------------------------+
					Chọn chức năng: """, utils.getDatasetName());
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
				System.out.println("[ERROR} Input không hợp lệ!");
			}
			}
		}
	}

	public static void menu5() {
		while (true) {
			System.out.printf("""
					+================================================================+
					|                 MENU 5 - KIỂM THỬ / HƯỚNG DẪN                  |
					+================================================================+
					| Dataset : %-52s |
					|                                                                |
					| [1] Chạy một testcase                                          |
					| [2] Chạy tất cả testcase                                       |
					| [3] Xem tổng hợp Pass / Fail                                   |
					| [4] Định dạng input / Hướng dẫn thuật toán                     |
					| [5] Thông tin project / thành viên                             |
					| [0] Quay lại Main Menu                                         |
					+----------------------------------------------------------------+
					Chọn chức năng [0-5]: """, utils.getDatasetName());
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("Nhập tên test case:");
				String path = scanner.nextLine().trim();
				try {
					utils.runTestCase(path);
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
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
				System.out.println("[ERROR} Input không hợp lệ!");
			}
			}
		}
	}
}