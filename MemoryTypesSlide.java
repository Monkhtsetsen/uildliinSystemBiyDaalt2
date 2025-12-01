import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryTypesSlide extends GradientSlide {

    // Binary чимэглэлд ашиглах жижиг цэгүүд
    private static class BinaryDot {
        float x, y;
        String bit; // "0" эсвэл "1"
        float offset; // анимэйшнд бага зөрүү
    }

    private final List<BinaryDot> dots = new ArrayList<>();
    private final Random rnd = new Random();
    private float phase = 0f;

    public MemoryTypesSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Санах ойн төрөл"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 24, 24));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));

        grid.add(new TypeCard(
                "RAM",
                "Шуурхай, дотоод санах ой.\nТэжээл тасарвал агшин зуур алга болно.",
                "ram"));
        grid.add(new TypeCard(
                "ROM",
                "Үйлдвэрлэлийн үед бичсэн өгөгдөлтэй.\nГол нь зөвхөн унших зориулалттай, тэжээл тасарсан ч хадгалагдана.",
                "rom"));
        grid.add(new TypeCard(
                "Кэш санах ой",
                "CPU-д хамгийн ойр бага хэмжээтэй, хурдан санах ой.\nСаяхан ашигласан заавар, өгөгдлийг хадгалж хурдыг нэмэгдүүлнэ.",
                "cache"));
        grid.add(new TypeCard(
                "Виртуал санах ой",
                "Дискний хэсгийг RAM-ийн өргөтгөл мэт ашиглаж,\nих хэмжээний програмыг ажиллуулах боломж олгоно.",
                "virtual"));

        add(grid, BorderLayout.CENTER);

        // Жижиг анимэйшн: binary 0/1 хөдөлгөх
        Timer timer = new Timer(40, e -> {
            phase += 0.04f;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // GradientSlide background

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        // BinaryDots-oo анх удаа үүсгэнэ
        if (dots.isEmpty()) {
            initDots(w, h);
        }

        // Зөөлөн төв glow
        paintCenterGlow(g2, w, h);

        // Binary 0/1 чимэглэл
        drawBinaryBackground(g2, w, h);

        g2.dispose();
    }

    private void initDots(int w, int h) {
        dots.clear();
        for (int i = 0; i < 26; i++) {
            BinaryDot d = new BinaryDot();
            d.x = 40 + rnd.nextInt(Math.max(1, w - 80));
            d.y = 80 + rnd.nextInt(Math.max(1, h - 160));
            d.bit = rnd.nextBoolean() ? "0" : "1";
            d.offset = rnd.nextFloat() * (float) (Math.PI * 2);
            dots.add(d);
        }
    }

    private void paintCenterGlow(Graphics2D g2, int w, int h) {
        int gw = (int) (w * 0.7);
        int gh = (int) (h * 0.7);
        int gx = (w - gw) / 2;
        int gy = (h - gh) / 2;

        g2.setPaint(new RadialGradientPaint(
                new Point(w / 2, h / 2),
                gw,
                new float[] { 0f, 1f },
                new Color[] {
                        new Color(80, 150, 255, 120),
                        new Color(15, 23, 42, 0)
                }));
        g2.fillRoundRect(gx, gy, gw, gh, 80, 80);
    }

    private void drawBinaryBackground(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Monospaced", Font.PLAIN, 13));

        for (int i = 0; i < dots.size(); i++) {
            BinaryDot d = dots.get(i);
            float animY = (float) (d.y + Math.sin(phase + d.offset) * 8);

            int alpha = 60 + (int) (40 * Math.sin(phase + d.offset + i * 0.2));
            alpha = Math.max(20, Math.min(120, alpha));

            g2.setColor(new Color(200, 240, 255, alpha));
            g2.drawString(d.bit, (int) d.x, (int) animY);
        }
    }

    // =========================================
    // Дотоод картын класс: RAM / ROM / Cache / Virtual
    // =========================================
    private static class TypeCard extends JPanel {

        private final String title;
        private final String body;
        private final String kind; // "ram", "rom", "cache", "virtual"

        TypeCard(String title, String body, String kind) {
            this.title = title;
            this.body = body;
            this.kind = kind;

            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

            JLabel t = new JLabel(title);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 22));

            JTextArea txt = new JTextArea(body);
            txt.setEditable(false);
            txt.setOpaque(false);
            txt.setForeground(new Color(215, 225, 255));
            txt.setFont(new Font("SansSerif", Font.PLAIN, 15));
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);
            txt.setBorder(null);

            add(t, BorderLayout.NORTH);
            add(txt, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background gradient card
            RoundRectangle2D.Double rr = new RoundRectangle2D.Double(
                    0, 0, w, h, 24, 24);

            Color c1 = new Color(30, 64, 175, 220);
            Color c2 = new Color(56, 189, 248, 180);
            g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
            g2.fill(rr);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(191, 219, 254, 200));
            g2.draw(rr);

            // Дотор талын нимгэн border
            g2.setColor(new Color(15, 23, 42, 130));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(6, 6, w - 12, h - 12, 18, 18);

            // Icon-д зориулсан хэсэг
            Rectangle iconArea = new Rectangle(
                    w - 90, 10,
                    70, 60);
            drawIcon(g2, iconArea, kind);

            g2.dispose();

            // Text / label-үүдээ хамгийн сүүлд зурна
            super.paintComponent(g);
        }

        /** Санах ойн төрлөөс хамааран icon зурах */
        private void drawIcon(Graphics2D g2, Rectangle area, String kind) {
            switch (kind) {
                case "ram" -> drawRamIcon(g2, area);
                case "rom" -> drawRomIcon(g2, area);
                case "cache" -> drawCacheIcon(g2, area);
                case "virtual" -> drawVirtualIcon(g2, area);
                default -> {
                }
            }
        }

        /** RAM – санах ойн модуль (stick) */
        private void drawRamIcon(Graphics2D g2, Rectangle a) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Ногоон модуль
            g2.setColor(new Color(34, 197, 94));
            g2.fillRoundRect(a.x, a.y + 10, a.width - 10, a.height - 18, 12, 12);

            g2.setColor(new Color(220, 252, 231));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(a.x, a.y + 10, a.width - 10, a.height - 18, 12, 12);

            // Чипүүд
            g2.setColor(new Color(15, 23, 42, 220));
            int chipW = 10;
            int chipH = 12;
            int cx = a.x + 6;
            int cy = a.y + 16;
            for (int i = 0; i < 4; i++) {
                g2.fillRoundRect(cx + i * (chipW + 4), cy, chipW, chipH, 4, 4);
            }

            // Доод талын пинууд
            g2.setColor(new Color(163, 230, 53));
            int pins = 6;
            int pinW = 4;
            int pinY = a.y + a.height - 11;
            int startX = a.x + 6;
            for (int i = 0; i < pins; i++) {
                g2.fillRect(startX + i * (pinW + 3), pinY, pinW, 4);
            }
        }

        /** ROM – chip хэлбэртэй, pins-тэй */
        private void drawRomIcon(Graphics2D g2, Rectangle a) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Хар chip
            g2.setColor(new Color(15, 23, 42, 230));
            g2.fillRoundRect(a.x + 6, a.y + 12, a.width - 16, a.height - 20, 10, 10);

            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(a.x + 6, a.y + 12, a.width - 16, a.height - 20, 10, 10);

            // ROM бичиг
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            String label = "ROM";
            FontMetrics fm = g2.getFontMetrics();
            int tx = a.x + 6 + (a.width - 16 - fm.stringWidth(label)) / 2;
            int ty = a.y + 12 + (a.height - 20 + fm.getAscent()) / 2 - 2;
            g2.setColor(new Color(248, 250, 252));
            g2.drawString(label, tx, ty);

            // Pins
            g2.setColor(new Color(148, 163, 184));
            int pinLen = 6;
            int pinGap = 5;
            int topY = a.y + 10;
            int bottomY = a.y + a.height - 6;

            for (int i = 0; i < 4; i++) {
                int px = a.x + 10 + i * pinGap;
                g2.drawLine(px, topY, px, topY - pinLen);
                g2.drawLine(px, bottomY, px, bottomY + pinLen);
            }
        }

        /** Cache – хурд, lightning / давхар шугам */
        private void drawCacheIcon(Graphics2D g2, Rectangle a) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Дугуй cache ring
            int r = Math.min(a.width, a.height) - 18;
            int cx = a.x + a.width / 2;
            int cy = a.y + a.height / 2 + 4;

            g2.setColor(new Color(59, 130, 246, 200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - r / 2, cy - r / 2, r, r);

            g2.setColor(new Color(191, 219, 254, 200));
            g2.drawOval(cx - r / 2 + 4, cy - r / 2 + 4, r - 8, r - 8);

            // Lightning bolt
            Polygon bolt = new Polygon();
            bolt.addPoint(cx, cy - 8);
            bolt.addPoint(cx - 4, cy + 0);
            bolt.addPoint(cx + 0, cy + 0);
            bolt.addPoint(cx - 6, cy + 12);
            bolt.addPoint(cx + 6, cy + 2);
            bolt.addPoint(cx + 0, cy + 2);
            bolt.addPoint(cx + 4, cy - 8);

            g2.setColor(new Color(248, 250, 252));
            g2.fillPolygon(bolt);
        }

        /** Virtual memory – RAM + Disk давхцсан дүрс, хооронд нь сум */
        private void drawVirtualIcon(Graphics2D g2, Rectangle a) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Дээд талд RAM блок
            int ramW = a.width - 26;
            int ramH = 16;
            int ramX = a.x + 10;
            int ramY = a.y + 8;

            g2.setColor(new Color(74, 222, 128));
            g2.fillRoundRect(ramX, ramY, ramW, ramH, 8, 8);
            g2.setColor(new Color(220, 252, 231));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(ramX, ramY, ramW, ramH, 8, 8);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.setColor(new Color(15, 23, 42));
            g2.drawString("RAM", ramX + 4, ramY + 11);

            // Доор нь Disk блок
            int diskW = ramW;
            int diskH = 16;
            int diskX = ramX;
            int diskY = a.y + a.height - diskH - 10;

            g2.setColor(new Color(148, 163, 184));
            g2.fillRoundRect(diskX, diskY, diskW, diskH, 8, 8);
            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(diskX, diskY, diskW, diskH, 8, 8);

            g2.setColor(new Color(15, 23, 42));
            g2.drawString("Disk", diskX + 4, diskY + 11);

            // RAM → Disk сум (виртуал санах ой)
            int sx1 = ramX + ramW / 2;
            int sy1 = ramY + ramH + 4;
            int sx2 = diskX + diskW / 2;
            int sy2 = diskY - 4;

            g2.setColor(new Color(96, 165, 250));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawLine(sx1, sy1, sx2, sy2);

            Polygon arrow = new Polygon();
            arrow.addPoint(sx2, sy2);
            arrow.addPoint(sx2 - 4, sy2 - 4);
            arrow.addPoint(sx2 + 4, sy2 - 4);
            g2.fillPolygon(arrow);
        }
    }
}
