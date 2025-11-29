import javax.swing.*;
import java.awt.*;

public class LruConceptSlide extends GradientSlide {
    public LruConceptSlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("LRU алгоритмын санаа"), BorderLayout.NORTH);

        JTextArea text = new JTextArea(
                "LRU (Least Recently Used) алгоритм нь хамгийн сүүлд ашиглагдаагүй хуудсыг солих зарчимтай.\n\n" +
                        "• Page бүр дээр \"сүүлд ашиглагдсан цаг\" гэсэн metadata байлаа гэж төсөөлье.\n" +
                        "• Page fault гарах бүрт – RAM доторх pages-ийн timestamp-г харж хамгийн хуучныг нь гаргана.\n"
                        +
                        "• Реал OS-д hardware counter эсвэл reference bit ашиглаж ойролцоо LRU-г хэрэгжүүлдэг.");
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
                int boxW = 80;
                int boxH = 50;
                int startX = w / 2 - 2 * boxW;
                int y = h / 2 - boxH / 2;

                int[] pages = { 2, 5, 1, 7 };
                int[] ages = { 12, 5, 2, 1 }; // smaller → recently used

                for (int i = 0; i < pages.length; i++) {
                    int x = startX + i * (boxW + 20);
                    g2.setColor(new Color(120, 150, 250, 220));
                    g2.fillRoundRect(x, y, boxW, boxH, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(x, y, boxW, boxH, 12, 12);
                    g2.drawString("P" + pages[i], x + 30, y + 20);
                    g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
                    g2.drawString("age=" + ages[i], x + 20, y + 38);
                }

                // Arrow to victim (largest age)
                int victimIndex = 0;
                for (int i = 1; i < ages.length; i++) {
                    if (ages[i] > ages[victimIndex])
                        victimIndex = i;
                }
                int vx = startX + victimIndex * (boxW + 20) + boxW / 2;
                g2.setColor(new Color(255, 120, 140));
                g2.drawLine(vx, y + boxH + 10, vx, y + boxH + 40);
                g2.drawString("LRU victim", vx - 30, y + boxH + 55);
            }
        };
        center.setOpaque(false);

        add(center, BorderLayout.CENTER);
        add(text, BorderLayout.SOUTH);
    }
}
