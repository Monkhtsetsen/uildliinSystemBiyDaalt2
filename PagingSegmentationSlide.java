import javax.swing.*;
import java.awt.*;

public class PagingSegmentationSlide extends GradientSlide {
    public PagingSegmentationSlide() {
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader("Хуудаслалт ба сегментац"), BorderLayout.NORTH);

        String[] paging = {
                "Физик санах ойг тогтмол хэмжээтэй frames болгон хуваана.",
                "Процессийн логик санах ойг pages болгон хувааж, page table-ээр map хийнэ.",
                "Дотоод fragmentation байж болно (frame-ээс бага үлдэгдэл)."
        };
        String[] seg = {
                "Процессийг логик сегментүүд (code, data, stack) болгон хуваана.",
                "Сегментийн хэмжээ хувьсах, segment table ашиглана.",
                "Гадаад fragmentation үүсч болно."
        };

        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        center.add(column("Paging", paging));
        center.add(column("Segmentation", seg));
        add(center, BorderLayout.CENTER);
    }

    private JComponent column(String title, String[] bullets) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 24));
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        for (String b : bullets) {
            JLabel l = new JLabel("• " + b);
            l.setForeground(new Color(210, 225, 255));
            l.setFont(new Font("SansSerif", Font.PLAIN, 15));
            p.add(l);
        }
        return p;
    }
}
