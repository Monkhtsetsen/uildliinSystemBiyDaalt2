import javax.swing.*;
import java.awt.*;

public class VirtualMemorySlide extends GradientSlide {
    public VirtualMemorySlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Виртуал санах ой"), BorderLayout.NORTH);

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                int ramX = (int) (w * 0.2);
                int diskX = (int) (w * 0.65);
                int y = h / 2 - 100;
                int boxW = 150;
                int boxH = 200;

                // RAM frames
                g2.setColor(new Color(120, 170, 255, 210));
                g2.fillRoundRect(ramX, y, boxW, boxH, 16, 16);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(ramX, y, boxW, boxH, 16, 16);
                g2.drawString("RAM", ramX + 55, y - 10);
                for (int i = 0; i < 6; i++) {
                    g2.drawRect(ramX + 20, y + 15 + i * 30, boxW - 40, 24);
                }

                // Disk pages
                g2.setColor(new Color(200, 160, 250, 210));
                g2.fillRoundRect(diskX, y, boxW, boxH, 16, 16);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(diskX, y, boxW, boxH, 16, 16);
                g2.drawString("Disk дээрх pages", diskX + 15, y - 10);
                for (int i = 0; i < 10; i++) {
                    g2.drawRect(diskX + 15, y + 10 + i * 18, boxW - 30, 14);
                }

                // Arrows (swap)
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(240, 240, 255));
                int midY = y + boxH / 2;
                g2.drawLine(ramX + boxW, midY - 25, diskX, midY - 25);
                g2.drawLine(diskX, midY + 25, ramX + boxW, midY + 25);
                g2.drawString("Page in", (ramX + diskX + boxW) / 2 - 20, midY - 30);
                g2.drawString("Page out", (ramX + diskX + boxW) / 2 - 20, midY + 40);
            }
        };
        center.setOpaque(false);

        JTextArea desc = new JTextArea(
                "• Програмын логик хаяглалтыг бодитоос илүү том “виртуал” зайд ажиллуулна.\n" +
                        "• OS нь шаардлагатай pages-ийг RAM руу татаж (page in), ашиглаагүйг нь буцааж бичнэ (page out).\n"
                        +
                        "• Page replacement алгоритмууд – FIFO, LRU, Optimal гэх мэт – аль хуудсыг гаргахыг шийднэ.");
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setForeground(new Color(215, 225, 255));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));

        add(center, BorderLayout.CENTER);
        add(desc, BorderLayout.SOUTH);
    }
}
