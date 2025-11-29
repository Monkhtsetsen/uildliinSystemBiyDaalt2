import javax.swing.*;
import java.awt.*;

public class PageReplacementIntroSlide extends GradientSlide {
    public PageReplacementIntroSlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("Page replacement алгоритмууд"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 16, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        center.add(iconText("FIFO",
                "Хамгийн түрүүнд орсон page-г хамгийн түрүүнд гаргана.\n" +
                        "Queue бүтэц ашиглана."));
        center.add(iconText("LRU",
                "Хамгийн сүүлд ашиглагдаагүй (удаан орхигдсон) page-г гаргана.\n" +
                        "Timestamp / stack ашиглаж болно."));
        center.add(iconText("Optimal",
                "Ирээдүйд хамгийн удаан хэрэглэхгүй page-г гаргах (теоретик).\n" +
                        "Сургалтын жишээнд л ашиглана."));

        add(center, BorderLayout.CENTER);
    }

    private JComponent iconText(String title, String text) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BorderLayout());

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 22));

        JTextArea a = new JTextArea(text);
        a.setOpaque(false);
        a.setEditable(false);
        a.setForeground(new Color(215, 225, 255));
        a.setFont(new Font("SansSerif", Font.PLAIN, 14));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);

        p.add(t, BorderLayout.NORTH);
        p.add(a, BorderLayout.CENTER);
        return p;
    }
}
