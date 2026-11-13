package ui;

import java.io.IOException;
import java.util.Scanner;

import io.IO;
import utils.Utils;

public class UI {
	public static Scanner scanner = new Scanner(System.in);
	public static Utils utils = new Utils();

	public static void mainMenu() {
		while (true) {
			System.out.println("""
					+================================================================+
					|  					GOODS DISTRIBUTION NETWORK        			 |
					+================================================================+
					| Dataset : logistics_mascanner.txt                                   |
					| Graph : Directed | Capacity(int >=0) + Weight(real >=0)     	 |
					| V = 20 | E = 34 | Zero-capacity arcs = 2                       |
					|                                                                |
					| [1] Quan ly du lieu mang van chuyen                            |
					| [2] Chay Maximum Flow / Dijkstra                               |
					| [3] Ket qua / Kiem chung                                       |
					| [4] Kiem thu / Huong dan                                       |
					| [0] Thoat chuong trinh                                         |
					+----------------------------------------------------------------+
					Chon chuc nang: """);
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
			System.out.println("""
					+================================================================+
					|                 QUAN LY DU LIEU MANG VAN CHUYEN                |
					+================================================================+
					| Dataset : logistics_mascanner.txt                                   |
					|                                                                |
					| [1] Nap du lieu tu file                                        |
					| [2] Xem tong hop graph goc + capacity/weight                   |
					| [3] Chon dataset mau / kiem thu                                |
					| [4] Reload hoac Clear dataset hien tai                         |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang: """);

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
				System.out.println("[3] Chon dataset mau / kiem thu ");
				utils.chooseDatasetAndDebug();
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
					if ((a == "1" || a == "2")) {
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
			System.out.println("""
					+================================================================+
					|                  CHAY MAXIMUM FLOW / DIJKSTRA                  |
					+================================================================+
					| Dataset : logistics_mascanner.txt                                   |
					|                                                                |
					| [1] Ford-Fulkerson - chon Source/Sink va chay                 |
					| [2] Dijkstra - chon Source/Target va chay tren graph goc      |
					| [3] Chay ca hai bai toan tren cung graph goc                  |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang: """);
			if (!scanner.hasNextInt()) {
				System.out.println("""
						Input khong hop le""");
				scanner.next();
				continue;
			}
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("[1] Ford-Fulkerson - chon Source/Sink va chay");
				utils.fordFulkerson();
			}
			case "2" -> {
				System.out.println("[2] Dijkstra - chon Source/Target va chay tren graph goc");
				String u = scanner.nextLine().trim();
				String v = scanner.nextLine().trim();
				utils.dijkstra(u, v);
			}
			case "3" -> {
				System.out.println("[3] Chay ca hai bai toan tren cung graph goc");
				utils.runFordFulkersonAndDijkstra();
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
			System.out.println("""
					+================================================================+
					|                     KET QUA / KIEM CHUNG                       |
					+================================================================+
					| Dataset : logistics_mascanner.txt                                   |
					|                                                                |
					| [1] Xem Max Flow va flow tren cung goc                         |
					| [2] Xem Residual Graph / augmenting paths / bottleneck         |
					| [3] Xem Dijkstra: Distance/Predecessor, Path/Total Cost        |
					| [4] Verify Flow va phan tich tong hop hai bai toan             |
					| [0] Quay lai Main Menu                                         |
					+----------------------------------------------------------------+
					Chon chuc nang:""");
			if (!scanner.hasNextInt()) {
				System.out.println("""
						Input khong hop le""");
				scanner.next();
				continue;
			}
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("[1] Xem Max Flow va flow tren cung goc ");
				utils.getMaxFlowAndTopFlow();
			}
			case "2" -> {
				System.out.println("[2] Xem Residual Graph / augmenting paths / bottleneck ");
				utils.displayResidualGraph();
			}
			case "3" -> {
				System.out.println("[4] Verify Flow va phan tich tong hop hai bai toan ");
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
			System.out.println("""
					MENU 5 - KIEM THU / HUONG DAN
					[1] Chay mot testcase
					[2] Chay tat ca testcase
					[3] Xem tong hop Pass / Fail
					[4] Dinh dang input / Huong dan thuat toan
					[5] Thong tin project / thanh vien
					[0] Quay lai Main Menu""");
			String i = scanner.nextLine().trim();
			switch (i) {
			case "1" -> {
				System.out.println("[1] Xem Max Flow va flow tren cung goc ");
				utils.runTestCase();
			}
			case "2" -> {
				System.out.println("[2] Xem Residual Graph / augmenting paths / bottleneck ");
				utils.runTestAllCase();
			}
			case "3" -> {
				System.out.println("[4] Verify Flow va phan tich tong hop hai bai toan ");
				utils.displayPassFail();
			}
			case "4" -> {
				System.out.println("[4] Verify Flow va phan tich tong hop hai bai toan ");
				utils.configAndHelper();
			}
			case "5" -> {
				System.out.println("[4] Verify Flow va phan tich tong hop hai bai toan ");
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
