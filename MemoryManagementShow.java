import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MemoryManagementShow
 * - OS: Санах ойн удирдлага сэдвийн 20 "слайд"-тай Java Swing презентац
 * - Text бага, visual их, нэг нь FIFO page replacement симуляцтай
 *
 * Ашиглах заавар:
 * 1. Файлыг MemoryManagementShow.java нэрээр хадгал
 * 2. javac MemoryManagementShow.java
 * 3. java MemoryManagementShow
 *
 * Keyboard:
 * ← / A : previous slide
 * → / D : next slide
 * Home : эхний слайд
 * End : сүүлийн слайд
 */
public class MemoryManagementShow extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final List<JPanel> slides = new ArrayList<>();
    private int currentIndex = 0;
    private final JLabel slideCounter = new JLabel("1 / 20");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryManagementShow::new);
    }

    public MemoryManagementShow() {
        super("Санах ойн удирдлага - Interactive Show");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        createSlides();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cardPanel, BorderLayout.CENTER);
        getContentPane().add(createNavBar(), BorderLayout.SOUTH);

        setupKeyBindings();

        setVisible(true);
    }

    /** Navigation bar (Prev / Next / Slide counter) */
    private JComponent createNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        nav.setBackground(new Color(10, 12, 30));

        JButton prev = new JButton("◀ Өмнөх");
        JButton next = new JButton("Дараах ▶");

        styleNavButton(prev);
        styleNavButton(next);

        prev.addActionListener(e -> showSlide(currentIndex - 1));
        next.addActionListener(e -> showSlide(currentIndex + 1));

        slideCounter.setForeground(Color.WHITE);
        slideCounter.setFont(slideCounter.getFont().deriveFont(Font.BOLD, 16f));
        slideCounter.setHorizontalAlignment(SwingConstants.CENTER);

        nav.add(prev, BorderLayout.WEST);
        nav.add(slideCounter, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        return nav;
    }

    private void styleNavButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 80, 200));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    /** Keyboard shortcuts for slides */
    private void setupKeyBindings() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prev");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "prev");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "first");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "last");

        am.put("next", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSlide(currentIndex + 1);
            }
        });
        am.put("prev", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSlide(currentIndex - 1);
            }
        });
        am.put("first", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSlide(0);
            }
        });
        am.put("last", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSlide(slides.size() - 1);
            }
        });
    }

    /** CardLayout дээр 0..19 хүртэл panel нэмнэ */
    private void createSlides() {
        // 1
        slides.add(new TitleSlide());
        // 2
        slides.add(new OverviewSlide());
        // 3
        slides.add(new MemoryTypesSlide());
        // 4
        slides.add(new HierarchySlide());
        // 5
        slides.add(new BasicMgmtSlide());
        // 6
        slides.add(new AllocationIntroSlide());
        // 7
        slides.add(new ContiguousPartitionSlide());
        // 8
        slides.add(new NonContiguousSlide());
        // 9
        slides.add(new FitAlgorithmsSlide());
        // 10
        slides.add(new FitSmallTableSlide());
        // 11
        slides.add(new VirtualMemorySlide());
        // 12
        slides.add(new PagingSegmentationSlide());
        // 13
        slides.add(new PageReplacementIntroSlide());
        // 14
        slides.add(new FifoSimulationSlide());
        // 15
        slides.add(new LruConceptSlide());
        // 16
        slides.add(new FragmentationSlide());
        // 17
        slides.add(new ThrashingSlide());
        // 18–20: Quiz slides
        slides.add(new QuizSlide(
                "Quiz #1 – Санах ойн төрөл",
                "CPU-д хамгийн ойр, хамгийн хурдан санах ой аль нь вэ?",
                new String[] { "RAM", "Кэш (Cache)", "SSD диск" },
                1));
        slides.add(new QuizSlide(
                "Quiz #2 – Paging vs Segmentation",
                "Paging-т фрейм, хуудасны хэмжээ ямар байдаг вэ?",
                new String[] { "Тогтмол хэмжээтэй", "Хувьсах хэмжээтэй", "Холимог" },
                0));
        slides.add(new QuizSlide(
                "Quiz #3 – Page replacement",
                "LRU алгоритм ямар хуудсыг хасч солих вэ?",
                new String[] {
                        "Хамгийн эрт орсон хуудсыг",
                        "Хамгийн сүүлд ашиглагдсан хуудсыг",
                        "Хамгийн бага давтамжтай ашиглагдсан хуудсыг"
                },
                1));

        // CardLayout-д бүртгэх
        for (int i = 0; i < slides.size(); i++) {
            cardPanel.add(slides.get(i), "slide-" + i);
        }
    }

    private void showSlide(int index) {
        if (index < 0)
            index = 0;
        if (index >= slides.size())
            index = slides.size() - 1;
        currentIndex = index;
        cardLayout.show(cardPanel, "slide-" + currentIndex);
        slideCounter.setText((currentIndex + 1) + " / " + slides.size());
    }

    /* ------------------------ Base reusable panel ------------------------ */

    /** Бүх слайд адилхан dark gradient background авахад зориулсан класс */
    static abstract class GradientSlide extends JPanel {
        protected final Color top = new Color(7, 10, 40);
        protected final Color bottom = new Color(40, 20, 90);

        GradientSlide() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, top, w, h, bottom);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /* ------------------------------ Slides ------------------------------ */

    /** Slide 1 – Title */
    static class TitleSlide extends GradientSlide {
        TitleSlide() {
            setLayout(new GridBagLayout());
            JLabel title = new JLabel("Санах ойн удирдлага");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("SansSerif", Font.BOLD, 52));

            JLabel subtitle = new JLabel("Operating System · Interactive Demo");
            subtitle.setForeground(new Color(190, 200, 255));
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 22));

            JPanel box = new JPanel();
            box.setOpaque(false);
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(title);
            box.add(Box.createVerticalStrut(20));
            box.add(subtitle);

            add(box, new GridBagConstraints());
        }
    }

    /** Slide 2 – What is memory management */
    static class OverviewSlide extends GradientSlide {
        OverviewSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Санах ойн удирдлага гэж юу вэ?"), BorderLayout.NORTH);

            JPanel center = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    // CPU -> MMU -> RAM -> Disk диаграм
                    int cx = w / 2;
                    int cy = h / 2;
                    int boxW = 140;
                    int boxH = 60;

                    drawBox(g2, cx - 3 * boxW, cy - boxH / 2, boxW, boxH, "CPU");
                    drawArrow(g2, cx - 2 * boxW + 10, cy, cx - boxW - 10, cy);
                    drawBox(g2, cx - 2 * boxW, cy - boxH / 2, boxW, boxH, "MMU");
                    drawArrow(g2, cx - boxW + 10, cy, cx - 10, cy);
                    drawBox(g2, cx - boxW / 2, cy - boxH / 2, boxW, boxH, "RAM");
                    drawArrow(g2, cx + boxW / 2 + 10, cy, cx + boxW + 10, cy);
                    drawBox(g2, cx + boxW, cy - boxH / 2, boxW + 20, boxH, "Disk");

                    g2.setColor(new Color(220, 230, 255));
                    g2.setFont(getFont().deriveFont(Font.PLAIN, 18f));
                    g2.drawString("Хаяг хөрвүүлэлт · блок хуваарилалт · чөлөөлөлт",
                            cx - 220, cy + boxH + 50);
                }

                private void drawBox(Graphics2D g2, int x, int y, int w, int h, String text) {
                    g2.setColor(new Color(60, 80, 220, 200));
                    g2.fillRoundRect(x, y, w, h, 16, 16);
                    g2.setColor(new Color(230, 235, 255));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(x, y, w, h, 16, 16);
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = x + (w - fm.stringWidth(text)) / 2;
                    int ty = y + (h + fm.getAscent()) / 2 - 4;
                    g2.drawString(text, tx, ty);
                }

                private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
                    g2.setColor(new Color(200, 210, 255));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(x1, y1, x2, y2);
                    int size = 7;
                    int dx = x2 - x1;
                    int dy = y2 - y1;
                    double angle = Math.atan2(dy, dx);
                    int x = x2;
                    int y = y2;
                    int xA = (int) (x - size * Math.cos(angle - Math.PI / 6));
                    int yA = (int) (y - size * Math.sin(angle - Math.PI / 6));
                    int xB = (int) (x - size * Math.cos(angle + Math.PI / 6));
                    int yB = (int) (y - size * Math.sin(angle + Math.PI / 6));
                    g2.fillPolygon(new int[] { x, xA, xB }, new int[] { y, yA, yB }, 3);
                }
            };
            center.setOpaque(false);
            add(center, BorderLayout.CENTER);
        }
    }

    /** Нийтлэг header component */
    static JComponent createHeader(String titleText) {
        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        p.add(title, BorderLayout.WEST);
        return p;
    }

    /** Slide 3 – RAM/ROM/Cache/Virtual cards */
    static class MemoryTypesSlide extends GradientSlide {
        MemoryTypesSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Санах ойн төрөл"), BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(2, 2, 24, 24));
            grid.setOpaque(false);
            grid.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));

            grid.add(typeCard("RAM", "Шуурхай, дотоод санах ой.\nТэжээл тасарвал алга болно."));
            grid.add(typeCard("ROM", "Зөвхөн уншина.\nТэжээл тасарсан ч хадгалагдана."));
            grid.add(typeCard("Cache", "CPU-д хамгийн ойр.\nСаяхан ашигласан өгөгдлийг барина."));
            grid.add(typeCard("Virtual", "Disk-ийг RAM-ийн өргөтгөл мэт\nашиглах логик санах ой."));

            add(grid, BorderLayout.CENTER);
        }

        private static JComponent typeCard(String title, String body) {
            JPanel card = new JPanel();
            card.setOpaque(false);
            card.setLayout(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(120, 130, 230), 2, true),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)));

            JLabel t = new JLabel(title);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 24));

            JTextArea txt = new JTextArea(body);
            txt.setEditable(false);
            txt.setOpaque(false);
            txt.setForeground(new Color(215, 225, 255));
            txt.setFont(new Font("SansSerif", Font.PLAIN, 16));
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);
            txt.setBorder(null);

            card.add(t, BorderLayout.NORTH);
            card.add(txt, BorderLayout.CENTER);
            return card;
        }
    }

    /** Slide 4 – Memory hierarchy */
    static class HierarchySlide extends GradientSlide {
        HierarchySlide() {
            setLayout(new BorderLayout());
            add(createHeader("Санах ойн шатлал"), BorderLayout.NORTH);

            JPanel center = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();
                    int levels = 5;
                    String[] names = { "Регистр", "Кэш", "RAM", "SSD/HDD", "Cloud / Network" };
                    String[] info = { "хамгийн хурдан", "маш хурдан", "дунд хурд", "удаан", "хамгийн удаан" };
                    int baseWidth = (int) (w * 0.7);
                    int heightStep = 60;
                    int yStart = h / 2 - (levels * heightStep) / 2;

                    for (int i = 0; i < levels; i++) {
                        int rectW = baseWidth - i * 80;
                        int x = (w - rectW) / 2;
                        int y = yStart + i * heightStep;

                        float ratio = i / (float) (levels - 1);
                        Color c = new Color(100 + (int) (80 * ratio), 120, 230);
                        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
                        g2.fillRoundRect(x, y, rectW, 45, 18, 18);
                        g2.setColor(Color.WHITE);
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawRoundRect(x, y, rectW, 45, 18, 18);

                        g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
                        g2.drawString(names[i], x + 12, y + 25);
                        g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
                        g2.drawString(info[i], x + rectW - 120, y + 25);
                    }
                }
            };
            center.setOpaque(false);
            add(center, BorderLayout.CENTER);
        }
    }

    /** Slide 5 – Basic OS/user layout */
    static class BasicMgmtSlide extends GradientSlide {
        BasicMgmtSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Нэг хэрэглэгчийн / олон хэрэглэгчийн санах ойн загвар"),
                    BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

            center.add(column("Энгийн (монопрограммчилсан)",
                    new String[] { "Доод хэсэгт OS", "Дээр нь ганц хэрэглэгчийн програм",
                            "Санах ой дан, хуваалтгүй" }));
            center.add(column("Олон програмтай",
                    new String[] { "Доод хэсэгт OS", "Дээр нь хэд хэдэн процесс",
                            "Хуваалт, хамгаалалт, isolation хэрэгтэй" }));

            add(center, BorderLayout.CENTER);
        }

        private JComponent column(String title, String[] bullets) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            JLabel t = new JLabel(title);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 20));
            p.add(t);
            p.add(Box.createVerticalStrut(10));

            for (String b : bullets) {
                JLabel l = new JLabel("• " + b);
                l.setForeground(new Color(200, 210, 255));
                l.setFont(new Font("SansSerif", Font.PLAIN, 15));
                p.add(l);
            }

            // add stylized memory bar
            p.add(Box.createVerticalStrut(18));
            JPanel bar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    g2.setColor(new Color(30, 40, 120));
                    g2.fillRoundRect(0, 0, w, h, 14, 14);
                    g2.setColor(new Color(110, 140, 255));
                    g2.fillRoundRect(4, 4, w - 8, h / 3, 12, 12);
                    g2.setColor(new Color(160, 220, 160));
                    g2.fillRoundRect(4, h / 3 + 8, w - 8, h / 3, 12, 12);
                }
            };
            bar.setPreferredSize(new Dimension(200, 120));
            bar.setOpaque(false);
            p.add(bar);
            return p;
        }
    }

    /** Slide 6 – Static vs Dynamic allocation explanation */
    static class AllocationIntroSlide extends GradientSlide {
        AllocationIntroSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Статик ба динамик хуваарилалт"), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

            center.add(allocationCard("Статик",
                    "Компиляцийн үед хуваарилна.\nРазмер нь тогтмол, өөрчлөгдөхгүй.\n" +
                            "Embedded систем, stack, global хувьсагч гэх мэт."));
            center.add(allocationCard("Динамик",
                    "Гүйцэтгэлийн үед new/malloc гэх мэтээр \nасуудал үүсэх үед хуваарилна.\n" +
                            "GUI, тоглоом, multimedia програмд түгээмэл."));

            add(center, BorderLayout.CENTER);
        }

        private JComponent allocationCard(String title, String text) {
            JPanel card = new JPanel();
            card.setOpaque(false);
            card.setLayout(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(140, 150, 250), 2, true),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)));

            JLabel label = new JLabel(title);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 24));

            JTextArea area = new JTextArea(text);
            area.setForeground(new Color(215, 225, 255));
            area.setOpaque(false);
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(new Font("SansSerif", Font.PLAIN, 15));

            card.add(label, BorderLayout.NORTH);
            card.add(area, BorderLayout.CENTER);
            return card;
        }
    }

    /** Slide 7 – Contiguous partition techniques */
    static class ContiguousPartitionSlide extends GradientSlide {
        ContiguousPartitionSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Холбогдмол (contiguous) хуваалт"), BorderLayout.NORTH);

            JPanel center = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();
                    int x = (int) (w * 0.15);
                    int width = (int) (w * 0.7);
                    int y = h / 2 - 80;
                    int height = 40;

                    // Fixed partitions
                    drawMemoryBar(g2, x, y, width, height,
                            new Color[] { new Color(130, 180, 255),
                                    new Color(180, 140, 255),
                                    new Color(255, 180, 130) });
                    g2.setColor(Color.WHITE);
                    g2.drawString("Тогтмол хэмжээтэй хуваалт (Fixed size)", x, y - 10);

                    // Variable partitions
                    y += 100;
                    drawMemoryBar(g2, x, y, width, height,
                            new Color[] { new Color(130, 180, 255),
                                    new Color(130, 220, 180),
                                    new Color(250, 150, 180),
                                    new Color(230, 210, 120) });
                    g2.drawString("Хувьсах хэмжээтэй хуваалт (Variable size)", x, y - 10);
                }

                private void drawMemoryBar(Graphics2D g2, int x, int y, int w, int h, Color[] colors) {
                    int segmentW = w / colors.length;
                    for (int i = 0; i < colors.length; i++) {
                        int sx = x + i * segmentW;
                        g2.setColor(new Color(colors[i].getRed(), colors[i].getGreen(),
                                colors[i].getBlue(), 220));
                        g2.fillRoundRect(sx, y, segmentW - 4, h, 14, 14);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(x, y, w, h, 18, 18);
                }
            };
            center.setOpaque(false);
            add(center, BorderLayout.CENTER);
        }
    }

    /** Slide 8 – Non-contiguous allocation (paging/segmentation preview) */
    static class NonContiguousSlide extends GradientSlide {
        NonContiguousSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Холбогдмол бус хуваарилалт"), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(1, 2, 24, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

            center.add(simpleDiagram("Paging",
                    "Процесс = тогтмол хэмжээтэй pages\nФизик санах ой = frames\nPage table ашиглаж map хийнэ."));
            center.add(simpleDiagram("Segmentation",
                    "Процесс = логик сегментүүд (code, data, stack)\n" +
                            "Сегментүүд хувьсах хэмжээтэй.\nSegment table ашиглана."));

            add(center, BorderLayout.CENTER);
        }

        private JComponent simpleDiagram(String title, String text) {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);

            JLabel t = new JLabel(title);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 24));
            p.add(t, BorderLayout.NORTH);

            JTextArea a = new JTextArea(text);
            a.setOpaque(false);
            a.setEditable(false);
            a.setForeground(new Color(210, 225, 255));
            a.setFont(new Font("SansSerif", Font.PLAIN, 15));
            a.setLineWrap(true);
            a.setWrapStyleWord(true);
            p.add(a, BorderLayout.SOUTH);

            JPanel boxes = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    int bx = 20;
                    int by = 10;
                    int bw = (w - 60) / 2;
                    int bh = h - 40;

                    // Left = logical address space
                    g2.setColor(new Color(120, 150, 250));
                    for (int i = 0; i < 4; i++) {
                        g2.fillRoundRect(bx, by + i * (bh / 4 + 4), bw, bh / 4 - 4, 10, 10);
                    }

                    // Right = physical frames scattered
                    int rx = bx + bw + 20;
                    for (int i = 0; i < 4; i++) {
                        int y = by + (i * 20) + (i * 15);
                        g2.setColor(new Color(130 + i * 20, 200 - i * 10, 210));
                        g2.fillRoundRect(rx, y, bw, 18, 10, 10);
                    }
                }
            };
            boxes.setOpaque(false);
            p.add(boxes, BorderLayout.CENTER);
            return p;
        }
    }

    /** Slide 9 – First/Best/Worst fit concepts */
    static class FitAlgorithmsSlide extends GradientSlide {
        FitAlgorithmsSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Санах ойн хуваарилалтын алгоритмууд"), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(1, 3, 16, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(30, 30, 40, 30));

            center.add(algCard("First Fit",
                    "Хамгийн эхэнд таарсан\nчанга хуваалтыг авна.\nХурдан, гэхдээ хагархай их."));
            center.add(algCard("Best Fit",
                    "Хэрэгцээтэй хэмжээнд\nхамгийн ойр жижиг хуваалтыг\nсонгоно."));
            center.add(algCard("Worst Fit",
                    "Хамгийн том боломжит\nхуваалтыг сонгож үлдсэн хэсгийг\nтомоор үлдээнэ."));

            add(center, BorderLayout.CENTER);
        }

        private JComponent algCard(String title, String body) {
            JPanel card = new JPanel();
            card.setOpaque(false);
            card.setLayout(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(170, 180, 255), 2, true),
                    BorderFactory.createEmptyBorder(16, 14, 16, 14)));

            JLabel t = new JLabel(title, SwingConstants.CENTER);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 22));
            card.add(t, BorderLayout.NORTH);

            JTextArea a = new JTextArea(body);
            a.setOpaque(false);
            a.setEditable(false);
            a.setForeground(new Color(220, 230, 255));
            a.setFont(new Font("SansSerif", Font.PLAIN, 15));
            a.setLineWrap(true);
            a.setWrapStyleWord(true);
            card.add(a, BorderLayout.CENTER);

            return card;
        }
    }

    /** Slide 10 – Small table showing example blocks vs process */
    static class FitSmallTableSlide extends GradientSlide {
        FitSmallTableSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Жишээ хүснэгт – First / Best / Worst Fit"), BorderLayout.NORTH);

            String[] columns = { "Хуваалт ID", "Хэмжээ (KB)", "FirstFit", "BestFit", "WorstFit" };
            Object[][] data = {
                    { "P1", "100", "Block #1", "Block #1", "Block #3" },
                    { "P2", "230", "Block #2", "Block #3", "Block #3" },
                    { "P3", "60", "Block #2", "Block #2", "Block #1" },
                    { "P4", "350", "Block #3", "Block #3", "Block #3" },
            };
            JTable table = new JTable(data, columns);
            table.setEnabled(false);
            table.setRowHeight(28);
            table.setFont(new Font("SansSerif", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
            table.getTableHeader().setForeground(new Color(20, 25, 60));
            table.getTableHeader().setBackground(new Color(210, 220, 255));
            table.setForeground(new Color(15, 20, 40));
            table.setGridColor(new Color(180, 190, 240));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.getViewport().setBackground(new Color(230, 235, 255));
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 240), 2));

            JPanel center = new JPanel(new BorderLayout());
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(40, 80, 60, 80));
            center.add(scrollPane, BorderLayout.CENTER);

            add(center, BorderLayout.CENTER);
        }
    }

    /** Slide 11 – Virtual memory concept */
    static class VirtualMemorySlide extends GradientSlide {
        VirtualMemorySlide() {
            setLayout(new BorderLayout());
            add(createHeader("Виртуал санах ой"), BorderLayout.NORTH);

            JPanel center = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    int ramX = (int) (w * 0.2);
                    int diskX = (int) (w * 0.65);
                    int y = h / 2 - 100;
                    int boxW = 150;
                    int boxH = 200;

                    // RAM frames
                    g2.setColor(new Color(120, 170, 255, 210));
                    g2.fillRoundRect(ramX, y, boxW, boxH, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(ramX, y, boxW, boxH, 16, 16);
                    g2.drawString("RAM", ramX + 55, y - 10);
                    for (int i = 0; i < 6; i++) {
                        g2.drawRect(ramX + 20, y + 15 + i * 30, boxW - 40, 24);
                    }

                    // Disk pages
                    g2.setColor(new Color(200, 160, 250, 210));
                    g2.fillRoundRect(diskX, y, boxW, boxH, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(diskX, y, boxW, boxH, 16, 16);
                    g2.drawString("Disk дээрх pages", diskX + 15, y - 10);
                    for (int i = 0; i < 10; i++) {
                        g2.drawRect(diskX + 15, y + 10 + i * 18, boxW - 30, 14);
                    }

                    // Arrows (swap)
                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(new Color(240, 240, 255));
                    int midY = y + boxH / 2;
                    g2.drawLine(ramX + boxW, midY - 25, diskX, midY - 25);
                    g2.drawLine(diskX, midY + 25, ramX + boxW, midY + 25);
                    g2.drawString("Page in", (ramX + diskX + boxW) / 2 - 20, midY - 30);
                    g2.drawString("Page out", (ramX + diskX + boxW) / 2 - 20, midY + 40);
                }
            };
            center.setOpaque(false);

            JTextArea desc = new JTextArea(
                    "• Програмын логик хаяглалтыг бодитоос илүү том “виртуал” зайд ажиллуулна.\n" +
                            "• OS нь шаардлагатай pages-ийг RAM руу татаж (page in), ашиглаагүйг нь буцааж бичнэ (page out).\n"
                            +
                            "• Page replacement алгоритмууд – FIFO, LRU, Optimal гэх мэт – аль хуудсыг гаргахыг шийднэ.");
            desc.setOpaque(false);
            desc.setEditable(false);
            desc.setForeground(new Color(215, 225, 255));
            desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
            desc.setLineWrap(true);
            desc.setWrapStyleWord(true);
            desc.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));

            add(center, BorderLayout.CENTER);
            add(desc, BorderLayout.SOUTH);
        }
    }

    /** Slide 12 – Paging vs segmentation comparison */
    static class PagingSegmentationSlide extends GradientSlide {
        PagingSegmentationSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Хуудаслалт ба сегментац"), BorderLayout.NORTH);

            String[] paging = {
                    "Физик санах ойг тогтмол хэмжээтэй frames болгон хуваана.",
                    "Процессийн логик санах ойг pages болгон хувааж, page table-ээр map хийнэ.",
                    "Дотоод fragmentation байж болно (frame-ээс бага үлдэгдэл)."
            };
            String[] seg = {
                    "Процессийг логик сегментүүд (code, data, stack) болгон хуваана.",
                    "Сегментийн хэмжээ хувьсах, segment table ашиглана.",
                    "Гадаад fragmentation үүсч болно."
            };

            JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
            center.add(column("Paging", paging));
            center.add(column("Segmentation", seg));
            add(center, BorderLayout.CENTER);
        }

        private JComponent column(String title, String[] bullets) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            JLabel t = new JLabel(title);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 24));
            p.add(t);
            p.add(Box.createVerticalStrut(10));
            for (String b : bullets) {
                JLabel l = new JLabel("• " + b);
                l.setForeground(new Color(210, 225, 255));
                l.setFont(new Font("SansSerif", Font.PLAIN, 15));
                p.add(l);
            }
            return p;
        }
    }

    /** Slide 13 – Page replacement intro visual */
    static class PageReplacementIntroSlide extends GradientSlide {
        PageReplacementIntroSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Page replacement алгоритмууд"), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(1, 3, 16, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

            center.add(iconText("FIFO",
                    "Хамгийн түрүүнд орсон page-г хамгийн түрүүнд гаргана.\n" +
                            "Queue бүтэц ашиглана."));
            center.add(iconText("LRU",
                    "Хамгийн сүүлд ашиглагдаагүй (удаан орхигдсон) page-г гаргана.\n" +
                            "Timestamp / stack ашиглаж болно."));
            center.add(iconText("Optimal",
                    "Ирээдүйд хамгийн удаан хэрэглэхгүй page-г гаргах (теоретик).\n" +
                            "Сургалтын жишээнд л ашиглана."));

            add(center, BorderLayout.CENTER);
        }

        private JComponent iconText(String title, String text) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BorderLayout());

            JLabel t = new JLabel(title, SwingConstants.CENTER);
            t.setForeground(Color.WHITE);
            t.setFont(new Font("SansSerif", Font.BOLD, 22));

            JTextArea a = new JTextArea(text);
            a.setOpaque(false);
            a.setEditable(false);
            a.setForeground(new Color(215, 225, 255));
            a.setFont(new Font("SansSerif", Font.PLAIN, 14));
            a.setLineWrap(true);
            a.setWrapStyleWord(true);

            p.add(t, BorderLayout.NORTH);
            p.add(a, BorderLayout.CENTER);
            return p;
        }
    }

    /** Slide 14 – Interactive FIFO simulation */
    static class FifoSimulationSlide extends GradientSlide {
        private final int[] referenceString = { 1, 3, 0, 3, 5, 6, 3 };
        private final int frameCount = 3;
        private final int[][] frameHistory;
        private final boolean[] faultHistory;
        private int currentStep = -1; // -1 = not started yet
        private int fifoPointer = 0;

        private final JLabel statusLabel = new JLabel("Start дарж симуляци эхлүүл.");

        FifoSimulationSlide() {
            setLayout(new BorderLayout());
            add(createHeader("FIFO Page Replacement – Симуляц"), BorderLayout.NORTH);

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

    /** Slide 15 – LRU explanation visual only */
    static class LruConceptSlide extends GradientSlide {
        LruConceptSlide() {
            setLayout(new BorderLayout());
            add(createHeader("LRU алгоритмын санаа"), BorderLayout.NORTH);

            JTextArea text = new JTextArea(
                    "LRU (Least Recently Used) алгоритм нь хамгийн сүүлд ашиглагдаагүй хуудсыг солих зарчимтай.\n\n" +
                            "• Page бүр дээр \"сүүлд ашиглагдсан цаг\" гэсэн metadata байлаа гэж төсөөлье.\n" +
                            "• Page fault гарах бүрт – RAM доторх pages-ийн timestamp-г харж хамгийн хуучныг нь гаргана.\n"
                            +
                            "• Реал OS-д hardware counter эсвэл reference bit ашиглаж ойролцоо LRU-г хэрэгжүүлдэг.");
            text.setOpaque(false);
            text.setEditable(false);
            text.setForeground(new Color(215, 225, 255));
            text.setFont(new Font("SansSerif", Font.PLAIN, 15));
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));

            JPanel center = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();
                    int boxW = 80;
                    int boxH = 50;
                    int startX = w / 2 - 2 * boxW;
                    int y = h / 2 - boxH / 2;

                    int[] pages = { 2, 5, 1, 7 };
                    int[] ages = { 12, 5, 2, 1 }; // smaller → recently used

                    for (int i = 0; i < pages.length; i++) {
                        int x = startX + i * (boxW + 20);
                        g2.setColor(new Color(120, 150, 250, 220));
                        g2.fillRoundRect(x, y, boxW, boxH, 12, 12);
                        g2.setColor(Color.WHITE);
                        g2.drawRoundRect(x, y, boxW, boxH, 12, 12);
                        g2.drawString("P" + pages[i], x + 30, y + 20);
                        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
                        g2.drawString("age=" + ages[i], x + 20, y + 38);
                    }

                    // Arrow to victim (largest age)
                    int victimIndex = 0;
                    for (int i = 1; i < ages.length; i++) {
                        if (ages[i] > ages[victimIndex])
                            victimIndex = i;
                    }
                    int vx = startX + victimIndex * (boxW + 20) + boxW / 2;
                    g2.setColor(new Color(255, 120, 140));
                    g2.drawLine(vx, y + boxH + 10, vx, y + boxH + 40);
                    g2.drawString("LRU victim", vx - 30, y + boxH + 55);
                }
            };
            center.setOpaque(false);

            add(center, BorderLayout.CENTER);
            add(text, BorderLayout.SOUTH);
        }
    }

    /** Slide 16 – Fragmentation */
    static class FragmentationSlide extends GradientSlide {
        FragmentationSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Дотоод ба гадаад фрагментаци"), BorderLayout.NORTH);

            JTextArea desc = new JTextArea(
                    "• Дотоод фрагментаци – хуваалтын доторх ашиглагдахгүй үлдэгдэл зай.\n" +
                            "• Гадаад фрагментаци – жижиг жижиг сул блокнууд нийлээд хангалттай зай болдог ч " +
                            "холбогдмол бус байгаагаас үүдэн ашиглаж чадахгүй байдал.");
            desc.setOpaque(false);
            desc.setEditable(false);
            desc.setForeground(new Color(215, 225, 255));
            desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
            desc.setLineWrap(true);
            desc.setWrapStyleWord(true);
            desc.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

            JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(10, 40, 40, 40));
            center.add(fragmentBlock("Дотоод",
                    new int[] { 60, 40 }, true));
            center.add(fragmentBlock("Гадаад",
                    new int[] { 25, 15, 10, 20, 30 }, false));

            add(desc, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);
        }

        private JComponent fragmentBlock(String label, int[] sizes, boolean internal) {
            return new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, 10, 20);

                    int x = 20, y = 40;
                    int totalWidth = w - 40;
                    int usedWidth = 0;

                    for (int size : sizes) {
                        int blockW = totalWidth * size / 100;
                        g2.setColor(new Color(120, 170, 250, 220));
                        g2.fillRect(x + usedWidth, y, blockW - 4, 40);
                        g2.setColor(Color.WHITE);
                        g2.drawRect(x + usedWidth, y, blockW - 4, 40);
                        usedWidth += blockW;
                    }

                    if (internal) {
                        g2.setColor(new Color(255, 200, 140, 220));
                        g2.fillRect(x + usedWidth - totalWidth / 4, y, totalWidth / 4 - 4, 40);
                        g2.setColor(Color.WHITE);
                        g2.drawString("unused", x + usedWidth - totalWidth / 4 + 10, y + 25);
                    } else {
                        // draw small gaps
                        g2.setColor(new Color(255, 150, 150));
                        g2.drawString("Олон жижиг сул блок", x, y + 70);
                    }
                }
            };
        }
    }

    /** Slide 17 – Thrashing */
    static class ThrashingSlide extends GradientSlide {
        ThrashingSlide() {
            setLayout(new BorderLayout());
            add(createHeader("Thrashing – Page fault хэт их үед"), BorderLayout.NORTH);

            JTextArea text = new JTextArea(
                    "Програмуудын working set RAM-д багтахгүй үед page fault маш их гарч, OS " +
                            "RAM ↔ Disk хооронд pages-ийг байнга зөөхийг thrashing гэнэ.\n\n" +
                            "• CPU utilization унаж, disk I/O л дүүрэн ажиллана.\n" +
                            "• Шийдэл: process-д оногдох frame-ийн тоог нэмэх, " +
                            "идэвхгүй процессыг түр зогсоох, ажиллаж буй програмын тоог цөөрүүлэх.");
            text.setOpaque(false);
            text.setEditable(false);
            text.setForeground(new Color(215, 225, 255));
            text.setFont(new Font("SansSerif", Font.PLAIN, 15));
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));

            JPanel center = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    // simple timeline chart: faults/time
                    int ox = 60;
                    int oy = h - 60;
                    int maxX = w - 40;
                    int maxY = 40;

                    g2.setColor(new Color(210, 220, 255));
                    g2.drawLine(ox, oy, maxX, oy);
                    g2.drawLine(ox, oy, ox, maxY);

                    int[] faults = { 2, 3, 4, 6, 10, 12, 13, 13 };
                    int n = faults.length;
                    int step = (maxX - ox - 20) / (n - 1);

                    g2.setColor(new Color(255, 140, 160));
                    for (int i = 0; i < n - 1; i++) {
                        int x1 = ox + i * step;
                        int y1 = oy - faults[i] * 3;
                        int x2 = ox + (i + 1) * step;
                        int y2 = oy - faults[i + 1] * 3;
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    g2.drawString("Page faults / time", ox, maxY - 10);
                    g2.drawString("Thrashing хэсэг", ox + step * 3, oy - 10);
                }
            };
            center.setOpaque(false);

            add(center, BorderLayout.CENTER);
            add(text, BorderLayout.SOUTH);
        }
    }

    /** Slides 18–20 – Small interactive quiz */
    static class QuizSlide extends GradientSlide {
        private final int correctIndex;

        QuizSlide(String title, String question, String[] options, int correctIndex) {
            this.correctIndex = correctIndex;
            setLayout(new BorderLayout());
            add(createHeader(title), BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

            JLabel q = new JLabel("<html>" + question + "</html>");
            q.setForeground(Color.WHITE);
            q.setFont(new Font("SansSerif", Font.BOLD, 20));
            q.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(q);
            center.add(Box.createVerticalStrut(20));

            ButtonGroup group = new ButtonGroup();
            JPanel btnPanel = new JPanel();
            btnPanel.setOpaque(false);
            btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));

            List<JRadioButton> radios = new ArrayList<>();
            for (int i = 0; i < options.length; i++) {
                JRadioButton rb = new JRadioButton(options[i]);
                rb.setOpaque(false);
                rb.setForeground(new Color(215, 225, 255));
                rb.setFont(new Font("SansSerif", Font.PLAIN, 16));
                group.add(rb);
                btnPanel.add(rb);
                radios.add(rb);
            }
            center.add(btnPanel);

            JLabel feedback = new JLabel("Хариуг сонгоод \"Check\" дар.");
            feedback.setForeground(Color.WHITE);
            feedback.setFont(new Font("SansSerif", Font.PLAIN, 15));
            feedback.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(Box.createVerticalStrut(20));
            center.add(feedback);

            JButton checkBtn = new JButton("Check");
            checkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            checkBtn.setFocusPainted(false);
            checkBtn.setForeground(Color.WHITE);
            checkBtn.setBackground(new Color(90, 110, 240));
            checkBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            checkBtn.addActionListener(e -> {
                int selected = -1;
                for (int i = 0; i < radios.size(); i++) {
                    if (radios.get(i).isSelected()) {
                        selected = i;
                        break;
                    }
                }
                if (selected == -1) {
                    feedback.setText("Эхлээд нэг сонголт дар.");
                    feedback.setForeground(new Color(255, 230, 160));
                } else if (selected == correctIndex) {
                    feedback.setText("ЗӨВ! 🎉");
                    feedback.setForeground(new Color(160, 250, 180));
                } else {
                    feedback.setText("Буруу. Дахин бодоод үз 😄");
                    feedback.setForeground(new Color(255, 160, 160));
                }
            });

            center.add(Box.createVerticalStrut(10));
            center.add(checkBtn);

            add(center, BorderLayout.CENTER);
        }
    }
}
