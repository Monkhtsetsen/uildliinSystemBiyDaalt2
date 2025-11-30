import javax.swing.*;
import java.awt.*;

public class ContiguousPartitionSlide extends GradientSlide {

    public ContiguousPartitionSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Холбогдмол хуваалт"), BorderLayout.NORTH);

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                int barW = (int) (w * 0.70);
                int barX = (int) (w * 0.15);

                /* ---------- FIXED (Тогтмол) PARTITION ---------- */
                int y1 = h / 2 - 140;

                g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
                g2.setColor(Color.WHITE);
                g2.drawString("Тогтмол хэмжээтэй хуваалт", barX, y1 - 12);

                drawFixedPartitions(g2, barX, y1, barW, 45);

                g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
                g2.setColor(new Color(220, 230, 250));
                g2.drawString("• Бүх хэсэг урьдчилан тогтоосон нэг ижил эсвэл тогтсон хэмжээтэй.", barX, y1 + 70);
                g2.drawString("• Том partition → internal fragmentation үүснэ.", barX, y1 + 86);

                /* ---------- VARIABLE (Хувьсах) PARTITION ---------- */
                int y2 = y1 + 150;

                g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
                g2.setColor(Color.WHITE);
                g2.drawString("Хувьсах хэмжээтэй хуваалт", barX, y2 - 12);

                drawVariablePartitions(g2, barX, y2, barW, 45);

                g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
                g2.setColor(new Color(220, 230, 250));
                g2.drawString("• Хэрэгцээний хэмжээгээр блок олгогдоно.", barX, y2 + 70);
                g2.drawString("• Блок гарвал дунд нь хоосон зай үүсч external fragmentation бий болдог.", barX, y2 + 86);
            }

            /* =====================================================
               FIXED PARTITIONS (equal-sized blocks)
               ===================================================== */
            private void drawFixedPartitions(Graphics2D g2, int x, int y, int width, int height) {
                int parts = 3;
                int gap = 4;
                int segmentW = (width - (parts + 1) * gap) / parts;

                Color[] colors = {
                    new Color(56, 189, 248),  // цэнхэр
                    new Color(129, 140, 248), // нил ягаандуу
                    new Color(251, 191, 36)   // шар
                };

                for (int i = 0; i < parts; i++) {
                    int sx = x + gap + i * (segmentW + gap);
                    paintBlock(g2, sx, y, segmentW, height, colors[i], "P" + (i + 1));
                }

                // Border
                g2.setColor(new Color(180, 190, 255));
                g2.drawRoundRect(x, y, width, height, 16, 16);
            }

            /* =====================================================
               VARIABLE PARTITIONS (different sizes + free hole)
               ===================================================== */
            private void drawVariablePartitions(Graphics2D g2, int x, int y, int width, int height) {

                // 4 blocks: P1, P2, FREE space, P3
                int gap = 4;

                int p1 = (int)(width * 0.30);
                int p2 = (int)(width * 0.22);
                int free = (int)(width * 0.15);
                int p3 = (int)(width * 0.25);

                int[] sizes = {p1, p2, free, p3};

                Color[] colors = {
                    new Color(56, 189, 248),
                    new Color(74, 222, 128),
                    new Color(30, 41, 59),      // dark – free block
                    new Color(251, 191, 36)
                };

                String[] labels = {"A", "B", "сул", "C"};

                int currX = x + gap;
                for (int i = 0; i < sizes.length; i++) {
                    paintBlock(g2, currX, y, sizes[i], height, colors[i], labels[i]);
                    currX += sizes[i] + gap;
                }

                // Border
                g2.setColor(new Color(180, 190, 255));
                g2.drawRoundRect(x, y, width, height, 16, 16);
            }

            /* =====================================================
               Helper: draw individual block
               ===================================================== */
            private void paintBlock(Graphics2D g2, int x, int y, int w, int h, Color color, String label) {
                // Shadow-like background
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 230));
                g2.fillRoundRect(x, y, w, h, 12, 12);

                // Label
                g2.setColor(new Color(15, 23, 42, 220));
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.drawString(label, x + 8, y + (h / 2) + 5);
            }
        };

        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }
}
