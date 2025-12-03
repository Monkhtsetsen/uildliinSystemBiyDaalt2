import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FifoSimulationSlide extends GradientSlide {
    private final int[] referenceString = { 1, 3, 0, 3, 5, 6, 3 };
    private final int frameCount = 3;
    private final int[][] frameHistory;
    private final boolean[] faultHistory;
    private int currentStep = -1;
    private int fifoPointer = 0;
    private Timer autoTimer;
    private boolean isAutoPlaying = false;
    private int animationPhase = 0;
    
    private final JLabel statusLabel = new JLabel("Start дарж симуляци эхлүүл.");
    private final JButton autoBtn = new JButton("Автомат тоглуулах");

    public FifoSimulationSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("FIFO Page Replacement – Симуляц"), BorderLayout.NORTH);

        frameHistory = new int[referenceString.length][frameCount];
        faultHistory = new boolean[referenceString.length];
        resetHistory();

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        SimulationCanvas canvas = new SimulationCanvas();
        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(1200, 500));
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        
        JButton startBtn = new JButton("Start / Reset");
        JButton stepBtn = new JButton("Дараагийн алхам");
        
        styleControlButton(startBtn);
        styleControlButton(stepBtn);
        styleControlButton(autoBtn);

        startBtn.addActionListener(e -> {
            resetSimulation();
            canvas.repaint();
        });

        stepBtn.addActionListener(e -> {
            performStep();
            canvas.repaint();
        });

        autoBtn.addActionListener(e -> toggleAutoPlay());

        autoTimer = new Timer(1500, e -> {
            if (currentStep + 1 >= referenceString.length) {
                stopAutoPlay();
                statusLabel.setText("Бүх алхам дууссан.");
            } else {
                performStep();
            }
            canvas.repaint();
        });

        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        controls.add(startBtn);
        controls.add(stepBtn);
        controls.add(autoBtn);
        controls.add(statusLabel);

        center.add(controls, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        
        add(createStatsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JLabel statsLabel = new JLabel("Статистик: 0 алхам, 0 page fault (0.0%)", SwingConstants.CENTER);
        statsLabel.setForeground(Color.YELLOW);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        
        statsPanel.add(statsLabel);
        
        Timer statsTimer = new Timer(100, e -> {
            int totalSteps = Math.max(0, currentStep + 1);
            int faults = countPageFaults();
            double faultRate = totalSteps > 0 ? (faults * 100.0 / totalSteps) : 0;
            statsLabel.setText(String.format("Статистик: %d алхам, %d page fault (%.1f%%)", 
                totalSteps, faults, faultRate));
        });
        statsTimer.start();
        
        return statsPanel;
    }

    private void styleControlButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(90, 110, 240));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void resetSimulation() {
        resetHistory();
        currentStep = -1;
        fifoPointer = 0;
        stopAutoPlay();
        statusLabel.setText("Симуляц reset хийгдлээ. Дараагийн алхам дар.");
        animationPhase = 0;
    }

    private void resetHistory() {
        for (int i = 0; i < frameHistory.length; i++) {
            for (int j = 0; j < frameCount; j++) {
                frameHistory[i][j] = -1;
            }
            faultHistory[i] = false;
        }
    }

    private void performStep() {
        if (currentStep + 1 >= referenceString.length) {
            statusLabel.setText("Бүх алхам дууссан.");
            return;
        }
        currentStep++;
        simulateStep(currentStep);
    }

    private void simulateStep(int step) {
        int page = referenceString[step];

        if (step > 0) {
            System.arraycopy(frameHistory[step - 1], 0,
                    frameHistory[step], 0, frameCount);
        }

        boolean hit = false;
        for (int f = 0; f < frameCount; f++) {
            if (frameHistory[step][f] == page) {
                hit = true;
                break;
            }
        }

        if (!hit) {
            frameHistory[step][fifoPointer] = page;
            fifoPointer = (fifoPointer + 1) % frameCount;
            faultHistory[step] = true;
            statusLabel.setText("Алхам " + (step + 1) + " : page=" + page +
                    " → PAGE FAULT (F" + ((fifoPointer + frameCount - 1) % frameCount) + " солигдлоо)");
        } else {
            statusLabel.setText("Алхам " + (step + 1) + " : page=" + page +
                    " → HIT (солилтгүй)");
        }
    }

    private void toggleAutoPlay() {
        if (isAutoPlaying) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        isAutoPlaying = true;
        autoBtn.setText("Зогсоох");
        autoBtn.setBackground(new Color(240, 90, 90));
        autoTimer.start();
    }

    private void stopAutoPlay() {
        isAutoPlaying = false;
        autoBtn.setText("Автомат тоглуулах");
        autoBtn.setBackground(new Color(90, 110, 240));
        autoTimer.stop();
    }

    private int countPageFaults() {
        int count = 0;
        for (int i = 0; i <= currentStep && i < faultHistory.length; i++) {
            if (faultHistory[i]) count++;
        }
        return count;
    }

    class SimulationCanvas extends JPanel {
        private final int CELL_WIDTH = 80;
        private final int CELL_HEIGHT = 50;
        private final int ROW_HEIGHT = 70;
        private final int HEADER_HEIGHT = 40;
        private final int LEFT_MARGIN = 100;
        private final int TOP_MARGIN = 200;
        
        SimulationCanvas() {
            setOpaque(false);
            setPreferredSize(new Dimension(1000, 600));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Reference string хэсэг
            drawReferenceString(g2);
            
            // Гол хүснэгт
            drawMainTable(g2);
            
            // Page fault индикаторууд
            drawFaultIndicators(g2);
            
            
            // Хүснэгтийн хүрээ
            drawTableFrame(g2);
        }

        private void drawReferenceString(Graphics2D g2) {
            g2.setColor(new Color(240, 245, 255));
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Reference String:", 30, 40);
            
            int x = 200;
            int y = 40;
            
            for (int i = 0; i < referenceString.length; i++) {
                int boxX = x + i * 45;
                int boxY = y - 25;
                
                // Background
                if (i == currentStep) {
                    g2.setColor(Color.YELLOW);
                    g2.fillRoundRect(boxX - 5, boxY - 5, 40, 40, 10, 10);
                    g2.setColor(Color.BLACK);
                } else if (i < currentStep) {
                    g2.setColor(new Color(70, 100, 200, 100));
                    g2.fillRoundRect(boxX - 5, boxY - 5, 40, 40, 10, 10);
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(new Color(100, 120, 180, 100));
                    g2.fillRoundRect(boxX - 5, boxY - 5, 40, 40, 10, 10);
                    g2.setColor(new Color(180, 200, 255));
                }
                
                // Text
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                String s = String.valueOf(referenceString[i]);
                FontMetrics fm = g2.getFontMetrics();
                int textX = boxX + (35 - fm.stringWidth(s)) / 2;
                g2.drawString(s, textX, y);
                
                // Step number
                g2.setColor(new Color(200, 220, 255));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Step " + (i + 1), boxX - 5, y + 25);
            }
        }

        private void drawMainTable(Graphics2D g2) {
            int tableWidth = referenceString.length * CELL_WIDTH;
            
            // Column headers
            g2.setColor(new Color(220, 230, 255));
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            for (int step = 0; step < referenceString.length; step++) {
                int colX = LEFT_MARGIN + step * CELL_WIDTH;
                
                // Header background
                if (step == currentStep) {
                    g2.setColor(new Color(255, 255, 100, 100));
                    g2.fillRect(colX, TOP_MARGIN - 50, CELL_WIDTH, 40);
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(new Color(70, 90, 160, 100));
                    g2.fillRect(colX, TOP_MARGIN - 50, CELL_WIDTH, 40);
                    g2.setColor(Color.WHITE);
                }
                
                // Column title
                g2.drawString("Step " + (step + 1), 
                    colX + CELL_WIDTH/2 - 25, 
                    TOP_MARGIN - 20);
                    
                // Reference value
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("Ref: " + referenceString[step],
                    colX + CELL_WIDTH/2 - 25,
                    TOP_MARGIN - 5);
            }

            // Draw frames
            for (int row = 0; row < frameCount; row++) {
                int rowY = TOP_MARGIN + row * ROW_HEIGHT;
                
                // Row header
                g2.setColor(new Color(180, 200, 255));
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("Frame " + row, LEFT_MARGIN - 100, rowY + ROW_HEIGHT/2 + 5);
                
                // Frame highlight for FIFO pointer
                if (row == fifoPointer) {
                    g2.setColor(new Color(255, 200, 50, 80));
                    g2.fillRoundRect(LEFT_MARGIN - 110, rowY - 10, 90, ROW_HEIGHT, 15, 15);
                }

                for (int col = 0; col < referenceString.length; col++) {
                    int colX = LEFT_MARGIN + col * CELL_WIDTH;
                    
                    // Cell background
                    if (col <= currentStep) {
                        // Current step highlight
                        if (col == currentStep) {
                            g2.setColor(new Color(255, 255, 100, 150));
                        } else {
                            g2.setColor(new Color(60, 80, 180, 150));
                        }
                    } else {
                        g2.setColor(new Color(40, 50, 120, 100));
                    }
                    g2.fillRoundRect(colX, rowY, CELL_WIDTH - 5, CELL_HEIGHT, 15, 15);
                    
                    // Cell border
                    g2.setStroke(new BasicStroke(2.0f));
                    if (faultHistory[col] && col <= currentStep) {
                        g2.setColor(new Color(255, 100, 100));
                    } else {
                        g2.setColor(new Color(150, 180, 255));
                    }
                    g2.drawRoundRect(colX, rowY, CELL_WIDTH - 5, CELL_HEIGHT, 15, 15);
                    
                    // Cell content
                    int val = frameHistory[col][row];
                    if (val != -1 && col <= currentStep) {
                        // Highlight newly added page
                        if (faultHistory[col] && frameHistory[col][row] == referenceString[col]) {
                            g2.setColor(new Color(255, 100, 100));
                        } else {
                            g2.setColor(Color.WHITE);
                        }
                        
                        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
                        String text = String.valueOf(val);
                        FontMetrics fm = g2.getFontMetrics();
                        int textX = colX + (CELL_WIDTH - fm.stringWidth(text)) / 2;
                        int textY = rowY + (CELL_HEIGHT + fm.getAscent()) / 2 - 5;
                        g2.drawString(text, textX, textY);
                        
                        // Show frame number in small text
                        g2.setColor(new Color(200, 220, 255));
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                        g2.drawString("Frame " + row, colX + 10, rowY + 20);
                    }
                    
                    // Empty cell indicator
                    if (val == -1 && col <= currentStep) {
                        g2.setColor(new Color(150, 150, 200, 100));
                        g2.setFont(new Font("SansSerif", Font.ITALIC, 16));
                        g2.drawString("Empty", colX + 25, rowY + CELL_HEIGHT/2 + 5);
                    }
                }
            }
        }

        private void drawFaultIndicators(Graphics2D g2) {
            int y = TOP_MARGIN + frameCount * ROW_HEIGHT + 40;
            
            g2.setColor(new Color(255, 220, 220));
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Page Fault Indications:", LEFT_MARGIN, y);
            
            for (int col = 0; col <= currentStep && col < referenceString.length; col++) {
                if (faultHistory[col]) {
                    int colX = LEFT_MARGIN + col * CELL_WIDTH;
                    int faultY = y + 30;
                    
                    // Animated fault indicator
                    float pulse = (float)(0.7f + 0.3f * Math.sin(System.currentTimeMillis() * 0.005));
                    int pulseSize = (int)(40 * pulse);
                    
                    // Flash effect
                    g2.setColor(new Color(255, 100, 100, (int)(150 * pulse)));
                    g2.fillOval(colX + CELL_WIDTH/2 - pulseSize/2, 
                               faultY - pulseSize/2, 
                               pulseSize, pulseSize);
                    
                    // Main indicator
                    g2.setColor(new Color(255, 50, 50));
                    g2.fillOval(colX + CELL_WIDTH/2 - 20, faultY - 20, 40, 40);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                    g2.drawString("!", colX + CELL_WIDTH/2 - 5, faultY + 7);
                    
                    // Fault count
                    g2.setColor(new Color(255, 200, 200));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g2.drawString("Fault #" + (countFaultsUpTo(col) + 1), 
                                colX + CELL_WIDTH/2 - 25, faultY + 30);
                }
            }
        }

        private void drawFifoPointer(Graphics2D g2) {
            if (currentStep >= 0) {
                int pointerX = LEFT_MARGIN - 30;
                int pointerY = TOP_MARGIN + fifoPointer * ROW_HEIGHT + CELL_HEIGHT/2;
                
                // Animated arrow
                float arrowPulse = (float)(0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.004));
                int arrowOffset = (int)(10 * arrowPulse);
                
                // Arrow
                g2.setColor(Color.ORANGE);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                Polygon arrow = new Polygon();
                arrow.addPoint(pointerX + arrowOffset, pointerY);
                arrow.addPoint(pointerX + 20 + arrowOffset, pointerY - 10);
                arrow.addPoint(pointerX + 20 + arrowOffset, pointerY + 10);
                g2.fill(arrow);
                
                g2.drawLine(pointerX + 20 + arrowOffset, pointerY, pointerX + 60 + arrowOffset, pointerY);
                
                // Pointer label with background
                g2.setColor(new Color(255, 200, 50, 200));
                g2.fillRoundRect(pointerX + 65, pointerY - 25, 180, 40, 10, 10);
                
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                
                // Explanation
                g2.setColor(new Color(255, 220, 150));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("(дараагийн солих frame)", 
                            pointerX + 85, pointerY + 15);
            }
        }

        private void drawTableFrame(Graphics2D g2) {
            int tableWidth = referenceString.length * CELL_WIDTH;
            int tableHeight = frameCount * ROW_HEIGHT;
            
            // Main table border
            g2.setStroke(new BasicStroke(3.0f));
            g2.setColor(new Color(100, 150, 255, 200));
            g2.drawRoundRect(LEFT_MARGIN - 10, TOP_MARGIN - 60, 
                           tableWidth + 20, tableHeight + 70, 20, 20);
            
            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString("FIFO Page Replacement Simulation Table", 
                         LEFT_MARGIN + tableWidth/2 - 150, TOP_MARGIN - 90);
            
            // Legend
            int legendY = TOP_MARGIN + tableHeight + 100;
            g2.setColor(new Color(220, 230, 255));
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("ХАРЬЦУУЛАЛТ:", LEFT_MARGIN, legendY);
            
            // Legend items
            String[] legends = {
                "■ Улаан хүрээ = Page Fault үеийн солигдсон cell",
                "■ Шар background = Одоогийн алхам",
                "■ Улбар шар сум = FIFO Pointer",
                "■ Улаан тойрог = Page Fault болсон алхам"
            };
            Color[] colors = {
                new Color(255, 100, 100),
                Color.YELLOW,
                Color.ORANGE,
                new Color(255, 50, 50)
            };
            
            for (int i = 0; i < legends.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRect(LEFT_MARGIN + 20, legendY + 30 + i * 25, 15, 15);
                g2.setColor(new Color(200, 220, 255));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString(legends[i], LEFT_MARGIN + 45, legendY + 30 + i * 25 + 12);
            }
        }

        private int countFaultsUpTo(int step) {
            int count = 0;
            for (int i = 0; i <= step; i++) {
                if (faultHistory[i]) count++;
            }
            return count;
        }
    }
}