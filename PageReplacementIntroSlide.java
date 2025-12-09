import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PageReplacementIntroSlide extends GradientSlide {
    
    // Color scheme
    private final Color TITLE_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = new Color(215, 225, 255);
    private final Color CARD_BG = new Color(25, 35, 65, 180);
    private final Color[] ALGORITHM_COLORS = {
        new Color(65, 105, 225),  // Royal Blue for FIFO
        new Color(50, 205, 50),   // Lime Green for LRU
        new Color(255, 69, 0)     // Orange Red for Optimal
    };
    
    public PageReplacementIntroSlide() {
        setLayout(new BorderLayout(0, 20));
        
        // Create and add header
        JComponent header = Main.createHeader("Page Replacement Алгоритмууд");
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);
        
        // Create center panel with cards
        JPanel center = new JPanel(new GridLayout(1, 3, 20, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 50, 40));
        
        // Algorithm data
        String[] titles = {"FIFO", "LRU", "Optimal"};
        String[] descriptions = {
            "Хамгийн түрүүнд орсон page-г хамгийн түрүүнд гаргана.\n\n" +
            "• Queue бүтэц ашиглана\n" +
            "• Тохиромжтой: Энгийн тохиолдолд\n" +
            "• Сул тал: Belady's anomaly",
            
            "Хамгийн сүүлд ашиглагдаагүй (удаан орхигдсон) page-г гаргана.\n\n" +
            "• Timestamp / stack ашиглаж болно\n" +
            "• Тохиромжтой: Locality-тэй програмууд\n" +
            "• Практикт өргөн хэрэглэгддэг",
            
            "Ирээдүйд хамгийн удаан хэрэглэгдэхгүй page-г гаргах (теоретик).\n\n" +
            "• Хамгийн оновчтой алгоритм\n" +
            "• Сургалтын жишээнд л ашиглана\n" +
            "• Бусад алгоритмын жишиг болдог"
        };
        
        String[] icons = {"🔄", "📊", "⭐"};
        
        // Create cards
        for (int i = 0; i < 3; i++) {
            center.add(createAlgorithmCard(titles[i], descriptions[i], icons[i], ALGORITHM_COLORS[i]));
        }
        
        add(center, BorderLayout.CENTER);
        
        // Add footer note
        JLabel footer = new JLabel("Page replacement нь physical memory дуусахад ашиглагддаг", SwingConstants.CENTER);
        footer.setForeground(new Color(180, 200, 255));
        footer.setFont(new Font("SansSerif", Font.ITALIC, 13));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(footer, BorderLayout.SOUTH);
    }
    
    private JComponent createAlgorithmCard(String title, String text, String icon, Color accentColor) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        // Card background
        JPanel cardBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_BG,
                    getWidth(), getHeight(), new Color(35, 45, 85, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Accent border
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(accentColor);
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                
                g2d.dispose();
            }
        };
        cardBackground.setLayout(new BorderLayout(10, 10));
        cardBackground.setOpaque(false);
        
        // Icon and title panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(accentColor);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Description text area
        JTextArea description = new JTextArea(text);
        description.setOpaque(false);
        description.setEditable(false);
        description.setForeground(TEXT_COLOR);
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Add components to card
        cardBackground.add(topPanel, BorderLayout.NORTH);
        cardBackground.add(description, BorderLayout.CENTER);
        
        card.add(cardBackground, BorderLayout.CENTER);
        
        return card;
    }
    
    // Optional: Add animation or hover effects
    static class HoverEffect extends MouseAdapter {
        private final Component component;
        
        HoverEffect(Component component) {
            this.component = component;
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Can add scale animation here
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            component.setCursor(Cursor.getDefaultCursor());
        }
    }
}