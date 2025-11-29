import javax.swing.*;
import java.awt.*;

public class AllocationIntroSlide extends GradientSlide {
    public AllocationIntroSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Статик ба динамик хуваарилалт"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        center.add(allocationCard("Статик",
                "Компиляцийн үед хуваарилна.\nРазмер нь тогтмол, өөрчлөгдөхгүй.\n" +
                        "Embedded систем, stack, global хувьсагч гэх мэт."));
        center.add(allocationCard("Динамик",
                "Гүйцэтгэлийн үед new/malloc гэх мэтээр \nасуудал үүсэх үед хуваарилна.\n" +
                        "GUI, тоглоом, multimedia програмд түгээмэл."));

        add(center, BorderLayout.CENTER);
    }

    private JComponent allocationCard(String title, String text) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 150, 250), 2, true),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel label = new JLabel(title);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));

        JTextArea area = new JTextArea(text);
        area.setForeground(new Color(215, 225, 255));
        area.setOpaque(false);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 15));

        card.add(label, BorderLayout.NORTH);
        card.add(area, BorderLayout.CENTER);
        return card;
    }
}
