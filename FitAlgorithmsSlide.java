import javax.swing.*;
import java.awt.*;

public class FitAlgorithmsSlide extends GradientSlide {
    public FitAlgorithmsSlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("Санах ойн хуваарилалтын алгоритмууд"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 16, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 30, 40, 30));

        center.add(algCard("First Fit",
                "Хамгийн эхэнд таарсан\nчанга хуваалтыг авна.\nХурдан, гэхдээ хагархай их."));
        center.add(algCard("Best Fit",
                "Хэрэгцээтэй хэмжээнд\nхамгийн ойр жижиг хуваалтыг\nсонгоно."));
        center.add(algCard("Worst Fit",
                "Хамгийн том боломжит\nхуваалтыг сонгож үлдсэн хэсгийг\nтомоор үлдээнэ."));

        add(center, BorderLayout.CENTER);
    }

    private JComponent algCard(String title, String body) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 180, 255), 2, true),
                BorderFactory.createEmptyBorder(16, 14, 16, 14)));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 22));
        card.add(t, BorderLayout.NORTH);

        JTextArea a = new JTextArea(body);
        a.setOpaque(false);
        a.setEditable(false);
        a.setForeground(new Color(220, 230, 255));
        a.setFont(new Font("SansSerif", Font.PLAIN, 15));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        card.add(a, BorderLayout.CENTER);

        return card;
    }
}
