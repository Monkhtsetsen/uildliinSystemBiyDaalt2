import javax.swing.*;
import java.awt.*;

public class BasicMgmtSlide extends GradientSlide {
    public BasicMgmtSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Нэг хэрэглэгчийн / олон хэрэглэгчийн санах ойн загвар"),
                BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        center.add(column("Энгийн (монопрограммчилсан)",
                new String[] { "Доод хэсэгт OS", "Дээр нь ганц хэрэглэгчийн програм",
                        "Санах ой дан, хуваалтгүй" }));
        center.add(column("Олон програмтай",
                new String[] { "Доод хэсэгт OS", "Дээр нь хэд хэдэн процесс",
                        "Хуваалт, хамгаалалт, isolation хэрэгтэй" }));

        add(center, BorderLayout.CENTER);
    }

    private JComponent column(String title, String[] bullets) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(t);
        p.add(Box.createVerticalStrut(10));

        for (String b : bullets) {
            JLabel l = new JLabel("• " + b);
            l.setForeground(new Color(200, 210, 255));
            l.setFont(new Font("SansSerif", Font.PLAIN, 15));
            p.add(l);
        }

        // add stylized memory bar
        p.add(Box.createVerticalStrut(18));
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(30, 40, 120));
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                g2.setColor(new Color(110, 140, 255));
                g2.fillRoundRect(4, 4, w - 8, h / 3, 12, 12);
                g2.setColor(new Color(160, 220, 160));
                g2.fillRoundRect(4, h / 3 + 8, w - 8, h / 3, 12, 12);
            }
        };
        bar.setPreferredSize(new Dimension(200, 120));
        bar.setOpaque(false);
        p.add(bar);
        return p;
    }
}
