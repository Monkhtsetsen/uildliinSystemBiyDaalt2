import javax.swing.*;
import java.awt.*;

public class MemoryTypesSlide extends GradientSlide {
    public MemoryTypesSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Санах ойн төрөл"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 24, 24));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));

        grid.add(typeCard("RAM", "Шуурхай, дотоод санах ой.\nТэжээл тасарвал алга болно."));
        grid.add(typeCard("ROM", "Зөвхөн уншина.\nТэжээл тасарсан ч хадгалагдана."));
        grid.add(typeCard("Cache", "CPU-д хамгийн ойр.\nСаяхан ашигласан өгөгдлийг барина."));
        grid.add(typeCard("Virtual", "Disk-ийг RAM-ийн өргөтгөл мэт\nашиглах логик санах ой."));

        add(grid, BorderLayout.CENTER);
    }

    private static JComponent typeCard(String title, String body) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 130, 230), 2, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 24));

        JTextArea txt = new JTextArea(body);
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setForeground(new Color(215, 225, 255));
        txt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBorder(null);

        card.add(t, BorderLayout.NORTH);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }
}
