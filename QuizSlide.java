import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizSlide extends GradientSlide {
    private final int correctIndex;

    public QuizSlide(String title, String question, String[] options, int correctIndex) {
        this.correctIndex = correctIndex;
        setLayout(new BorderLayout());
        add(MemoryManagementShow.createHeader(title), BorderLayout.NORTH);

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
