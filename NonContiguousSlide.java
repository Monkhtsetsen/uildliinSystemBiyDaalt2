import javax.swing.*;
import java.awt.*;

public class NonContiguousSlide extends GradientSlide {

    public NonContiguousSlide() {
        setLayout(new BorderLayout());
        add(Main.createHeader("Холбогдмол бус хуваарилалт"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 32, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        center.add(new PagingPanel());
        center.add(new SegmentationPanel());

        add(center, BorderLayout.CENTER);
    }

    /* ===================== Зүүн тал: Paging ===================== */

    private static class PagingPanel extends JPanel {
        public PagingPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.drawString("Paging", 10, 26);

            // Bullets – илүү ойлгомжтой монгол үгтэй
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(210, 225, 255));
            g2.drawString("• Процессийн логик санах ойг ижил хэмжээтэй page-үүдэд хуваана.", 10, 48);
            g2.drawString("• Физик санах ойг мөн ижил хэмжээтэй frame-үүдэд хуваана.", 10, 66);
            g2.drawString("• Page table нь page бүр аль frame дотор байгааг зааж өгдөг.", 10, 84);

            // Boxes – арай доош нь буулгаад зай гаргав
            int boxTop = 130;
            int boxHeight = h - 230;

            // Logical address space (pages)
            int leftX = 30;
            int leftW = 90;

            g2.setColor(new Color(15, 23, 42, 170));
            g2.fillRoundRect(leftX - 10, boxTop - 10, leftW + 20, boxHeight + 20, 18, 18);

            g2.setColor(new Color(148, 163, 253));
            g2.drawRoundRect(leftX - 10, boxTop - 10, leftW + 20, boxHeight + 20, 18, 18);

            int pages = 4;
            int gap = 6;
            int innerH = boxHeight - (pages + 1) * gap;
            int pageH = innerH / pages;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (int i = 0; i < pages; i++) {
                int y = boxTop + gap + i * (pageH + gap);
                g2.setColor(new Color(96, 165, 250));
                g2.fillRoundRect(leftX, y, leftW, pageH, 10, 10);

                g2.setColor(new Color(15, 23, 42, 220));
                g2.drawString("Page " + i, leftX + 10, y + pageH / 2 + 4);
            }

            g2.setColor(new Color(210, 225, 255));
            g2.drawString("Логик хаягийн орон зай", leftX - 4, boxTop - 20);

            // Physical memory (frames)
            int rightX = w / 2 + 20;
            int rightW = 110;

            g2.setColor(new Color(15, 23, 42, 170));
            g2.fillRoundRect(rightX - 10, boxTop - 10, rightW + 20, boxHeight + 20, 18, 18);

            g2.setColor(new Color(148, 163, 253));
            g2.drawRoundRect(rightX - 10, boxTop - 10, rightW + 20, boxHeight + 20, 18, 18);

            int frames = 7;
            gap = 4;
            innerH = boxHeight - (frames + 1) * gap;
            int frameH = innerH / frames;

            // some frames used, some free
            int[] usedFrames = {1, 3, 5}; // index-үүд
            Color[] pageColors = {
                    new Color(96, 165, 250),
                    new Color(52, 211, 153),
                    new Color(251, 191, 36)
            };

            for (int i = 0; i < frames; i++) {
                int y = boxTop + gap + i * (frameH + gap);

                boolean used = false;
                int pageIdx = -1;
                for (int j = 0; j < usedFrames.length; j++) {
                    if (usedFrames[j] == i) {
                        used = true;
                        pageIdx = j;
                        break;
                    }
                }

                if (used) {
                    g2.setColor(pageColors[pageIdx]);
                    g2.fillRoundRect(rightX, y, rightW, frameH, 10, 10);

                    g2.setColor(new Color(15, 23, 42, 220));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g2.drawString("Frame " + i + " = Page " + pageIdx, rightX + 6, y + frameH / 2 + 4);
                } else {
                    g2.setColor(new Color(30, 64, 175));
                    g2.fillRoundRect(rightX, y, rightW, frameH, 10, 10);

                    g2.setColor(new Color(191, 219, 254));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g2.drawString("сул frame", rightX + 10, y + frameH / 2 + 4);
                }
            }

            g2.setColor(new Color(210, 225, 255));
            g2.drawString("Физик санах ой", rightX - 2, boxTop - 20);

            // Mapping arrows (Page 0 → Frame1, Page1 → Frame3, Page2 → Frame5)
            drawMappingArrow(g2, leftX + leftW, boxTop + gap + pageH / 2,
                    rightX, boxTop + gap + frameH / 2);
            drawMappingArrow(g2, leftX + leftW, boxTop + gap + (pageH + gap) + pageH / 2,
                    rightX, boxTop + gap + (frameH + gap) * 3 + frameH / 2);
            drawMappingArrow(g2, leftX + leftW, boxTop + gap + 2 * (pageH + gap) + pageH / 2,
                    rightX, boxTop + gap + (frameH + gap) * 5 + frameH / 2);

            // Page table label – илүү энгийн
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g2.setColor(new Color(210, 225, 255));
            g2.drawString("Page table нь page бүр аль frame-д байгааг хадгалж, хаягийг хөрвүүлнэ.", 10, h - 24);
        }

        private void drawMappingArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.setColor(new Color(248, 250, 252, 220));
            Stroke old = g2.getStroke();
            g2.setStroke(new BasicStroke(1.2f));

            int midX = (x1 + x2) / 2;
            g2.drawLine(x1 + 5, y1, midX, y1);
            g2.drawLine(midX, y1, midX, y2);
            g2.drawLine(midX, y2, x2 - 5, y2);

            // arrowhead
            g2.drawLine(x2 - 5, y2, x2 - 11, y2 - 4);
            g2.drawLine(x2 - 5, y2, x2 - 11, y2 + 4);

            g2.setStroke(old);
        }
    }

    /* ===================== Баруун тал: Segmentation ===================== */

    private static class SegmentationPanel extends JPanel {
        public SegmentationPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.drawString("Segmentation", 10, 26);

            // Bullets – илүү ойлгомжтой
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(210, 225, 255));
            g2.drawString("• Процессыг логик хэсгүүдэд (code, data, stack …) хуваана.", 10, 48);
            g2.drawString("• Сегмент бүр өөр хэмжээтэй, логик нэгжээр (модуль, блок) ялгарна.", 10, 66);
            g2.drawString("• Segment table нь сегмент бүрийн base ба limit утгыг хадгална.", 10, 84);

            int top = 130;
            int heightBox = h - 230;

            // Logical segments (left)
            int leftX = 30;
            int leftW = 110;

            g2.setColor(new Color(15, 23, 42, 170));
            g2.fillRoundRect(leftX - 10, top - 10, leftW + 20, heightBox + 20, 18, 18);
            g2.setColor(new Color(148, 163, 253));
            g2.drawRoundRect(leftX - 10, top - 10, leftW + 20, heightBox + 20, 18, 18);

            int gap = 8;
            int seg1H = (int) (heightBox * 0.30);
            int seg2H = (int) (heightBox * 0.24);
            int seg3H = (int) (heightBox * 0.18);

            int yCode = top + gap;
            int yData = yCode + seg1H + gap;
            int yStack = yData + seg2H + gap;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

            // Code
            g2.setColor(new Color(56, 189, 248));
            g2.fillRoundRect(leftX, yCode, leftW, seg1H, 10, 10);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.drawString("Code seg", leftX + 10, yCode + seg1H / 2 + 4);

            // Data
            g2.setColor(new Color(52, 211, 153));
            g2.fillRoundRect(leftX, yData, leftW, seg2H, 10, 10);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.drawString("Data seg", leftX + 10, yData + seg2H / 2 + 4);

            // Stack
            g2.setColor(new Color(251, 191, 36));
            g2.fillRoundRect(leftX, yStack, leftW, seg3H, 10, 10);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.drawString("Stack seg", leftX + 10, yStack + seg3H / 2 + 4);

            g2.setColor(new Color(210, 225, 255));
            g2.drawString("Логик сегментүүд", leftX - 2, top - 20);

            // Physical memory (right)
            int rightX = w / 2 + 40;
            int rightW = 110;

            g2.setColor(new Color(15, 23, 42, 170));
            g2.fillRoundRect(rightX - 10, top - 10, rightW + 20, heightBox + 20, 18, 18);
            g2.setColor(new Color(148, 163, 253));
            g2.drawRoundRect(rightX - 10, top - 10, rightW + 20, heightBox + 20, 18, 18);

            // place segments scattered
            int physGap = 6;

            int phY1 = top + physGap;                     // code
            int phH1 = seg1H;
            int phY2 = top + heightBox / 2;               // data somewhere middle
            int phH2 = seg2H;
            int phY3 = top + heightBox - seg3H - physGap; // stack near bottom
            int phH3 = seg3H;

            // some free holes between them
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

            // Code in physical
            g2.setColor(new Color(56, 189, 248));
            g2.fillRoundRect(rightX, phY1, rightW, phH1, 10, 10);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.drawString("Code", rightX + 8, phY1 + phH1 / 2 + 4);

            // Free hole between code & data
            g2.setColor(new Color(30, 64, 175));
            int holeY = phY1 + phH1 + physGap;
            int holeH = phY2 - holeY - physGap;
            g2.fillRoundRect(rightX, holeY, rightW, holeH, 10, 10);
            g2.setColor(new Color(191, 219, 254));
            g2.drawString("сул зай", rightX + 8, holeY + holeH / 2 + 4);

            // Data in physical
            g2.setColor(new Color(52, 211, 153));
            g2.fillRoundRect(rightX, phY2, rightW, phH2, 10, 10);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.drawString("Data", rightX + 8, phY2 + phH2 / 2 + 4);

            // Another free hole
            g2.setColor(new Color(30, 64, 175));
            int hole2Y = phY2 + phH2 + physGap;
            int hole2H = phY3 - hole2Y - physGap;
            g2.fillRoundRect(rightX, hole2Y, rightW, hole2H, 10, 10);
            g2.setColor(new Color(191, 219, 254));
            g2.drawString("сул зай", rightX + 8, hole2Y + hole2H / 2 + 4);

            // Stack in physical
            g2.setColor(new Color(251, 191, 36));
            g2.fillRoundRect(rightX, phY3, rightW, phH3, 10, 10);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.drawString("Stack", rightX + 8, phY3 + phH3 / 2 + 4);

            g2.setColor(new Color(210, 225, 255));
            g2.drawString("Физик санах ой", rightX - 4, top - 20);

            // Caption
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g2.setColor(new Color(210, 225, 255));
            g2.drawString("Segment table нь сегмент бүрийн base ба limit-ийг хадгалж, логик хаягийг физик хаягт хөрвүүлдэг.", 10, h - 24);
        }
    }
}
