import javax.swing.*;
import java.awt.*;

public class TitleSlide extends GradientSlide {
    public TitleSlide() {
        setLayout(new GridBagLayout());
        JLabel title = new JLabel("Санах ойн удирдлага");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 52));

        JLabel subtitle = new JLabel("Operating System · Interactive Demo");
        subtitle.setForeground(new Color(190, 200, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 22));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(20));
        box.add(subtitle);

        add(box, new GridBagConstraints());
    }
}
