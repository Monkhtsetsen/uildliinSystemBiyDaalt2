import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HierarchySlide extends GradientSlide {

    public HierarchySlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Санах ойн шатлал"), BorderLayout.NORTH);

        HierarchyPanel center = new HierarchyPanel();
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }

    // ======================================
    // Дотоод панел: шатлал + анимэйшн + чимэглэл
    // ======================================
    private static class HierarchyPanel extends JPanel implements ActionListener {

        private final String[] names = {
                "Регистр",
                "Кэш",
                "RAM",
                "SSD / HDD",
                "Cloud / Network"
        };

        private final String[] info = {
                "CPU-ийн доторх хамгийн хурдан, маш бага хэмжээний санах ой.",
                "Процессорын ойр байрлах маш хурдан кэш санах ой.",
                "Гол дотоод санах ой, програм ба өгөгдөл шууд эндээс уншигдана.",
                "Гадаад санах ой, их багтаамжтай ч унших/бичих хурд бага.",
                "Алсын сервер / сүлжээн дэх хадгалалт, latency хамгийн их."
        };

        // Binary particles (чимэглэл)
        private static class BinaryDot {
            float x, y;
            float offset;
            String bit;
        }

        private final List<BinaryDot> dots = new ArrayList<>();
        private final Random rnd = new Random();

        private float phase = 0f; // binary анимэйшн
        private int tick = 0;
        private int highlightIndex = 0; // аль шат glow-оор онцлогдож байгааг заана

        private final Timer timer;

        HierarchyPanel() {
            setOpaque(false);
            timer = new Timer(33, this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                g2.dispose();
                return;
            }

            if (dots.isEmpty()) {
                initDots(w, h);
            }

            // Зөөлөн төв glow
            paintCenterGlow(g2, w, h);

            // Binary чимэглэл
            drawBinaryDots(g2, w, h);

            // Зүүн/баруун талын theory bars
            drawCapacityBar(g2, w, h);
            drawSpeedBar(g2, w, h);

            // Санах ойн шатлалуудыг зурах
            drawHierarchy(g2, w, h);

            g2.dispose();
        }

        // ---------------- Core hierarchy drawing ----------------

        private void drawHierarchy(Graphics2D g2, int w, int h) {
            int levels = names.length;
            int baseWidth = (int) (w * 0.58);
            int barHeight = 56;
            int verticalGap = 68;

            int totalHeight = (levels - 1) * verticalGap + barHeight;
            int yStart = h / 2 - totalHeight / 2;

            // Эхлээд center line + холбогч node-уудыг зурна
            int centerLineX = (int) (w * 0.47);
            g2.setColor(new Color(148, 163, 184, 140));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int topY = yStart + barHeight / 2;
            int bottomY = yStart + (levels - 1) * verticalGap + barHeight / 2;
            g2.drawLine(centerLineX, topY, centerLineX, bottomY);

            for (int i = 0; i < levels; i++) {
                int y = yStart + i * verticalGap + barHeight / 2;
                int radius = 8;
                if (i == highlightIndex) {
                    g2.setColor(new Color(96, 165, 250, 220));
                    g2.fillOval(centerLineX - radius, y - radius, radius * 2, radius * 2);
                    g2.setColor(new Color(15, 23, 42, 220));
                    g2.fillOval(centerLineX - 3, y - 3, 6, 6);
                } else {
                    g2.setColor(new Color(148, 163, 184, 190));
                    g2.fillOval(centerLineX - 5, y - 5, 10, 10);
                }
            }

            // Дараа нь картуудыг зурна
            for (int i = 0; i < levels; i++) {
                float ratio = i / (float) (levels - 1); // 0..1 (дээрээс доош)
                int rectW = baseWidth - (int) (ratio * 120); // доошлох тусам бага өргөнтэй

                int x = centerLineX - rectW / 2;
                int y = yStart + i * verticalGap;

                Color c1 = new Color(59, 130, 246);
                Color c2 = new Color(96, 165, 250);
                Color blended = blend(c1, c2, ratio);

                // Highlight glow (анимацтай)
                if (i == highlightIndex) {
                    float glowAlpha = 0.35f + 0.15f * (float) Math.sin(phase * 1.5);
                    g2.setColor(new Color(96, 165, 250, (int) (255 * glowAlpha)));
                    g2.fillRoundRect(x - 14, y - 10, rectW + 28, barHeight + 20, 30, 30);
                }

                // Glass card background
                RoundRectangle2D.Double rr = new RoundRectangle2D.Double(
                        x, y, rectW, barHeight, 22, 22);

                g2.setPaint(new GradientPaint(
                        x, y,
                        new Color(blended.getRed(), blended.getGreen(), blended.getBlue(), 230),
                        x + rectW, y + barHeight,
                        new Color(15, 23, 42, 210)));
                g2.fill(rr);

                // Border
                g2.setColor(new Color(226, 232, 240, 220));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(rr);

                // Дотор талын нарийн border (glass effect)
                g2.setColor(new Color(15, 23, 42, 140));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(x + 5, y + 5, rectW - 10, barHeight - 10, 18, 18);

                // Icon area
                Rectangle iconArea = new Rectangle(
                        x + 12,
                        y + 8,
                        48,
                        barHeight - 16);
                drawIcon(g2, iconArea, i);

                // Нэр
                g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
                g2.setColor(new Color(248, 250, 252));
                int textX = iconArea.x + iconArea.width + 10;
                int textY = y + 23;
                g2.drawString(names[i], textX, textY);

                // Богино тайлбар
                g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
                g2.setColor(new Color(226, 232, 240, 215));
                drawMultiLineText(g2, info[i], textX, y + 25, rectW - (textX - x) - 16, 16);

                // Жижиг tag (hurdan / udaан байдлыг богино тэмдэглэл)
                g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
                String shortHint;
                if (i == 0)
                    shortHint = "Latency: хамгийн бага";
                else if (i == levels - 1)
                    shortHint = "Latency: хамгийн их";
                else if (i == 1)
                    shortHint = "Latency: маш бага";
                else if (i == 2)
                    shortHint = "Latency: дунд зэрэг";
                else
                    shortHint = "Latency: их";

                FontMetrics fm = g2.getFontMetrics();
                int hintW = fm.stringWidth(shortHint) + 12;
                int hintX = x + rectW - hintW - 10;
                int hintY = y + barHeight - 8;

                g2.setColor(new Color(15, 23, 42, 200));
                g2.fillRoundRect(hintX, hintY - 12, hintW, 18, 10, 10);
                g2.setColor(new Color(248, 250, 252, 230));
                g2.drawString(shortHint, hintX + 6, hintY + 1);
            }
        }

        /** Нэг мөрийг хэдэн мөр болгож багтаах helper */
        private void drawMultiLineText(Graphics2D g2, String text, int x, int startY, int maxWidth, int lineHeight) {
            FontMetrics fm = g2.getFontMetrics();
            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();
            int y = startY + lineHeight;

            for (String w : words) {
                String test = line.length() == 0 ? w : line + " " + w;
                if (fm.stringWidth(test) > maxWidth) {
                    g2.drawString(line.toString(), x, y);
                    line = new StringBuilder(w);
                    y += lineHeight;
                } else {
                    line = new StringBuilder(test);
                }
            }
            if (!line.isEmpty()) {
                g2.drawString(line.toString(), x, y);
            }
        }

        // ---------------- Side bars (Speed / Capacity) ----------------

        /** Баруун талд хурдны gradient bar (дээр хурдан, доор удаан) */
        private void drawSpeedBar(Graphics2D g2, int w, int h) {
            int barW = 22;
            int barH = (int) (h * 0.55);
            int x = (int) (w * 0.86);
            int y = h / 2 - barH / 2;

            // Бар
            g2.setPaint(new GradientPaint(
                    0, y,
                    new Color(74, 222, 128, 230),
                    0, y + barH,
                    new Color(248, 113, 113, 220)));
            g2.fillRoundRect(x, y, barW, barH, 16, 16);

            g2.setColor(new Color(15, 23, 42, 220));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRoundRect(x, y, barW, barH, 16, 16);

            // Дээр/доод label
            g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
            g2.setColor(new Color(226, 232, 240));
            String fast = "Хурд";
            String slow = "Удаан";
            FontMetrics fm = g2.getFontMetrics();

            int fastX = x - fm.stringWidth(fast) - 8;
            int fastY = y + fm.getAscent();
            g2.drawString(fast, fastX, fastY);

            int slowX = x - fm.stringWidth(slow) - 8;
            int slowY = y + barH;
            g2.drawString(slow, slowX, slowY);
        }

        /**
         * Зүүн талд багтаамжийн bar (дээр бага, доор их) – bar + текстийг хамтад нь
         * зүүн тийш
         */
        private void drawCapacityBar(Graphics2D g2, int w, int h) {
            int barW = 22;
            int barH = (int) (h * 0.55);

            // ⬇ Энд x-ийг 0.10 → 0.04 болгож бүр илүү зүүн тал руу шилжүүлсэн
            int x = (int) (w * 0.04);
            int y = h / 2 - barH / 2;

            g2.setPaint(new GradientPaint(
                    0, y,
                    new Color(251, 191, 36, 230),
                    0, y + barH,
                    new Color(56, 189, 248, 220)));
            g2.fillRoundRect(x, y, barW, barH, 16, 16);

            g2.setColor(new Color(15, 23, 42, 220));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRoundRect(x, y, barW, barH, 16, 16);

            g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
            g2.setColor(new Color(226, 232, 240));
            String low = "Багтаамж бага";
            String high = "Багтаамж их";
            FontMetrics fm = g2.getFontMetrics();

            int lowX = x + barW + 10;
            int lowY = y + fm.getAscent();
            g2.drawString(low, lowX, lowY);

            int highX = x + barW + 10;
            int highY = y + barH;
            g2.drawString(high, highX, highY);
        }

        // ---------------- Icon-ууд ----------------

        private void drawIcon(Graphics2D g2, Rectangle area, int levelIndex) {
            switch (levelIndex) {
                case 0 -> drawRegisterIcon(g2, area);
                case 1 -> drawCacheIcon(g2, area);
                case 2 -> drawRamIcon(g2, area);
                case 3 -> drawDiskIcon(g2, area);
                case 4 -> drawCloudIcon(g2, area);
            }
        }

        /** Регистр – CPU core-ийн жижиг блок + register strip */
        private void drawRegisterIcon(Graphics2D g2, Rectangle a) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int x = a.x;
            int y = a.y + 4;
            int w = a.width - 6;
            int h = a.height - 10;

            // CPU block
            g2.setColor(new Color(59, 130, 246));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(new Color(219, 234, 254));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            // Registers
            g2.setColor(new Color(15, 23, 42, 230));
            int regW = 6;
            int regH = h - 10;
            int rx = x + 4;
            int ry = y + 5;
            for (int i = 0; i < 4; i++) {
                g2.fillRoundRect(rx + i * (regW + 3), ry, regW, regH, 3, 3);
            }
        }

        /** Кэш – ring хэлбэртэй хурдны icon */
        private void drawCacheIcon(Graphics2D g2, Rectangle a) {
            int cx = a.x + a.width / 2;
            int cy = a.y + a.height / 2;

            int r = Math.min(a.width, a.height) - 6;

            g2.setColor(new Color(129, 140, 248, 220));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - r / 2, cy - r / 2, r, r);

            g2.setColor(new Color(191, 219, 254, 230));
            g2.drawOval(cx - r / 2 + 3, cy - r / 2 + 3, r - 6, r - 6);

            // Small speed arc
            g2.setColor(new Color(248, 250, 252, 230));
            g2.drawArc(cx - r / 2, cy - r / 2, r, r, 210, 70);
        }

        /** RAM – модуль */
        private void drawRamIcon(Graphics2D g2, Rectangle a) {
            int x = a.x;
            int y = a.y + 4;
            int w = a.width - 6;
            int h = a.height - 10;

            g2.setColor(new Color(34, 197, 94));
            g2.fillRoundRect(x, y, w, h, 8, 8);
            g2.setColor(new Color(220, 252, 231));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(x, y, w, h, 8, 8);

            g2.setColor(new Color(15, 23, 42, 230));
            int chipW = 8;
            int chipH = h - 10;
            int cx = x + 4;
            int cy = y + 5;
            for (int i = 0; i < 3; i++) {
                g2.fillRoundRect(cx + i * (chipW + 4), cy, chipW, chipH, 3, 3);
            }
        }

        /** SSD/HDD – диск */
        private void drawDiskIcon(Graphics2D g2, Rectangle a) {
            int cx = a.x + a.width / 2;
            int cy = a.y + a.height / 2 + 2;

            int r = Math.min(a.width, a.height) - 10;
            g2.setColor(new Color(148, 163, 184, 230));
            g2.fillOval(cx - r / 2, cy - r / 2, r, r);
            g2.setColor(new Color(226, 232, 240, 230));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawOval(cx - r / 2, cy - r / 2, r, r);

            g2.setColor(new Color(30, 64, 175));
            g2.fillOval(cx - 4, cy - 4, 8, 8);
        }

        /** Cloud / Network – үүл + network base */
        private void drawCloudIcon(Graphics2D g2, Rectangle a) {
            int x = a.x + 4;
            int y = a.y + 8;
            int w = a.width - 8;
            int h = a.height - 12;

            g2.setColor(new Color(191, 219, 254, 230));
            g2.fillOval(x + 4, y + h / 3, w / 2, h / 2);
            g2.fillOval(x + 10, y, w / 2, h / 2);
            g2.fillOval(x + w / 3, y + 6, w / 2, h / 2);

            g2.fillRoundRect(x + 6, y + h / 2 + 2, w - 12, h / 3, 10, 10);

            g2.setColor(new Color(30, 64, 175, 230));
            g2.setStroke(new BasicStroke(1.3f));
            int baseY = y + h - 4;
            g2.drawLine(x + 8, baseY, x + w - 8, baseY);
            g2.drawLine(x + 12, baseY - 4, x + 12, baseY);
            g2.drawLine(x + w - 12, baseY - 4, x + w - 12, baseY);
        }

        // ---------------- Binary background ----------------

        private void initDots(int w, int h) {
            dots.clear();
            for (int i = 0; i < 32; i++) {
                BinaryDot d = new BinaryDot();
                d.x = 50 + rnd.nextInt(Math.max(1, w - 100));
                d.y = 80 + rnd.nextInt(Math.max(1, h - 160));
                d.offset = rnd.nextFloat() * (float) (Math.PI * 2);
                d.bit = rnd.nextBoolean() ? "0" : "1";
                dots.add(d);
            }
        }

        private void drawBinaryDots(Graphics2D g2, int w, int h) {
            phase += 0.03f;
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));

            for (int i = 0; i < dots.size(); i++) {
                BinaryDot d = dots.get(i);
                float animY = (float) (d.y + Math.sin(phase + d.offset + i * 0.1) * 8);

                int alpha = 50 + (int) (40 * Math.sin(phase + d.offset + i * 0.25));
                alpha = Math.max(15, Math.min(110, alpha));

                g2.setColor(new Color(200, 240, 255, alpha));
                g2.drawString(d.bit, (int) d.x, (int) animY);
            }
        }

        private void paintCenterGlow(Graphics2D g2, int w, int h) {
            int gw = (int) (w * 0.70);
            int gh = (int) (h * 0.75);
            int gx = (w - gw) / 2;
            int gy = (h - gh) / 2 + 10;

            g2.setPaint(new RadialGradientPaint(
                    new Point(w / 2, h / 2),
                    gw,
                    new float[] { 0f, 1f },
                    new Color[] {
                            new Color(37, 99, 235, 140),
                            new Color(15, 23, 42, 0)
                    }));
            g2.fillRoundRect(gx, gy, gw, gh, 60, 60);
        }

        private Color blend(Color a, Color b, float t) {
            t = Math.max(0, Math.min(1, t));
            int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
            int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
            int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
            return new Color(r, g, bl);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tick++;
            // 2 орчим секунд тутамд highlight шат солих
            if (tick % 60 == 0) {
                highlightIndex = (highlightIndex + 1) % names.length;
            }
            repaint();
        }
    }
}
