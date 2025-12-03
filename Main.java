import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final List<JPanel> slides = new ArrayList<>();
    private int currentIndex = 0;
    private final JLabel slideCounter = new JLabel("1 / 20");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
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

    private void createSlides() {
        slides.add(new TitleSlide());
        slides.add(new OverviewSlide());
        slides.add(new MemoryTypesSlide());
        slides.add(new HierarchySlide());
        slides.add(new BasicMgmtSlide());
        slides.add(new AllocationIntroSlide());
        slides.add(new ContiguousPartitionSlide());
        slides.add(new NonContiguousSlide());
        slides.add(new FitAlgorithmsSlide());
        slides.add(new FitSmallTableSlide());
        slides.add(new VirtualMemorySlide());
        slides.add(new PagingSegmentationSlide());
        slides.add(new PageReplacementIntroSlide());
        slides.add(new FifoSimulationSlide());
        slides.add(new LruConceptSlide());
        slides.add(new FragmentationSlide());
        slides.add(new ThrashingSlide());
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
                new String[] { "Хамгийн эрт орсон хуудсыг", "Хамгийн сүүлд ашиглагдсан хуудсыг", "Хамгийн бага давтамжтай ашиглагдсан хуудсыг" },
                1));

        for (int i = 0; i < slides.size(); i++) {
            cardPanel.add(slides.get(i), "slide-" + i);
        }
    }

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

    private void showSlide(int index) {
        if (index < 0)
            index = 0;
        if (index >= slides.size())
            index = slides.size() - 1;
        currentIndex = index;
        cardLayout.show(cardPanel, "slide-" + currentIndex);
        slideCounter.setText((currentIndex + 1) + " / " + slides.size());
    }

    /** Нийтлэг header component (public for slide classes to call) */
    public static JComponent createHeader(String titleText) {
        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        p.add(title, BorderLayout.WEST);
        return p;
    }
}
