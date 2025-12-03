import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TitleSlide extends GradientSlide {

    // ===== Animated "memory blocks" =====
    private static class FloatingBlock {
        float x, y, w, h;
        float speed;
        float alpha;
    }

    private final List<FloatingBlock> blocks = new ArrayList<>();
    private final Random random = new Random();
    private float glowPhase = 0f;

    private JPanel contentBox; // to know where to draw glow behind

    public TitleSlide() {
        setOpaque(false);
        setDoubleBuffered(true);
        setLayout(new GridBagLayout());

        // ===== MAIN TITLE =====
        JLabel title = new JLabel("Санах ойн удирдлага");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 54));

        // ===== TAGLINE =====
        JLabel tagline = new JLabel("Үйлдлийн системийн санах ойн менежментийн үндсэн ойлголтууд");
        tagline.setForeground(new Color(210, 220, 255));
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 16));

        // Center align
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== META INFO PILL (bottom of box) =====
        JPanel metaPanel = new JPanel();
        metaPanel.setOpaque(false);
        metaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        metaPanel.add(createChip("Үйлдлийн системийн бие даалт 2"));
        metaPanel.add(createChip("2-р баг"));
        metaPanel.add(createChip("2025"));

        // ===== Content box (like a Canva slide card) =====
        contentBox = new JPanel();
        contentBox.setOpaque(false);
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));

        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(title);
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(tagline);
        contentBox.add(Box.createVerticalStrut(22));
        contentBox.add(metaPanel);
        contentBox.add(Box.createVerticalStrut(10));

        // Add main content to center with a tiny inset
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 40, 0, 40);
        add(contentBox, gbc);

        // Init animated blocks
        initBlocks();

        // Timer for animation
        Timer timer = new Timer(40, e -> {
            updateBlocks();
            glowPhase += 0.03f;
            repaint();
        });
        timer.start();
    }

    // Small rounded chips for meta info
    private JComponent createChip(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(230, 235, 255));
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel chip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Soft translucent pill
                Color c1 = new Color(255, 255, 255, 35);
                Color c2 = new Color(180, 200, 255, 35);
                g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
                g2.fillRoundRect(0, 0, w, h, h, h);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setLayout(new BorderLayout());
        chip.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        chip.add(label, BorderLayout.CENTER);
        return chip;
    }

    // ===== Animated blocks setup =====
    private void initBlocks() {
        blocks.clear();
        int count = 14;
        for (int i = 0; i < count; i++) {
            blocks.add(randomBlock());
        }
    }

    private FloatingBlock randomBlock() {
        FloatingBlock b = new FloatingBlock();
        int w = getWidth() > 0 ? getWidth() : 1280;
        int h = getHeight() > 0 ? getHeight() : 720;

        b.w = 40 + random.nextInt(40); // width 40–80
        b.h = 18 + random.nextInt(20); // height 18–38
        b.x = random.nextInt(w);
        b.y = h + random.nextInt(h); // start below screen
        b.speed = 0.5f + random.nextFloat() * 1.2f;
        b.alpha = 0.18f + random.nextFloat() * 0.25f;
        return b;
    }

    private void updateBlocks() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0)
            return;

        for (FloatingBlock b : blocks) {
            b.y -= b.speed;
            if (b.y + b.h < -50) {
                // respawn at bottom with new random x
                b.y = h + random.nextInt(h / 2);
                b.x = random.nextInt(w);
                b.speed = 0.5f + random.nextFloat() * 1.3f;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // GradientSlide background

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // ===== Soft glow behind content box =====
        if (contentBox != null) {
            Rectangle r = contentBox.getBounds();
            int glowW = (int) (r.width * 1.3);
            int glowH = (int) (r.height * 1.5);
            int gx = r.x + r.width / 2 - glowW / 2;
            int gy = r.y + r.height / 2 - glowH / 2;

            // glow pulses slightly with time
            float pulse = 0.15f + 0.07f * (float) Math.sin(glowPhase);
            // (unused glowColor variable removed)

            g2.setPaint(new RadialGradientPaint(
                    new Point(r.x + r.width / 2, r.y + r.height / 2),
                    glowW,
                    new float[] { 0f, 1f },
                    new Color[] {
                            new Color(120, 200, 255, (int) (120 + 60 * pulse)),
                            new Color(40, 40, 80, 0)
                    }));
            g2.fillRoundRect(gx, gy, glowW, glowH, 80, 80);
        }

        // ===== Animated floating memory blocks =====
        for (FloatingBlock b : blocks) {
            float a = b.alpha;
            Color c1 = new Color(180, 220, 255, (int) (255 * a));
            Color c2 = new Color(120, 170, 255, (int) (255 * (a * 0.8)));

            g2.setPaint(new GradientPaint(
                    (int) b.x, (int) b.y, c1,
                    (int) (b.x + b.w), (int) (b.y + b.h), c2));
            g2.fillRoundRect(Math.round(b.x), Math.round(b.y),
                    Math.round(b.w), Math.round(b.h), 16, 16);

            // small inner highlight line (like a RAM cell)
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, a + 0.1f));
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(new Color(240, 245, 255, 180));
            g2.drawLine(
                    Math.round(b.x + 6),
                    Math.round(b.y + b.h / 2f),
                    Math.round(b.x + b.w - 6),
                    Math.round(b.y + b.h / 2f));
        }

        // reset composite for next drawings
        g2.setComposite(AlphaComposite.SrcOver);

        // ===== Cute RAM sticker + bubble (left bottom-ish) =====
        drawCuteRamSticker(g2, w, h);

        // ===== Binary rain at top (just a subtle loop of 0/1) =====
        drawBinaryRain(g2, w, h);

        // ===== Subtle bottom "timeline" bar (like memory segments) =====
        int barY = (int) (h * 0.84);
        int barH = 6;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2.setPaint(new GradientPaint(
                0, barY,
                new Color(150, 190, 255),
                w, barY + barH,
                new Color(90, 130, 255)));
        g2.fillRoundRect((int) (w * 0.15), barY, (int) (w * 0.7), barH, barH, barH);

        // moving "pointer" on the bar (like current frame pointer)
        float t = (float) ((Math.sin(glowPhase / 2.0) + 1) / 2.0); // 0..1
        int pointerX = (int) (w * 0.15 + t * (w * 0.7));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(pointerX - 7, barY - 4, 14, barH + 8, 10, 10);

        g2.dispose();
    }

    // ================================
    // Cute RAM Sticker drawing
    // ================================
    private void drawCuteRamSticker(Graphics2D g2, int w, int h) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // position & size
        int chipW = 190;
        int chipH = 120;
        // bobbing animation
        float bob = (float) Math.sin(glowPhase * 1.3) * 8f;
        int x = (int) (w * 0.1);
        int y = (int) (h * 0.55 + bob);

        // shadow
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(new Color(10, 10, 30, 180));
        g.fillRoundRect(x + 6, y + chipH - 4, chipW - 12, 18, 18, 18);

        // main body gradient
        g.setComposite(AlphaComposite.SrcOver);
        GradientPaint bodyGrad = new GradientPaint(
                x, y,
                new Color(90, 210, 250),
                x + chipW, y + chipH,
                new Color(120, 90, 250));
        g.setPaint(bodyGrad);
        g.fillRoundRect(x, y, chipW, chipH, 26, 26);

        // border
        g.setStroke(new BasicStroke(2.2f));
        g.setColor(new Color(230, 245, 255, 220));
        g.drawRoundRect(x, y, chipW, chipH, 26, 26);

        // pins
        g.setColor(new Color(220, 230, 255));
        int pinCount = 7;
        int pinW = 10;
        int pinH = 18;
        int pinGap = (chipW - pinCount * pinW) / (pinCount + 1);
        int px = x + pinGap;
        int py = y + chipH;
        for (int i = 0; i < pinCount; i++) {
            g.fillRoundRect(px, py, pinW, pinH, 6, 6);
            px += pinW + pinGap;
        }

        // face
        int eyeY = y + chipH / 2 - 12;
        int eyeLX = x + chipW / 3 - 10;
        int eyeRX = x + 2 * chipW / 3 - 10;

        // white eyeballs
        g.setColor(Color.WHITE);
        g.fillOval(eyeLX, eyeY, 22, 22);
        g.fillOval(eyeRX, eyeY, 22, 22);

        // pupils bounce a tiny bit
        float blink = (float) ((Math.sin(glowPhase * 2.5) + 1) / 2.0); // 0..1
        int pupilOffsetY = (blink < 0.1) ? 5 : 2; // pseudo blink

        g.setColor(new Color(40, 60, 90));
        g.fillOval(eyeLX + 7, eyeY + 6 + pupilOffsetY, 9, 9);
        g.fillOval(eyeRX + 7, eyeY + 6 + pupilOffsetY, 9, 9);

        // blush
        g.setColor(new Color(255, 160, 190, 190));
        g.fillOval(eyeLX - 10, eyeY + 18, 18, 10);
        g.fillOval(eyeRX + 14, eyeY + 18, 18, 10);

        // mouth
        int mouthX = x + chipW / 2 - 10;
        int mouthY = eyeY + 28;
        g.setStroke(new BasicStroke(2.2f));
        g.setColor(new Color(40, 50, 80));
        g.drawArc(mouthX, mouthY, 20, 14, 0, -180);

        // "Санах ой" label
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(240, 250, 255));
        String label = "Санах ой";
        int textW = g.getFontMetrics().stringWidth(label);
        g.drawString(label, x + (chipW - textW) / 2, y + 24);

        // small page stripes like memory cells
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g.setColor(new Color(220, 240, 255, 200));
        int cellY = y + chipH - 32;
        int cellW = chipW - 40;
        int cellX = x + 20;
        g.drawRoundRect(cellX, cellY, cellW, 16, 8, 8);

        g.dispose();

        // speech bubble "Бэлэн!"
        drawSpeechBubble(g2, x + chipW + 16, y + 20, "Сайн уу!");
    }

    private void drawSpeechBubble(Graphics2D g2, int x, int y, String text) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int paddingX = 12;
        int paddingY = 6;
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int textW = g.getFontMetrics().stringWidth(text);
        int textH = g.getFontMetrics().getHeight();

        int bubbleW = textW + paddingX * 2;
        int bubbleH = textH + paddingY * 2;

        // bubble body
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g.setColor(new Color(255, 255, 255, 230));
        g.fillRoundRect(x, y, bubbleW, bubbleH, 16, 16);

        // border
        g.setColor(new Color(120, 150, 230, 230));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(x, y, bubbleW, bubbleH, 16, 16);

        // tail
        int tailX = x - 10;
        int tailY = y + bubbleH - 18;
        Polygon tail = new Polygon();
        tail.addPoint(tailX, tailY + 6);
        tail.addPoint(tailX + 10, tailY);
        tail.addPoint(tailX + 10, tailY + 12);
        g.setColor(new Color(255, 255, 255, 230));
        g.fillPolygon(tail);
        g.setColor(new Color(120, 150, 230, 230));
        g.drawPolygon(tail);

        // text
        g.setColor(new Color(40, 50, 90));
        g.drawString(text, x + paddingX, y + paddingY + textH - 6);

        g.dispose();
    }

    // ================================
    // Binary rain at top
    // ================================
    private void drawBinaryRain(Graphics2D g2, int w, int h) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));

        int rows = 3;
        int cols = 18;
        int startY = 40;
        int rowGap = 24;
        int colGap = w / (cols + 2);

        for (int row = 0; row < rows; row++) {
            int y = startY + row * rowGap;
            for (int col = 0; col < cols; col++) {
                // scroll offset
                float phase = glowPhase * 0.6f + row * 0.8f;
                int offset = (int) (5 * Math.sin(phase + col * 0.4f));
                int x = colGap + col * colGap + offset;

                // alternate 0 / 1
                String bit = ((row + col) % 2 == 0) ? "0" : "1";

                // little fading by column
                int alpha = 80 + (int) (60 * Math.sin(phase + col * 0.5f));
                alpha = Math.max(30, Math.min(140, alpha));

                g.setColor(new Color(200, 240, 255, alpha));
                g.drawString(bit, x, y);
            }
        }

        g.dispose();
    }
}
