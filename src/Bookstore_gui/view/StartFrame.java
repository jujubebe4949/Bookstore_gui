package Bookstore_gui.view;

import javax.swing.*;
import java.awt.*;

/**
 * KOR: 시작 화면 (배경 + 로그인 카드, placeholder 구현 포함)
 * ENG: Start screen (background + login card, with placeholder fields)
 */
public class StartFrame extends JFrame {
    public StartFrame() {
        setTitle("Bookstore"); setDefaultCloseOperation(EXIT_ON_CLOSE);
        BackgroundPanel root = new BackgroundPanel("/Bookstore_gui/view/common/mainimage.jpg");
        setContentPane(root); root.setLayout(new BorderLayout());

        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 28));
        root.add(rightWrap, BorderLayout.EAST);

        JPanel card = Ui.roundedCard();
        GridBagConstraints c = new GridBagConstraints();
        card.setLayout(new GridBagLayout());
        c.insets = new Insets(10,14,10,14);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0;

        JLabel title = new JLabel("Sign In"); title.setFont(Ui.H1);
        card.add(title, c);

        c.gridy++; JTextField tfName  = createPlaceholderField("Name");  tfName.setColumns(18);
        card.add(tfName, c);

        c.gridy++; JTextField tfEmail = createPlaceholderField("Email"); tfEmail.setColumns(18);
        card.add(tfEmail, c);

        c.gridy++; JButton btn = Ui.primaryButton("Enter");
        card.add(btn, c);

        // 카드 자체 폭 고정 느낌
        card.setPreferredSize(new Dimension(320, 0));
        rightWrap.add(card, new GridBagConstraints());

        btn.addActionListener(e -> { new MainFrame().setVisible(true); dispose(); });
        setSize(1100, 620); setLocationRelativeTo(null);
    }

    private JTextField createPlaceholderField(String ph) {
        JTextField f = new JTextField(20);
        f.setForeground(Color.GRAY); f.setText(ph);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e){
                if(f.getText().equals(ph)){ f.setText(""); f.setForeground(Color.BLACK); }
            }
            public void focusLost(java.awt.event.FocusEvent e){
                if(f.getText().isEmpty()){ f.setForeground(Color.GRAY); f.setText(ph); }
            }
        });
        return f;
    }
}