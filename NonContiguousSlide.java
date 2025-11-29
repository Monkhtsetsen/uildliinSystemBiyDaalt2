import javax.swing.*;
import java.awt.*;

public class NonContiguousSlide extends GradientSlide {
    public NonContiguousSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Холбогдмол бус хуваарилалт"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 24, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        center.add(simpleDiagram("Paging",
                "Процесс = тогтмол хэмжээтэй pages\nФизик санах ой = frames\nPage table ашиглаж map хийнэ."));
        center.add(simpleDiagram("Segmentation",
                "Процесс = логик сегментүүд (code, data, stack)\n" +
                        "Сегментүүд хувьсах хэмжээтэй.\nSegment table ашиглана."));

        add(center, BorderLayout.CENTER);
    }

    private JComponent simpleDiagram(String title, String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 24));
        p.add(t, BorderLayout.NORTH);

        JTextArea a = new JTextArea(text);
        a.setOpaque(false);
        a.setEditable(false);
        a.setForeground(new Color(210, 225, 255));
        a.setFont(new Font("SansSerif", Font.PLAIN, 15));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        p.add(a, BorderLayout.SOUTH);

        JPanel boxes = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                int bx = 20;
                int by = 10;
                int bw = (w - 60) / 2;
                int bh = h - 40;

                // Left = logical address space
                g2.setColor(new Color(120, 150, 250));
                for (int i = 0; i < 4; i++) {
                    g2.fillRoundRect(bx, by + i * (bh / 4 + 4), bw, bh / 4 - 4, 10, 10);
                }

                // Right = physical frames scattered
                int rx = bx + bw + 20;
                for (int i = 0; i < 4; i++) {
                    int y = by + (i * 20) + (i * 15);
                    g2.setColor(new Color(130 + i * 20, 200 - i * 10, 210));
                    g2.fillRoundRect(rx, y, bw, 18, 10, 10);
                }
            }
        };
        boxes.setOpaque(false);
        p.add(boxes, BorderLayout.CENTER);
        return p;
    }
}
