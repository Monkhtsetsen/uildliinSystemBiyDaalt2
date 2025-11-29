import javax.swing.*;
import java.awt.*;

public class OverviewSlide extends GradientSlide {
    public OverviewSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Санах ойн удирдлага гэж юу вэ?"), BorderLayout.NORTH);

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // CPU -> MMU -> RAM -> Disk диаграм
                int cx = w / 2;
                int cy = h / 2;
                int boxW = 140;
                int boxH = 60;

                drawBox(g2, cx - 3 * boxW, cy - boxH / 2, boxW, boxH, "CPU");
                drawArrow(g2, cx - 2 * boxW + 10, cy, cx - boxW - 10, cy);
                drawBox(g2, cx - 2 * boxW, cy - boxH / 2, boxW, boxH, "MMU");
                drawArrow(g2, cx - boxW + 10, cy, cx - 10, cy);
                drawBox(g2, cx - boxW / 2, cy - boxH / 2, boxW, boxH, "RAM");
                drawArrow(g2, cx + boxW / 2 + 10, cy, cx + boxW + 10, cy);
                drawBox(g2, cx + boxW, cy - boxH / 2, boxW + 20, boxH, "Disk");

                g2.setColor(new Color(220, 230, 255));
                g2.setFont(getFont().deriveFont(Font.PLAIN, 18f));
                g2.drawString("Хаяг хөрвүүлэлт · блок хуваарилалт · чөлөөлөлт",
                        cx - 220, cy + boxH + 50);
            }

            private void drawBox(Graphics2D g2, int x, int y, int w, int h, String text) {
                g2.setColor(new Color(60, 80, 220, 200));
                g2.fillRoundRect(x, y, w, h, 16, 16);
                g2.setColor(new Color(230, 235, 255));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x, y, w, h, 16, 16);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (w - fm.stringWidth(text)) / 2;
                int ty = y + (h + fm.getAscent()) / 2 - 4;
                g2.drawString(text, tx, ty);
            }

            private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
                g2.setColor(new Color(200, 210, 255));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(x1, y1, x2, y2);
                int size = 7;
                int dx = x2 - x1;
                int dy = y2 - y1;
                double angle = Math.atan2(dy, dx);
                int x = x2;
                int y = y2;
                int xA = (int) (x - size * Math.cos(angle - Math.PI / 6));
                int yA = (int) (y - size * Math.sin(angle - Math.PI / 6));
                int xB = (int) (x - size * Math.cos(angle + Math.PI / 6));
                int yB = (int) (y - size * Math.sin(angle + Math.PI / 6));
                g2.fillPolygon(new int[] { x, xA, xB }, new int[] { y, yA, yB }, 3);
            }
        };
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }
}
