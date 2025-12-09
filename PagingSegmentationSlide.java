import javax.swing.*;
import java.awt.*;

public class PagingSegmentationSlide extends GradientSlide {
    
    // Color scheme
    private final Color TEXT_COLOR = new Color(210, 225, 255);
    private final Color PAGING_COLOR = new Color(65, 105, 225);    // Blue
    private final Color SEGMENTATION_COLOR = new Color(50, 205, 50);
    
    public PagingSegmentationSlide() {
        setLayout(new BorderLayout(0, 20));
        
        // Header
        JComponent header = Main.createHeader("Хуудаслалт vs Сегментац");
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);
        
        // Main content panel
        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        // Paging data
        String[] pagingPoints = {
            "Физик санах ойг тогтмол хэмжээтэй frames болгон хуваана",
            "Процессийн логик санах ойг pages болгон хувааж, page table-ээр map хийнэ",
            "Дотоод fragmentation байж болно (frame-ээс бага үлдэгдэл)",
            "Шугаман хаягийн орон зай ашигладаг",
            "Хамгаалалт, хуваалцах илүү хялбар"
        };
        
        // Segmentation data
        String[] segPoints = {
            "Процессийг логик сегментүүд (code, data, stack) болгон хуваана",
            "Сегментийн хэмжээ хувьсах, segment table ашиглана",
            "Гадаад fragmentation үүсч болно",
            "Хоёр хэмжээст хаягийн орон зай (segment + offset)",
            "Логик бүтэцтэй илүү нийцнэ"
        };
        
        // Create cards
        center.add(createCard("Paging", "", pagingPoints, PAGING_COLOR, true));
        center.add(createCard("Segmentation", "", segPoints, SEGMENTATION_COLOR, false));
        
        add(center, BorderLayout.CENTER);
        
        // Bottom panel with visualization
        add(createVisualizationPanel(), BorderLayout.SOUTH);
    }
    
    private JComponent createCard(String title, String icon, String[] points, Color accentColor, boolean isPaging) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        
        // Card background with rounded corners
        JPanel cardBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(65, 105, 225),
                    0, getHeight(), new Color(65, 105, 225)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Accent border
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(accentColor);
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                
                g2d.dispose();
            }
        };
        cardBackground.setLayout(new BorderLayout(10, 10));
        cardBackground.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        
        // Title panel with icon
        JPanel titlePanel = new JPanel(new BorderLayout(15, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(accentColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        
        titlePanel.add(iconLabel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Points panel
        JPanel pointsPanel = new JPanel();
        pointsPanel.setOpaque(false);
        pointsPanel.setLayout(new BoxLayout(pointsPanel, BoxLayout.Y_AXIS));
        pointsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10));
        
        for (String point : points) {
            JLabel pointLabel = new JLabel("<html><div style='padding:3px 0;'>• " + point + "</div></html>");
            pointLabel.setForeground(TEXT_COLOR);
            pointLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            pointLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pointsPanel.add(pointLabel);
            pointsPanel.add(Box.createVerticalStrut(5));
        }
        
        // Add small visualization
        pointsPanel.add(Box.createVerticalStrut(15));
        pointsPanel.add(createMiniVisualization(isPaging, accentColor));
        
        // Assemble card
        cardBackground.add(titlePanel, BorderLayout.NORTH);
        cardBackground.add(pointsPanel, BorderLayout.CENTER);
        card.add(cardBackground, BorderLayout.CENTER);
        
        return card;
    }
    
    private JComponent createMiniVisualization(boolean isPaging, Color color) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                if (isPaging) {
                    // Draw paging visualization - equal sized blocks
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(1));
                    
                    int blockSize = 25;
                    int cols = 4;
                    int rows = 3;
                    
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            int x = j * (blockSize + 5);
                            int y = i * (blockSize + 5);
                            
                            // Fill some blocks
                            if ((i + j) % 3 != 0) {
                                g2d.setColor(color);
                                g2d.fillRect(x, y, blockSize, blockSize);
                                g2d.setColor(Color.WHITE);
                                g2d.drawString("P" + (i*cols + j), x + 5, y + 16);
                            } else {
                                g2d.setColor(color.darker());
                                g2d.drawRect(x, y, blockSize, blockSize);
                            }
                        }
                    }
                    
                    g2d.setColor(TEXT_COLOR);
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    g2d.drawString("Тогтмол хэмжээтэй frame-үүд", 0, height - 5);
                    
                } else {
                    // Draw segmentation visualization - variable sized blocks
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(1));
                    
                    int[] segmentHeights = {40, 25, 35, 20, 30};
                    int y = 0;
                    
                    for (int i = 0; i < segmentHeights.length; i++) {
                        if (i % 2 == 0) {
                            g2d.setColor(color);
                            g2d.fillRect(0, y, width, segmentHeights[i]);
                            g2d.setColor(Color.BLUE);
                            g2d.drawString("Seg" + i, 10, y + segmentHeights[i]/2 + 5);
                        } else {
                            g2d.setColor(color.darker());
                            g2d.drawRect(0, y, width, segmentHeights[i]);
                        }
                        y += segmentHeights[i] + 2;
                    }
                    
                    g2d.setColor(TEXT_COLOR);
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    g2d.drawString("Хувьсах хэмжээтэй сегментүүд", 0, height - 5);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 120);
            }
        };
    }
    
    private JComponent createVisualizationPanel() {
        JPanel visPanel = new JPanel(new BorderLayout());
        visPanel.setOpaque(false);
        visPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));
        
        JLabel title = new JLabel("Харьцуулалт:", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Create comparison visualization
        JPanel comparison = new JPanel(new GridLayout(1, 2, 20, 0));
        comparison.setOpaque(false);
        
        comparison.add(createComparisonVisualization(true));
        comparison.add(createComparisonVisualization(false));
        
        visPanel.add(title, BorderLayout.NORTH);
        visPanel.add(comparison, BorderLayout.CENTER);
        
        return visPanel;
    }
    
    private JComponent createComparisonVisualization(boolean isPaging) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Title
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2d.drawString(isPaging ? "Хуудаслалт" : "Сегментац", 10, 20);
                
                // Subtitle
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2d.drawString(isPaging ? "Тогтмол хэмжээ" : "Хувьсах хэмжээ", 10, 35);
                
                // Draw visualization
                if (isPaging) {
                    drawPagingVisualization(g2d, width, height);
                } else {
                    drawSegmentationVisualization(g2d, width, height);
                }
            }
            
            private void drawPagingVisualization(Graphics2D g2d, int width, int height) {
                // Draw logical memory
                g2d.setColor(new Color(100, 149, 237, 150));
                g2d.fillRoundRect(10, 50, width - 20, 80, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Логик санах ой (Pages)", 15, 65);
                
                // Draw pages
                int pageSize = 30;
                for (int i = 0; i < 4; i++) {
                    int x = 15 + i * (pageSize + 5);
                    g2d.setColor(PAGING_COLOR);
                    g2d.fillRect(x, 75, pageSize, pageSize);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("Pg" + i, x + 8, 93);
                }
                
                // Draw mapping arrow
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int arrowY = 135;
                g2d.drawLine(width/2, arrowY, width/2, arrowY + 30);
                
                // Draw arrow head
                Polygon arrowHead = new Polygon();
                arrowHead.addPoint(width/2 - 5, arrowY + 25);
                arrowHead.addPoint(width/2 + 5, arrowY + 25);
                arrowHead.addPoint(width/2, arrowY + 30);
                g2d.fill(arrowHead);
                
                // Draw physical memory
                g2d.setColor(new Color(100, 149, 237, 150));
                g2d.fillRoundRect(10, 170, width - 20, 100, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Физик санах ой (Frames)", 15, 185);
                
                // Draw frames (scattered)
                g2d.setColor(PAGING_COLOR);
                g2d.fillRect(15, 195, 30, 30);  // Frame 0
                g2d.fillRect(50, 225, 30, 30);  // Frame 2
                g2d.fillRect(85, 195, 30, 30);  // Frame 1
                g2d.fillRect(120, 235, 30, 30); // Frame 3
                
                // Draw fragmentation
                g2d.setColor(new Color(255, 100, 100, 100));
                g2d.fillRect(15, 230, 30, 15);  // Internal fragmentation
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2d.drawString("Дотоод fragmentation", 10, 280);
            }
            
            private void drawSegmentationVisualization(Graphics2D g2d, int width, int height) {
                // Draw logical segments
                g2d.setColor(new Color(60, 179, 113, 150));
                g2d.fillRoundRect(10, 50, width - 20, 80, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Логик сегментүүд", 15, 65);
                
                // Draw different sized segments
                g2d.setColor(SEGMENTATION_COLOR);
                int[] segHeights = {25, 35, 20};
                int y = 75;
                for (int i = 0; i < segHeights.length; i++) {
                    g2d.fillRect(15, y, width - 30, segHeights[i]);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(i == 0 ? "Code" : i == 1 ? "Data" : "Stack", 20, y + segHeights[i]/2 + 5);
                    g2d.setColor(SEGMENTATION_COLOR);
                    y += segHeights[i] + 5;
                }
                
                // Draw mapping arrow
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int arrowY = 135;
                g2d.drawLine(width/2, arrowY, width/2, arrowY + 30);
                
                // Draw arrow head
                Polygon arrowHead = new Polygon();
                arrowHead.addPoint(width/2 - 5, arrowY + 25);
                arrowHead.addPoint(width/2 + 5, arrowY + 25);
                arrowHead.addPoint(width/2, arrowY + 30);
                g2d.fill(arrowHead);
                
                // Draw physical memory with fragmentation
                g2d.setColor(new Color(60, 179, 113, 150));
                g2d.fillRoundRect(10, 170, width - 20, 100, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Физик санах ой", 15, 185);
                
                // Draw segments scattered with gaps
                g2d.setColor(SEGMENTATION_COLOR);
                g2d.fillRect(15, 195, width - 80, 25);   // Code segment
                g2d.fillRect(15, 225, 50, 35);           // Data segment (part)
                g2d.fillRect(70, 225, width - 100, 20);  // Stack segment
                
                // Draw fragmentation gaps
                g2d.setColor(new Color(255, 100, 100, 100));
                g2d.fillRect(width - 65, 195, 50, 25);   // External fragmentation
                g2d.fillRect(65, 225, 5, 35);           // Gap between segments
                
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2d.drawString("Гадаад fragmentation", 10, 280);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 300);
            }
        };
    }
}