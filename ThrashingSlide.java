import javax.swing.*;
import java.awt.*;

public class ThrashingSlide extends GradientSlide {
    public ThrashingSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Thrashing – Page fault хэт их үед"), BorderLayout.NORTH);

        JTextArea text = new JTextArea(
                "Програмуудын working set RAM-д багтахгүй үед page fault маш их гарч, OS " +
                        "RAM ↔ Disk хооронд pages-ийг байнга зөөхийг thrashing гэнэ.\n\n" +
                        "• CPU utilization унаж, disk I/O л дүүрэн ажиллана.\n" +
                        "• Шийдэл: process-д оногдох frame-ийн тоог нэмэх, " +
                        "идэвхгүй процессыг түр зогсоох, ажиллаж буй програмын тоог цөөрүүлэх.");
        text.setOpaque(false);
        text.setEditable(false);
        text.setForeground(new Color(215, 225, 255));
        text.setFont(new Font("SansSerif", Font.PLAIN, 15));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // simple timeline chart: faults/time
                int ox = 60;
                int oy = h - 60;
                int maxX = w - 40;
                int maxY = 40;

                g2.setColor(new Color(210, 220, 255));
                g2.drawLine(ox, oy, maxX, oy);
                g2.drawLine(ox, oy, ox, maxY);

                int[] faults = { 2, 3, 4, 6, 10, 12, 13, 13 };
                int n = faults.length;
                int step = (maxX - ox - 20) / (n - 1);

                g2.setColor(new Color(255, 140, 160));
                for (int i = 0; i < n - 1; i++) {
                    int x1 = ox + i * step;
                    int y1 = oy - faults[i] * 3;
                    int x2 = ox + (i + 1) * step;
                    int y2 = oy - faults[i + 1] * 3;
                    g2.drawLine(x1, y1, x2, y2);
                }
                g2.drawString("Page faults / time", ox, maxY - 10);
                g2.drawString("Thrashing хэсэг", ox + step * 3, oy - 10);
            }
        };
        center.setOpaque(false);

        add(center, BorderLayout.CENTER);
        add(text, BorderLayout.SOUTH);
    }
}
