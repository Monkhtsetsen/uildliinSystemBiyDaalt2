import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverviewSlide extends GradientSlide {

    public OverviewSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Санах ойн удирдлага гэж юу вэ?"), BorderLayout.NORTH);

        SimulationPanel center = new SimulationPanel();
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }

    /**
     * CPU → Cache → MMU(+TLB) → RAM → Disk урсгалыг жижиг бөмбөлөгөөр хөдөлгөж
     * харуулдаг панел.
     */
    private static class SimulationPanel extends JPanel implements ActionListener {

        private static final int TOKEN_COUNT = 3;
        private final double[] progress = new double[TOKEN_COUNT];

        // 0 = CPU, 1 = MMU, 2 = RAM, 3 = Disk (тайлбар хэсэгт ашиглана)
        private int currentStage = 0;
        private int tick = 0;

        private final Timer timer;

        // ===== Cute / decoration animation state =====
        private static class BinaryParticle {
            float x, y;
            float speed;
            float alpha;
            String bit; // "0" or "1"
        }

        private final List<BinaryParticle> particles = new ArrayList<>();
        private final Random rnd = new Random();

        private float fairyPhase = 0f;
        private float wavePhase = 0f;
        private float ramPulse = 0f;

        SimulationPanel() {
            progress[0] = 0.0;
            progress[1] = -0.35;
            progress[2] = -0.70;

            timer = new Timer(33, this);
            timer.start();

            setToolTipText("CPU → Cache → MMU (TLB) → RAM → Disk урсгалыг харуулсан симуляц");
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

            // particles анх удаа зурж байхдаа init хийх
            if (particles.isEmpty()) {
                initParticles(w, h);
            }

            int boxW = 140;
            int boxH = 60;

            int cy = h / 2;
            int cx = w / 2;

            // ===== ТЭНЦҮҮ ЗАЙТАЙ байрлал =====
            int gap = 80;
            int totalWidth = 4 * boxW + 3 * gap;
            int startX = cx - totalWidth / 2;

            Rectangle cpu = new Rectangle(startX, cy - boxH / 2, boxW, boxH);
            Rectangle mmu = new Rectangle(startX + (boxW + gap), cy - boxH / 2, boxW, boxH);
            Rectangle ram = new Rectangle(startX + 2 * (boxW + gap), cy - boxH / 2, boxW, boxH);
            Rectangle disk = new Rectangle(startX + 3 * (boxW + gap), cy - boxH / 2, boxW, boxH);

            // background capsule
            paintSoftBackground(g2, cpu, disk);

            // RAM-ийн glow-ыг хайрцагнаас өмнө зурах (доороос нь гэрэлтэж байгаа мэт)
            drawRamGlow(g2, ram);

            Point[] centers = new Point[] {
                    centerOf(cpu),
                    centerOf(mmu),
                    centerOf(ram),
                    centerOf(disk)
            };

            drawPipeArrow(g2, centers[0], centers[1]);
            drawPipeArrow(g2, centers[1], centers[2]);
            drawPipeArrow(g2, centers[2], centers[3]);

            paintHighlightedBox(g2, cpu, mmu, ram, disk);

            drawBox(g2, cpu, "CPU");
            drawBox(g2, mmu, "MMU");
            drawBox(g2, ram, "RAM");
            drawBox(g2, disk, "Disk");

            // ==== ЖИНХЭНЭ САНАХ ОЙН БҮТЭЦИЙН НЭМЭЛТ ====
            paintCpuCache(g2, cpu);
            paintMmuTlb(g2, mmu); // <- энд TLB текстийг хоёр мөр болгосон
            paintRamFrames(g2, ram);
            paintDiskPages(g2, disk);

            // Cute memory fairy (RAM-ийн хажууд)
            drawMemoryFairy(g2, ram);

            // CPU waveform animation
            drawCpuWave(g2, cpu);

            // Урсаж буй "процесс" бөмбөлгүүд
            paintRequests(g2, centers);

            // Доод талын текстэн тайлбар (mongol)
            paintLegend(g2, w, h);

            // Floating binary particles + sparkles (дээрээс нь)
            drawSparkles(g2, w, h);
            drawParticles(g2);
            updateParticles(w, h);

            g2.dispose();
        }

        private void paintSoftBackground(Graphics2D g2, Rectangle left, Rectangle right) {
            int padding = 40;
            int x = left.x - padding;
            int y = left.y - padding;
            int w = (right.x + right.width) - left.x + 2 * padding;
            int h = left.height + 2 * padding;

            g2.setColor(new Color(15, 23, 42, 160));
            g2.fillRoundRect(x, y, w, h, 40, 40);

            g2.setColor(new Color(88, 101, 242, 80));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, w, h, 40, 40);
        }

        private Point centerOf(Rectangle r) {
            return new Point(r.x + r.width / 2, r.y + r.height / 2);
        }

        private void drawBox(Graphics2D g2, Rectangle r, String text) {
            Color base = new Color(60, 80, 220, 210);
            Color border = new Color(230, 235, 255);

            RoundRectangle2D.Double rr = new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 18, 18);

            g2.setColor(base);
            g2.fill(rr);

            g2.setStroke(new BasicStroke(2.2f));
            g2.setColor(border);
            g2.draw(rr);

            g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
            FontMetrics fm = g2.getFontMetrics();
            int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
            int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
            g2.setColor(new Color(240, 244, 255));
            g2.drawString(text, tx, ty);
        }

        private void drawPipeArrow(Graphics2D g2, Point from, Point to) {
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(180, 195, 255));

            int y = from.y;
            g2.drawLine(from.x + 40, y, to.x - 40, y);

            int x2 = to.x - 40;
            int y2 = y;
            int size = 8;
            Polygon arrow = new Polygon();
            arrow.addPoint(x2, y2);
            arrow.addPoint(x2 - size, y2 - size / 2);
            arrow.addPoint(x2 - size, y2 + size / 2);
            g2.fill(arrow);
        }

        private void paintHighlightedBox(Graphics2D g2,
                Rectangle cpu, Rectangle mmu,
                Rectangle ram, Rectangle disk) {
            Rectangle target;
            switch (currentStage) {
                case 0 -> target = cpu;
                case 1 -> target = mmu;
                case 2 -> target = ram;
                default -> target = disk;
            }

            int glowPad = 8;
            RoundRectangle2D.Double rr = new RoundRectangle2D.Double(
                    target.x - glowPad, target.y - glowPad,
                    target.width + glowPad * 2, target.height + glowPad * 2,
                    28, 28);

            g2.setColor(new Color(129, 140, 248, 80));
            g2.fill(rr);
        }

        // ============= Жижиг нэмэлт бүтэцүүд =============

        /** CPU дээгүүр L1/L2 cache жижиг хайрцаг */
        private void paintCpuCache(Graphics2D g2, Rectangle cpu) {
            int cw = cpu.width - 40;
            int ch = 22;
            int cx = cpu.x + 20;
            int cy = cpu.y - ch - 10;

            RoundRectangle2D.Double box = new RoundRectangle2D.Double(cx, cy, cw, ch, 10, 10);

            g2.setColor(new Color(34, 197, 94, 200));
            g2.fill(box);
            g2.setColor(new Color(220, 252, 231));
            g2.setStroke(new BasicStroke(1.6f));
            g2.draw(box);

            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.setColor(new Color(240, 252, 255));
            String text = "L1 / L2 кэш";
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx + (cw - fm.stringWidth(text)) / 2;
            int ty = cy + (ch + fm.getAscent()) / 2 - 3;
            g2.drawString(text, tx, ty);
        }

        /** MMU дээгүүр TLB жижиг хайрцаг (хоёр мөр тексттэй) */
        private void paintMmuTlb(Graphics2D g2, Rectangle mmu) {
            int tw = mmu.width - 40;
            int th = 26; // жаахан өндөр болгосон
            int tx = mmu.x + 20;
            int ty = mmu.y - th - 10;

            RoundRectangle2D.Double box = new RoundRectangle2D.Double(tx, ty, tw, th, 10, 10);

            g2.setColor(new Color(234, 179, 8, 210));
            g2.fill(box);
            g2.setColor(new Color(254, 249, 195));
            g2.setStroke(new BasicStroke(1.6f));
            g2.draw(box);

            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.setColor(new Color(40, 40, 20));

            String line1 = "TLB";
            String line2 = "хаягийн кэш";

            FontMetrics fm = g2.getFontMetrics();
            int l1W = fm.stringWidth(line1);
            int l2W = fm.stringWidth(line2);

            int centerX = tx + tw / 2;
            int baseY = ty + (th - fm.getHeight()) / 2 + fm.getAscent() / 2;

            g2.drawString(line1, centerX - l1W / 2, baseY - 4);
            g2.drawString(line2, centerX - l2W / 2, baseY + 8);
        }

        /** RAM хайрцган дотор хэдэн frame-г дүрслэх */
        private void paintRamFrames(Graphics2D g2, Rectangle ram) {
            int frameCount = 4;
            int margin = 10;
            int fw = ram.width - margin * 2;
            int fh = ram.height - margin * 2;
            int frameH = fh / frameCount;

            g2.setStroke(new BasicStroke(1.2f));
            for (int i = 0; i < frameCount; i++) {
                int y = ram.y + margin + i * frameH;
                RoundRectangle2D.Double r = new RoundRectangle2D.Double(
                        ram.x + margin, y, fw, frameH - 4, 8, 8);
                g2.setColor(new Color(148, 163, 184, 180));
                g2.draw(r);
            }

            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.setColor(new Color(226, 232, 240));
            g2.drawString("Frames (физик блок)", ram.x + 8, ram.y + 14);
        }

        /** Disk-ийн хажууд хуудасны стек (page file / swap) */
        private void paintDiskPages(Graphics2D g2, Rectangle disk) {
            int px = disk.x + disk.width + 20;
            int py = disk.y + 10;
            int pw = 60;
            int ph = 26;

            for (int i = 0; i < 4; i++) {
                int offset = i * 6;
                RoundRectangle2D.Double page = new RoundRectangle2D.Double(
                        px + offset, py + offset, pw, ph, 6, 6);
                g2.setColor(new Color(148, 163, 184, 160));
                g2.fill(page);
                g2.setColor(new Color(226, 232, 240, 200));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(page);
            }

            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.setColor(new Color(226, 232, 240));
            g2.drawString("Pages (swap)", px + 2, py + ph + 26);
        }

        /** CPU→MMU→RAM→Disk дагуу хөдөлж буй бөмбөлгүүд */
        private void paintRequests(Graphics2D g2, Point[] centers) {
            int segments = centers.length - 1; // 3 сегмент

            for (int i = 0; i < TOKEN_COUNT; i++) {
                double p = progress[i];
                if (p < 0 || p > 1.05)
                    continue;

                double scaled = p * segments; // 0..3
                int segIndex = (int) Math.floor(scaled);
                if (segIndex >= segments)
                    segIndex = segments - 1;
                double t = scaled - segIndex; // 0..1

                Point a = centers[segIndex];
                Point b = centers[segIndex + 1];

                int x = (int) lerp(a.x, b.x, t);
                int y = (int) lerp(a.y, b.y, t);

                int radius = 18;
                int r = radius / 2;

                g2.setColor(new Color(96, 165, 250, 120));
                g2.fillOval(x - r - 4, y - r - 4, radius + 8, radius + 8);

                g2.setColor(new Color(56, 189, 248));
                g2.fillOval(x - r, y - r, radius, radius);

                g2.setColor(new Color(15, 23, 42));
                g2.setFont(getFont().deriveFont(Font.BOLD, 11f));
                String label = "P" + (i + 1);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x - fm.stringWidth(label) / 2;
                int ty = y + fm.getAscent() / 2 - 2;
                g2.drawString(label, tx, ty);
            }
        }

        /** Доод талын текстэн тайлбар (илүү ойлгомжтой монгол тайлбар) */
        private void paintLegend(Graphics2D g2, int w, int h) {
            String[] lines = new String[] {
                    "1. CPU: програм зааврыг гүйцэтгэж, виртуал (логик) хаяг үүсгэнэ. Эхлээд L1/L2 кэшээс уншиж оролддог.",
                    "2. MMU + TLB: виртуал хаягийг физик хаяг руу хөрвүүлнэ. TLB-д байвал хурдан, байхгүй бол page table-ийг RAM-ээс уншина.",
                    "3. RAM: тухайн хуудсанд харгалзах frame-д өгөгдөл байгаа бол шууд ашиглана, байхгүй бол page fault үүснэ.",
                    "4. Disk (swap/page file): page fault гарсан үед ОС хэрэгтэй хуудсыг дискнээс уншиж RAM-ийн нэг frame-д байрлуулна."
            };

            int baseY = h - 120;
            int x = 60;

            g2.setFont(getFont().deriveFont(Font.PLAIN, 15f));

            for (int i = 0; i < lines.length; i++) {
                boolean active = (i == currentStage);
                if (active) {
                    g2.setColor(new Color(191, 219, 254));
                    int padX = 10;
                    int padY = 4;
                    FontMetrics fm = g2.getFontMetrics();
                    int textW = fm.stringWidth(lines[i]);
                    int textH = fm.getHeight();
                    g2.fillRoundRect(
                            x - padX,
                            baseY + i * 24 - textH + padY,
                            textW + padX * 2,
                            textH + padY,
                            14, 14);
                    g2.setColor(new Color(15, 23, 42));
                } else {
                    g2.setColor(new Color(148, 163, 184));
                }
                g2.drawString(lines[i], x, baseY + i * 24);
            }

            g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
            g2.setColor(new Color(226, 232, 240));
            g2.drawString("Санах ойн удирдлагын урсгал (ойлгомжтой загвар):", x, baseY - 18);
        }

        // ======== Cute extra drawings ========

        /** RAM-ийн эргэн тойронд pulse glow */
        private void drawRamGlow(Graphics2D g2, Rectangle ram) {
            ramPulse += 0.04f;
            float alpha = (float) (0.18 + Math.sin(ramPulse) * 0.12); // 0.06–0.30

            g2.setColor(new Color(120, 200, 255, (int) (alpha * 255)));
            g2.fillRoundRect(
                    ram.x - 10,
                    ram.y - 10,
                    ram.width + 20,
                    ram.height + 20,
                    30, 30);
        }

        /** RAM-ийн хажууд хөөрхөн "memory fairy" */
        private void drawMemoryFairy(Graphics2D g2, Rectangle ram) {
            fairyPhase += 0.05f;

            int fx = ram.x + ram.width + 30;
            int fy = ram.y + 20 + (int) (Math.sin(fairyPhase) * 10);

            // Glow
            g2.setColor(new Color(255, 255, 180, 140));
            g2.fillOval(fx - 12, fy - 12, 40, 40);

            // Body
            g2.setColor(new Color(255, 230, 140));
            g2.fillOval(fx, fy, 22, 22);

            // Eyes
            g2.setColor(new Color(90, 60, 0));
            g2.fillOval(fx + 6, fy + 6, 4, 4);
            g2.fillOval(fx + 14, fy + 6, 4, 4);

            // Wings
            g2.setColor(new Color(255, 255, 255, 160));
            g2.fillOval(fx - 8, fy + 2, 12, 18);
            g2.fillOval(fx + 20, fy + 2, 12, 18);
        }

        /** CPU-ийн доор waveform — CPU ажиллаж байгаа мэдрэмж */
        private void drawCpuWave(Graphics2D g2, Rectangle cpu) {
            wavePhase += 0.08f;

            int startX = cpu.x;
            int y = cpu.y + cpu.height + 15;
            int width = cpu.width;

            g2.setColor(new Color(160, 200, 255, 180));
            g2.setStroke(new BasicStroke(2f));

            for (int i = 0; i < width; i++) {
                int wx = startX + i;
                int wy = y + (int) (Math.sin((i * 0.15) + wavePhase) * 6);
                g2.drawLine(wx, wy, wx, wy);
            }
        }

        /** Sparkle одод */
        private void drawSparkles(Graphics2D g2, int w, int h) {
            g2.setColor(new Color(255, 255, 255, 90));
            for (int i = 0; i < 12; i++) {
                int x = rnd.nextInt(w);
                int y = rnd.nextInt(h);
                g2.fillOval(x, y, 2, 2);
            }
        }

        /** Binary particles: 0/1-үүд хөөрнө */
        private void initParticles(int w, int h) {
            particles.clear();
            for (int i = 0; i < 25; i++) {
                BinaryParticle p = new BinaryParticle();
                p.x = rnd.nextInt(w);
                p.y = rnd.nextInt(h);
                p.speed = 0.4f + rnd.nextFloat() * 1.2f;
                p.alpha = 0.25f + rnd.nextFloat() * 0.5f;
                p.bit = rnd.nextBoolean() ? "0" : "1";
                particles.add(p);
            }
        }

        private void updateParticles(int w, int h) {
            for (BinaryParticle p : particles) {
                p.y -= p.speed;
                if (p.y < -10) {
                    p.y = h + 10;
                    p.x = rnd.nextInt(w);
                }
            }
        }

        private void drawParticles(Graphics2D g2) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            for (BinaryParticle p : particles) {
                g2.setColor(new Color(200, 240, 255, (int) (255 * p.alpha)));
                g2.drawString(p.bit, (int) p.x, (int) p.y);
            }
        }

        private double lerp(double a, double b, double t) {
            return a + (b - a) * t;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            double speed = 0.007;
            for (int i = 0; i < TOKEN_COUNT; i++) {
                progress[i] += speed;
                if (progress[i] > 1.1) {
                    progress[i] = -0.4 - 0.2 * i;
                }
            }

            tick++;
            if (tick % 40 == 0) {
                currentStage = (currentStage + 1) % 4;
            }

            repaint();
        }
    }
}
