package Bookstore_gui.view;

import Bookstore_gui.controller.UserContext;
import Bookstore_gui.db.DbUserRepository;
import Bookstore_gui.repo.UserRepository;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {
    private final UserRepository users = new DbUserRepository();
    private final UserContext userCtx = new UserContext();

    public StartFrame() {
        setTitle("Bookstore");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel root = new BackgroundPanel("/Bookstore_gui/view/common/mainimage.jpg");
        setContentPane(root); root.setLayout(new BorderLayout());

        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.setBorder(BorderFactory.createEmptyBorder(0,0,0,28));
        root.add(rightWrap, BorderLayout.EAST);

        JPanel card = Ui.roundedCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 14, 10, 14);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;

        JLabel title = new JLabel("Sign In");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        card.add(title, c);

        c.gridy++; JTextField tfName  = createPlaceholderField("Name");
        card.add(tfName, c);
        c.gridy++; JTextField tfEmail = createPlaceholderField("Email (as ID)");
        card.add(tfEmail, c);
        c.gridy++; JButton btn = new JButton("Enter");
        card.add(btn, c);

        rightWrap.add(card, new GridBagConstraints());

        btn.addActionListener(e -> {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            if(name.isEmpty() || email.isEmpty()){
                JOptionPane.showMessageDialog(this, "Enter name and email");
                return;
            }
            try {
                // DB에서 사용자 확인/생성
                String uid = users.findOrCreate(name, email);
                userCtx.setUser(uid, name, email);

                new MainFrame(userCtx).setVisible(true);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Login failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setSize(1100, 620);
        setLocationRelativeTo(null);
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