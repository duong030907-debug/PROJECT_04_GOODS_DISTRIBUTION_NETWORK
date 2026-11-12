import java.util.List;
import java.util.Locale;

import testing.TestRunner;
import ui.SwingUI;
import ui.UI;

/**
 * Diem vao duy nhat cua chuong trinh.
 *
 * <p>Chay giao dien Console: {@code java -cp bin Main}</p>
 * <p>Chay giao dien Swing: {@code java -cp bin Main --gui}</p>
 * <p>Chay self-test: {@code java -cp bin Main --selftest}</p>
 */
public final class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--selftest")) {
            System.exit(runSelfTest());
        }
        if (args.length > 0 && (args[0].equalsIgnoreCase("--gui")
                || args[0].equalsIgnoreCase("gui"))) {
            SwingUI.launch();
            return;
        }
        UI.mainMenu();
    }

    private static int runSelfTest() {
        System.out.println("=== SELF-TEST: PROJECT 04 GOODS DISTRIBUTION NETWORK ===");
        List<TestRunner.CaseResult> results = TestRunner.runAll();
        int passed = 0;
        for (TestRunner.CaseResult result : results) {
            System.out.printf(Locale.US, "%-5s %-8s %s%n", result.getCode(),
                    result.isPass() ? "PASS" : "FAIL", result.getTitle());
            if (!result.isPass()) {
                System.out.println("      Expected: " + result.getExpected());
                System.out.println("      Actual  : " + result.getActual());
                for (String detail : result.getDetails()) {
                    System.out.println("      - " + detail);
                }
            } else {
                passed++;
            }
        }
        System.out.println("------------------------------------------------------");
        System.out.println("TOTAL: " + passed + "/" + results.size() + " PASS");
        return passed == results.size() ? 0 : 1;
    }
}
