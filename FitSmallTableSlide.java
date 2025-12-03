import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class FitSmallTableSlide extends GradientSlide {

    public FitSmallTableSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Жишээ хүснэгт – First / Best / Worst Fit"), BorderLayout.NORTH);

        // ---- Таблицын өгөгдөл ----
        String[] columns = {
                "Процесс",
                "Хүссэн хэмжээ (KB)",
                "First Fit – сонгосон блок",
                "Best Fit – сонгосон блок",
                "Worst Fit – сонгосон блок"
        };

        Object[][] data = {
                { "P1", "100", "Block #1", "Block #1", "Block #3" },
                { "P2", "230", "Block #2", "Block #3", "Block #3" },
                { "P3", "60",  "Block #2", "Block #2", "Block #1" },
                { "P4", "350", "Block #3", "Block #3", "Block #3" },
        };

        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(new Color(15, 23, 42));
        table.setBackground(new Color(241, 245, 255));
        table.setGridColor(new Color(209, 213, 219));

        // ---- Header style ----
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setForeground(new Color(248, 250, 252));
        header.setBackground(new Color(129, 140, 248));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        // ---- Cell alignment (center all) ----
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // ---- ScrollPane ----
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(230, 235, 255));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 240), 2));

        // ---- Дээр нь тайлбар text ----
        JLabel desc = new JLabel(
                "<html>" +
                "<div style='text-align:left;'>" +
                "Төсөөлье: санах ойд гурван сул блок байгаа гэж үзье " +
                "(Block #1 = 120KB, Block #2 = 260KB, Block #3 = 400KB).<br>" +
                "Доорх хүснэгтэд P1–P4 процесс бүрийг First / Best / Worst Fit алгоритмаар " +
                "аль блок дээр байршуулсныг харьцуулан үзүүлсэн." +
                "</div></html>"
        );
        desc.setForeground(new Color(226, 232, 240));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 80, 60, 80));
        center.add(desc, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }
}
