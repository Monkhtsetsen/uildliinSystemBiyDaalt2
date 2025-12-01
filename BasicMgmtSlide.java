import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Нэг / олон хэрэглэгчийн санах ойн загварын aesthetic + animated слайд.
 */
public class BasicMgmtSlide extends GradientSlide {

    public BasicMgmtSlide() {
        setLayout(new BorderLayout());

        // Дээрх гарчиг
        add(Main.createHeader("Нэг хэрэглэгчийн / олон хэрэглэгчийн санах ойн загвар"),
                BorderLayout.NORTH);

        // Гол хэсэг: 2 багана (single vs multi)
        JPanel center = new JPanel(new GridLayout(1, 2, 28, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(26, 40, 36, 40));

        center.add(createModeCard(
                "Энгийн (монопрограммчилсан)",
                "1 програм + OS",
                new String[] {
                        "Доод хэсэгт OS бүх санах ойг хянадаг",
                        "Дээр нь ганц хэрэглэгчийн програм байрлана",
                        "Санах ой нэг том хэсэг, address space тусдаа биш"
                },
                false));

        center.add(createModeCard(
                "Олон програмтай (multiprogramming)",
                "Олон процесс + хамгаалалт",
                new String[] {
                        "Доод түвшинд OS / kernel үргэлж байж байна",
                        "Дээр нь хэд хэдэн процесс зэрэг санах ойд байрлана",
                        "Хуваалт, хамгаалалт, тусдаа address space заавал хэрэгтэй"
                },
                true));

        add(center, BorderLayout.CENTER);
    }

    /**
     * Нэг загварын card (гарчиг + тайлбар + bullet + memory диаграм).
     */
    private JComponent createModeCard(String title, String subtitle, String[] bullets, boolean multiProgram) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Card background – зөөлөн gradient
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 30, 70, 190),
                        0, h, new Color(10, 15, 40, 230));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 26, 26);

                // Outer border
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 26, 26);

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // === Header хэсэг: гарчиг + subtitle chip ===
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(t);

        header.add(Box.createVerticalStrut(6));

        JLabel chip = new JLabel(subtitle);
        chip.setOpaque(true);
        chip.setBackground(new Color(88, 101, 242, 200));
        chip.setForeground(new Color(230, 235, 255));
        chip.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chip.setBorder(new EmptyBorder(3, 10, 3, 10));
        header.add(chip);

        card.add(header, BorderLayout.NORTH);

        // === Bullet хэсэг ===
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(12, 0, 0, 0));

        for (String b : bullets) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            row.setOpaque(false);

            // Жижиг дугуй bullet icon
            JComponent dot = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    int d = Math.min(getWidth(), getHeight()) - 2;
                    g2.setColor(new Color(144, 190, 255));
                    g2.fillOval(1, 1, d, d);
                    g2.dispose();
                }
            };
            dot.setPreferredSize(new Dimension(10, 10));

            JLabel lbl = new JLabel(b);
            lbl.setForeground(new Color(210, 220, 255));
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

            row.add(dot);
            row.add(lbl);
            content.add(row);
        }

        card.add(content, BorderLayout.CENTER);

        // === Доод хэсэг: animated memory диаграм ===
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel caption = new JLabel(
                multiProgram ? "Физик санах ой – олон процесс хуваалцана" : "Физик санах ой – ганц програм эзэлнэ");
        caption.setForeground(new Color(200, 210, 255));
        caption.setFont(new Font("SansSerif", Font.PLAIN, 13));

        bottom.add(caption, BorderLayout.NORTH);

        MemoryDiagram diagram = new MemoryDiagram(multiProgram);
        diagram.setPreferredSize(new Dimension(230, 160));
        diagram.setOpaque(false);
        bottom.add(diagram, BorderLayout.CENTER);

        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Санах ойн хайрцаг: OS + 1 програм эсвэл олон програм.
     * Animation:
     * - single-д програм блок нь зөөлөн pulse
     * - multi-д процессуудыг CPU highlight-лаж ээлжилж тодруулна
     */
    private static class MemoryDiagram extends JComponent {
        private final boolean multi;
        private javax.swing.Timer timer;
        private float phase = 0f; // single: pulse animation
        private int highlightIdx = 0; // multi: аль процессыг тодруулах

        public MemoryDiagram(boolean multiProgram) {
            this.multi = multiProgram;

            if (multi) {
                // Олон програмтай – процесс бүрийг ээлжилж highlight
                timer = new javax.swing.Timer(900, e -> {
                    highlightIdx = (highlightIdx + 1) % 3; // 3 процесс
                    repaint();
                });
            } else {
                // Нэг програмтай – нэг блок зөөлөн pulse
                timer = new javax.swing.Timer(40, e -> {
                    phase += 0.08f;
                    if (phase > Math.PI * 2) {
                        phase -= (float) (Math.PI * 2);
                    }
                    repaint();
                });
            }
        }

        @Override
        public void addNotify() {
            super.addNotify();
            if (timer != null && !timer.isRunning()) {
                timer.start();
            }
        }

        @Override
        public void removeNotify() {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int barWidth = (int) (w * 0.38);
            int x = (w - barWidth) / 2;
            int y = (int) (h * 0.08);
            int barHeight = (int) (h * 0.84);

            // Арын сүүдэр
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(x + 4, y + 5, barWidth, barHeight, 18, 18);

            // Memory box
            g2.setColor(new Color(16, 24, 60));
            g2.fillRoundRect(x, y, barWidth, barHeight, 18, 18);
            g2.setColor(new Color(180, 190, 230, 140));
            g2.drawRoundRect(x, y, barWidth, barHeight, 18, 18);

            // RAM label (зүүн талд босоо)
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(190, 200, 240));
            String ram = "RAM";
            int ry = y + barHeight / 2;
            g2.drawString(ram, x - 28, ry);

            // OS хэсэг (доод талд)
            int osHeight = (int) (barHeight * 0.18);
            int osY = y + barHeight - osHeight;

            g2.setColor(new Color(72, 84, 200));
            g2.fillRoundRect(x + 4, osY, barWidth - 8, osHeight - 4, 14, 14);

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(230, 235, 255));
            drawCenteredString(g2, "OS / Kernel", x + 4, osY, barWidth - 8, osHeight - 4);

            if (!multi) {
                // === НЭГ ПРОГРАМ ===
                int progHeight = barHeight - osHeight - 12;
                int progY = y + 6;

                // Pulse-ийн intensity
                float pulse = (float) (0.4 + 0.2 * Math.sin(phase));
                int alpha = (int) (120 + 80 * pulse);

                // Glow эффект
                g2.setColor(new Color(122, 190, 255, alpha));
                g2.fillRoundRect(x + 2, progY - 3, barWidth - 4, progHeight + 6, 18, 18);

                // Main програм блок
                g2.setColor(new Color(122, 190, 255, 230));
                g2.fillRoundRect(x + 6, progY, barWidth - 12, progHeight, 14, 14);

                g2.setColor(new Color(18, 29, 70, 200));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                drawCenteredString(g2, "Хэрэглэгчийн програм", x + 8, progY, barWidth - 16, progHeight);

                // Жижиг тайлбар текст
                g2.setColor(new Color(210, 220, 255));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("Бүх санах ойг ганц program эзэлж байна",
                        x + barWidth + 10, progY + 18);

            } else {
                // === ОЛОН ПРОГРАМ ===
                int freeHeight = barHeight - osHeight - 12;

                int segments = 3;
                int gap = 6;
                int segHeight = (freeHeight - (segments - 1) * gap) / segments;

                Color[] baseColors = {
                        new Color(116, 185, 255, 220),
                        new Color(129, 236, 171, 220),
                        new Color(255, 234, 167, 220)
                };

                g2.setFont(new Font("SansSerif", Font.BOLD, 11));

                for (int i = 0; i < segments; i++) {
                    int segY = y + 6 + i * (segHeight + gap);

                    boolean highlighted = (i == highlightIdx);

                    Color c = baseColors[i % baseColors.length];
                    if (highlighted) {
                        // Highlighted байхад арай тод өнгө
                        c = new Color(
                                Math.min(255, c.getRed() + 25),
                                Math.min(255, c.getGreen() + 25),
                                Math.min(255, c.getBlue() + 25),
                                240);
                        // Glow
                        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
                        g2.fillRoundRect(x + 3, segY - 3, barWidth - 6, segHeight + 6, 18, 18);
                    }

                    g2.setColor(c);
                    g2.fillRoundRect(x + 6, segY, barWidth - 12, segHeight, 12, 12);

                    g2.setColor(new Color(15, 25, 60, 210));
                    String label = "Процесс " + (i + 1);
                    drawCenteredString(g2, label, x + 8, segY, barWidth - 16, segHeight);

                    // Highlight-лагдсан process рүү CPU сум заана
                    if (highlighted) {
                        g2.setColor(new Color(230, 235, 255));
                        int cx = x + barWidth + 18;
                        int cy = segY + segHeight / 2;

                        // CPU box
                        int cpuW = 40;
                        int cpuH = 20;
                        g2.drawRoundRect(cx, cy - cpuH / 2, cpuW, cpuH, 8, 8);
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                        g2.drawString("CPU", cx + 8, cy + 4);

                        // Arrow Process -> CPU
                        int ax1 = x + barWidth - 4;
                        int ay1 = segY + segHeight / 2;
                        int ax2 = cx;
                        int ay2 = cy;
                        g2.drawLine(ax1, ay1, ax2, ay2);
                        int arrowSize = 5;
                        Polygon arrow = new Polygon();
                        arrow.addPoint(ax2, ay2);
                        arrow.addPoint(ax2 - arrowSize, ay2 - arrowSize);
                        arrow.addPoint(ax2 - arrowSize, ay2 + arrowSize);
                        g2.fill(arrow);
                    }
                }

                // Жижиг тайлбар текст
                g2.setColor(new Color(210, 220, 255));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("CPU процесстэй ээлжилж ажиллана (multiprogramming)",
                        x + barWidth + 4, y + 20);
            }

            g2.dispose();
        }

        private void drawCenteredString(Graphics2D g2, String text, int x, int y, int width, int height) {
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int tx = x + (width - textWidth) / 2;
            int ty = y + (height + textHeight) / 2 - 2;
            g2.drawString(text, tx, ty);
        }
    }
}
