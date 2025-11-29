import javax.swing.*;
import java.awt.*;

public class FitSmallTableSlide extends GradientSlide {
    public FitSmallTableSlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("Жишээ хүснэгт – First / Best / Worst Fit"), BorderLayout.NORTH);

        String[] columns = { "Хуваалт ID", "Хэмжээ (KB)", "FirstFit", "BestFit", "WorstFit" };
        Object[][] data = {
                { "P1", "100", "Block #1", "Block #1", "Block #3" },
                { "P2", "230", "Block #2", "Block #3", "Block #3" },
                { "P3", "60", "Block #2", "Block #2", "Block #1" },
                { "P4", "350", "Block #3", "Block #3", "Block #3" },
        };
        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setForeground(new Color(20, 25, 60));
        table.getTableHeader().setBackground(new Color(210, 220, 255));
        table.setForeground(new Color(15, 20, 40));
        table.setGridColor(new Color(180, 190, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(230, 235, 255));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 240), 2));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(40, 80, 60, 80));
        center.add(scrollPane, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }
}
