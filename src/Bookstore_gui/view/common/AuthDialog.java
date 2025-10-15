// path: src/Bookstore_gui/view/common/AuthDialog.java
package Bookstore_gui.view.common;

import Bookstore_gui.controller.UserContext;
import Bookstore_gui.repo.UserRepository;

import javax.swing.*;
import java.awt.*;

/** Auth dialog (Sign In / Sign Up). Password-based. */
public class AuthDialog extends JDialog {
    private final UserRepository users;
    private final UserContext userCtx;
    private final ErrorBanner error = new ErrorBanner();

    // Sign In
    private final JTextField inEmail = new JTextField(20);
    private final JPasswordField inPw = new JPasswordField(20);

    // Sign Up
    private final JTextField upName = new JTextField(18);
    private final JTextField upEmail = new JTextField(18);
    private final JPasswordField upPw = new JPasswordField(18);

    public AuthDialog(Window owner, UserRepository users, UserContext userCtx) {
        super(owner, "Login / Register", ModalityType.APPLICATION_MODAL);
        this.users = users;
        this.userCtx = userCtx;

        setLayout(new BorderLayout(8,8));
        add(error, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Sign In", buildSignIn());
        tabs.addTab("Sign Up", buildSignUp());
        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnOk = new JButton("Enter");
        south.add(btnCancel); south.add(btnOk);
        add(south, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);

        btnCancel.addActionListener(e -> dispose());
        btnOk.addActionListener(e -> {
            error.clear();
            if (tabs.getSelectedIndex() == 0) doSignIn(); else doSignUp();
        });

        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildSignIn() {
        JPanel p = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; p.add(new JLabel("Email:"), gc);
        gc.gridx=1; p.add(inEmail, gc);

        gc.gridx=0; gc.gridy=1; p.add(new JLabel("Password:"), gc);
        gc.gridx=1; p.add(inPw, gc);

        return p;
    }

    private JPanel buildSignUp() {
        JPanel p = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; p.add(new JLabel("Name:"), gc);
        gc.gridx=1; p.add(upName, gc);

        gc.gridx=0; gc.gridy=1; p.add(new JLabel("Email:"), gc);
        gc.gridx=1; p.add(upEmail, gc);

        gc.gridx=0; gc.gridy=2; p.add(new JLabel("Password:"), gc);
        gc.gridx=1; p.add(upPw, gc);

        return p;
    }

    private void doSignIn() {
        String email = text(inEmail).toLowerCase();
        String pw = new String(inPw.getPassword());

        if (email.isBlank() || pw.isBlank()) { error.showError("Enter email and password."); return; }

        try {
            String uid = users.authenticate(email, pw);
            if (uid == null) { error.showError("Email or password is incorrect."); return; }
            userCtx.setUser(uid, null, email);
            dispose();
        } catch (Exception ex) {
            error.showError("Login failed: " + ex.getMessage());
        }
    }

    private void doSignUp() {
        String name = text(upName);
        String email = text(upEmail).toLowerCase();
        String pw = new String(upPw.getPassword());

        if (name.isBlank() || email.isBlank() || pw.isBlank()) { error.showError("All fields are required."); return; }
        if (pw.length() < 4) { error.showError("Password must be at least 4 characters."); return; }

        try {
            String uid = users.register(name, email, pw);
            userCtx.setUser(uid, name, email);
            dispose();
        } catch (Exception ex) {
            String cause = ex.getCause()!=null ? " ("+ex.getCause().getMessage()+")" : "";
            error.showError("Sign up failed: " + ex.getMessage() + cause);
        }
    }

    private static String text(JTextField f){ return f.getText()==null? "" : f.getText().trim(); }
}