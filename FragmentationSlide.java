import javax.swing.*;
import java.awt.*;

public class FragmentationSlide extends GradientSlide {
    public FragmentationSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Дотоод ба гадаад фрагментаци"), BorderLayout.NORTH);

        JTextArea desc = new JTextArea(
                "• Дотоод фрагментаци – хуваалтын доторх ашиглагдахгүй үлдэгдэл зай.\n" +
                        "• Гадаад фрагментаци – жижиг жижиг сул блокнууд нийлээд хангалттай зай болдог ч " +
                        "холбогдмол бус байгаагаас үүдэн ашиглаж чадахгүй байдал.");
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setForeground(new Color(215, 225, 255));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 40, 40, 40));
        center.add(fragmentBlock("Дотоод",
                new int[] { 60, 40 }, true));
        center.add(fragmentBlock("Гадаад",
                new int[] { 25, 15, 10, 20, 30 }, false));

        add(desc, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JComponent fragmentBlock(String label, int[] sizes, boolean internal) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                g2.setColor(Color.WHITE);
                g2.drawString(label, 10, 20);

                int x = 20, y = 40;
                int totalWidth = w - 40;
                int usedWidth = 0;

                for (int size : sizes) {
                    int blockW = totalWidth * size / 100;
                    g2.setColor(new Color(120, 170, 250, 220));
                    g2.fillRect(x + usedWidth, y, blockW - 4, 40);
                    g2.setColor(Color.WHITE);
                    g2.drawRect(x + usedWidth, y, blockW - 4, 40);
                    usedWidth += blockW;
                }

                if (internal) {
                    g2.setColor(new Color(255, 200, 140, 220));
                    g2.fillRect(x + usedWidth - totalWidth / 4, y, totalWidth / 4 - 4, 40);
                    g2.setColor(Color.WHITE);
                    g2.drawString("unused", x + usedWidth - totalWidth / 4 + 10, y + 25);
                } else {
                    // draw small gaps
                    g2.setColor(new Color(255, 150, 150));
                    g2.drawString("Олон жижиг сул блок", x, y + 70);
                }
            }
        };
    }
}
