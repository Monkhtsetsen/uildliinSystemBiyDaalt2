import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;

public class VirtualMemorySlide extends GradientSlide {
    private final ArrayList<MovingPage> movingPages = new ArrayList<>();
    private int pageCounter = 0;
    private boolean animationActive = true;
    private final Random random = new Random();
    private final Timer animationTimer;

    public VirtualMemorySlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Виртуал санах ой"), BorderLayout.NORTH);

        // Animation control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        JButton toggleBtn = new JButton("Анимацийг зогсоох");
        toggleBtn.setBackground(new Color(70, 130, 180));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);
        toggleBtn.addActionListener(e -> {
            animationActive = !animationActive;
            toggleBtn.setText(animationActive ? "Анимацийг зогсоох" : "Анимацийг эхлүүлэх");
        });
        
        JButton addPageBtn = new JButton("Page үүсгэх");
        addPageBtn.setBackground(new Color(100, 150, 100));
        addPageBtn.setForeground(Color.WHITE);
        addPageBtn.setFocusPainted(false);
        addPageBtn.addActionListener(e -> createRandomPage());
        
        controlPanel.add(toggleBtn);
        controlPanel.add(addPageBtn);
        add(controlPanel, BorderLayout.NORTH);

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                int ramX = (int) (w * 0.15);
                int diskX = (int) (w * 0.65);
                int y = h / 2 - 120;
                int ramW = 180;
                int ramH = 240;
                int diskW = 180;
                int diskH = 300;

                // RAM - Цэнхэр өнгө
                GradientPaint ramGradient = new GradientPaint(
                    ramX, y, new Color(100, 150, 255, 220),
                    ramX, y + ramH, new Color(60, 110, 210, 220)
                );
                g2.setPaint(ramGradient);
                g2.fillRoundRect(ramX, y, ramW, ramH, 20, 20);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(ramX, y, ramW, ramH, 20, 20);
                
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("RAM (Бодит санах ой)", ramX + 15, y - 10);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Хязгаарлагдмал хүчин чадал", ramX + 15, y + ramH + 15);

                // RAM доторх frame-үүд
                g2.setColor(new Color(255, 255, 255, 120));
                for (int i = 0; i < 8; i++) {
                    int frameY = y + 15 + i * 28;
                    g2.setColor(new Color(255, 255, 255, 80));
                    g2.drawRect(ramX + 15, frameY, ramW - 30, 22);
                    
                    // Хэрэглэгдэж буй frame-д өөр өнгө
                    if (i < 5) {
                        g2.setColor(new Color(100, 200, 255, 100));
                        g2.fillRect(ramX + 16, frameY + 1, ramW - 31, 21);
                    }
                    
                    if (i < 5) {
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                        g2.drawString("Frame " + i + ": Page " + (i + 1), ramX + 20, frameY + 15);
                    }
                }

                // Disk - Нил ягаан өнгө
                GradientPaint diskGradient = new GradientPaint(
                    diskX, y, new Color(180, 140, 240, 220),
                    diskX, y + diskH, new Color(140, 100, 200, 220)
                );
                g2.setPaint(diskGradient);
                g2.fillRoundRect(diskX, y, diskW, diskH, 20, 20);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(diskX, y, diskW, diskH, 20, 20);
                
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("Disk (Виртуал санах ой)", diskX + 10, y - 10);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Том хэмжээтэй зай", diskX + 35, y + diskH + 15);

                // Disk доторх pages
                for (int i = 0; i < 16; i++) {
                    int pageY = y + 10 + i * 18;
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.drawRect(diskX + 15, pageY, diskW - 30, 14);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.drawString("Page " + (i + 10), diskX + 20, pageY + 10);
                }

                // Хөдөлж буй pages-уудыг зурах
                synchronized (movingPages) {
                    for (MovingPage page : movingPages) {
                        page.draw(g2);
                    }
                }

                // Сумнууд
                drawArrows(g2, ramX + ramW, diskX, y + ramH/2);
            }
        };
        center.setOpaque(false);

        // Тайлбар текст
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        JTextArea desc = new JTextArea(
                "• Виртуал санах ой нь програмд бодит санах ойноос илүү том логик хаягын орон зай өгдөг.\n" +
                "• Хэрэглэгчийн програм нь виртуал хаяг ашигладаг бөгөөд үүнийг санах ойн менежер (MMU) бодит хаяг руу хөрвүүлдэг.\n" +
                "• RAM дүүрэх үед OS нь ашиглагдаагүй хуудсуудыг Disk руу шилжүүлж (Page out), шаардлагатай хуудсуудыг RAM руу дуудаж (Page in) авдаг.\n" +
                "• Page replacement алгоритмууд (FIFO, LRU, Optimal) нь аль хуудсыг солихыг шийддэг.");
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setForeground(new Color(215, 225, 255));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        
        // Алгоритмуудын харьцуулалт
        JPanel algorithmPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        algorithmPanel.setOpaque(false);
        algorithmPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        
        String[] algorithms = {"FIFO", "LRU", "Optimal", "Clock"};
        String[] descriptions = {
            "Эхэлж орсон хуудсыг эхлэлд нь гаргана",
            "Хамгийн урт хугацаанд ашиглагдаагүй хуудсыг гаргана",
            "Ирээдүйд хамгийн алслагдсан хуудсыг гаргана",
            "Ашиглалтын битийг шалгаж хуудсыг сонгоно"
        };
        
        for (int i = 0; i < algorithms.length; i++) {
            JPanel algoCard = createAlgorithmCard(algorithms[i], descriptions[i]);
            algorithmPanel.add(algoCard);
        }
        
        textPanel.add(desc, BorderLayout.NORTH);
        textPanel.add(algorithmPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(textPanel, BorderLayout.SOUTH);

        // Анимацийн таймер
        animationTimer = new Timer(30, e -> {
            if (animationActive) {
                synchronized (movingPages) {
                    Iterator<MovingPage> iterator = movingPages.iterator();
                    while (iterator.hasNext()) {
                        MovingPage page = iterator.next();
                        page.update();
                        if (page.isFinished()) {
                            iterator.remove();
                        }
                    }
                    
                    // Санамсаргүй page үүсгэх
                    if (random.nextInt(100) < 3) { // 3% магадлалтай
                        createRandomPage();
                    }
                }
                center.repaint();
            }
        });
        animationTimer.start();
        
        // Эхлэх page-үүд
        for (int i = 0; i < 3; i++) {
            createRandomPage();
        }
    }

    private void drawArrows(Graphics2D g2, int startX, int endX, int centerY) {
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Page in сум (Диск → RAM)
        g2.setColor(new Color(100, 255, 100, 200));
        int[] xPointsIn = {startX, startX + 50, endX - 50, endX};
        int[] yPointsIn = {centerY - 40, centerY - 40, centerY - 40, centerY - 40};
        drawCurvedArrow(g2, xPointsIn, yPointsIn, false);
        g2.setColor(Color.WHITE);
        g2.drawString("Page In (Диск → RAM)", (startX + endX)/2 - 60, centerY - 50);

        // Page out сум (RAM → Диск)
        g2.setColor(new Color(255, 100, 100, 200));
        int[] xPointsOut = {endX, endX - 50, startX + 50, startX};
        int[] yPointsOut = {centerY + 40, centerY + 40, centerY + 40, centerY + 40};
        drawCurvedArrow(g2, xPointsOut, yPointsOut, true);
        g2.setColor(Color.WHITE);
        g2.drawString("Page Out (RAM → Диск)", (startX + endX)/2 - 60, centerY + 55);
    }

    private void drawCurvedArrow(Graphics2D g2, int[] x, int[] y, boolean reverse) {
        // Муруй шугам зурна
        for (int i = 0; i < x.length - 1; i++) {
            g2.drawLine(x[i], y[i], x[i+1], y[i+1]);
        }
        
        // Сумны толгой
        int arrowX = reverse ? x[0] : x[x.length-1];
        int arrowY = y[0];
        Polygon arrowHead = new Polygon();
        if (reverse) {
            arrowHead.addPoint(arrowX, arrowY);
            arrowHead.addPoint(arrowX + 15, arrowY - 8);
            arrowHead.addPoint(arrowX + 15, arrowY + 8);
        } else {
            arrowHead.addPoint(arrowX, arrowY);
            arrowHead.addPoint(arrowX - 15, arrowY - 8);
            arrowHead.addPoint(arrowX - 15, arrowY + 8);
        }
        g2.fill(arrowHead);
    }

    private void createRandomPage() {
        int w = getWidth();
        int h = getHeight();
        
        if (w == 0 || h == 0) return;
        
        boolean fromDiskToRam = random.nextBoolean();
        int startX, startY, endX, endY;
        
        if (fromDiskToRam) {
            // Дискнээс RAM руу
            startX = (int) (w * 0.65) + 90;
            endX = (int) (w * 0.15) + 90;
            startY = getHeight()/2 - 120 + random.nextInt(280);
            endY = getHeight()/2 - 120 + 15 + random.nextInt(8) * 28;
        } else {
            // RAM-аас Диск рүү
            startX = (int) (w * 0.15) + 90;
            endX = (int) (w * 0.65) + 90;
            startY = getHeight()/2 - 120 + 15 + random.nextInt(8) * 28;
            endY = getHeight()/2 - 120 + 10 + random.nextInt(16) * 18;
        }
        
        Color pageColor = fromDiskToRam ? 
            new Color(100, 255, 100, 180) : // Ногоон - Page in
            new Color(255, 100, 100, 180);  // Улаан - Page out
        
        synchronized (movingPages) {
            movingPages.add(new MovingPage(
                "Page " + (++pageCounter),
                startX, startY, endX, endY,
                pageColor,
                fromDiskToRam ? "RAM руу орж байна" : "Диск рүү гарч байна"
            ));
        }
    }

    private JPanel createAlgorithmCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 255, 255, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(100, 200, 255));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JTextArea descArea = new JTextArea(description);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setForeground(new Color(215, 225, 255));
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descArea, BorderLayout.CENTER);
        
        return card;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            animationTimer.stop();
        } else if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    // Хөдөлж буй Page класс
    class MovingPage {
        private final String label;
        private final Color color;
        private final String action;
        private float x, y;
        private final float targetX, targetY;
        private final float speed;
        private float progress = 0;
        
        public MovingPage(String label, int startX, int startY, int targetX, int targetY, Color color, String action) {
            this.label = label;
            this.x = startX;
            this.y = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.color = color;
            this.action = action;
            this.speed = 0.02f + random.nextFloat() * 0.03f;
        }
        
        public void update() {
            if (progress < 1) {
                progress += speed;
                x = x + (targetX - x) * speed;
                y = y + (targetY - y) * speed;
            }
        }
        
        public void draw(Graphics2D g2) {
            if (progress >= 1) return;
            
            // Page хэлбэр
            g2.setColor(color);
            g2.fillRoundRect((int)x - 25, (int)y - 10, 50, 20, 8, 8);
            
            // Хүрээ
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect((int)x - 25, (int)y - 10, 50, 20, 8, 8);
            
            // Текст
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            g2.drawString(label, (int)x - textWidth/2, (int)y + 4);
            
            // Үйлдлийн текст
            g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString(action, (int)x - 35, (int)y - 15);
            
            // Замын шугам
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{5}, progress * 20));
            g2.setColor(new Color(255, 255, 255, 100));
            g2.drawLine((int)(x), (int)(y), (int)(targetX), (int)(targetY));
        }
        
        public boolean isFinished() {
            return progress >= 0.95;
        }
    }
}