package Bookstore_gui.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// package Bookstore_gui.view;
public final class Ui {
    private Ui(){}

    public static final Color BG_DIM = new Color(245,245,248);
    public static final Color ACCENT = new Color(40, 91, 140);
    public static final Font  H1 = new JLabel().getFont().deriveFont(Font.BOLD, 18f);
    public static final Font  H2 = new JLabel().getFont().deriveFont(Font.BOLD, 15f);

    public static JPanel roundedCard() {
        return new JPanel(new BorderLayout()) {
            { setOpaque(false); setBorder(BorderFactory.createEmptyBorder(14,14,14,14)); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(), h=getHeight();
                g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(6,6,w-12,h-12,16,16);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0,0,w-12,h-12,16,16);
                g2.setColor(new Color(210,225,240)); g2.drawRoundRect(0,0,w-12,h-12,16,16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10,16,10,16)); // why: 터치 타겟 충분
        return b;
    }

    public static JPanel padded(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(c, BorderLayout.CENTER);
        return p;
    }
}