package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import algorithm.Dijkstra;
import algorithm.FordFulkerson;
import io.InputOuputManager;
import models.Arc;
import models.Graph;
import models.Network;
import models.ResidualGraph;
import testing.TestRunner;

/**
 * Giao dien do hoa bang Swing (javax.swing).
 *
 * <p>Nam tab tuong ung 5 man hinh cua ban Console: Du lieu, Chay thuat toan,
 * Ket qua, Kiem thu, Huong dan. GUI dung lai toan bo model/thuat toan cua
 * project, khong thay the hay lam mat chuc nang bat buoc.</p>
 */
public final class SwingUI {

    private final InputOuputManager io = new InputOuputManager();

    private Network network;
    private String currentPath;
    private FordFulkerson.MaxFlowResult ffResult;
    private Dijkstra.Result djResult;
    private int ffSource = -1;
    private int ffSink = -1;
    private int djSource = -1;
    private int djTarget = -1;
    private List<TestRunner.CaseResult> testResults = List.of();

    private JFrame frame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JLabel datasetSummaryLabel;
    private JLabel datasetVerticesLabel;

    private JComboBox<String> datasetCombo;
    private JComboBox<String> sourceCombo;
    private JComboBox<String> sinkCombo;
    private JComboBox<String> targetCombo;
    private JComboBox<String> testCombo;

    private DefaultTableModel arcModel;
    private DefaultTableModel flowModel;
    private DefaultTableModel distModel;
    private DefaultTableModel testModel;
    private JTable arcTable;
    private JTable flowTable;
    private JTable distTable;
    private JTable testTable;

    private JTextArea adjacencyArea;
    private JTextArea capacityArea;
    private JTextArea weightArea;
    private JTextArea algorithmArea;
    private JTextArea residualArea;
    private JTextArea dijkstraDetailArea;
    private JTextArea verifyArea;
    private JTextArea testDetailArea;

    private JLabel flowSummaryLabel;
    private JLabel dijkstraSummaryLabel;
    private JLabel testSummaryLabel;

    private JTabbedPane mainTabs;
    private JTabbedPane dataTabs;
    private JTabbedPane resultTabs;
    private JTabbedPane helpTabs;

    public static void launch() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[ERROR] Moi truong hien tai khong ho tro GUI (headless).");
            System.out.println("Hay chay Console: java -cp bin Main");
            return;
        }
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignore) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // dung Look and Feel mac dinh
            }
        }
        Theme.install();
        SwingUtilities.invokeLater(() -> new SwingUI().show());
    }

    private void show() {
        buildFrame();
        loadInitialDataset();
        frame.setVisible(true);
    }

    // =====================================================================
    // Dung giao dien
    // =====================================================================

    private void buildFrame() {
        frame = new JFrame("Goods Distribution Network — Project 04");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1120, 740));
        frame.setSize(1240, 820);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Theme.BG);
        frame.setJMenuBar(buildMenuBar());
        frame.add(buildHeader(), BorderLayout.NORTH);

        mainTabs = new JTabbedPane();
        Theme.styleTabs(mainTabs);
        mainTabs.addTab("Dữ liệu", buildDataTab());
        mainTabs.addTab("Thuật toán", buildAlgorithmTab());
        mainTabs.addTab("Kết quả & kiểm chứng", buildResultTab());
        mainTabs.addTab("Kiểm thử", buildTestTab());
        mainTabs.addTab("Hướng dẫn", buildHelpTab());
        mainTabs.setBorder(new javax.swing.border.EmptyBorder(6, 18, 0, 18));
        frame.add(mainTabs, BorderLayout.CENTER);
        frame.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        Theme.GradientHeader panel = new Theme.GradientHeader();
        panel.setLayout(new BorderLayout(16, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Goods Distribution Network");
        title.setFont(Theme.font(22, Font.BOLD));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "Ford-Fulkerson · Dijkstra · Phân tích mạng phân phối hàng hóa");
        subtitle.setFont(Theme.font(12));
        subtitle.setForeground(new Color(0xC7D2FE));

        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);

        headerLabel = new Theme.ChipLabel("Chưa nạp dataset");
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chip.setOpaque(false);
        chip.add(headerLabel);

        panel.add(text, BorderLayout.WEST);
        panel.add(chip, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                new javax.swing.border.EmptyBorder(8, 18, 8, 18)));

        JLabel dot = new JLabel("●");
        dot.setForeground(Theme.SUCCESS);
        dot.setFont(Theme.font(11));

        statusLabel = new JLabel("Sẵn sàng.");
        statusLabel.setFont(Theme.font(12));
        statusLabel.setForeground(Theme.MUTED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(dot);
        left.add(statusLabel);
        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("Tệp");
        JMenuItem openItem = new JMenuItem("Nạp dataset...");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> chooseFile());
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> frame.dispose());
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu runMenu = new JMenu("Chạy");
        JMenuItem ffItem = new JMenuItem("Ford-Fulkerson");
        ffItem.addActionListener(e -> {
            mainTabs.setSelectedIndex(1);
            runFordFulkerson();
        });
        JMenuItem djItem = new JMenuItem("Dijkstra");
        djItem.addActionListener(e -> {
            mainTabs.setSelectedIndex(1);
            runDijkstra();
        });
        JMenuItem bothItem = new JMenuItem("Cả hai bài toán");
        bothItem.addActionListener(e -> {
            mainTabs.setSelectedIndex(1);
            runBoth();
        });
        runMenu.add(ffItem);
        runMenu.add(djItem);
        runMenu.add(bothItem);

        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem guideItem = new JMenuItem("Hướng dẫn sử dụng");
        guideItem.addActionListener(e -> mainTabs.setSelectedIndex(4));
        JMenuItem aboutItem = new JMenuItem("Thông tin project");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(guideItem);
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(runMenu);
        bar.add(helpMenu);
        Theme.styleMenuBar(bar);
        return bar;
    }

    private JPanel buildDataTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 10, 4));

        Theme.Card toolbar = new Theme.Card(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton openButton = Theme.primaryButton("Nạp file...");
        openButton.addActionListener(e -> chooseFile());
        JButton reloadButton = Theme.secondaryButton("Reload");
        reloadButton.addActionListener(e -> reloadDataset());
        JButton clearButton = Theme.dangerButton("Clear");
        clearButton.addActionListener(e -> clearDataset());

        datasetCombo = new JComboBox<>();
        for (InputOuputManager.DatasetInfo info : InputOuputManager.VALID_DATASETS) {
            datasetCombo.addItem(info.code() + " - " + info.label());
        }
        Theme.styleCombo(datasetCombo);
        datasetCombo.setPreferredSize(new Dimension(360, 36));

        JButton loadSampleButton = Theme.secondaryButton("Nạp dataset mẫu");
        loadSampleButton.addActionListener(e -> loadSelectedSample());

        toolbar.add(openButton);
        toolbar.add(reloadButton);
        toolbar.add(clearButton);
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(Theme.fieldLabel("DATASET MẪU"));
        toolbar.add(datasetCombo);
        toolbar.add(loadSampleButton);

        Theme.Card info = new Theme.Card(new BorderLayout(0, 4));
        datasetSummaryLabel = new JLabel("Chưa nạp dataset");
        datasetSummaryLabel.setFont(Theme.font(13, Font.BOLD));
        datasetSummaryLabel.setForeground(Theme.TEXT);
        datasetVerticesLabel = new JLabel(" ");
        datasetVerticesLabel.setFont(Theme.font(12));
        datasetVerticesLabel.setForeground(Theme.MUTED);
        JLabel convention = new JLabel(
                "Quy ước: capacity nguyên ≥ 0 · weight thực ≥ 0 · capacity = 0 là tuyến không khả dụng");
        convention.setFont(Theme.font(11));
        convention.setForeground(Theme.MUTED);
        info.add(datasetSummaryLabel, BorderLayout.NORTH);
        info.add(datasetVerticesLabel, BorderLayout.CENTER);
        info.add(convention, BorderLayout.SOUTH);

        JPanel headerArea = new JPanel();
        headerArea.setOpaque(false);
        headerArea.setLayout(new BoxLayout(headerArea, BoxLayout.Y_AXIS));
        headerArea.add(toolbar);
        headerArea.add(Box.createVerticalStrut(12));
        headerArea.add(info);

        arcModel = tableModel("STT", "u", "v", "Capacity", "Weight", "Ghi chú");
        arcTable = new JTable(arcModel);
        configureTable(arcTable);
        setColumnWidths(arcTable, 55, 90, 90, 100, 90, 140);

        adjacencyArea = textArea();
        capacityArea = textArea();
        weightArea = textArea();

        dataTabs = new JTabbedPane();
        Theme.styleTabs(dataTabs);
        dataTabs.addTab("Danh sách cung", scroll(arcTable));
        dataTabs.addTab("Adjacency list", scroll(adjacencyArea));
        dataTabs.addTab("Capacity matrix", scroll(capacityArea));
        dataTabs.addTab("Weight matrix", scroll(weightArea));

        Theme.Card content = new Theme.Card(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        content.add(dataTabs, BorderLayout.CENTER);

        panel.add(headerArea, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAlgorithmTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 10, 4));

        Theme.Card controls = new Theme.Card(new BorderLayout(0, 12));

        JPanel endpoints = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        endpoints.setOpaque(false);
        sourceCombo = new JComboBox<>();
        sinkCombo = new JComboBox<>();
        targetCombo = new JComboBox<>();
        for (JComboBox<String> combo : List.of(sourceCombo, sinkCombo, targetCombo)) {
            Theme.styleCombo(combo);
            combo.setPreferredSize(new Dimension(130, 36));
        }
        endpoints.add(Theme.fieldLabel("SOURCE"));
        endpoints.add(sourceCombo);
        endpoints.add(Theme.fieldLabel("SINK (MAX FLOW)"));
        endpoints.add(sinkCombo);
        endpoints.add(Theme.fieldLabel("TARGET (DIJKSTRA)"));
        endpoints.add(targetCombo);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        JButton ffButton = Theme.secondaryButton("Chạy Ford-Fulkerson");
        ffButton.addActionListener(e -> runFordFulkerson());
        JButton djButton = Theme.secondaryButton("Chạy Dijkstra");
        djButton.addActionListener(e -> runDijkstra());
        JButton bothButton = Theme.primaryButton("Chạy cả hai");
        bothButton.addActionListener(e -> runBoth());
        JButton resultButton = Theme.ghostButton("Xem kết quả  →");
        resultButton.addActionListener(e -> mainTabs.setSelectedIndex(2));

        buttons.add(ffButton);
        buttons.add(djButton);
        buttons.add(bothButton);
        buttons.add(Box.createHorizontalStrut(4));
        buttons.add(resultButton);

        JLabel hint = new JLabel("Ford-Fulkerson dùng Source/Sink. Dijkstra dùng Source/Target "
                + "(Source = Target hợp lệ, cost = 0).");
        hint.setFont(Theme.font(12));
        hint.setForeground(Theme.MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        JPanel controlBody = new JPanel();
        controlBody.setOpaque(false);
        controlBody.setLayout(new BoxLayout(controlBody, BoxLayout.Y_AXIS));
        controlBody.add(endpoints);
        controlBody.add(buttons);
        controlBody.add(Box.createVerticalStrut(8));
        controlBody.add(hint);

        controls.add(controlBody, BorderLayout.CENTER);

        algorithmArea = textArea();
        Theme.Card output = new Theme.Card(new BorderLayout(0, 8));
        output.add(Theme.sectionTitle("KẾT QUẢ CHẠY"), BorderLayout.NORTH);
        output.add(scroll(algorithmArea), BorderLayout.CENTER);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(output, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildResultTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 10, 4));

        resultTabs = new JTabbedPane();
        Theme.styleTabs(resultTabs);

        flowModel = tableModel("STT", "u", "v", "Capacity", "Flow", "Residual", "Trạng thái");
        flowTable = new JTable(flowModel);
        configureTable(flowTable);
        setColumnWidths(flowTable, 55, 90, 90, 100, 85, 95, 130);
        Theme.styleStatusColumn(flowTable, 6);
        flowSummaryLabel = new JLabel("Chưa chạy Ford-Fulkerson.");
        flowSummaryLabel.setFont(Theme.font(13, Font.BOLD));
        flowSummaryLabel.setForeground(Theme.TEXT);
        flowSummaryLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 2));

        JPanel flowPanel = new JPanel(new BorderLayout());
        flowPanel.setBackground(Theme.BG);
        flowPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        Theme.Card flowCard = new Theme.Card(new BorderLayout(0, 6));
        flowCard.add(flowSummaryLabel, BorderLayout.NORTH);
        flowCard.add(scroll(flowTable), BorderLayout.CENTER);
        flowPanel.add(flowCard, BorderLayout.CENTER);
        resultTabs.addTab("Max Flow / Flow", flowPanel);

        residualArea = textArea();
        JPanel residualPanel = new JPanel(new BorderLayout());
        residualPanel.setBackground(Theme.BG);
        residualPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        Theme.Card residualCard = new Theme.Card(new BorderLayout());
        residualCard.add(scroll(residualArea), BorderLayout.CENTER);
        residualPanel.add(residualCard, BorderLayout.CENTER);
        resultTabs.addTab("Residual / Augmenting paths", residualPanel);

        distModel = tableModel("Đỉnh", "Distance", "Predecessor");
        distTable = new JTable(distModel);
        configureTable(distTable);
        setColumnWidths(distTable, 150, 160, 160);
        dijkstraSummaryLabel = new JLabel("Chưa chạy Dijkstra.");
        dijkstraSummaryLabel.setFont(Theme.font(13, Font.BOLD));
        dijkstraSummaryLabel.setForeground(Theme.TEXT);
        dijkstraSummaryLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 2));
        dijkstraDetailArea = textArea();

        JPanel distPanel = new JPanel(new BorderLayout());
        distPanel.setBackground(Theme.BG);
        distPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        Theme.Card distCard = new Theme.Card(new BorderLayout(0, 6));
        distCard.add(dijkstraSummaryLabel, BorderLayout.NORTH);
        JSplitPane djSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scroll(distTable), scroll(dijkstraDetailArea));
        djSplit.setResizeWeight(0.45);
        djSplit.setDividerSize(8);
        djSplit.setBorder(null);
        djSplit.setBackground(Theme.BG);
        distCard.add(djSplit, BorderLayout.CENTER);
        distPanel.add(distCard, BorderLayout.CENTER);
        resultTabs.addTab("Dijkstra", distPanel);

        verifyArea = textArea();
        JPanel verifyPanel = new JPanel(new BorderLayout());
        verifyPanel.setBackground(Theme.BG);
        verifyPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        Theme.Card verifyCard = new Theme.Card(new BorderLayout());
        verifyCard.add(scroll(verifyArea), BorderLayout.CENTER);
        verifyPanel.add(verifyCard, BorderLayout.CENTER);
        resultTabs.addTab("Verify / Phân tích", verifyPanel);

        panel.add(resultTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTestTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 10, 4));

        Theme.Card top = new Theme.Card(new BorderLayout(0, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);
        testCombo = new JComboBox<>();
        for (TestRunner.TestInfo info : TestRunner.CATALOG) {
            testCombo.addItem(info.code() + " - " + info.title());
        }
        Theme.styleCombo(testCombo);
        testCombo.setPreferredSize(new Dimension(430, 36));
        JButton runOne = Theme.secondaryButton("Chạy test đã chọn");
        runOne.addActionListener(e -> runSelectedTest());
        JButton runAll = Theme.primaryButton("Chạy tất cả testcase");
        runAll.addActionListener(e -> runAllTests());
        controls.add(Theme.fieldLabel("TESTCASE"));
        controls.add(testCombo);
        controls.add(runOne);
        controls.add(runAll);

        testSummaryLabel = new JLabel("Chưa chạy testcase.");
        testSummaryLabel.setFont(Theme.font(13, Font.BOLD));
        testSummaryLabel.setForeground(Theme.TEXT);
        testSummaryLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        top.add(controls, BorderLayout.NORTH);
        top.add(testSummaryLabel, BorderLayout.SOUTH);

        testModel = tableModel("Mã", "Tên testcase", "Expected", "Actual", "Kết quả");
        testTable = new JTable(testModel);
        configureTable(testTable);
        setColumnWidths(testTable, 65, 340, 250, 250, 95);
        Theme.styleStatusColumn(testTable, 4);
        testTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showTestDetail(testTable.getSelectedRow());
            }
        });

        testDetailArea = textArea();
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scroll(testTable), scroll(testDetailArea));
        split.setResizeWeight(0.5);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setBackground(Theme.BG);

        Theme.Card body = new Theme.Card(new BorderLayout());
        body.add(split, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildHelpTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 10, 4));

        helpTabs = new JTabbedPane();
        Theme.styleTabs(helpTabs);
        helpTabs.addTab("Bắt đầu", helpPage(startHelpText()));
        helpTabs.addTab("Thuật toán", helpPage(algorithmHelpText()));
        helpTabs.addTab("Dataset & testcase", helpPage(datasetHelpText()));
        helpTabs.addTab("Project", helpPage(projectHelpText()));

        Theme.Card card = new Theme.Card(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        card.add(helpTabs, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel helpPage(String text) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Theme.BG);
        page.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JTextArea area = textArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        setAreaText(area, text);
        Theme.Card card = new Theme.Card(new BorderLayout());
        card.add(scroll(area), BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static void configureTable(JTable table) {
        Theme.styleTable(table);
    }

    private static void setColumnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private static JTextArea textArea() {
        JTextArea area = new JTextArea();
        Theme.styleTextArea(area);
        return area;
    }

    private static JScrollPane scroll(java.awt.Component component) {
        return Theme.scroll(component);
    }

    // =====================================================================
    // Nap / thay doi dataset
    // =====================================================================

    private void loadInitialDataset() {
        InputOuputManager.LoadResult loaded = io.load("main_dataset.txt");
        if (loaded.isSuccess()) {
            applyNetwork(loaded);
            statusLabel.setText("Đã nạp sẵn dataset chính. Chọn tab 2 để chạy thuật toán.");
        } else {
            statusLabel.setText("Chưa nạp dataset. Vào tab 1 để nạp file.");
        }
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser(InputOuputManager.dataDirectory().toFile());
        chooser.setDialogTitle("Chọn dataset");
        chooser.setFileFilter(new FileNameExtensionFilter("Dataset (*.txt)", "txt"));
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadPath(file.getAbsolutePath());
        }
    }

    private void loadSelectedSample() {
        int index = datasetCombo.getSelectedIndex();
        if (index < 0) {
            return;
        }
        loadPath(InputOuputManager.VALID_DATASETS.get(index).fileName());
    }

    private void loadPath(String path) {
        InputOuputManager.LoadResult loaded = io.load(path);
        if (!loaded.isSuccess()) {
            showErrors("Dataset không hợp lệ", loaded.getErrors());
            statusLabel.setText("Nạp thất bại: " + path);
            return;
        }
        applyNetwork(loaded);
        mainTabs.setSelectedIndex(0);
        statusLabel.setText("Đã nạp: " + network.getDatasetName() + " | V="
                + network.vertexCount() + " | E=" + network.arcCount());
    }

    private void applyNetwork(InputOuputManager.LoadResult loaded) {
        network = loaded.getNetwork();
        currentPath = loaded.getResolvedPath();
        clearAlgorithmResults();
        refreshEndpointCombos();
        refreshAll();
        setAreaText(algorithmArea, "Đã nạp dataset: " + network.getDatasetName()
                + "\nSource mặc định: " + network.getSourceName()
                + " | Sink mặc định: " + network.getSinkName()
                + "\nHãy chọn endpoint và chạy thuật toán.");
    }

    private void reloadDataset() {
        if (currentPath == null) {
            showInfo("Chưa có dataset để reload.");
            return;
        }
        loadPath(currentPath);
    }

    private void clearDataset() {
        int answer = JOptionPane.showConfirmDialog(frame,
                "Xóa dataset và toàn bộ kết quả hiện tại?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        network = null;
        currentPath = null;
        clearAlgorithmResults();
        sourceCombo.removeAllItems();
        sinkCombo.removeAllItems();
        targetCombo.removeAllItems();
        refreshAll();
        setAreaText(algorithmArea, "Chưa có dataset.");
        statusLabel.setText("Đã xóa dataset và kết quả.");
    }

    private void clearAlgorithmResults() {
        ffResult = null;
        djResult = null;
        ffSource = -1;
        ffSink = -1;
        djSource = -1;
        djTarget = -1;
    }

    private void refreshEndpointCombos() {
        sourceCombo.removeAllItems();
        sinkCombo.removeAllItems();
        targetCombo.removeAllItems();
        if (network == null) {
            return;
        }
        for (String name : network.getVertices()) {
            sourceCombo.addItem(name);
            sinkCombo.addItem(name);
            targetCombo.addItem(name);
        }
        String source = network.hasVertex("WH") ? "WH" : network.nameOf(1);
        String sink = network.hasVertex("ST") ? "ST" : network.nameOf(network.vertexCount());
        sourceCombo.setSelectedItem(source);
        sinkCombo.setSelectedItem(sink);
        targetCombo.setSelectedItem(sink);
    }

    // =====================================================================
    // Chay thuat toan
    // =====================================================================

    private boolean ensureNetwork() {
        if (network == null) {
            showInfo("Chưa có dữ liệu. Hãy nạp dataset ở tab 1.");
            mainTabs.setSelectedIndex(0);
            return false;
        }
        return true;
    }

    private void runFordFulkerson() {
        if (!ensureNetwork()) {
            return;
        }
        String source = selected(sourceCombo);
        String sink = selected(sinkCombo);
        if (source == null || sink == null) {
            showInfo("Hãy chọn Source và Sink.");
            return;
        }
        if (source.equals(sink)) {
            showError("Source phải khác Sink cho bài toán Maximum Flow.");
            return;
        }
        computeFordFulkerson(network.idOf(source), network.idOf(sink));
        setAreaText(algorithmArea, describeFordFulkerson());
        refreshResults();
        statusLabel.setText("Đã chạy Ford-Fulkerson: Max Flow = " + ffResult.getMaxFlow()
                + " | verify = " + (ffResult.getResidualGraph().verifyFlow(ffSource, ffSink) ? "PASS" : "FAIL"));
    }

    private void runDijkstra() {
        if (!ensureNetwork()) {
            return;
        }
        String source = selected(sourceCombo);
        String target = selected(targetCombo);
        if (source == null || target == null) {
            showInfo("Hãy chọn Source và Target.");
            return;
        }
        computeDijkstra(network.idOf(source), network.idOf(target));
        setAreaText(algorithmArea, describeDijkstra());
        refreshResults();
        statusLabel.setText("Đã chạy Dijkstra: " + djResult.getStatus().label()
                + (djResult.isSuccess() ? " | cost = " + formatDouble(djResult.getTotalCost()) : ""));
    }

    private void runBoth() {
        if (!ensureNetwork()) {
            return;
        }
        String source = selected(sourceCombo);
        String sink = selected(sinkCombo);
        String target = selected(targetCombo);
        if (source == null || sink == null || target == null) {
            showInfo("Hãy chọn Source, Sink và Target.");
            return;
        }
        StringBuilder text = new StringBuilder();
        if (source.equals(sink)) {
            ffResult = null;
            ffSource = -1;
            ffSink = -1;
            text.append("[LƯU Ý] Source = Sink: Maximum Flow bị từ chối, chỉ chạy Dijkstra.\n\n");
        } else {
            computeFordFulkerson(network.idOf(source), network.idOf(sink));
            text.append(describeFordFulkerson()).append("\n\n");
        }
        computeDijkstra(network.idOf(source), network.idOf(target));
        text.append(describeDijkstra());
        setAreaText(algorithmArea, text.toString());
        refreshResults();
        statusLabel.setText("Đã chạy xong hai bài toán.");
    }

    private void computeFordFulkerson(int source, int sink) {
        ffResult = FordFulkerson.run(network.getGraph(), source, sink);
        ffSource = source;
        ffSink = sink;
    }

    private void computeDijkstra(int source, int target) {
        djResult = Dijkstra.run(network.getGraph(), source, target);
        djSource = source;
        djTarget = target;
    }

    private String describeFordFulkerson() {
        StringBuilder text = new StringBuilder();
        text.append("FORD-FULKERSON").append('\n');
        text.append("Dataset : ").append(network.getDatasetName()).append('\n');
        text.append("Source  : ").append(network.nameOf(ffSource))
                .append(" | Sink: ").append(network.nameOf(ffSink)).append('\n');
        text.append("Status  : ").append(ffResult.getMessage()).append('\n');
        text.append("Max Flow: ").append(ffResult.getMaxFlow()).append('\n');
        text.append("Số đường tăng: ").append(ffResult.getSteps().size()).append('\n');
        text.append("Cung ngược được dùng: ")
                .append(ffResult.usedReverseArc()
                        ? "CÓ (" + ffResult.getReverseArcVisits() + " lần)" : "KHÔNG")
                .append('\n');
        text.append("verifyFlow: ")
                .append(ffResult.getResidualGraph().verifyFlow(ffSource, ffSink) ? "PASS" : "FAIL");
        return text.toString();
    }

    private String describeDijkstra() {
        StringBuilder text = new StringBuilder();
        text.append("DIJKSTRA").append('\n');
        text.append("Dataset : ").append(network.getDatasetName()).append('\n');
        text.append("Source  : ").append(network.nameOf(djSource))
                .append(" | Target: ").append(network.nameOf(djTarget)).append('\n');
        text.append("Status  : ").append(djResult.getStatus().label()).append('\n');
        if (djResult.isSuccess()) {
            text.append("Path    : ").append(pathNames(djResult.getPath())).append('\n');
            text.append("Total   : ").append(formatDouble(djResult.getTotalCost())).append('\n');
        } else {
            text.append("Thông báo: ").append(djResult.getMessage());
        }
        return text.toString();
    }

    // =====================================================================
    // Cap nhat hien thi
    // =====================================================================

    private void refreshAll() {
        refreshHeader();
        refreshData();
        refreshResults();
    }

    private void refreshHeader() {
        if (network == null) {
            headerLabel.setText("Chưa nạp dataset");
        } else {
            String name = network.getDatasetName();
            if (name.length() > 22) {
                name = name.substring(0, 20) + "…";
            }
            headerLabel.setText(String.format("%s   ·   V=%d   ·   E=%d   ·   c=0: %d",
                    name, network.vertexCount(), network.arcCount(),
                    network.zeroCapacityCount()));
            headerLabel.revalidate();
        }
    }

    private void refreshData() {
        arcModel.setRowCount(0);
        setAreaText(adjacencyArea, "");
        setAreaText(capacityArea, "");
        setAreaText(weightArea, "");
        if (network == null) {
            datasetSummaryLabel.setText("Chưa nạp dataset");
            datasetVerticesLabel.setText("Hãy nạp file hoặc chọn dataset mẫu để bắt đầu.");
            return;
        }

        datasetSummaryLabel.setText(String.format(
                "%s   ·   file: %s   ·   V=%d   ·   E=%d   ·   zero-capacity=%d   ·   Source=%s   ·   Sink=%s",
                network.getDatasetName(), network.getFileName(),
                network.vertexCount(), network.arcCount(), network.zeroCapacityCount(),
                network.getSourceName(), network.getSinkName()));
        datasetVerticesLabel.setText("Đỉnh (" + network.vertexCount() + "): "
                + String.join(", ", network.getVertices()));

        Graph graph = network.getGraph();
        int index = 1;
        for (Arc arc : graph.getAllArcs()) {
            arcModel.addRow(new Object[]{
                    index++, network.nameOf(arc.getU()), network.nameOf(arc.getV()),
                    arc.getCapacity(), formatDouble(arc.getWeight()),
                    arc.getCapacity() == 0 ? "ZERO-CAP" : ""});
        }

        StringBuilder adjacency = new StringBuilder();
        for (int u = 1; u <= network.vertexCount(); u++) {
            adjacency.append(String.format("%-8s ->", network.nameOf(u)));
            for (Arc arc : graph.getOutgoingArcs(u)) {
                adjacency.append(String.format("  %s(c=%d,w=%s)", network.nameOf(arc.getV()),
                        arc.getCapacity(), formatDouble(arc.getWeight())));
            }
            adjacency.append('\n');
        }
        setAreaText(adjacencyArea, adjacency.toString());
        setAreaText(capacityArea, matrixText(true));
        setAreaText(weightArea, matrixText(false));
    }

    private String matrixText(boolean capacity) {
        Graph graph = network.getGraph();
        StringBuilder text = new StringBuilder();
        text.append(String.format("%-8s", ""));
        for (int v = 1; v <= network.vertexCount(); v++) {
            text.append(String.format("%-8s", network.nameOf(v)));
        }
        text.append('\n');
        for (int u = 1; u <= network.vertexCount(); u++) {
            text.append(String.format("%-8s", network.nameOf(u)));
            for (int v = 1; v <= network.vertexCount(); v++) {
                if (!graph.hasArc(u, v)) {
                    text.append(String.format("%-8s", "-"));
                } else if (capacity) {
                    text.append(String.format("%-8d", graph.getCapacity(u, v)));
                } else {
                    text.append(String.format("%-8s", formatDouble(graph.getWeight(u, v))));
                }
            }
            text.append('\n');
        }
        return text.toString();
    }

    private void refreshResults() {
        refreshFlow();
        refreshResidual();
        refreshDijkstra();
        refreshVerify();
    }

    private void refreshFlow() {
        flowModel.setRowCount(0);
        if (ffResult == null) {
            flowSummaryLabel.setText("Chưa chạy Ford-Fulkerson.");
            return;
        }
        ResidualGraph residual = ffResult.getResidualGraph();
        int index = 1;
        for (Arc arc : network.getGraph().getAllArcs()) {
            int flow = residual.getFlow(arc.getU(), arc.getV());
            int residualCap = residual.getResidualCapacity(arc.getU(), arc.getV());
            String state;
            if (arc.getCapacity() == 0) {
                state = "ZERO-CAP";
            } else if (flow == arc.getCapacity()) {
                state = "SATURATED";
            } else if (flow > 0) {
                state = "IN USE";
            } else {
                state = "EMPTY";
            }
            flowModel.addRow(new Object[]{index++, network.nameOf(arc.getU()),
                    network.nameOf(arc.getV()), arc.getCapacity(), flow, residualCap, state});
        }
        boolean verify = residual.verifyFlow(ffSource, ffSink);
        flowSummaryLabel.setText(String.format(
                "Max Flow = %d | augmenting paths = %d | cung ngược = %s | verifyFlow = %s",
                ffResult.getMaxFlow(), ffResult.getSteps().size(),
                ffResult.usedReverseArc() ? "CÓ" : "KHÔNG", verify ? "PASS" : "FAIL"));
    }

    private void refreshResidual() {
        if (ffResult == null) {
            setAreaText(residualArea, "Chưa chạy Ford-Fulkerson.");
            return;
        }
        ResidualGraph residual = ffResult.getResidualGraph();
        StringBuilder text = new StringBuilder();
        text.append("AUGMENTING PATHS\n");
        for (FordFulkerson.AugmentingPathStep step : ffResult.getSteps()) {
            text.append(String.format("  %2d. %s | bottleneck = %d%s%n",
                    step.getIndex(), pathWithReverse(step), step.getBottleneck(),
                    step.usesReverseArc() ? "  [có cung ngược]" : ""));
        }
        text.append("\nRESIDUAL ARCS (residual capacity > 0)\n");
        text.append(String.format("%-10s %-10s %12s %10s %-10s%n",
                "u", "v", "residual", "flow", "loại"));
        int forward = 0;
        int reverse = 0;
        for (int u = 1; u <= network.vertexCount(); u++) {
            for (int v = 1; v <= network.vertexCount(); v++) {
                int cap = residual.getResidualCapacity(u, v);
                if (cap <= 0) {
                    continue;
                }
                boolean physical = network.getGraph().hasArc(u, v);
                if (physical) {
                    forward++;
                } else {
                    reverse++;
                }
                text.append(String.format("%-10s %-10s %12d %10s %-10s%n",
                        network.nameOf(u), network.nameOf(v), cap,
                        physical ? String.valueOf(residual.getFlow(u, v)) : "-",
                        physical ? "physical" : "reverse"));
            }
        }
        text.append("\nTổng hợp: ").append(forward).append(" residual arc vật lý, ")
                .append(reverse).append(" residual reverse arc.\n");
        text.append("Cung ngược được dùng: ")
                .append(ffResult.usedReverseArc() ? "CÓ" : "KHÔNG");
        setAreaText(residualArea, text.toString());
    }

    private void refreshDijkstra() {
        distModel.setRowCount(0);
        if (djResult == null) {
            dijkstraSummaryLabel.setText("Chưa chạy Dijkstra.");
            setAreaText(dijkstraDetailArea, "");
            return;
        }
        dijkstraSummaryLabel.setText("Status: " + djResult.getStatus().label()
                + (djResult.isSuccess()
                        ? " | Path: " + pathNames(djResult.getPath())
                                + " | Total: " + formatDouble(djResult.getTotalCost())
                        : ""));
        for (int id = 1; id <= network.vertexCount(); id++) {
            double distance = djResult.getDistances() == null
                    ? Double.POSITIVE_INFINITY : djResult.getDistances()[id];
            int pred = djResult.getPredecessors() == null ? -1 : djResult.getPredecessors()[id];
            distModel.addRow(new Object[]{network.nameOf(id),
                    Double.isInfinite(distance) ? "INF" : formatDouble(distance),
                    pred > 0 ? network.nameOf(pred) : "-"});
        }

        StringBuilder text = new StringBuilder();
        if (djResult.isSuccess()) {
            text.append("Chi tiết từng bước:\n");
            List<Integer> path = djResult.getPath();
            for (int i = 0; i < path.size() - 1; i++) {
                int u = path.get(i);
                int v = path.get(i + 1);
                Arc arc = network.getGraph().getArc(u, v);
                text.append(String.format("  %s -> %s : capacity=%d, weight=%s%n",
                        network.nameOf(u), network.nameOf(v),
                        arc != null ? arc.getCapacity() : 0,
                        arc != null ? formatDouble(arc.getWeight()) : "-"));
            }
        } else {
            text.append("Không có path. Chỉ hiển thị Distance/Predecessor của các đỉnh reachable.\n");
            text.append("Thông báo: ").append(djResult.getMessage()).append('\n');
        }
        text.append("\nDijkstra chỉ dùng cung capacity > 0 và weight; không dùng residual/flow.");
        setAreaText(dijkstraDetailArea, text.toString());
    }

    private void refreshVerify() {
        if (ffResult == null && djResult == null) {
            setAreaText(verifyArea, "Chưa có kết quả. Hãy chạy thuật toán ở tab 2.");
            return;
        }
        StringBuilder text = new StringBuilder();
        if (ffResult != null) {
            ResidualGraph residual = ffResult.getResidualGraph();
            text.append("A. FORD-FULKERSON\n");
            text.append("  Source/Sink: ").append(network.nameOf(ffSource))
                    .append(" / ").append(network.nameOf(ffSink)).append('\n');
            text.append("  Max Flow   : ").append(ffResult.getMaxFlow()).append('\n');

            boolean capacityOk = true;
            for (Arc arc : network.getGraph().getAllArcs()) {
                int flow = residual.getFlow(arc.getU(), arc.getV());
                if (flow < 0 || flow > arc.getCapacity()) {
                    capacityOk = false;
                }
            }
            boolean conservationOk = true;
            for (int id = 1; id <= network.vertexCount(); id++) {
                if (id == ffSource || id == ffSink) {
                    continue;
                }
                int inflow = 0;
                int outflow = 0;
                for (int other = 1; other <= network.vertexCount(); other++) {
                    inflow += residual.getFlow(other, id);
                    outflow += residual.getFlow(id, other);
                }
                conservationOk &= inflow == outflow;
            }
            int sourceOut = residual.getSourceOutflow(ffSource);
            int sinkIn = residual.getSinkInflow(ffSink);
            boolean totalsOk = sourceOut == sinkIn;

            text.append("  [1] 0 <= f(u,v) <= c(u,v) : ").append(capacityOk ? "PASS" : "FAIL").append('\n');
            text.append("  [2] Bảo toàn luồng         : ").append(conservationOk ? "PASS" : "FAIL").append('\n');
            text.append("  [3] Source out = Sink in   : ").append(sourceOut).append(" = ")
                    .append(sinkIn).append(" -> ").append(totalsOk ? "PASS" : "FAIL").append('\n');
            text.append("  KẾT LUẬN FLOW: ")
                    .append(capacityOk && conservationOk && totalsOk ? "FLOW VALID" : "FLOW INVALID")
                    .append("\n\n");
        } else {
            text.append("A. FORD-FULKERSON: chưa chạy.\n\n");
        }

        if (djResult != null) {
            text.append("B. DIJKSTRA\n");
            text.append("  Status: ").append(djResult.getStatus().label()).append('\n');
            if (djResult.isSuccess()) {
                text.append("  Path  : ").append(pathNames(djResult.getPath())).append('\n');
                text.append("  Total : ").append(formatDouble(djResult.getTotalCost())).append('\n');
                boolean pathOk = true;
                double sum = 0.0;
                List<Integer> path = djResult.getPath();
                for (int i = 0; i < path.size() - 1; i++) {
                    Arc arc = network.getGraph().getArc(path.get(i), path.get(i + 1));
                    if (arc == null || arc.getCapacity() <= 0) {
                        pathOk = false;
                        break;
                    }
                    sum += arc.getWeight();
                }
                boolean costOk = Math.abs(sum - djResult.getTotalCost()) < 1e-9;
                text.append("  [1] Mọi bước là cung capacity > 0 : ").append(pathOk ? "PASS" : "FAIL").append('\n');
                text.append("  [2] Tổng weight = Total Cost       : ").append(costOk ? "PASS" : "FAIL").append('\n');
                text.append("  KẾT LUẬN PATH: ").append(pathOk && costOk ? "PATH VALID" : "PATH INVALID")
                        .append('\n');
            } else {
                text.append("  Không có path để kiểm chứng (NO PATH/INVALID).\n");
            }
            text.append('\n');
        } else {
            text.append("B. DIJKSTRA: chưa chạy.\n\n");
        }

        text.append("C. PHÂN TÍCH TỔNG HỢP\n");
        text.append("  Maximum Flow tối đa hóa lưu lượng trên residual graph, dùng capacity.\n");
        text.append("  Dijkstra tối thiểu hóa tổng weight trên graph gốc, chỉ dùng cung c>0.\n");
        text.append("  Hai bài toán độc lập: không suy diễn shortest path tạo max flow\n");
        text.append("  và không dùng capacity/flow làm trọng số của Dijkstra.\n");
        setAreaText(verifyArea, text.toString());
    }

    // =====================================================================
    // Kiem thu
    // =====================================================================

    private void runSelectedTest() {
        int index = testCombo.getSelectedIndex();
        if (index < 0) {
            return;
        }
        TestRunner.CaseResult result = TestRunner.run(TestRunner.CATALOG.get(index).code());
        if (result == null) {
            return;
        }
        testResults = List.of(result);
        fillTestTable();
        if (testModel.getRowCount() > 0) {
            testTable.setRowSelectionInterval(0, 0);
            showTestDetail(0);
        }
        statusLabel.setText("Đã chạy " + result.getCode() + ": " + (result.isPass() ? "PASS" : "FAIL"));
    }

    private void runAllTests() {
        testResults = TestRunner.runAll();
        fillTestTable();
        long passed = testResults.stream().filter(TestRunner.CaseResult::isPass).count();
        testSummaryLabel.setText("TOTAL: " + passed + "/" + testResults.size() + " PASS");
        statusLabel.setText("Đã chạy toàn bộ testcase: " + passed + "/" + testResults.size() + " PASS");
        if (!testResults.isEmpty()) {
            testTable.setRowSelectionInterval(0, 0);
            showTestDetail(0);
        }
    }

    private void fillTestTable() {
        testModel.setRowCount(0);
        for (TestRunner.CaseResult result : testResults) {
            testModel.addRow(new Object[]{result.getCode(), result.getTitle(),
                    result.getExpected(), result.getActual(),
                    result.isPass() ? "PASS" : "FAIL"});
        }
        if (testResults.isEmpty()) {
            testSummaryLabel.setText("Chưa chạy testcase.");
            setAreaText(testDetailArea, "");
        } else {
            long passed = testResults.stream().filter(TestRunner.CaseResult::isPass).count();
            testSummaryLabel.setText("TOTAL: " + passed + "/" + testResults.size() + " PASS");
        }
    }

    private void showTestDetail(int row) {
        if (row < 0 || row >= testResults.size()) {
            setAreaText(testDetailArea, "");
            return;
        }
        TestRunner.CaseResult result = testResults.get(row);
        StringBuilder text = new StringBuilder();
        text.append(result.getCode()).append(" - ").append(result.getTitle()).append("\n\n");
        text.append("Expected: ").append(result.getExpected()).append('\n');
        text.append("Actual  : ").append(result.getActual()).append('\n');
        text.append("Result  : ").append(result.isPass() ? "PASS" : "FAIL").append("\n\n");
        text.append("Chi tiết:\n");
        for (String detail : result.getDetails()) {
            text.append("  - ").append(detail).append('\n');
        }
        setAreaText(testDetailArea, text.toString());
    }

    // =====================================================================
    // Tro giup
    // =====================================================================

    private String startHelpText() {
        return """
                HƯỚNG DẪN SỬ DỤNG GUI
                =====================

                MỞ NHANH
                --------
                  run-gui.bat
                  java -cp bin Main --gui

                LUỒNG DEMO GỢI Ý
                ----------------
                  1. Tab "Dữ liệu": chọn dataset mẫu -> Nạp dataset mẫu.
                  2. Tab "Thuật toán": chọn Source/Sink/Target -> Chạy cả hai.
                  3. Tab "Kết quả & kiểm chứng": xem Flow, Residual, Dijkstra, Verify.
                  4. Tab "Kiểm thử": Chạy tất cả testcase -> xem PASS/FAIL.
                  5. Tab "Hướng dẫn": tra cứu định dạng, thuật toán, project.

                5 TAB CHÍNH
                -----------
                  1) DỮ LIỆU
                     - Nạp file .txt hoặc dataset mẫu.
                     - Reload nạp lại dataset hiện tại; Clear xóa dataset và kết quả.
                     - Thông tin dataset: tên file, V, E, zero-capacity, Source, Sink,
                       danh sách đỉnh.
                     - Xem danh sách cung, adjacency list, capacity matrix, weight matrix.
                     - Bảng có tooltip: rê chuột lên ô bị cắt để xem đầy đủ.

                  2) THUẬT TOÁN
                     - Source dùng cho cả hai bài toán.
                     - Sink dùng cho Ford-Fulkerson (Max Flow).
                     - Target dùng cho Dijkstra.
                     - "Chạy Ford-Fulkerson": residual graph mới mỗi lần.
                     - "Chạy Dijkstra": chạy trên graph gốc.
                     - "Chạy cả hai": chạy cả hai và hiển thị kết quả cạnh nhau.
                     - Source = Target: Dijkstra hợp lệ (cost 0), Max Flow bị từ chối.

                  3) KẾT QUẢ & KIỂM CHỨNG
                     - Max Flow / Flow: bảng capacity, flow, residual trên cung gốc.
                     - Residual / Augmenting paths: path, bottleneck, cung ngược [R],
                       danh sách residual arc vật lý/ngược.
                     - Dijkstra: Distance/Predecessor cho mọi đỉnh, path, total cost.
                     - Verify / Phân tích: kiểm tra 0<=f<=c, bảo toàn luồng,
                       Source outflow = Sink inflow, kiểm tra path và phân tích hai bài toán.

                  4) KIỂM THỬ
                     - Chọn 1 trong 9 testcase hoặc chạy tất cả.
                     - Bảng Expected/Actual/Kết quả; chọn dòng để xem chi tiết.
                     - Testcase dùng dataset thật trong thư mục data/.

                  5) HƯỚNG DẪN
                     - Bắt đầu: luồng sử dụng và mapping Console.
                     - Thuật toán: pseudocode, cập nhật residual, độ phức tạp.
                     - Dataset & testcase: định dạng, danh sách dataset, TC01-TC09.
                     - Project: thông tin môn học, chức năng, kiến trúc, AI.

                MAPPING GUI <-> CONSOLE
                -----------------------
                  Tab Dữ liệu              <-> Menu 2 - Quản lý dữ liệu
                  Tab Thuật toán           <-> Menu 3 - Chạy Maximum Flow / Dijkstra
                  Tab Kết quả & kiểm chứng <-> Menu 4 - Kết quả / Kiểm chứng
                  Tab Kiểm thử             <-> Menu 5 - Kiểm thử (mục 1-3)
                  Tab Hướng dẫn            <-> Menu 5 - Hướng dẫn / thông tin project

                MENU
                ----
                  Tệp     : Nạp dataset (Ctrl+O), Thoát.
                  Chạy    : Ford-Fulkerson, Dijkstra, Cả hai.
                  Trợ giúp: Hướng dẫn sử dụng, Thông tin project.

                CHẠY BẰNG DÒNG LỆNH
                -------------------
                  run-gui.bat                   build + mở GUI
                  java -cp bin Main --gui       mở GUI
                  java -cp bin Main             Console
                  java -cp bin Main --selftest  chạy 9 testcase

                YÊU CẦU MÔI TRƯỜNG
                -------------------
                  JDK 17+ (khuyến nghị 21). GUI dùng javax.swing có sẵn trong JDK,
                  không cần thư viện ngoài. Cần môi trường desktop (không headless).
                """;
    }

    private String algorithmHelpText() {
        return """
                THUẬT TOÁN VÀ KIỂM CHỨNG
                ========================

                1. FORD-FULKERSON (BFS / EDMONDS-KARP)
                --------------------------------------
                Bài toán: tìm lượng hàng tối đa từ Source đến Sink.
                Thuộc tính dùng: capacity (số nguyên >= 0).
                Trạng thái làm việc: residual graph mới + flow = 0 cho mỗi lần chạy.

                Ý tưởng:
                  - residual capacity c_f(u,v) = c(u,v) - f(u,v) với cung vật lý.
                  - Cung ngược (v,u) có residual capacity = f(u,v).
                  - Lặp: tìm augmenting path từ Source đến Sink trên residual graph.
                  - Bottleneck = residual capacity nhỏ nhất trên path.
                  - Tăng flow thêm bottleneck dọc path.

                Pseudocode:
                  FORD_FULKERSON(G, s, t):
                      residual <- copy capacity của G
                      flow <- 0
                      while BFS(residual, s, t) tìm được path P:
                          b <- min residual capacity trên P
                          for (u,v) in P:
                              residual[u][v] -= b
                              residual[v][u] += b
                              if (u,v) là cung vật lý:
                                  flow[u][v] += b
                              else:  // cung ngược residual
                                  flow[v][u] -= b
                          maxFlow += b
                      return maxFlow, flow, residual

                Cập nhật cung thuận / cung ngược:
                  - Cung thuận (u,v): residual giảm, flow tăng.
                  - Cung ngược (u,v) (tức (v,u) vật lý): residual giảm,
                    flow trên (v,u) giảm để "trả lại" flow đã đẩy trước đó.
                  - Graph gốc không bao giờ bị sửa.

                Độ phức tạp:
                  - Ford-Fulkerson cơ bản: O(E * f) với f là max flow (capacity nguyên).
                  - BFS (Edmonds-Karp): O(V * E^2), độc lập giá trị capacity.
                  - Bộ nhớ: O(V^2 + E) với ma trận residual và danh sách cung.

                Kết thúc và kiểm chứng:
                  - Dừng khi BFS không còn đường từ Source đến Sink.
                  - Kiểm tra 0 <= f(u,v) <= c(u,v).
                  - Kiểm tra bảo toàn luồng tại mọi đỉnh trung gian.
                  - Kiểm tra tổng flow ra Source = tổng flow vào Sink.
                  - Nếu Source không tới được Sink: Max Flow = 0, đây là kết quả hợp lệ.

                2. DIJKSTRA
                -----------
                Bài toán: tìm tuyến có tổng weight nhỏ nhất từ Source đến Target.
                Thuộc tính dùng: weight (số thực >= 0).
                Điều kiện: chỉ dùng cung capacity > 0; không dùng residual/flow.

                Ý tưởng:
                  - dist[Source] = 0, các đỉnh khác = vô cực.
                  - Priority Queue chọn đỉnh có dist nhỏ nhất chưa chốt.
                  - Relax: nếu dist[u] + w(u,v) < dist[v] thì cập nhật dist[v],
                    predecessor[v] = u.
                  - Tính dist cho tất cả đỉnh reachable; Target chỉ dùng truy vết.

                Pseudocode:
                  DIJKSTRA(G, s):
                      for v in V: dist[v] <- INF; pred[v] <- -1
                      dist[s] <- 0
                      PQ <- {(0, s)}
                      while PQ không rỗng:
                          u <- đỉnh có dist nhỏ nhất
                          if u đã chốt: continue
                          chốt u
                          for (u,v) in G.adj[u]:
                              if capacity(u,v) <= 0: continue
                              if dist[u] + w(u,v) < dist[v]:
                                  dist[v] <- dist[u] + w(u,v)
                                  pred[v] <- u
                                  PQ.push((dist[v], v))
                      return dist, pred

                Truy vết:
                  - Nếu dist[Target] = INF: trả về NO PATH, không tạo path giả.
                  - Ngược từ Target theo pred đến Source, đảo ngược để được path.
                  - Source = Target: cost = 0, path chỉ gồm Source.
                  - Nếu phát hiện weight âm: INVALID FOR DIJKSTRA.

                Độ phức tạp:
                  - O((V + E) * log V) với priority queue.
                  - Bộ nhớ O(V + E).

                3. PHÂN BIỆT HAI BÀI TOÁN
                -------------------------
                  Tiêu chí        Ford-Fulkerson              Dijkstra
                  Mục tiêu        Max flow                    Min tổng weight
                  Thuộc tính      capacity                    weight
                  Graph dùng      residual graph mới           graph gốc
                  Kết quả         max flow + flow từng cung    dist/pred/path/cost
                  Câu hỏi         Năng lực vận chuyển tối đa   Tuyến ngắn/chi phí thấp
                  Không được suy diễn: shortest path không tạo ra max flow và ngược lại.
                  Không dùng capacity/flow làm trọng số Dijkstra.

                4. TRẠNG THÁI VÀ LỖI
                --------------------
                  SUCCESS                 Dijkstra tìm được đường.
                  NO PATH                 Target không reachable qua cung capacity > 0.
                  MAX FLOW REACHED        Không còn augmenting path.
                  MAX FLOW = 0            Source không tới Sink (kết quả hợp lệ).
                  INVALID FOR DIJKSTRA    Weight âm/không hữu hạn.
                  INVALID ENDPOINTS       Source = Sink cho Max Flow.
                  INVALID CAPACITY        capacity âm hoặc không nguyên.
                  INVALID INPUT           File/menu/tên đỉnh không hợp lệ.
                  NOT LOADED              Chưa nạp dataset.

                5. KIỂM CHỨNG TRÊN GUI
                ----------------------
                  Tab "Kết quả & kiểm chứng" -> "Verify / Phân tích" hiển thị:
                    - [1] 0 <= f(u,v) <= c(u,v)
                    - [2] Bảo toàn luồng tại đỉnh trung gian
                    - [3] Source outflow = Sink inflow
                    - [1] Mọi bước path là cung capacity > 0
                    - [2] Tổng weight trên path = Total Cost
                """;
    }

    private String datasetHelpText() {
        return """
                ĐỊNH DẠNG DATASET VÀ TESTCASE
                =============================

                ĐỊNH DẠNG FILE
                --------------
                  Mỗi dòng: u v capacity weight
                    u, v      : tên đỉnh, không chứa khoảng trắng (WH, A, ST, ...)
                    capacity  : số nguyên >= 0
                    weight    : số thực hữu hạn >= 0
                  - Dòng bắt đầu bằng # là chú thích.
                  - Dòng trống bị bỏ qua.
                  - File dùng UTF-8.

                VÍ DỤ
                ------
                  # Dataset mẫu
                  WH A 10 3.0
                  WH B 15 5.0
                  A ST 8 2.5
                  B ST 10 3.0

                QUY ƯỚC VALIDATION
                ------------------
                  - Graph có hướng, đơn cung.
                  - Không self-loop (u,u).
                  - Không cung trùng (u,v).
                  - Không dùng đồng thời (u,v) và (v,u) trong cùng dataset.
                  - capacity phải là số nguyên >= 0.
                  - weight phải là số thực hữu hạn >= 0.
                  - capacity = 0 là tuyến không khả dụng:
                      + Không truyền flow.
                      + Dijkstra bỏ qua.
                  - Source mặc định WH, Sink mặc định ST.
                  - File sai bất kỳ quy ước nào sẽ bị từ chối toàn bộ;
                    dataset hiện tại được giữ nguyên.

                DATASET HỢP LỆ
                --------------
                  MAIN    main_dataset.txt
                          V=20, E=34. Max flow WH->ST = 35.
                          Dijkstra WH->ST = 20.5 (WH-B-E-H-K-M-P-ST).
                  DEMO    demo_dataset.txt
                          V=10, E=13. Max flow = 27.
                          Dijkstra WH->ST = 7.5 (WH-A-C-ST).
                  REV     residual_reverse_update_dataset.txt
                          V=6, E=7. Max flow = 8.
                          Bắt buộc dùng cung ngược: WH-C-B-A[R]-D-ST.
                          Dijkstra WH->ST = 5.0.
                  FLOW0   source_sink_disconnected_dataset.txt
                          V=6, E=4. Max flow = 0, Dijkstra NO PATH.
                  ZERO    zero_capacity_arc_dataset.txt
                          V=5, E=5. Có cung capacity = 0.
                          Max flow = 10; flow WH->B = 0; Dijkstra = 9.0.
                  NOPATH  dijkstra_no_path_dataset.txt
                          V=4, E=3. Dijkstra NO PATH; max flow = 0.

                DATASET KHÔNG HỢP LỆ (dùng cho TC07)
                ------------------------------------
                  invalid_capacity_dataset.txt   capacity âm
                  invalid_weight_dataset.txt     weight âm
                  invalid_duplicate_dataset.txt  cung trùng
                  invalid_reverse_dataset.txt    tồn tại (u,v) và (v,u)
                  invalid_selfloop_dataset.txt   self-loop
                  invalid_format_dataset.txt     sai số cột

                9 TESTCASE BẮT BUỘC
                -------------------
                  TC01  Ford-Fulkerson trên MAIN
                        Expected: max flow = 35, verify flow PASS.
                  TC02  Residual reverse update trên REV
                        Expected: max flow = 8, có dùng cung ngược [R].
                  TC03  Source-Sink không reachable trên FLOW0
                        Expected: max flow = 0, Dijkstra NO PATH.
                  TC04  Cung capacity = 0 trên ZERO
                        Expected: flow WH->B = 0, max flow = 10, Dijkstra = 9.0.
                  TC05  Dijkstra bình thường trên DEMO
                        Expected: WH-A-C-ST, cost = 7.5.
                  TC06  Dijkstra NO PATH trên NOPATH
                        Expected: NO PATH, không có path giả.
                  TC07  Validation dataset không hợp lệ
                        Expected: 6/6 file bị từ chối đúng loại lỗi.
                  TC08  Source = Sink
                        Expected: Max Flow bị từ chối;
                        Dijkstra cost = 0, path chỉ gồm Source.
                  TC09  Dijkstra MAIN và chiều ngược
                        Expected: WH->ST cost 20.5; A->WH NO PATH.

                CÁCH CHẠY
                ----------
                  - Tab "Kiểm thử": chọn testcase -> "Chạy test đã chọn".
                  - Hoặc "Chạy tất cả testcase" -> xem bảng PASS/FAIL.
                  - Hoặc dòng lệnh: java -cp bin Main --selftest
                  - Chọn một dòng trong bảng để xem Expected/Actual/chi tiết.
                """;
    }

    private String projectHelpText() {
        return """
                THÔNG TIN PROJECT
                =================

                TÊN PROJECT
                -----------
                  PROJECT 04 - GOODS DISTRIBUTION NETWORK
                  Phân tích năng lực vận chuyển và tuyến giao hàng.

                MÔN HỌC
                -------
                  Lý thuyết đồ thị - CSC106400
                  Hình thức: Project cuối kỳ, thực hiện 4 tuần.

                MỤC TIÊU
                --------
                  - Mô hình hóa mạng vận chuyển bằng graph có hướng.
                  - Mỗi cung có capacity (số nguyên >= 0) và weight (số thực >= 0).
                  - Tự cài đặt Ford-Fulkerson để tính max flow.
                  - Tự cài đặt Dijkstra để tìm tuyến ngắn nhất trên graph gốc.
                  - Hiển thị augmenting path, bottleneck, residual, flow.
                  - Hiển thị Distance/Predecessor, path, total cost.
                  - Thiết kế testcase cho residual reverse, max flow = 0,
                    capacity = 0, NO PATH, dữ liệu không hợp lệ.
                  - Xây dựng Console/GUI rõ ràng, chạy hoàn chỉnh, demo trực tiếp.

                CHỨC NĂNG BẮT BUỘC
                -------------------
                  F01  Quản lý graph gốc: nạp/đổi dataset, xem đỉnh/cung,
                       capacity/weight, adjacency/matrix.
                  F02  Chạy Maximum Flow: Ford-Fulkerson trên residual graph mới.
                  F03  Chạy Dijkstra: graph gốc, cung capacity > 0, weight >= 0.
                  F04  Kết quả & kiểm chứng: flow/residual, dist/pred, path/cost,
                       verify và phân tích hai bài toán.
                  F05  Kiểm thử & hướng dẫn: 9 testcase, Expected/Actual/Pass,
                       định dạng input, thông tin project, điều hướng/reset.

                KIẾN TRÚC
                ---------
                  Main
                   ├─ ui.UI           Console 5 màn hình
                   ├─ ui.SwingUI      GUI javax.swing 5 tab
                   ├─ ui.Theme        Theme màu/font/component
                   ├─ utils.Utils     Controller Console, hiển thị, verify
                   ├─ io.InputOuputManager  Parser + validation + catalog
                   ├─ models.Arc      Cung (u,v,capacity,weight)
                   ├─ models.Graph    Graph gốc
                   ├─ models.ResidualGraph  residual + flow
                   ├─ models.Network  Dataset + map tên đỉnh
                   ├─ algorithm.FordFulkerson
                   ├─ algorithm.Dijkstra
                   └─ testing.TestRunner  9 testcase

                DATASET
                -------
                  6 bộ hợp lệ: MAIN, DEMO, REV, FLOW0, ZERO, NOPATH.
                  6 bộ không hợp lệ cho validation (TC07).

                KẾT QUẢ NỔI BẬT
                ---------------
                  - MAIN: max flow 35, Dijkstra 20.5.
                  - DEMO: max flow 27, Dijkstra 7.5.
                  - REV: max flow 8, có residual reverse arc.
                  - FLOW0/NOPATH: xử lý đúng max flow 0 và NO PATH.
                  - ZERO: cung capacity 0 không truyền flow, Dijkstra bỏ qua.
                  - Self-test: 9/9 PASS.

                THÀNH VIÊN
                ----------
                  [Cập nhật họ tên - MSSV - phần việc - % đóng góp]
                  [Cập nhật họ tên - MSSV - phần việc - % đóng góp]

                SỬ DỤNG AI
                ----------
                  Có sử dụng AI có kiểm soát. Cần ghi rõ AI hỗ trợ gì,
                  sinh viên tự làm gì, đã kiểm chứng/chỉnh sửa ra sao.
                  Xem file AI_Prompt_Log kèm báo cáo.

                GHI CHÚ
                -------
                  - Graph gốc chỉ lưu capacity/weight, không chứa flow.
                  - ResidualGraph được tạo mới mỗi lần chạy Ford-Fulkerson.
                  - Dijkstra không dùng residual/flow làm trọng số.
                  - Không dùng thư viện ngoài để thay thuật toán bắt buộc.
                  - Chạy self-test để có minh chứng 9/9 PASS.
                """;
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(frame,
                "PROJECT 04 - GOODS DISTRIBUTION NETWORK\n\n"
                        + "Môn học: Lý thuyết đồ thị (CSC106400)\n"
                        + "Thuật toán: Ford-Fulkerson (BFS/Edmonds-Karp) + Dijkstra\n"
                        + "Giao diện: Java Swing (javax.swing)\n"
                        + "Dataset: 6 bộ hợp lệ + 6 bộ không hợp lệ cho kiểm thử\n"
                        + "Testcase: " + TestRunner.CATALOG.size() + " (TC01 - TC09)\n\n"
                        + "Chạy Console: java -cp bin Main\n"
                        + "Chạy GUI    : java -cp bin Main --gui\n"
                        + "Self-test   : java -cp bin Main --selftest",
                "Thông tin project", JOptionPane.INFORMATION_MESSAGE);
    }

    // =====================================================================
    // Tien ich
    // =====================================================================

    private static void setAreaText(JTextArea area, String text) {
        area.setText(text);
        area.setCaretPosition(0);
    }

    private static String selected(JComboBox<String> combo) {
        Object value = combo.getSelectedItem();
        return value == null ? null : value.toString();
    }

    private static String formatDouble(double value) {
        if (Double.isInfinite(value)) {
            return "INF";
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private String pathNames(List<Integer> path) {
        List<String> names = new ArrayList<>();
        for (int id : path) {
            names.add(network.nameOf(id));
        }
        return String.join(" -> ", names);
    }

    private String pathWithReverse(FordFulkerson.AugmentingPathStep step) {
        List<Integer> path = step.getPath();
        StringBuilder text = new StringBuilder(network.nameOf(path.get(0)));
        for (int i = 0; i < path.size() - 1; i++) {
            text.append(" -> ").append(network.nameOf(path.get(i + 1)));
            if (step.getReverseFlags().get(i)) {
                text.append("[R]");
            }
        }
        return text.toString();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showErrors(String title, List<String> errors) {
        JTextArea area = new JTextArea(String.join("\n", errors));
        Theme.styleTextArea(area);
        area.setFont(Theme.mono(12));
        area.setRows(Math.min(12, Math.max(4, errors.size())));
        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(620, 220));
        JOptionPane.showMessageDialog(frame, pane, title, JOptionPane.ERROR_MESSAGE);
    }
}
