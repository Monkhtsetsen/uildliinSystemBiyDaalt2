import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FifoSimulationSlide extends GradientSlide {
    private final int[] referenceString = { 1, 3, 0, 3, 5, 6, 3 };
    private final int frameCount = 3;
    private final int[][] frameHistory;
    private final boolean[] faultHistory;
    private int currentStep = -1; // -1 = not started yet
    private int fifoPointer = 0;

    private final JLabel statusLabel = new JLabel("Start дарж симуляци эхлүүл.");

    public FifoSimulationSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("FIFO Page Replacement – Симуляц"), BorderLayout.NORTH);

        frameHistory = new int[referenceString.length][frameCount];
        faultHistory = new boolean[referenceString.length];
        resetHistory();

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        SimulationCanvas canvas = new SimulationCanvas();
        center.add(canvas, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        JButton startBtn = new JButton("Start / Reset");
        JButton stepBtn = new JButton("Дараагийн алхам");
        styleControlButton(startBtn);
        styleControlButton(stepBtn);

        startBtn.addActionListener(e -> {
            resetHistory();
            currentStep = -1;
            fifoPointer = 0;
            statusLabel.setText("Симуляц reset хийгдлээ. Дараагийн алхам дар.");
            canvas.repaint();
        });

        stepBtn.addActionListener(e -> {
            if (currentStep + 1 >= referenceString.length) {
                statusLabel.setText("Бүх алхам дууссан.");
                return;
            }
            currentStep++;
            simulateStep(currentStep);
            canvas.repaint();
        });

        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        controls.add(startBtn);
        controls.add(stepBtn);
        controls.add(statusLabel);

        center.add(controls, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
    }

    private void styleControlButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(90, 110, 240));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    private void resetHistory() {
        for (int i = 0; i < frameHistory.length; i++) {
            for (int j = 0; j < frameCount; j++) {
                frameHistory[i][j] = -1;
            }
            faultHistory[i] = false;
        }
    }

    /** Нэг алхамыг FIFO-аар тооцоолно */
    private void simulateStep(int step) {
        int page = referenceString[step];

        // өмнөх step-ийн frame-үүдийг хуулж авна
        if (step > 0) {
            System.arraycopy(frameHistory[step - 1], 0,
                    frameHistory[step], 0, frameCount);
        }

        // Хуудас одоо frames-д байгаа эсэх
        boolean hit = false;
        for (int f = 0; f < frameCount; f++) {
            if (frameHistory[step][f] == page) {
                hit = true;
                break;
            }
        }

        if (!hit) {
            // page fault -> FIFO pointer зааж буй frame рүү солино
            frameHistory[step][fifoPointer] = page;
            fifoPointer = (fifoPointer + 1) % frameCount;
            faultHistory[step] = true;
            statusLabel.setText("Алхам " + (step + 1) + " : page=" + page +
                    " → PAGE FAULT");
        } else {
            statusLabel.setText("Алхам " + (step + 1) + " : page=" + page +
                    " → hit (солилтгүй)");
        }
    }

    /** Зурах canvas – Frame × Step хүснэгт */
    class SimulationCanvas extends JPanel {
        SimulationCanvas() {
            setOpaque(false);
            setPreferredSize(new Dimension(800, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // reference string
            g2.setColor(new Color(215, 225, 255));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g2.drawString("Reference string:", 20, 30);
            int x = 170;
            for (int i = 0; i < referenceString.length; i++) {
                String s = String.valueOf(referenceString[i]);
                g2.drawString(s, x + i * 30, 30);
            }

            // table origin
            int originX = 60;
            int originY = 70;
            int cellW = 40;
            int cellH = 30;

            // column labels
            g2.drawString("Алхам", 10, originY + cellH);
            for (int step = 0; step < referenceString.length; step++) {
                String s = String.valueOf(step + 1);
                int colX = originX + step * cellW;
                g2.drawString(s, colX + 12, originY - 5);
            }

            // Draw grid & numbers
            g2.setStroke(new BasicStroke(1f));
            for (int row = 0; row < frameCount; row++) {
                int rowY = originY + row * cellH;
                g2.drawString("F" + row, 25, rowY + 20);

                for (int col = 0; col < referenceString.length; col++) {
                    int colX = originX + col * cellW;
                    g2.setColor(new Color(35, 45, 110));
                    g2.fillRect(colX, rowY, cellW - 2, cellH - 2);
                    g2.setColor(new Color(130, 140, 230));
                    g2.drawRect(colX, rowY, cellW - 2, cellH - 2);

                    int val = frameHistory[col][row];
                    if (val != -1 && col <= currentStep) {
                        g2.setColor(Color.WHITE);
                        String text = String.valueOf(val);
                        FontMetrics fm = g2.getFontMetrics();
                        int tx = colX + (cellW - fm.stringWidth(text)) / 2;
                        int ty = rowY + (cellH + fm.getAscent()) / 2 - 4;
                        g2.drawString(text, tx, ty);
                    }
                }
            }

            // Page fault indicators
            for (int col = 0; col <= currentStep && col < referenceString.length; col++) {
                if (faultHistory[col]) {
                    int colX = originX + col * cellW;
                    int yFault = originY + frameCount * cellH + 10;
                    g2.setColor(new Color(255, 120, 140));
                    g2.fillOval(colX + 10, yFault, 16, 16);
                }
            }

            g2.setColor(new Color(220, 230, 255));
            g2.drawString("Улаан цэг = PAGE FAULT (солигдсон алхам)",
                    originX, originY + frameCount * cellH + 40);
        }
    }
}
