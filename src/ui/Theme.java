package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Bo mau, font va component tuy bien de giao dien Swing co cam giac hien dai:
 * nen sang, card trang bo goc, header gradient, accent indigo, bang gon va
 * trang thai co mau.
 */
public final class Theme {

    public static final Color BG = new Color(0xF4F6FB);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(0xF8FAFC);
    public static final Color BORDER = new Color(0xE2E8F0);
    public static final Color BORDER_STRONG = new Color(0xCBD5E1);
    public static final Color TEXT = new Color(0x0F172A);
    public static final Color MUTED = new Color(0x64748B);
    public static final Color PRIMARY = new Color(0x2563EB);
    public static final Color PRIMARY_DARK = new Color(0x1D4ED8);
    public static final Color PRIMARY_SOFT = new Color(0xEFF6FF);
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color WARNING = new Color(0xD97706);
    public static final Color HEADER_START = new Color(0x0F172A);
    public static final Color HEADER_END = new Color(0x312E81);
    public static final Color SELECTION = new Color(0xDBEAFE);

    private static final String UI_FONT = pickFont("Segoe UI", "Inter", "SF Pro Text", "SansSerif");
    private static final String UI_MONO = pickFont("Cascadia Mono", "Consolas", "JetBrains Mono", "Monospaced");

    private Theme() {
    }

    private static String pickFont(String... candidates) {
        java.util.Set<String> available = java.util.Set.of(
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames());
        for (String candidate : candidates) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return "SansSerif";
    }

    public static Font font(int size) {
        return new Font(UI_FONT, Font.PLAIN, size);
    }

    public static Font font(int size, int style) {
        return new Font(UI_FONT, style, size);
    }

    public static Font mono(int size) {
        return new Font(UI_MONO, Font.PLAIN, size);
    }

    /** Ap dung palette/font vao UIManager truoc khi tao component. */
    public static void install() {
        UIManager.put("control", new ColorUIResource(BG));
        UIManager.put("Panel.background", new ColorUIResource(BG));
        UIManager.put("Label.foreground", new ColorUIResource(TEXT));
        UIManager.put("Label.font", font(13));
        UIManager.put("Button.font", font(13, Font.BOLD));
        UIManager.put("ComboBox.font", font(13));
        UIManager.put("Table.font", font(13));
        UIManager.put("TextArea.font", mono(13));
        UIManager.put("OptionPane.messageFont", font(13));
        UIManager.put("OptionPane.buttonFont", font(13, Font.BOLD));
        UIManager.put("Menu.font", font(13));
        UIManager.put("MenuItem.font", font(13));
        UIManager.put("TabbedPane.font", font(13, Font.BOLD));
        UIManager.put("ToolTip.background", new ColorUIResource(TEXT));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("ToolTip.font", font(12));
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("nimbusBase", new ColorUIResource(PRIMARY));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(BORDER_STRONG));
        UIManager.put("nimbusLightBackground", new ColorUIResource(SURFACE));
        UIManager.put("nimbusSelectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("nimbusFocus", new ColorUIResource(PRIMARY));
    }

    // =====================================================================
    // Buttons
    // =====================================================================

    public enum ButtonKind {
        PRIMARY,
        SECONDARY,
        DANGER,
        GHOST
    }

    public static JButton primaryButton(String text) {
        return new FlatButton(text, ButtonKind.PRIMARY);
    }

    public static JButton secondaryButton(String text) {
        return new FlatButton(text, ButtonKind.SECONDARY);
    }

    public static JButton dangerButton(String text) {
        return new FlatButton(text, ButtonKind.DANGER);
    }

    public static JButton ghostButton(String text) {
        return new FlatButton(text, ButtonKind.GHOST);
    }

    private static final class FlatButton extends JButton {

        private static final long serialVersionUID = 1L;

        private final ButtonKind kind;
        private final Color fill;
        private final Color hover;
        private final Color border;
        private final Color textColor;

        FlatButton(String text, ButtonKind kind) {
            super(text);
            this.kind = kind;
            switch (kind) {
                case PRIMARY -> {
                    fill = PRIMARY;
                    hover = PRIMARY_DARK;
                    border = PRIMARY;
                    textColor = Color.WHITE;
                }
                case DANGER -> {
                    fill = SURFACE;
                    hover = new Color(0xFEF2F2);
                    border = new Color(0xFECACA);
                    textColor = DANGER;
                }
                case GHOST -> {
                    fill = new Color(0, 0, 0, 0);
                    hover = new Color(0xF1F5F9);
                    border = new Color(0, 0, 0, 0);
                    textColor = MUTED;
                }
                default -> {
                    fill = SURFACE;
                    hover = PRIMARY_SOFT;
                    border = BORDER;
                    textColor = TEXT;
                }
            }
            setForeground(textColor);
            setFont(font(13, Font.BOLD));
            setBorder(new EmptyBorder(9, 16, 9, 16));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width, 36));
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setCursor(enabled
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color background = fill;
            if (!isEnabled()) {
                background = new Color(0xE2E8F0);
            } else if (getModel().isPressed()) {
                background = kind == ButtonKind.PRIMARY ? PRIMARY_DARK : hover.darker();
            } else if (getModel().isRollover()) {
                background = hover;
            }

            if (background.getAlpha() > 0) {
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            if (border.getAlpha() > 0) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            if (hasFocus()) {
                g2.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 90));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    // Containers
    // =====================================================================

    /** Panel trang bo goc, vien nhe, co bong do. */
    public static final class Card extends javax.swing.JPanel {

        private static final long serialVersionUID = 1L;
        private static final int ARC = 16;

        public Card() {
            this(new java.awt.BorderLayout());
        }

        public Card(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(16, 16, 16, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(15, 23, 42, 14));
            g2.fillRoundRect(2, 3, w - 4, h - 5, ARC, ARC);

            g2.setColor(SURFACE);
            g2.fillRoundRect(1, 1, w - 3, h - 4, ARC, ARC);
            g2.setColor(BORDER);
            g2.drawRoundRect(1, 1, w - 3, h - 4, ARC, ARC);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Header gradient toi, co them hai vong tron mo de tao chieu sau. */
    public static final class GradientHeader extends javax.swing.JPanel {

        private static final long serialVersionUID = 1L;

        public GradientHeader() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, HEADER_START, w, h, HEADER_END));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillOval(w - 260, -120, 360, 360);
            g2.setColor(new Color(255, 255, 255, 9));
            g2.fillOval(w - 90, -40, 200, 200);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Chip trang thai tren header toi. */
    public static final class ChipLabel extends JLabel {

        private static final long serialVersionUID = 1L;

        public ChipLabel(String text) {
            super(text);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(font(12, Font.BOLD));
            setBorder(new EmptyBorder(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 38));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.setColor(new Color(255, 255, 255, 70));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(12, Font.BOLD));
        label.setForeground(MUTED);
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(12, Font.BOLD));
        label.setForeground(MUTED);
        label.setBorder(new EmptyBorder(0, 2, 0, 0));
        return label;
    }

    // =====================================================================
    // Tabs
    // =====================================================================

    public static void styleTabs(JTabbedPane tabs) {
        tabs.setUI(new ModernTabbedPaneUI());
        tabs.setFont(font(13, Font.BOLD));
        tabs.setBackground(BG);
        tabs.setForeground(MUTED);
        tabs.setOpaque(false);
        tabs.setBorder(new EmptyBorder(4, 0, 0, 0));

        Runnable updateColors = () -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                tabs.setForegroundAt(i, i == tabs.getSelectedIndex() ? PRIMARY : MUTED);
            }
            tabs.repaint();
        };
        tabs.addChangeListener(e -> updateColors.run());
        updateColors.run();
    }

    private static final class ModernTabbedPaneUI extends BasicTabbedPaneUI {

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(6, 6, 0, 6);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            tabInsets = new Insets(10, 18, 10, 18);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return Math.max(42, super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 8);
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 8;
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            if (!isSelected) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(PRIMARY_SOFT);
            g2.fillRoundRect(x + 4, y + 5, w - 8, h - 8, 12, 12);
            g2.setColor(PRIMARY);
            g2.fillRoundRect(x + 16, y + h - 4, w - 32, 3, 3, 3);
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            // phang: khong ve vien tab
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement,
                Rectangle[] rects, int tabIndex, Rectangle iconRect,
                Rectangle textRect, boolean isSelected) {
            // khong ve focus dotted
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // khong ve vien content
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font,
                FontMetrics metrics, int tabIndex, String title,
                Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(isSelected ? PRIMARY : MUTED);
            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g2.dispose();
        }
    }

    // =====================================================================
    // Tables
    // =====================================================================

    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(font(13));
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(0xEEF2F7));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setDefaultRenderer(Object.class, new BodyRenderer());
        installCellTooltips(table);
    }

    /** Cho phep xem day du noi dung o bi cat bang tooltip. */
    private static void installCellTooltips(JTable table) {
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                int column = table.columnAtPoint(event.getPoint());
                if (row < 0 || column < 0) {
                    table.setToolTipText(null);
                    return;
                }
                Object value = table.getValueAt(row, column);
                table.setToolTipText(value == null || value.toString().isBlank()
                        ? null : value.toString());
            }
        });
    }

    public static void styleStatusColumn(JTable table, int columnIndex) {
        if (columnIndex < 0 || columnIndex >= table.getColumnModel().getColumnCount()) {
            return;
        }
        TableCellRenderer renderer = new StatusRenderer();
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        HeaderRenderer() {
            setOpaque(true);
            setBackground(SURFACE_ALT);
            setForeground(MUTED);
            setFont(font(11, Font.BOLD));
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    new EmptyBorder(0, 12, 0, 12)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(SURFACE_ALT);
            setForeground(MUTED);
            setFont(font(11, Font.BOLD));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    new EmptyBorder(0, 12, 0, 12)));
            return this;
        }
    }

    private static class BodyRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? SURFACE : SURFACE_ALT);
            }
            setForeground(TEXT);
            setFont(font(13));
            setBorder(new EmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(value instanceof Number ? RIGHT : LEFT);
            return this;
        }
    }

    private static final class StatusRenderer extends BodyRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = value == null ? "" : value.toString();
            Color color = switch (text) {
                case "PASS" -> SUCCESS;
                case "FAIL" -> DANGER;
                case "SATURATED" -> WARNING;
                case "IN USE" -> PRIMARY;
                case "ZERO-CAP" -> DANGER;
                case "EMPTY" -> MUTED;
                default -> TEXT;
            };
            setForeground(color);
            setFont(font(12, Font.BOLD));
            setHorizontalAlignment(LEFT);
            return this;
        }
    }

    // =====================================================================
    // Inputs / text
    // =====================================================================

    public static void styleTextArea(JTextArea area) {
        area.setEditable(false);
        area.setFont(mono(13));
        area.setForeground(TEXT);
        area.setBackground(SURFACE);
        area.setCaretColor(TEXT);
        area.setMargin(new Insets(14, 16, 14, 16));
        area.setLineWrap(false);
        area.setBorder(null);
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane pane = new JScrollPane(component);
        pane.setBorder(BorderFactory.createLineBorder(BORDER));
        pane.getViewport().setBackground(SURFACE);
        pane.setBackground(SURFACE);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        return pane;
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(font(13));
        combo.setBackground(SURFACE);
        combo.setForeground(TEXT);
        combo.setPreferredSize(new Dimension(170, 36));
        combo.setMinimumSize(new Dimension(130, 36));
    }

    public static void styleMenuBar(javax.swing.JMenuBar bar) {
        bar.setBackground(SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        bar.setFont(font(13));
    }
}
