package Bookstore_gui.view.common;

import Bookstore_gui.controller.UserContext;
import Bookstore_gui.repo.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class LoginDialog extends JDialog {
    private final JTextField tfName  = new JTextField(18);
    private final JTextField tfEmail = new JTextField(18);
    private final UserRepository users;
    private final UserContext userCtx;

    private static final Pattern EMAIL =
        Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public LoginDialog(Window owner, UserRepository users, UserContext userCtx) {
        super(owner, "Sign in", ModalityType.APPLICATION_MODAL);
        this.users = users;
        this.userCtx = userCtx;

        var form = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; form.add(new JLabel("Name"), gc);
        gc.gridx=1; gc.gridy=0; form.add(tfName, gc);
        gc.gridx=0; gc.gridy=1; form.add(new JLabel("Email"), gc);
        gc.gridx=1; gc.gridy=1; form.add(tfEmail, gc);

        var south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var btnCancel = new JButton("Cancel");
        var btnOk = new JButton("Sign in");
        south.add(btnCancel); south.add(btnOk);

        setLayout(new BorderLayout(8,8));
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);

        btnCancel.addActionListener(e -> dispose());
        btnOk.addActionListener(e -> submit());

        pack();
        setLocationRelativeTo(owner);
    }

    private void submit() {
        String name = tfName.getText() == null ? "" : tfName.getText().trim();
        String email = tfEmail.getText() == null ? "" : tfEmail.getText().trim().toLowerCase();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.",
                    "Invalid name", JOptionPane.WARNING_MESSAGE);
            tfName.requestFocus(); return;
        }
        if (!EMAIL.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.",
                    "Invalid email", JOptionPane.WARNING_MESSAGE);
            tfEmail.requestFocus(); return;
        }

        try {
            String uid = users.findOrCreate(name, email);
            userCtx.setUser(uid, name, email); // UserContext에 맞춰 사용
            JOptionPane.showMessageDialog(this, "Signed in as " + name);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}