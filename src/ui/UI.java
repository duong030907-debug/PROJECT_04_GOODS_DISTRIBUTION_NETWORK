package ui;

import utils.Utils;

/**
 * Giao dien Console gom 5 man hinh menu toi thieu:
 * 1 Main Menu + 4 menu phu (Quan ly du lieu, Chay thuat toan,
 * Ket qua/Kiem chung, Kiem thu/Huong dan).
 */
public class UI {

    public static final Utils utils = new Utils();

    public static void mainMenu() {
        if (utils.autoLoadMainDataset()) {
            System.out.println("[INFO] Da nap san dataset chinh: " + utils.statusLine());
        } else {
            System.out.println("[INFO] Chua nap dataset. Hay vao menu 2 -> [1] de nap du lieu.");
        }

        while (true) {
            System.out.println();
            System.out.println("+================================================================+");
            System.out.println("|                 GOODS DISTRIBUTION NETWORK                     |");
            System.out.println("+================================================================+");
            System.out.println("| Dataset : " + pad(utils.statusLine(), 52) + " |");
            System.out.println("| Graph   : Directed | Capacity(int >=0) + Weight(real >=0)    |");
            System.out.println("|                                                                |");
            System.out.println("| [1] Quan ly du lieu mang van chuyen                            |");
            System.out.println("| [2] Chay Maximum Flow / Dijkstra                               |");
            System.out.println("| [3] Ket qua / Kiem chung                                       |");
            System.out.println("| [4] Kiem thu / Huong dan                                       |");
            System.out.println("| [0] Thoat chuong trinh                                         |");
            System.out.println("+----------------------------------------------------------------+");

            int choice = Utils.readIntInRange("Chon chuc nang: ", 0, 4);
            if (choice == Integer.MIN_VALUE) {
                break;
            }
            switch (choice) {
                case 1 -> menu2();
                case 2 -> menu3();
                case 3 -> menu4();
                case 4 -> menu5();
                case 0 -> {
                    System.out.println("Tam biet!");
                    return;
                }
                default -> System.out.println("INVALID INPUT: vui long chon lai.");
            }
        }
    }

    private static void menu2() {
        while (true) {
            System.out.println();
            System.out.println("+================================================================+");
            System.out.println("|              QUAN LY DU LIEU MANG VAN CHUYEN                   |");
            System.out.println("+================================================================+");
            System.out.println("| Dataset : " + pad(utils.statusLine(), 52) + " |");
            System.out.println("|                                                                |");
            System.out.println("| [1] Nap du lieu tu file                                        |");
            System.out.println("| [2] Xem tong hop graph goc + capacity/weight                   |");
            System.out.println("| [3] Chon dataset mau / kiem thu                                |");
            System.out.println("| [4] Reload hoac Clear dataset hien tai                         |");
            System.out.println("| [0] Quay lai Main Menu                                         |");
            System.out.println("+----------------------------------------------------------------+");

            int choice = Utils.readIntInRange("Chon chuc nang: ", 0, 4);
            if (choice == Integer.MIN_VALUE) {
                return;
            }
            switch (choice) {
                case 1 -> utils.setDataFromFile();
                case 2 -> utils.displayGraph();
                case 3 -> utils.chooseDatasetAndDebug();
                case 4 -> {
                    int action = Utils.readIntInRange(
                            "  [1] Reload  [2] Clear  [0] Quay lai: ", 0, 2);
                    if (action == Integer.MIN_VALUE) {
                        return;
                    }
                    if (action != 0) {
                        utils.reloadOrClearDataset(action);
                    }
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("INVALID INPUT: vui long chon lai.");
            }
        }
    }

    private static void menu3() {
        while (true) {
            System.out.println();
            System.out.println("+================================================================+");
            System.out.println("|                CHAY MAXIMUM FLOW / DIJKSTRA                    |");
            System.out.println("+================================================================+");
            System.out.println("| Dataset : " + pad(utils.statusLine(), 52) + " |");
            System.out.println("|                                                                |");
            System.out.println("| [1] Ford-Fulkerson - chon Source/Sink va chay                  |");
            System.out.println("| [2] Dijkstra - chon Source/Target va chay tren graph goc       |");
            System.out.println("| [3] Chay ca hai bai toan tren cung graph goc                   |");
            System.out.println("| [0] Quay lai Main Menu                                         |");
            System.out.println("+----------------------------------------------------------------+");

            int choice = Utils.readIntInRange("Chon chuc nang: ", 0, 3);
            if (choice == Integer.MIN_VALUE) {
                return;
            }
            switch (choice) {
                case 1 -> utils.fordFulkerson();
                case 2 -> utils.dijkstra();
                case 3 -> utils.runFordFulkersonAndDijkstra();
                case 0 -> {
                    return;
                }
                default -> System.out.println("INVALID INPUT: vui long chon lai.");
            }
        }
    }

    private static void menu4() {
        while (true) {
            System.out.println();
            System.out.println("+================================================================+");
            System.out.println("|                    KET QUA / KIEM CHUNG                        |");
            System.out.println("+================================================================+");
            System.out.println("| Dataset : " + pad(utils.statusLine(), 52) + " |");
            System.out.println("|                                                                |");
            System.out.println("| [1] Xem Max Flow va flow tren cung goc                         |");
            System.out.println("| [2] Xem Residual Graph / augmenting paths / bottleneck         |");
            System.out.println("| [3] Xem Dijkstra: Distance/Predecessor, Path/Total Cost        |");
            System.out.println("| [4] Verify Flow va phan tich tong hop hai bai toan             |");
            System.out.println("| [0] Quay lai Main Menu                                         |");
            System.out.println("+----------------------------------------------------------------+");

            int choice = Utils.readIntInRange("Chon chuc nang: ", 0, 4);
            if (choice == Integer.MIN_VALUE) {
                return;
            }
            switch (choice) {
                case 1 -> utils.getMaxFlowAndTopFlow();
                case 2 -> utils.displayResidualGraph();
                case 3 -> utils.displayDijkstraResult();
                case 4 -> utils.verifyFlowAndExamine();
                case 0 -> {
                    return;
                }
                default -> System.out.println("INVALID INPUT: vui long chon lai.");
            }
        }
    }

    private static void menu5() {
        while (true) {
            System.out.println();
            System.out.println("+================================================================+");
            System.out.println("|                    KIEM THU / HUONG DAN                        |");
            System.out.println("+================================================================+");
            System.out.println("| [1] Chay mot testcase                                          |");
            System.out.println("| [2] Chay tat ca testcase                                       |");
            System.out.println("| [3] Xem tong hop Pass / Fail                                   |");
            System.out.println("| [4] Dinh dang input / Huong dan thuat toan                     |");
            System.out.println("| [5] Thong tin project / thanh vien                             |");
            System.out.println("| [0] Quay lai Main Menu                                         |");
            System.out.println("|                                                                |");
            System.out.println("| Vi du: TC01 max flow dataset chinh = 35                        |");
            System.out.println("|        TC02 residual reverse update                            |");
            System.out.println("|        TC03 max flow = 0 khi Source khong toi Sink             |");
            System.out.println("+----------------------------------------------------------------+");

            int choice = Utils.readIntInRange("Chon chuc nang: ", 0, 5);
            if (choice == Integer.MIN_VALUE) {
                return;
            }
            switch (choice) {
                case 1 -> utils.runTestCase();
                case 2 -> utils.runTestAllCase();
                case 3 -> utils.displayPassFail();
                case 4 -> utils.configAndHelper();
                case 5 -> utils.displayProjectInfo();
                case 0 -> {
                    return;
                }
                default -> System.out.println("INVALID INPUT: vui long chon lai.");
            }
        }
    }

    private static String pad(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }
}
