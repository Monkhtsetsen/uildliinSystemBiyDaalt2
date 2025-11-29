import javax.swing.*;
import java.awt.*;

public abstract class GradientSlide extends JPanel {
    protected final Color top = new Color(7, 10, 40);
    protected final Color bottom = new Color(40, 20, 90);

    public GradientSlide() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, top, w, h, bottom);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        super.paintComponent(g);
    }
}
