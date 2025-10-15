package Bookstore_gui.view.common;

import javax.swing.*;
import java.awt.*;

public class ErrorBanner extends JPanel {
    private final JLabel label = new JLabel();

    public ErrorBanner() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xFFEEEE));
        label.setForeground(new Color(0xB00020));
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(label, BorderLayout.CENTER);
        setVisible(false);
    }

    public void showError(String msg) {
        label.setText(msg);
        setVisible(true);
    }

    public void clear() {
        label.setText("");
        setVisible(false);
    }
}