import javax.swing.*;
import java.awt.*;

public class FitAlgorithmsSlide extends GradientSlide {

    public FitAlgorithmsSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Санах ойн хуваарилалтын алгоритмууд"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 16, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 30, 40, 30));

        center.add(algCard(
                "First Fit",
                "• Сул блокийг зүүнээс нь дараалж шалгана.\n" +
                "• Хүссэн хэмжээг багтааж чадсан хамгийн эхний блокт байрлуулна.\n" +
                "• Хэрэгжүүлэхэд хурдан ч удах тусам санах ой их (fragmentation)-тай болдог.",
                AlgType.FIRST
        ));

        center.add(algCard(
                "Best Fit",
                "• Бүх тохирох сул блокуудаас аль боломжитыг нь шалгана.\n" +
                "• Хүссэн хэмжээнд хамгийн ойрхон, хамгийн жижиг хангалттай блокийг сонгоно.\n" +
                "• Үлдэгдэл нь жижиг жижиг олон сул зай болж external fragmentation нэмэгдэнэ.",
                AlgType.BEST
        ));

        center.add(algCard(
                "Worst Fit",
                "• Хамгийн том сул блок дээр шинэ процессыг байршуулна.\n" +
                "• Үлдсэн хэсэг аль болох томоор үлдэнэ.\n" +
                "• Дараагийн том хүсэлтүүдийг багтаах боломжийг нэмэгдүүлэх зорилготой.",
                AlgType.WORST
        ));

        add(center, BorderLayout.CENTER);
    }

    /* ======================== Алгоритмын төрөл ======================== */

    private enum AlgType {
        FIRST, BEST, WORST
    }

    /* ======================== Card үүсгэх ======================== */

    private JComponent algCard(String title, String body, AlgType type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 180, 255), 2, true),
                BorderFactory.createEmptyBorder(16, 14, 14, 14)
        ));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.BOLD, 22));
        t.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(t, BorderLayout.NORTH);

        JTextArea a = new JTextArea(body);
        a.setOpaque(false);
        a.setEditable(false);
        a.setForeground(new Color(220, 230, 255));
        a.setFont(new Font("SansSerif", Font.PLAIN, 13));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(a, BorderLayout.CENTER);

        AlgDiagramPanel diagram = new AlgDiagramPanel(type);
        diagram.setPreferredSize(new Dimension(0, 90));
        card.add(diagram, BorderLayout.SOUTH);

        return card;
    }

    /* ======================== Диаграмын panel ======================== */

    private static class AlgDiagramPanel extends JPanel {
        private final AlgType type;

        public AlgDiagramPanel(AlgType type) {
            this.type = type;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int barX = 12;
            int barY = h / 2 - 10;
            int barW = w - 24;
            int barH = 20;

            // Background frame
            g2.setColor(new Color(15, 23, 42, 180));
            g2.fillRoundRect(barX - 4, barY - 6, barW + 8, barH + 12, 14, 14);

            // Request size label
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(new Color(209, 213, 219));
            g2.drawString("Шинэ хүсэлт: 18MB", barX, barY - 10);

            // Memory free blocks – алгоритм бүр өөр шийдвэр гаргана
            switch (type) {
                case FIRST -> drawFirstFit(g2, barX, barY, barW, barH);
                case BEST  -> drawBestFit(g2, barX, barY, barW, barH);
                case WORST -> drawWorstFit(g2, barX, barY, barW, barH);
            }
        }

        /* ---------- First Fit: зүүнээс эхний таарах блок ---------- */
        private void drawFirstFit(Graphics2D g2, int x, int y, int w, int h) {
            // 4 blocks: 10, 20, 8, 24 (MB)
            double[] ratios = {0.18, 0.28, 0.14, 0.30};
            boolean[] chosen = {false, true, false, false};

            int currX = x;
            for (int i = 0; i < ratios.length; i++) {
                int bw = (int) (w * ratios[i]) - 3;

                if (chosen[i]) {
                    g2.setColor(new Color(52, 211, 153));   // сонгогдсон блок
                } else {
                    g2.setColor(new Color(59, 130, 246));
                }
                g2.fillRoundRect(currX, y, bw, h, 10, 10);

                g2.setColor(new Color(15, 23, 42, 220));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("free", currX + 5, y + h / 2 + 3);

                currX += bw + 3;
            }

            g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
            g2.setColor(new Color(209, 213, 219));
            g2.drawString("→18MB-ийг багтааж чадах хамгийн эхний блок дээр байршуулна.", x, y + h + 14);
        }

        /* ---------- Best Fit: хамгийн ойр жижиг блок ---------- */
        private void drawBestFit(Graphics2D g2, int x, int y, int w, int h) {
            // 4 blocks: 12, 20, 30, 40 (MB) – 18MB-д хамгийн ойр нь 20
            double[] ratios = {0.16, 0.24, 0.26, 0.30};
            boolean[] chosen = {false, true, false, false};

            int currX = x;
            for (int i = 0; i < ratios.length; i++) {
                int bw = (int) (w * ratios[i]) - 3;

                if (chosen[i]) {
                    g2.setColor(new Color(52, 211, 153));
                } else {
                    g2.setColor(new Color(59, 130, 246));
                }
                g2.fillRoundRect(currX, y, bw, h, 10, 10);

                g2.setColor(new Color(15, 23, 42, 220));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("free", currX + 5, y + h / 2 + 3);

                currX += bw + 3;
            }

            g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
            g2.setColor(new Color(209, 213, 219));
            g2.drawString("→блокуудаас хамгийн (жижиг) блокийг сонгож байршуулна.", x, y + h + 14);
        }

        /* ---------- Worst Fit: хамгийн том блок ---------- */
        private void drawWorstFit(Graphics2D g2, int x, int y, int w, int h) {
            // 4 blocks: 12, 22, 26, 40 (MB) – хамгийн том нь 40
            double[] ratios = {0.16, 0.20, 0.24, 0.34};
            boolean[] chosen = {false, false, false, true};

            int currX = x;
            for (int i = 0; i < ratios.length; i++) {
                int bw = (int) (w * ratios[i]) - 3;

                if (chosen[i]) {
                    g2.setColor(new Color(52, 211, 153));
                } else {
                    g2.setColor(new Color(59, 130, 246));
                }
                g2.fillRoundRect(currX, y, bw, h, 10, 10);

                g2.setColor(new Color(15, 23, 42, 220));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("free", currX + 5, y + h / 2 + 3);

                currX += bw + 3;
            }

            g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
            g2.setColor(new Color(209, 213, 219));
            g2.drawString("→дараагийн том хүсэлтэнд том зай үлдээнэ.", x, y + h + 14);
        }
    }
}
