import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThrashingSlide extends GradientSlide {
    private float animationProgress = 0f;
    private Timer animationTimer;
    private int currentPhase = 0; // 0: normal, 1: thrashing start, 2: full thrashing, 3: recovery
    private float[] cpuUtilization = new float[100];
    private float[] diskIO = new float[100];
    private float[] pageFaults = new float[100];
    private int currentDataPoint = 0;
    private boolean isThrashingActive = false;

    public ThrashingSlide() {
        setLayout(new BorderLayout());
        
        // Header with icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JComponent title = Main.createHeader(" Thrashing – Page Fault Хэт Их Үед");
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        headerPanel.add(title, BorderLayout.NORTH);
        
        // Description with better formatting
        JTextArea text = new JTextArea(
                "Thrashing (Дуугаралт) нь системийн RAM-ын багтаамж хангалтгүй болж, " +
                "програмуудын working set RAM-д багтахгүй үест гардаг. Энэ үед:\n\n" +
                "• Page fault хэт их гарч, OS RAM ↔ Disk хооронд pages-ийг байнга зөөх\n" +
                "• CPU utilization эрс унаж, disk I/O л дүүрэн ажиллана\n" +
                "• Системийн гүйцэтгэл эрс мууддаг\n\n" +
                "Шийдлүүд:\n" +
                "• Process-д оногдох frame-ийн тоог нэмэх\n" +
                "• Идэвхгүй процессыг түр зогсоох\n" +
                "• Ажиллаж буй програмын тоог цөөрүүлэх\n" +
                "• Working Set Model ашиглах");
        text.setOpaque(false);
        text.setEditable(false);
        text.setForeground(new Color(240, 245, 255));
        text.setFont(new Font("SansSerif", Font.PLAIN, 16));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        headerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main visualization panel
        ThrashingVisualization visualization = new ThrashingVisualization();
        visualization.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(visualization, BorderLayout.CENTER);
        
        // Control panel
        add(createControlPanel(), BorderLayout.SOUTH);
        
        // Initialize data
        initializeData();
        
        // Start animation
        animationTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += 0.01f;
                if (animationProgress > 1f) {
                    animationProgress = 0f;
                    currentPhase = (currentPhase + 1) % 4;
                    updateThrashingState();
                }
                
                // Update data points
                if (currentDataPoint < cpuUtilization.length - 1) {
                    currentDataPoint++;
                } else {
                    // Shift data to make room for new points
                    shiftDataArrays();
                    currentDataPoint = cpuUtilization.length - 20;
                }
                
                updateDataPoints();
                visualization.repaint();
            }
        });
        animationTimer.start();
    }
    
    private void initializeData() {
        for (int i = 0; i < cpuUtilization.length; i++) {
            cpuUtilization[i] = 80f; // Start with normal CPU usage
            diskIO[i] = 20f; // Normal disk I/O
            pageFaults[i] = 5f; // Normal page faults
        }
    }
    
    private void shiftDataArrays() {
        // Shift data to left to make room for new points
        for (int i = 1; i < cpuUtilization.length; i++) {
            cpuUtilization[i-1] = cpuUtilization[i];
            diskIO[i-1] = diskIO[i];
            pageFaults[i-1] = pageFaults[i];
        }
    }
    
    private void updateDataPoints() {
        int dataIndex = currentDataPoint;
        
        if (isThrashingActive) {
            // During thrashing
            cpuUtilization[dataIndex] = 20f + (float)Math.random() * 10f; // Low CPU
            diskIO[dataIndex] = 90f + (float)Math.random() * 10f; // High disk I/O
            pageFaults[dataIndex] = 80f + (float)Math.random() * 20f; // High page faults
        } else {
            // Normal operation
            cpuUtilization[dataIndex] = 70f + (float)Math.random() * 20f; // Normal CPU
            diskIO[dataIndex] = 10f + (float)Math.random() * 15f; // Normal disk I/O
            pageFaults[dataIndex] = 5f + (float)Math.random() * 10f; // Normal page faults
        }
    }
    
    private void updateThrashingState() {
        switch (currentPhase) {
            case 0: // Normal
                isThrashingActive = false;
                break;
            case 1: // Thrashing start
                isThrashingActive = true;
                break;
            case 2: // Full thrashing
                isThrashingActive = true;
                break;
            case 3: // Recovery
                isThrashingActive = false;
                break;
        }
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JLabel phaseLabel = new JLabel();
        phaseLabel.setForeground(Color.YELLOW);
        phaseLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JLabel warningLabel = new JLabel("⚠️ Thrashing үед системийн гүйцэтгэл эрс муудна!");
        warningLabel.setForeground(new Color(255, 100, 100));
        warningLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        panel.add(phaseLabel);
        panel.add(warningLabel);
        
        // Timer to update phase label
        Timer labelTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] phases = {
                    "🔵 НОРМАЛЬ АЖИЛЛАГАА: CPU ашиглалт өндөр, disk I/O бага",
                    "🟡 THRASHING ЭХЭЛЛЭЭ: Page fault нэмэгдэж, CPU ашиглалт буурч байна",
                    "🔴 БҮРЭН THRASHING: CPU ашиглалт маш бага, disk I/O дүүрэн ажиллаж байна",
                    "🟢 СЭРГЭЭГДЭЖ БАЙНА: Процессуудыг зогсоож, систем сэргэж байна"
                };
                phaseLabel.setText(phases[currentPhase]);
            }
        });
        labelTimer.start();
        
        return panel;
    }

    private class ThrashingVisualization extends JPanel {
        private final int CHART_HEIGHT = 150;
        private final int CHART_MARGIN = 60;
        private final int CHART_SPACING = 30;
        
        public ThrashingVisualization() {
            setOpaque(false);
            setPreferredSize(new Dimension(1000, 550));
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
            
            // Draw three charts
            drawCpuChart(g2, w, h);
            drawDiskIOChart(g2, w, h);
            drawPageFaultChart(g2, w, h);
            
            // Draw system diagram
            drawSystemDiagram(g2, w, h);
            
            // Draw thrashing explanation
            drawThrashingExplanation(g2, w, h);
        }
        
        private void drawCpuChart(Graphics2D g2, int w, int h) {
            int chartY = CHART_MARGIN;
            drawChart(g2, w, chartY, "CPU Utilization (%)", cpuUtilization, 
                     new Color(100, 200, 255), new Color(100, 200, 255, 100));
            
            // Draw threshold line
            g2.setColor(new Color(255, 100, 100, 150));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{5}, 0));
            int thresholdY = chartY + CHART_HEIGHT - (int)(30 * CHART_HEIGHT / 100);
            g2.drawLine(CHART_MARGIN, thresholdY, w - CHART_MARGIN, thresholdY);
            
            g2.setColor(new Color(255, 150, 150));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.drawString("Thrashing босго (30%)", w - CHART_MARGIN - 100, thresholdY - 5);
        }
        
        private void drawDiskIOChart(Graphics2D g2, int w, int h) {
            int chartY = CHART_MARGIN + CHART_HEIGHT + CHART_SPACING;
            drawChart(g2, w, chartY, "Disk I/O Activity (%)", diskIO, 
                     new Color(255, 200, 100), new Color(255, 200, 100, 100));
        }
        
        private void drawPageFaultChart(Graphics2D g2, int w, int h) {
            int chartY = CHART_MARGIN + 2 * (CHART_HEIGHT + CHART_SPACING);
            drawChart(g2, w, chartY, "Page Fault Rate (%)", pageFaults, 
                     new Color(255, 100, 150), new Color(255, 100, 150, 100));
            
            // Highlight thrashing area
            if (isThrashingActive) {
                int highlightStart = w - CHART_MARGIN - 100;
                int highlightWidth = 100;
                
                g2.setColor(new Color(255, 50, 50, 50));
                g2.fillRect(highlightStart, chartY, highlightWidth, CHART_HEIGHT);
                
                g2.setColor(new Color(255, 100, 100));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("THRASHING!", highlightStart + 10, chartY + 20);
            }
        }
        
        private void drawChart(Graphics2D g2, int w, int chartY, String title, 
                              float[] data, Color lineColor, Color fillColor) {
            int chartWidth = w - 2 * CHART_MARGIN;
            int chartHeight = CHART_HEIGHT;
            
            // Chart background
            g2.setColor(new Color(40, 45, 70, 100));
            g2.fillRoundRect(CHART_MARGIN - 10, chartY - 30, chartWidth + 20, 
                           chartHeight + 40, 15, 15);
            
            // Chart title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString(title, CHART_MARGIN, chartY - 10);
            
            // Draw grid
            g2.setColor(new Color(100, 110, 140, 80));
            g2.setStroke(new BasicStroke(0.5f));
            
            // Horizontal grid lines
            for (int i = 0; i <= 4; i++) {
                int y = chartY + chartHeight - i * chartHeight / 4;
                g2.drawLine(CHART_MARGIN, y, CHART_MARGIN + chartWidth, y);
                
                // Y-axis labels
                g2.setColor(new Color(180, 190, 220));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString((i * 25) + "%", CHART_MARGIN - 25, y + 4);
            }
            
            // Draw data line
            g2.setStroke(new BasicStroke(2.0f));
            
            // Calculate visible data points
            int startIndex = Math.max(0, currentDataPoint - 50);
            int pointCount = Math.min(50, data.length - startIndex);
            
            // Draw filled area under line
            if (pointCount > 1) {
                Polygon fillPolygon = new Polygon();
                fillPolygon.addPoint(CHART_MARGIN, chartY + chartHeight);
                
                for (int i = 0; i < pointCount; i++) {
                    int dataIndex = startIndex + i;
                    if (dataIndex >= data.length) break;
                    
                    int x = CHART_MARGIN + (i * chartWidth / pointCount);
                    int y = chartY + chartHeight - (int)(data[dataIndex] * chartHeight / 100);
                    fillPolygon.addPoint(x, y);
                }
                
                fillPolygon.addPoint(CHART_MARGIN + chartWidth, chartY + chartHeight);
                
                g2.setColor(fillColor);
                g2.fillPolygon(fillPolygon);
            }
            
            // Draw line
            g2.setColor(lineColor);
            for (int i = 0; i < pointCount - 1; i++) {
                int dataIndex1 = startIndex + i;
                int dataIndex2 = startIndex + i + 1;
                
                if (dataIndex2 >= data.length) break;
                
                int x1 = CHART_MARGIN + (i * chartWidth / pointCount);
                int y1 = chartY + chartHeight - (int)(data[dataIndex1] * chartHeight / 100);
                int x2 = CHART_MARGIN + ((i + 1) * chartWidth / pointCount);
                int y2 = chartY + chartHeight - (int)(data[dataIndex2] * chartHeight / 100);
                
                g2.drawLine(x1, y1, x2, y2);
            }
            
            // Current value indicator
            if (currentDataPoint < data.length) {
                int currentX = CHART_MARGIN + chartWidth - 10;
                int currentY = chartY + chartHeight - (int)(data[currentDataPoint] * chartHeight / 100);
                
                // Value circle
                g2.setColor(lineColor);
                g2.fillOval(currentX - 5, currentY - 5, 10, 10);
                
                // Value text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(String.format("%.1f%%", data[currentDataPoint]), 
                            currentX + 10, currentY + 4);
            }
            
            // Time labels
            g2.setColor(new Color(180, 190, 220));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.drawString("Цаг хугацаа →", CHART_MARGIN + chartWidth - 50, 
                         chartY + chartHeight + 15);
        }
        
        private void drawSystemDiagram(Graphics2D g2, int w, int h) {
            int diagramY = CHART_MARGIN + 3 * (CHART_HEIGHT + CHART_SPACING) + 20;
            int diagramHeight = 120;
            
            // Diagram background
            g2.setColor(new Color(40, 45, 70, 150));
            g2.fillRoundRect(CHART_MARGIN - 10, diagramY - 10, 
                           w - 2 * CHART_MARGIN + 20, diagramHeight + 20, 15, 15);
            
            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString("Thrashing үеийн Системийн Төлөв", CHART_MARGIN, diagramY - 15);
            
            // Draw RAM
            int ramX = CHART_MARGIN + 50;
            int ramY = diagramY + 20;
            int ramWidth = 200;
            int ramHeight = 80;
            
            g2.setColor(new Color(100, 150, 255, 200));
            g2.fillRoundRect(ramX, ramY, ramWidth, ramHeight, 10, 10);
            g2.setColor(new Color(180, 200, 255));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(ramX, ramY, ramWidth, ramHeight, 10, 10);
            
            // RAM processes
            int processCount = isThrashingActive ? 8 : 4;
            for (int i = 0; i < processCount; i++) {
                int procX = ramX + 10 + (i % 4) * 45;
                int procY = ramY + 10 + (i / 4) * 30;
                int procWidth = 40;
                int procHeight = 25;
                
                if (isThrashingActive && i >= 4) {
                    // Processes that don't fit in RAM (thrashing)
                    g2.setColor(new Color(255, 100, 100, 200));
                } else {
                    g2.setColor(new Color(100, 200, 100, 200));
                }
                
                g2.fillRoundRect(procX, procY, procWidth, procHeight, 5, 5);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("P" + (i + 1), procX + 12, procY + 16);
            }
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("RAM", ramX + ramWidth/2 - 15, ramY - 5);
            
            // Draw Disk
            int diskX = ramX + ramWidth + 100;
            int diskY = ramY;
            int diskWidth = 150;
            
            g2.setColor(new Color(200, 150, 100, 200));
            g2.fillRoundRect(diskX, diskY, diskWidth, ramHeight, 10, 10);
            g2.setColor(new Color(255, 220, 180));
            g2.drawRoundRect(diskX, diskY, diskWidth, ramHeight, 10, 10);
            
            // Disk activity indicator
            float diskActivity = diskIO[currentDataPoint] / 100f;
            int indicatorSize = (int)(60 * diskActivity);
            
            g2.setColor(new Color(255, 200, 100, (int)(150 * diskActivity)));
            g2.fillOval(diskX + diskWidth/2 - indicatorSize/2, 
                       diskY + ramHeight/2 - indicatorSize/2, 
                       indicatorSize, indicatorSize);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("DISK (Swap)", diskX + diskWidth/2 - 30, diskY - 5);
            
            // Draw CPU
            int cpuX = diskX + diskWidth + 100;
            int cpuY = ramY;
            int cpuSize = 80;
            
            // CPU activity indicator
            float cpuActivity = cpuUtilization[currentDataPoint] / 100f;
            int cpuIndicator = (int)(cpuSize * cpuActivity);
            
            g2.setColor(new Color(100, 200, 255, (int)(150 * cpuActivity)));
            g2.fillOval(cpuX, cpuY, cpuSize, cpuSize);
            g2.setColor(new Color(180, 220, 255));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(cpuX, cpuY, cpuSize, cpuSize);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("CPU", cpuX + cpuSize/2 - 15, cpuY - 5);
            
            // Draw arrows showing data flow
            drawDataFlowArrows(g2, ramX + ramWidth, ramY + ramHeight/2, 
                              diskX, diskY + ramHeight/2, isThrashingActive);
            drawDataFlowArrows(g2, diskX + diskWidth, diskY + ramHeight/2, 
                              cpuX, cpuY + cpuSize/2, isThrashingActive);
            
            // Status text
            g2.setColor(isThrashingActive ? new Color(255, 100, 100) : new Color(100, 220, 100));
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String status = isThrashingActive ? 
                "⛔ THRASHING: RAM багтахгүй, disk дүүрэн ажиллаж байна" :
                "✅ НОРМАЛЬ: RAM-д бүх процесс багтаж байна";
            g2.drawString(status, CHART_MARGIN, diagramY + diagramHeight + 20);
        }
        
        private void drawDataFlowArrows(Graphics2D g2, int fromX, int fromY, 
                                        int toX, int toY, boolean isBusy) {
            g2.setStroke(new BasicStroke(isBusy ? 3 : 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (isBusy) {
                // Animated busy arrow
                float anim = (float)(0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.003));
                g2.setColor(new Color(255, 100, 100, (int)(200 * anim)));
            } else {
                g2.setColor(new Color(150, 200, 255, 150));
            }
            
            // Draw line
            g2.drawLine(fromX, fromY, toX, toY);
            
            // Draw arrow head
            int arrowSize = 8;
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(toX, toY);
            arrowHead.addPoint(toX - arrowSize, toY - arrowSize);
            arrowHead.addPoint(toX - arrowSize, toY + arrowSize);
            g2.fill(arrowHead);
            
            // Draw data packets if busy
            if (isBusy) {
                int packetCount = 3;
                int packetSpacing = 15;
                
                for (int i = 0; i < packetCount; i++) {
                    int packetX = fromX + i * packetSpacing;
                    float packetAnim = (animationProgress + i * 0.2f) % 1f;
                    int packetY = fromY + (int)((toY - fromY) * packetAnim);
                    
                    g2.setColor(new Color(255, 200, 100, 200));
                    g2.fillOval(packetX - 4, packetY - 4, 8, 8);
                }
            }
        }
        
        private void drawThrashingExplanation(Graphics2D g2, int w, int h) {
            int explanationY = h - 60;
            
            g2.setColor(new Color(255, 220, 180));
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString("Thrashing Дүрслэл:", CHART_MARGIN, explanationY);
            
            String[] points = {
                "1. Олон процесс RAM-д багтахгүй бол page fault нэмэгдэнэ",
                "2. OS page-үүдийг disk рүү шилжүүлж эхэлнэ (swapping)",
                "3. CPU ихэнх цагаа disk I/O хүлээлтэд зарцуулна",
                "4. Системийн гүйцэтгэл эрс мууддаг",
                "5. Зөвхөн process-уудыг цөөрүүлж эсвэл RAM нэмэхэд л шийдэгдэнэ"
            };
            
            g2.setColor(new Color(220, 230, 255));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            
            for (int i = 0; i < points.length; i++) {
                g2.drawString(points[i], CHART_MARGIN + 150, explanationY + i * 15);
            }
        }
    }
}