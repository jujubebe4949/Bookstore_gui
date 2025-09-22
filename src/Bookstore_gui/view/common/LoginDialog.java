// File: src/Bookstore_gui/view/common/LoginDialog.java
// KOR: 간단 로그인 다이얼로그(이름/이메일) -> UserContext 채움
// ENG: Simple login dialog (name/email) -> fills UserContext
package Bookstore_gui.view.common;

import Bookstore_gui.controller.UserContext;

import javax.swing.*;
import java.awt.*;

public final class LoginDialog extends JDialog {
    private boolean ok = false; // KOR: 로그인 성공 여부 / ENG: whether sign-in succeeded

    /**
     * KOR: 다이얼로그를 모달로 띄워 UserContext를 채움. true=성공.
     * ENG: Opens modal dialog, fills UserContext. true=success.
     */
    public static boolean show(Component parent, UserContext ctx) {
        Window owner = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        LoginDialog d = new LoginDialog(owner, ctx);
        d.setVisible(true);
        return d.ok;
    }

    private LoginDialog(Window owner, UserContext ctx) {
        super(owner, "Sign In", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JLabel title = new JLabel("Sign In");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        c.gridwidth = 2; c.gridx = 0; c.gridy = 0; root.add(title, c);

        JTextField tfName  = new JTextField();
        JTextField tfEmail = new JTextField();

        c.gridwidth = 1; c.gridy++; c.gridx = 0; root.add(new JLabel("Name"), c);
        c.gridx = 1; root.add(tfName, c);

        c.gridy++; c.gridx = 0; root.add(new JLabel("Email"), c);
        c.gridx = 1; root.add(tfEmail, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnOk     = new JButton("Sign In");
        actions.add(btnCancel); actions.add(btnOk);

        c.gridy++; c.gridx = 0; c.gridwidth = 2; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        root.add(actions, c);

        // KOR: 유효성 체크 후 UserContext 채움 / ENG: validate input then fill UserContext
        btnOk.addActionListener(e -> {
            String name  = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both name and email.");
                return;
            }
            // why: 과제 단순화 — userId는 이메일로 사용 / use email as userId for simplicity
            ctx.signIn(email, name);
            ok = true;
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());

        setContentPane(root);
        pack();
        setLocationRelativeTo(owner);
    }
}