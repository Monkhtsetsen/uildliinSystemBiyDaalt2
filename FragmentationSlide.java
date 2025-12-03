import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class FragmentationSlide extends GradientSlide {

    public FragmentationSlide() {
        setLayout(new BorderLayout());

        // Гарчиг
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JComponent title = Main.createHeader("Дотоод ба гадаад фрагментаци");
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        headerPanel.add(title, BorderLayout.NORTH);
        
        // Тайлбар текст
        JTextArea desc = new JTextArea(
                "• Дотоод фрагментаци – санах ойн хуваалтанд өгөгдөл багтахаас илүү их зай хуваарилагдсан тохиолдолд үүсэх дэмий хоосон зай.\n" +
                "• Гадаад фрагментаци – нийт сул зай хангалттай боловч тасархай жижиг хэсгүүдэд тархсан байдлаас ашиглаж чадахгүй байх үзэгдэл.\n" +
                "• Фрагментаци нь санах ойн ашиглалтын үр ашгийг бууруулж, системийн гүйцэтгэлд сөргөөр нөлөөлдөг.");
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setForeground(new Color(240, 245, 255));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 16));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBorder(BorderFactory.createEmptyBorder(10, 45, 20, 45));
        
        JScrollPane scrollPane = new JScrollPane(desc);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        headerPanel.add(scrollPane, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Хоёр багана
        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 30, 40, 30));

        center.add(new InternalFragmentationPanel());
        center.add(new ExternalFragmentationPanel());

        add(center, BorderLayout.CENTER);
        
        // Тайлбар доод хэсэгт
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        infoPanel.add(createInfoPanel("Дотоод фрагментаци: Санах ой үр ашиггүй ашиглагдана", 
            new Color(120, 180, 255)));
        infoPanel.add(createInfoPanel("Гадаад фрагментаци: Шинэ процесс багтахад хэцүү", 
            new Color(255, 150, 150)));
            
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoPanel(String text, Color color) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(label);
        return panel;
    }
}
class InternalFragmentationPanel extends JPanel implements ActionListener {

    private float anim = 0;      // 0 → 1 гүйцэтгэл
    private Timer timer;
    private int pulseOffset = 0;
    private boolean pulseGrowing = true;

    public InternalFragmentationPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 180, 255, 150), 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Гарчиг
        GradientPaint titleGradient = new GradientPaint(0, 0, new Color(120, 180, 255), 
                                                        w, 0, new Color(200, 220, 255));
        g2.setPaint(titleGradient);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Дотоод фрагментаци", 15, 25);

        // Хуваалтын тогтолцоо
        int x = 30, y = 50;
        int total = w - 60;
        int blockHeight = 50;

        // Бүх санах ойн блок
        g2.setColor(new Color(60, 80, 120, 100));
        g2.fillRoundRect(x, y, total, blockHeight, 15, 15);
        g2.setColor(new Color(180, 200, 255));
        g2.drawRoundRect(x, y, total, blockHeight, 15, 15);

        // Ашиглагдаж буй хэсгүүд
        int[] processSizes = { 40, 30, 15 };
        String[] processNames = { "Process A", "Process B", "Process C" };
        int used = 0;

        for (int i = 0; i < processSizes.length; i++) {
            int actualSize = (int)(total * (processSizes[i] / 100f));
            int animatedSize = (int)(actualSize * anim);
            
            // Градиент өнгө
            GradientPaint processGradient = new GradientPaint(
                x + used, y, new Color(120, 180, 255, 220),
                x + used + animatedSize, y + blockHeight, new Color(80, 140, 220, 180)
            );
            g2.setPaint(processGradient);
            
            // Процесс блок
            Shape block = new RoundRectangle2D.Double(x + used, y, animatedSize - 2, blockHeight, 10, 10);
            g2.fill(block);
            
            // Хүрээ
            g2.setColor(new Color(240, 245, 255));
            g2.draw(block);
            
            // Процесс нэр
            if (animatedSize > 40) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(processNames[i], x + used + 5, y + 20);
                g2.drawString(processSizes[i] + "%", x + used + 5, y + 35);
            }
            
            used += actualSize;
        }

        int[] wastedSpaces = { 8, 12, 5 };
        int wastedIndex = 0;
        int currentPos = 0;
        
        for (int i = 0; i < processSizes.length; i++) {
            int actualSize = (int)(total * (processSizes[i] / 100f));
            int wastedWidth = (int)(total * (wastedSpaces[wastedIndex] / 100f));
            
            if (i < wastedSpaces.length) {
                float wastedAnim = Math.max(0, anim - 0.3f);
                int animatedWasted = (int)(wastedWidth * wastedAnim);
                
                if (animatedWasted > 0) {
                    g2.setColor(new Color(255, 200, 100, 100 + (int)(100 * (pulseOffset / 10f))));
                    g2.fillRoundRect(x + currentPos + actualSize - animatedWasted, y, 
                                    animatedWasted, blockHeight, 10, 10);
                    
                    g2.setColor(new Color(255, 220, 120));
                    g2.drawRoundRect(x + currentPos + actualSize - animatedWasted, y, 
                                    animatedWasted, blockHeight, 10, 10);
                    
                    // "Wasted" текст
                    if (animatedWasted > 30) {
                        g2.setColor(new Color(255, 230, 180));
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                        g2.drawString("Wasted: " + wastedSpaces[wastedIndex] + "%", 
                                    x + currentPos + actualSize - animatedWasted + 5, y + 25);
                    }
                }
                wastedIndex++;
            }
            currentPos += actualSize;
        }
        g2.setColor(new Color(200, 220, 255));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Нийт санах ой: 100%", x, y + blockHeight + 20);
        g2.drawString("Ашиглагдаж буй: 85%", x, y + blockHeight + 35);
        g2.drawString("Дэмий хоосон: 15%", x, y + blockHeight + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        anim += 0.015f;
        if (anim > 1) anim = 1;
        
        // Цацраг анимаци
        if (pulseGrowing) {
            pulseOffset += 1;
            if (pulseOffset >= 10) pulseGrowing = false;
        } else {
            pulseOffset -= 1;
            if (pulseOffset <= 0) pulseGrowing = true;
        }
        
        repaint();
    }
}


/* ==========================
     Гадаад фрагментаци (Сайжруулсан)
   ========================== */
class ExternalFragmentationPanel extends JPanel implements ActionListener {

    private float anim = 0;
    private Timer timer;
    private float waveOffset = 0;
    private Color[] blockColors = {
        new Color(255, 150, 150),
        new Color(255, 180, 120),
        new Color(255, 120, 180),
        new Color(180, 150, 255),
        new Color(150, 220, 255)
    };
    
    // Процессууд болон хоосон зайнууд
    private boolean[] isProcess = { true, false, true, false, true, false, true, false, true };
    private int[] sizes = { 20, 15, 18, 12, 22, 8, 17, 10, 20 };

    public ExternalFragmentationPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 150, 150, 150), 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Гарчиг
        GradientPaint titleGradient = new GradientPaint(0, 0, new Color(255, 150, 150), 
                                                        w, 0, new Color(255, 200, 180));
        g2.setPaint(titleGradient);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Гадаад фрагментаци", 15, 25);

        // Санах ойн блок диаграм
        int x = 30, y = 50;
        int totalWidth = w - 60;
        int blockHeight = 40;
        int verticalSpacing = 60;

        // Эхний мөр - фрагментчлогдсон төлөв
        drawFragmentedRow(g2, x, y, totalWidth, blockHeight, 0);

        // Хоёр дахь мөр - шинэ процесс оруулах оролдлого
        drawNewProcessRow(g2, x, y + verticalSpacing, totalWidth, blockHeight, 1);

        // Гурав дахь мөр - фрагментаци арилгасан төлөв
        drawDefragmentedRow(g2, x, y + verticalSpacing * 2, totalWidth, blockHeight, 2);

        // Хөдөлгөөний сумнууд
        g2.setColor(new Color(255, 200, 150));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Сумын анимаци
        float arrowAnim = (anim * 3) % 1;
        int arrowY1 = y + blockHeight + 5;
        int arrowY2 = y + verticalSpacing - 5;
        
        if (arrowAnim < 0.7f) {
            int currentY = (int)(arrowY1 + (arrowY2 - arrowY1) * (arrowAnim / 0.7f));
            drawAnimatedArrow(g2, w/2 - 40, arrowY1, w/2 - 40, currentY);
        }

        // Статистик мэдээлэл
        g2.setColor(new Color(255, 220, 200));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Фрагментчилсон байдал: " + (int)(anim * 100) + "%", x, y + verticalSpacing * 2 + blockHeight + 30);
        
        int freeBlocks = countFreeBlocks();
        g2.drawString("Чөлөөт блок: " + freeBlocks + " (Тархсан)", x, y + verticalSpacing * 2 + blockHeight + 45);
        
        // Нийт чөлөөт зай
        int totalFree = 0;
        for (int i = 0; i < isProcess.length; i++) {
            if (!isProcess[i]) {
                totalFree += sizes[i];
            }
        }
        g2.drawString("Нийт чөлөөт зай: " + totalFree + "%", x, y + verticalSpacing * 2 + blockHeight + 60);
    }

    private void drawFragmentedRow(Graphics2D g2, int x, int y, int totalWidth, int blockHeight, float offset) {
        g2.setColor(new Color(100, 80, 120, 50));
        g2.fillRoundRect(x, y, totalWidth, blockHeight, 10, 10);
        g2.setColor(new Color(200, 180, 220));
        g2.drawRoundRect(x, y, totalWidth, blockHeight, 10, 10);
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString("Фрагментчилсон санах ой", x + 5, y - 5);

        int pos = 0;
        for (int i = 0; i < sizes.length; i++) {
            int blockWidth = (int)(totalWidth * (sizes[i] / 100f));
            int animatedWidth = (int)(blockWidth * Math.min(1, anim * 1.5f - offset * 0.5f));
            
            if (animatedWidth > 0) {
                // Блокны өнгө
                Color blockColor;
                if (isProcess[i]) {
                    blockColor = new Color(
                        blockColors[i % blockColors.length].getRed(),
                        blockColors[i % blockColors.length].getGreen(),
                        blockColors[i % blockColors.length].getBlue(),
                        180
                    );
                } else {
                    // Чөлөөт зай - тодруулалттай
                    float pulse = (float)(0.7f + 0.3f * Math.sin(waveOffset + i * 0.5f));
                    blockColor = new Color(100, 200, 100, (int)(100 * pulse));
                }
                
                // Тархалтын эффект
                int scatterOffset = (int)(Math.sin(waveOffset * 2 + i) * 5 * anim);
                
                // Блок зурах
                g2.setColor(blockColor);
                g2.fillRoundRect(x + pos + scatterOffset, y, animatedWidth - 2, blockHeight, 8, 8);
                
                // Хүрээ
                g2.setColor(isProcess[i] ? new Color(240, 220, 220) : new Color(200, 240, 200));
                g2.drawRoundRect(x + pos + scatterOffset, y, animatedWidth - 2, blockHeight, 8, 8);
                
                // Текст (хэрэв блок хангалттай том бол)
                if (animatedWidth > 25) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    String label = isProcess[i] ? "P" + (i+1) : "Free";
                    g2.drawString(label, x + pos + scatterOffset + 5, y + 15);
                    g2.drawString(sizes[i] + "%", x + pos + scatterOffset + 5, y + 25);
                }
                
                pos += blockWidth;
            }
        }
    }

    private void drawNewProcessRow(Graphics2D g2, int x, int y, int totalWidth, int blockHeight, float offset) {
        g2.setColor(new Color(100, 80, 120, 50));
        g2.fillRoundRect(x, y, totalWidth, blockHeight, 10, 10);
        g2.setColor(new Color(200, 180, 220));
        g2.drawRoundRect(x, y, totalWidth, blockHeight, 10, 10);
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString("Шинэ процесс (30% шаардлагатай)", x + 5, y - 5);

        // Шинэ процессын хайгуул
        int newProcessSize = 30;
        int requiredWidth = (int)(totalWidth * (newProcessSize / 100f));
        int searchPos = (int)((totalWidth - requiredWidth) * (anim % 0.5f) * 2);
        
        // Шинэ процессын блок (хайж байгаа)
        g2.setColor(new Color(255, 255, 150, 150));
        g2.fillRoundRect(x + searchPos, y, requiredWidth, blockHeight, 8, 8);
        g2.setColor(new Color(255, 255, 100));
        g2.drawRoundRect(x + searchPos, y, requiredWidth, blockHeight, 8, 8);
        
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.drawString("New Process", x + searchPos + 5, y + 15);
        g2.drawString(newProcessSize + "%", x + searchPos + 5, y + 25);
        
        // "X" тэмдэг - багтахгүй байгааг харуулах
        if (anim > 0.7f) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString("✗", x + searchPos + requiredWidth/2 - 5, y + blockHeight/2 + 5);
        }
    }

    private void drawDefragmentedRow(Graphics2D g2, int x, int y, int totalWidth, int blockHeight, float offset) {
        g2.setColor(new Color(100, 80, 120, 50));
        g2.fillRoundRect(x, y, totalWidth, blockHeight, 10, 10);
        g2.setColor(new Color(200, 180, 220));
        g2.drawRoundRect(x, y, totalWidth, blockHeight, 10, 10);
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString("Фрагментаци арилгасан (Дефрагмент)", x + 5, y - 5);

        // Чөлөөт зайг нэгтгэсэн байдал
        int processPos = 0;
        int freePos = totalWidth;
        
        for (int i = 0; i < sizes.length; i++) {
            int blockWidth = (int)(totalWidth * (sizes[i] / 100f));
            
            if (isProcess[i]) {
                // Процессуудыг эхэнд нь байрлуулах
                int animatedWidth = (int)(blockWidth * Math.min(1, (anim - 1.5f) * 2));
                
                if (animatedWidth > 0) {
                    Color blockColor = new Color(
                        blockColors[i % blockColors.length].getRed(),
                        blockColors[i % blockColors.length].getGreen(),
                        blockColors[i % blockColors.length].getBlue(),
                        180
                    );
                    
                    g2.setColor(blockColor);
                    g2.fillRoundRect(x + processPos, y, animatedWidth - 2, blockHeight, 8, 8);
                    g2.setColor(new Color(240, 220, 220));
                    g2.drawRoundRect(x + processPos, y, animatedWidth - 2, blockHeight, 8, 8);
                    
                    if (animatedWidth > 25) {
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                        g2.drawString("P" + (i+1), x + processPos + 5, y + 15);
                        g2.drawString(sizes[i] + "%", x + processPos + 5, y + 25);
                    }
                    
                    processPos += blockWidth;
                }
            }
        }
        
        // Чөлөөт зайг төгсгөлд нь нэгтгэх
        int freeSpaceWidth = totalWidth - processPos;
        if (freeSpaceWidth > 0 && anim > 1.5f) {
            float freeAnim = Math.min(1, (anim - 1.5f) * 2);
            int animatedFreeWidth = (int)(freeSpaceWidth * freeAnim);
            
            g2.setColor(new Color(100, 200, 100, 150));
            g2.fillRoundRect(x + processPos, y, animatedFreeWidth - 2, blockHeight, 8, 8);
            g2.setColor(new Color(200, 240, 200));
            g2.drawRoundRect(x + processPos, y, animatedFreeWidth - 2, blockHeight, 8, 8);
            
            if (animatedFreeWidth > 40) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("Free Space", x + processPos + 10, y + 15);
                g2.drawString(freeSpaceWidth * 100 / totalWidth + "%", x + processPos + 10, y + 30);
                
                // Шинэ процесс багтаж чадна
                g2.setColor(Color.GREEN);
                g2.drawString("✓ New Process fits!", x + processPos + 10, y - 10);
            }
        }
    }

    private void drawAnimatedArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // Сумны шугам
        g2.drawLine(x1, y1, x2, y2);
        
        // Сумны толгой
        int arrowSize = 8;
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(x2, y2);
        arrowHead.addPoint(x2 - arrowSize, y2 - arrowSize);
        arrowHead.addPoint(x2 - arrowSize, y2 + arrowSize);
        g2.fill(arrowHead);
    }

    private int countFreeBlocks() {
        int count = 0;
        for (boolean process : isProcess) {
            if (!process) count++;
        }
        return count;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        anim += 0.01f;
        if (anim > 2.5f) anim = 0;
        
        waveOffset += 0.05f;
        
        repaint();
    }
}