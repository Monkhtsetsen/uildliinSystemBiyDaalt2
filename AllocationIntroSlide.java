import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AllocationIntroSlide extends GradientSlide {

    public AllocationIntroSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Статик ба динамик хуваарилалт"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 32, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        center.add(new StaticAllocationPanel());
        center.add(new DynamicAllocationPanel());

        add(center, BorderLayout.CENTER);
    }

    /* ===================== Зүүн тал: Статик хуваарилалт ===================== */

    private static class StaticAllocationPanel extends JPanel {
        public StaticAllocationPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.setColor(Color.WHITE);
            g2.drawString("Статик хуваарилалт", 10, 28);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(200, 210, 240));
            g2.drawString("• Програм эхлэхээс өмнө санах ойг тогтмол хэмжээтэй хэсгүүдэд хуваадаг", 10, 48);
            g2.drawString("• блокуудын хэмжээ програм дуусах хүртэл өөрчлөгддөггүй", 10, 64);
            g2.drawString("• Global/static хувьсагч болон stack нь энэ зарчмаар ажилладаг", 10, 80);

            // Memory vertical bar
            int memX = w / 2;
            int memY = 110;
            int memW = 150;         // бүх багана (RAM) -ын өргөн
            int memH = h - 160;    // бүх багана -ын өндөр

            // Background glass
            g2.setColor(new Color(15, 23, 42, 160));
            g2.fillRoundRect(memX - memW / 2 - 20, memY - 20, memW + 40, memH + 40, 26, 26);

            // Memory border
            g2.setColor(new Color(148, 163, 253));
            g2.fillRoundRect(memX - memW / 2, memY, memW, memH, 20, 20);

            // Partitions (fixed)
            int parts = 4;
            int gap = 4;
            int innerH = memH - (parts + 1) * gap;
            int partH = innerH / parts;

            String[] labels = {
                "Програмын код",      // Кодын хэсэг
                "Статик өгөгдөл",      // Глобал/статик хувьсагчид
                "Stack (дуудлагын бүс)", // Функц дуудагдахад ашиглагддаг санах ой
                "Сул зай"              // Ашиглагдаагүй үлдсэн хэсэг
            };

            Color[] colors = {
                    new Color(56, 189, 248),
                    new Color(74, 222, 128),
                    new Color(251, 191, 36),
                    new Color(30, 64, 175)
            };

            int y = memY + gap;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (int i = 0; i < parts; i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(memX - memW / 2 + 6, y, memW - 12, partH, 14, 14);

                g2.setColor(new Color(15, 23, 42, 180));
                g2.drawString(labels[i], memX - memW / 2 + 12, y + partH / 2 + 4);

                y += partH + gap;
            }

            // Caption
            g2.setColor(new Color(200, 210, 240));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
        }
    }

    /* ===================== Баруун тал: Динамик хуваарилалт (анимацтай) ===================== */

    private static class DynamicAllocationPanel extends JPanel implements ActionListener {
        private Timer timer;
        private int step = 0;      // 0..3
        private int counter = 0;   // frame counter

        public DynamicAllocationPanel() {
            setOpaque(false);
            // 40ms → ~25 fps
            timer = new Timer(40, this);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            counter++;
            if (counter >= 25) {   // ~1s тутамд state солих
                counter = 0;
                step = (step + 1) % 4;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.setColor(Color.WHITE);
            g2.drawString("Динамик хуваарилалт", 10, 28);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(200, 210, 240));
            g2.drawString("• Программ ажиллах явцад санах ойг динамикаар хүсэлтээр авдаг.", 10, 48);
            g2.drawString("• Хэрэгцээ дуусахад блокийг буцааж чөлөөлж дахин ашиглана.", 10, 64);
            g2.drawString("• Блокуудын хооронд жигд бус сул зай үүсч external fragmentation бий болдог.", 10, 80);

            // Memory vertical bar
            int memX = w / 2;
            int memY = 110;
            int memW = 150;
            int memH = h - 160;

            // Background glass
            g2.setColor(new Color(15, 23, 42, 160));
            g2.fillRoundRect(memX - memW / 2 - 20, memY - 20, memW + 40, memH + 40, 26, 26);

            g2.setColor(new Color(30, 64, 175));
            g2.fillRoundRect(memX - memW / 2, memY, memW, memH, 20, 20);

            // Draw dynamic blocks by step
            drawDynamicBlocks(g2, memX, memY, memW, memH);

            // Caption
            g2.setColor(new Color(200, 210, 240));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12)); 
        }

        private void drawDynamicBlocks(Graphics2D g2, int memX, int memY, int memW, int memH) {
            int innerX = memX - memW / 2 + 6;
            int innerW = memW - 12;
            int baseY = memY + memH - 6;
            int gap = 4;

            switch (step) {
                case 0 -> {
                    // A + том сул блок
                    int hA = (int) (memH * 0.35);
                    int hFree = memH - hA - gap * 3;

                    int yFree = baseY - hFree;
                    int yA = yFree - hA - gap;

                    drawBlock(g2, innerX, yA, innerW, hA, new Color(56, 189, 248), "A", "32MB");
                    drawFreeBlock(g2, innerX, yFree, innerW, hFree, "сул блок");
                }
                case 1 -> {
                    int hA = (int) (memH * 0.35);
                    int hB = (int) (memH * 0.25);
                    int hC = (int) (memH * 0.20);

                    int yC = baseY - hC;
                    int yB = yC - hB - gap;
                    int yA = yB - hA - gap;

                    drawBlock(g2, innerX, yA, innerW, hA, new Color(56, 189, 248), "A", "32MB");
                    drawBlock(g2, innerX, yB, innerW, hB, new Color(74, 222, 128), "B", "24MB");
                    drawBlock(g2, innerX, yC, innerW, hC, new Color(251, 191, 36), "C", "20MB");
                }
                case 2 -> {
                    int hA = (int) (memH * 0.35);
                    int hHole = (int) (memH * 0.25);
                    int hC = (int) (memH * 0.20);

                    int yC = baseY - hC;
                    int yHole = yC - hHole - gap;
                    int yA = yHole - hA - gap;

                    drawBlock(g2, innerX, yA, innerW, hA, new Color(56, 189, 248), "A", "32MB");
                    drawFreeBlock(g2, innerX, yHole, innerW, hHole, "сул блок");
                    drawBlock(g2, innerX, yC, innerW, hC, new Color(251, 191, 36), "C", "20MB");
                }
                case 3 -> {
                    int hA = (int) (memH * 0.35);
                    int hHole = (int) (memH * 0.25);
                    int hD = (int) (memH * 0.14);
                    int hFree = hHole - hD - gap;

                    int yFree = baseY - hFree;
                    int yD = yFree - hD - gap;
                    int yA = yD - hA - gap;

                    drawBlock(g2, innerX, yA, innerW, hA, new Color(56, 189, 248), "A", "32MB");
                    drawBlock(g2, innerX, yD, innerW, hD, new Color(129, 140, 248), "D", "12MB");
                    drawFreeBlock(g2, innerX, yFree, innerW, hFree, "сул");
                }
            }
        }

        private void drawBlock(Graphics2D g2, int x, int y, int w, int h, Color color, String label, String size) {
            g2.setColor(color);
            g2.fillRoundRect(x, y, w, h, 12, 12);

            g2.setColor(new Color(15, 23, 42, 220));
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString("P" + label, x + 8, y + 18);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(size, x + 8, y + 34);
        }

        private void drawFreeBlock(Graphics2D g2, int x, int y, int w, int h, String label) {
            g2.setColor(new Color(15, 23, 42, 180));
            g2.fillRoundRect(x, y, w, h, 12, 12);
            g2.setColor(new Color(148, 163, 253));
            Stroke old = g2.getStroke();
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w, h, 12, 12);
            g2.setStroke(old);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(label, x + 8, y + 18);
        }
    }
}
