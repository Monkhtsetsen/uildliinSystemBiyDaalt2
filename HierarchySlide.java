import javax.swing.*;
import java.awt.*;

public class HierarchySlide extends GradientSlide {
    public HierarchySlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("Санах ойн шатлал"), BorderLayout.NORTH);

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int levels = 5;
                String[] names = { "Регистр", "Кэш", "RAM", "SSD/HDD", "Cloud / Network" };
                String[] info = { "хамгийн хурдан", "маш хурдан", "дунд хурд", "удаан", "хамгийн удаан" };
                int baseWidth = (int) (w * 0.7);
                int heightStep = 60;
                int yStart = h / 2 - (levels * heightStep) / 2;

                for (int i = 0; i < levels; i++) {
                    int rectW = baseWidth - i * 80;
                    int x = (w - rectW) / 2;
                    int y = yStart + i * heightStep;

                    float ratio = i / (float) (levels - 1);
                    Color c = new Color(100 + (int) (80 * ratio), 120, 230);
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
                    g2.fillRoundRect(x, y, rectW, 45, 18, 18);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(x, y, rectW, 45, 18, 18);

                    g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
                    g2.drawString(names[i], x + 12, y + 25);
                    g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
                    g2.drawString(info[i], x + rectW - 120, y + 25);
                }
            }
        };
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }
}
