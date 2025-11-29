import javax.swing.*;
import java.awt.*;

public class ContiguousPartitionSlide extends GradientSlide {
    public ContiguousPartitionSlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("Холбогдмол (contiguous) хуваалт"), BorderLayout.NORTH);

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int x = (int) (w * 0.15);
                int width = (int) (w * 0.7);
                int y = h / 2 - 80;
                int height = 40;

                // Fixed partitions
                drawMemoryBar(g2, x, y, width, height,
                        new Color[] { new Color(130, 180, 255),
                                new Color(180, 140, 255),
                                new Color(255, 180, 130) });
                g2.setColor(Color.WHITE);
                g2.drawString("Тогтмол хэмжээтэй хуваалт (Fixed size)", x, y - 10);

                // Variable partitions
                y += 100;
                drawMemoryBar(g2, x, y, width, height,
                        new Color[] { new Color(130, 180, 255),
                                new Color(130, 220, 180),
                                new Color(250, 150, 180),
                                new Color(230, 210, 120) });
                g2.drawString("Хувьсах хэмжээтэй хуваалт (Variable size)", x, y - 10);
            }

            private void drawMemoryBar(Graphics2D g2, int x, int y, int w, int h, Color[] colors) {
                int segmentW = w / colors.length;
                for (int i = 0; i < colors.length; i++) {
                    int sx = x + i * segmentW;
                    g2.setColor(new Color(colors[i].getRed(), colors[i].getGreen(),
                            colors[i].getBlue(), 220));
                    g2.fillRoundRect(sx, y, segmentW - 4, h, 14, 14);
                }
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, y, w, h, 18, 18);
            }
        };
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }
}
