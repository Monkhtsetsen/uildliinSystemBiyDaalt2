import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LruConceptSlide extends GradientSlide {

    private float animationProgress = 0f;
    private Timer timer;
    private int currentStep = 0;
    private float[] pageTimers = {0f, 0f, 0f, 0f};
    private boolean[] pageActive = {true, true, true, true};
    private String[] pageAccessTimes = {"3:45", "1:20", "2:15", "4:30"};
    private Color[] pageColors = {
        new Color(100, 180, 255),
        new Color(120, 220, 180),
        new Color(255, 200, 100),
        new Color(220, 160, 255)
    };

    public LruConceptSlide() {
        setLayout(new BorderLayout());
        
        // Header with icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JComponent title = Main.createHeader("LRU Алгоритм (Least Recently Used)");
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        headerPanel.add(title, BorderLayout.NORTH);
        
        JTextArea description = new JTextArea(
                "LRU (Least Recently Used): Хамгийн удаан хугацаанд ашиглагдаагүй хуудас/блокыг солих алгоритм.\n" +
                "Өгөгдлийн бүтэц: Хоёр дахь шугам санах ой эсвэл хэрэглээний цагийн мэдээлэл хадгална.\n" +
                "Давуу тал: Хямралын багтаамж сайтай, олон програмд үр дүнтэй.");
        description.setOpaque(false);
        description.setEditable(false);
        description.setForeground(new Color(240, 245, 255));
        description.setFont(new Font("SansSerif", Font.PLAIN, 16));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        
        JScrollPane scrollPane = new JScrollPane(description);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        headerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main animation panel
        AnimatedLRUDemo animationPanel = new AnimatedLRUDemo();
        animationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(animationPanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Start animation timer
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += 0.015f;
                if (animationProgress > 1.5f) {
                    animationProgress = 0f;
                    currentStep = (currentStep + 1) % 4;
                    updatePageAccessTimes();
                }
                
                // Update individual page timers
                for (int i = 0; i < pageTimers.length; i++) {
                    pageTimers[i] += 0.02f;
                    if (pageTimers[i] > 1f) pageTimers[i] = 0f;
                }
                
                animationPanel.repaint();
            }
        });
        timer.start();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Тайлбар: Хуудас бүрийн хэрэглээний цагийг хянаж, хамгийн хуучин хэрэглэгдсэнийг нь устгана");
        statusLabel.setForeground(new Color(200, 220, 255));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        infoPanel.add(createInfoLabel("✅ LRU Victim", new Color(255, 100, 120)));
        infoPanel.add(createInfoLabel("🔄 Шинэчлэгдсэн", new Color(100, 220, 150)));
        infoPanel.add(createInfoLabel("⏰ Цаг хугацаа", new Color(255, 200, 100)));
        infoPanel.add(createInfoLabel("📊 Харьцуулалт", new Color(180, 160, 255)));
        
        panel.add(statusLabel);
        panel.add(infoPanel);
        
        return panel;
    }
    
    private JLabel createInfoLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return label;
    }
    
    private void updatePageAccessTimes() {
        // Simulate page access pattern
        int accessedPage = currentStep % 4;
        pageActive[accessedPage] = true;
        pageTimers[accessedPage] = 0f;
        
        // Update access times
        for (int i = 0; i < pageAccessTimes.length; i++) {
            if (i == accessedPage) {
                pageAccessTimes[i] = "0:01";
            } else {
                // Increment time for other pages
                String[] parts = pageAccessTimes[i].split(":");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]) + 30;
                if (seconds >= 60) {
                    minutes++;
                    seconds -= 60;
                }
                pageAccessTimes[i] = minutes + ":" + String.format("%02d", seconds);
            }
        }
    }

    private class AnimatedLRUDemo extends JPanel {
        private int[] pageSizes = {256, 512, 384, 640}; // KB
        private String[] pageContents = {"Program Code", "Data", "Stack", "Heap"};
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Draw memory frame
            drawMemoryFrame(g2, w, h);
            
            // Draw pages with animation
            drawPages(g2, w, h);
            
            // Draw timeline
            drawTimeline(g2, w, h);
            
            // Draw comparison with other algorithms
            drawAlgorithmComparison(g2, w, h);
        }
        
        private void drawMemoryFrame(Graphics2D g2, int w, int h) {
            int frameX = 40;
            int frameY = 40;
            int frameW = w - 80;
            int frameH = h - 200;
            
            // Memory frame
            GradientPaint frameGradient = new GradientPaint(
                frameX, frameY, new Color(40, 50, 80, 150),
                frameX + frameW, frameY + frameH, new Color(30, 40, 70, 200)
            );
            g2.setPaint(frameGradient);
            g2.fillRoundRect(frameX, frameY, frameW, frameH, 20, 20);
            
            g2.setColor(new Color(180, 200, 255, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(frameX, frameY, frameW, frameH, 20, 20);
            
            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Санах ойн Хуудаснууд (Pages)", frameX + 20, frameY - 10);
        }
        
        private void drawPages(Graphics2D g2, int w, int h) {
            int frameX = 40;
            int frameY = 40;
            int frameW = w - 80;
            int frameH = h - 200;
            
            int pageCount = 4;
            int padding = 20;
            int totalPagesWidth = frameW - 2 * padding;
            int pageWidth = totalPagesWidth / pageCount - padding;
            int pageHeight = frameH - 100;
            
            // Find LRU victim (oldest page)
            int lruIndex = 0;
            String[] timeParts = pageAccessTimes[0].split(":");
            int maxTime = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
            
            for (int i = 1; i < pageAccessTimes.length; i++) {
                String[] parts = pageAccessTimes[i].split(":");
                int time = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
                if (time > maxTime) {
                    maxTime = time;
                    lruIndex = i;
                }
            }
            
            // Draw each page
            for (int i = 0; i < pageCount; i++) {
                int pageX = frameX + padding + i * (pageWidth + padding);
                int pageY = frameY + 60;
                
                // Animate page entry
                float pageAnim = Math.min(1f, animationProgress * 1.2f - i * 0.1f);
                int animatedY = pageY + (int)((1 - pageAnim) * 50);
                
                // Determine page state
                boolean isLRU = (i == lruIndex);
                boolean isAccessed = pageActive[i];
                boolean isHighlighted = (currentStep % 4 == i);
                
                // Page background with gradient
                Color baseColor = pageColors[i];
                float alpha = isAccessed ? 0.9f : 0.6f;
                
                if (isLRU) {
                    // LRU victim - pulsating effect
                    float pulse = (float)(0.7f + 0.3f * Math.sin(animationProgress * 10));
                    g2.setColor(new Color(
                        (int)(255 * pulse),
                        (int)(100 * (1 - pulse)),
                        (int)(120 * (1 - pulse)),
                        (int)(200 * pulse)
                    ));
                } else if (isHighlighted) {
                    // Recently accessed - glowing effect
                    g2.setColor(new Color(
                        Math.min(255, baseColor.getRed() + 50),
                        Math.min(255, baseColor.getGreen() + 50),
                        Math.min(255, baseColor.getBlue() + 50),
                        200
                    ));
                } else {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int)(200 * alpha)));
                }
                
                // Draw page
                g2.fillRoundRect(pageX, animatedY, pageWidth, pageHeight, 15, 15);
                
                // Page border
                if (isLRU) {
                    g2.setColor(new Color(255, 80, 100, 200));
                    g2.setStroke(new BasicStroke(3));
                } else {
                    g2.setColor(new Color(255, 255, 255, 150));
                    g2.setStroke(new BasicStroke(2));
                }
                g2.drawRoundRect(pageX, animatedY, pageWidth, pageHeight, 15, 15);
                
                // Page content
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("P" + (i + 1), pageX + 15, animatedY + 25);
                
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString(pageContents[i], pageX + 15, animatedY + 45);
                g2.drawString(pageSizes[i] + " KB", pageX + 15, animatedY + 65);
                
                // Access time with clock icon
                g2.setColor(new Color(255, 255, 200));
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.drawString("⏰ " + pageAccessTimes[i], pageX + 15, animatedY + 85);
                
                // Usage indicator bar
                int usageBarWidth = (int)(pageWidth * 0.7f * pageTimers[i]);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillRoundRect(pageX + 15, animatedY + 95, pageWidth - 30, 8, 4, 4);
                g2.setColor(new Color(100, 220, 100, 200));
                g2.fillRoundRect(pageX + 15, animatedY + 95, usageBarWidth, 8, 4, 4);
                
                // LRU marker
                if (isLRU) {
                    g2.setColor(new Color(255, 100, 100));
                    g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g2.drawString("LRU VICTIM", pageX + pageWidth/2 - 40, animatedY - 10);
                    
                    // Arrow pointing to victim
                    int arrowX = pageX + pageWidth/2;
                    int arrowY = animatedY + pageHeight + 20;
                    
                    g2.setColor(new Color(255, 100, 100, 200));
                    g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(arrowX, arrowY, arrowX, arrowY + 40);
                    
                    // Arrow head
                    Polygon arrowHead = new Polygon();
                    arrowHead.addPoint(arrowX, arrowY + 40);
                    arrowHead.addPoint(arrowX - 8, arrowY + 32);
                    arrowHead.addPoint(arrowX + 8, arrowY + 32);
                    g2.fill(arrowHead);
                    
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g2.drawString("Солигдох хуудас", arrowX - 45, arrowY + 60);
                }
                
                // Access animation for current page
                if (isHighlighted && animationProgress > 0.5f) {
                    float accessAnim = (animationProgress - 0.5f) * 2;
                    int glowSize = (int)(20 * accessAnim);
                    
                    g2.setColor(new Color(255, 255, 200, (int)(100 * (1 - accessAnim))));
                    g2.fillOval(pageX + pageWidth/2 - glowSize/2, 
                               animatedY + pageHeight/2 - glowSize/2, 
                               glowSize, glowSize);
                }
            }
        }
        
        private void drawTimeline(Graphics2D g2, int w, int h) {
            int timelineY = h - 140;
            int timelineX = 40;
            int timelineW = w - 80;
            
            // Timeline background
            g2.setColor(new Color(40, 40, 60, 150));
            g2.fillRoundRect(timelineX, timelineY, timelineW, 80, 15, 15);
            
            g2.setColor(new Color(180, 200, 255, 100));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(timelineX, timelineY, timelineW, 80, 15, 15);
            
            // Timeline title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Хуудасны Хэрэглээний Цаг Хугацаа", timelineX + 20, timelineY - 10);
            
            // Time markers
            int markerCount = 6;
            for (int i = 0; i <= markerCount; i++) {
                int markerX = timelineX + (timelineW * i / markerCount);
                g2.setColor(new Color(200, 220, 255, 150));
                g2.drawLine(markerX, timelineY + 10, markerX, timelineY + 70);
                
                g2.setColor(new Color(180, 200, 255));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(i + " мин", markerX - 10, timelineY + 85);
            }
            
            // Page access history lines
            for (int i = 0; i < 4; i++) {
                int lineY = timelineY + 20 + i * 12;
                g2.setColor(pageColors[i]);
                g2.setStroke(new BasicStroke(2));
                
                // Simulated access pattern
                for (int j = 0; j < 5; j++) {
                    int startX = timelineX + (timelineW * j / 5) + 10;
                    int endX = startX + timelineW / 10;
                    
                    if ((i + j) % 3 == currentStep % 2) {
                        g2.drawLine(startX, lineY, endX, lineY);
                        
                        // Access point
                        g2.fillOval(endX - 3, lineY - 3, 6, 6);
                    }
                }
                
                // Page label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("P" + (i + 1), timelineX - 25, lineY + 4);
            }
        }
        
        private void drawAlgorithmComparison(Graphics2D g2, int w, int h) {
            int compY = h - 45;
            int compW = w - 80;
            int compX = 40;
            
            g2.setColor(new Color(60, 70, 100, 150));
            g2.fillRoundRect(compX, compY, compW, 35, 10, 10);
            
            g2.setColor(new Color(200, 220, 255));
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString("Бусад алгоритмуудтай харьцуулалт:", compX + 10, compY + 15);
            
            String[] algorithms = {"FIFO", "OPT", "LFU", "Random"};
            String[] descriptions = {"Эхний орсон нь гарах", "Ирээдүйг таамаглах", "Хамгийн бага хэрэглэгдсэн", "Санамсаргүй"};
            
            for (int i = 0; i < algorithms.length; i++) {
                int algoX = compX + 250 + i * 140;
                
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(algoX, compY + 5, 130, 25, 5, 5);
                
                g2.setColor(i == 2 ? new Color(100, 220, 150) : new Color(200, 200, 220));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(algorithms[i], algoX + 5, compY + 15);
                
                g2.setColor(new Color(180, 180, 200));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString(descriptions[i], algoX + 5, compY + 28);
            }
        }
    }
}