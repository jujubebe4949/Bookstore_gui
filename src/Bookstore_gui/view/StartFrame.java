/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Bookstore_gui.view;

import Bookstore_gui.view.common.BackgroundPanel;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.db.DbUserRepository;
import Bookstore_gui.repo.UserRepository;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author julia
 */
public class StartFrame extends javax.swing.JFrame {
    // Application state
    private final UserRepository users = new DbUserRepository();
    private final UserContext userCtx = new UserContext();

    public StartFrame() {
    setContentPane(new BackgroundPanel("/Bookstore_gui/view/common/mainimage.jpg"));
    initComponents();
    
    authCard.remove(southBar); 
    authCard.add(southBar, java.awt.BorderLayout.PAGE_END);

    lblErrorIn.setText("");
    lblErrorUp.setText("");
    
    authCard.remove(tabAuth);
    authCard.add(tabAuth, java.awt.BorderLayout.CENTER);

    
    java.awt.FlowLayout fl = (java.awt.FlowLayout) southBar.getLayout();
    fl.setAlignment(java.awt.FlowLayout.RIGHT);

    authCard.revalidate();
    authCard.repaint();
    
    
    setTitle("Bookstore");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    setSize(1100, 620);
    setLocationRelativeTo(null);

    
    txtInName.setColumns(25);
    pwdIn.setColumns(22);
    txtUpName.setColumns(22);
    txtUpEmail.setColumns(22);
    pwdUp.setColumns(22);

    int inIdx = tabAuth.indexOfComponent(signInPanel);
    int upIdx = tabAuth.indexOfComponent(signUpPanel);
    if (inIdx >= 0) tabAuth.setTitleAt(inIdx, "Sign In");
    if (upIdx >= 0) tabAuth.setTitleAt(upIdx, "Sign Up");


    
    getRootPane().setDefaultButton(btnEnter);
    btnEnter.addActionListener(e -> {
    if (tabAuth.getSelectedComponent() == signInPanel) {
        signIn();
    } else {
        signUp();
    }
});
}
    private void signIn() {
      lblErrorIn.setText(""); 
      String name = safe(txtInName).toLowerCase();
      String pw    = new String(pwdIn.getPassword());
      if (name.isBlank() || pw.isBlank()) {
          lblErrorIn.setText("Enter name and password.");
          return;
      }
    try {
        DbUserRepository users = new DbUserRepository();
        String uid = users.authenticateByName(name, pw);
        if (uid == null) {
            lblErrorIn.setText("Name or password is incorrect.");
            return;
        }
        userCtx.setUser(uid, name, null);
        goMain();
    } catch (Exception ex) {
        String msg = String.valueOf(ex.getMessage());
        if (msg.contains("XSDB6")) {
            lblErrorIn.setText("Database is locked by another run. Close other windows and try again.");
        } else {
            lblErrorIn.setText("Login failed: " + msg);
        }
    }
}

private void signUp() {
    lblErrorUp.setText("");

    String name  = safe(txtUpName);
    String email = safe(txtUpEmail).toLowerCase();
    String pw    = new String(pwdUp.getPassword());

    if (name.isBlank() || email.isBlank() || pw.isBlank()) {
        lblErrorUp.setText("All fields are required.");
        return;
    }
    
    if (pw.length() < 4) {
        lblErrorUp.setText("Password must be at least 6 characters.");
        return;
    }

    try {
        DbUserRepository users = new DbUserRepository();

        if (users.findByEmail(email) != null) {
            lblErrorUp.setText("This email is already registered. Please Sign In.");
            return;
        }

        String uid = users.register(name, email, pw);

        if (uid == null || uid.isBlank()) {
            lblErrorUp.setText("Sign up failed: invalid user id.");
            return;
        }

        userCtx.setUser(uid, name, email);
        goMain();
    } catch (Exception ex) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage();
        if (msg.contains("XSDB6")) {
            lblErrorUp.setText("Database is locked by another run. Close other windows and try again.");
        } else if (msg.contains("23505") || msg.toLowerCase().contains("already registered")) {
            lblErrorUp.setText("This email is already registered. Please Sign In.");
        } else if (msg.toLowerCase().contains("name already taken")) {
            lblErrorUp.setText("This name is already taken. Choose another name.");
        } else {
            lblErrorUp.setText("Sign up failed: " + msg);
        }
        return; 
    }
}

private void goMain() {
        SwingUtilities.invokeLater(() -> {
            new MainFrameForm(userCtx).setVisible(true);
            dispose();
        });
    }


private static String safe(JTextField f) {
    return f.getText() == null ? "" : f.getText().trim();
}
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        southBar = new javax.swing.JPanel();
        btnEnter = new javax.swing.JButton();
        rootPanel = new javax.swing.JPanel();
        rightWrap = new javax.swing.JPanel();
        authCard = new javax.swing.JPanel();
        tabAuth = new javax.swing.JTabbedPane();
        signInPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtInName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        pwdIn = new javax.swing.JPasswordField();
        lblErrorIn = new javax.swing.JLabel();
        signUpPanel = new javax.swing.JPanel();
        txtUpName = new javax.swing.JTextField();
        txtUpEmail = new javax.swing.JTextField();
        pwdUp = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblErrorUp = new javax.swing.JLabel();

        southBar.setBackground(new java.awt.Color(153, 204, 255));
        southBar.setFocusCycleRoot(true);
        southBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnEnter.setText("Enter");
        btnEnter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEnter.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btnEnter.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        btnEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnterActionPerformed(evt);
            }
        });
        southBar.add(btnEnter);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        rootPanel.setOpaque(false);
        rootPanel.setLayout(new java.awt.BorderLayout());

        rightWrap.setOpaque(false);
        rightWrap.setLayout(new java.awt.GridBagLayout());

        authCard.setBackground(new java.awt.Color(255, 255, 255));
        authCard.setLayout(new java.awt.BorderLayout());

        tabAuth.setBackground(java.awt.Color.pink);
        tabAuth.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tabAuth.setToolTipText("");
        tabAuth.setName(""); // NOI18N

        signInPanel.setBackground(new java.awt.Color(204, 204, 255));
        signInPanel.setToolTipText("Sign In");

        jLabel4.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel4.setText("Name :");

        txtInName.setColumns(25);
        txtInName.setMinimumSize(new java.awt.Dimension(15, 25));

        jLabel5.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel5.setText("Password :");

        pwdIn.setColumns(22);

        lblErrorIn.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblErrorIn.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout signInPanelLayout = new javax.swing.GroupLayout(signInPanel);
        signInPanel.setLayout(signInPanelLayout);
        signInPanelLayout.setHorizontalGroup(
            signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signInPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblErrorIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(signInPanelLayout.createSequentialGroup()
                        .addGroup(signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtInName, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pwdIn, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        signInPanelLayout.setVerticalGroup(
            signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signInPanelLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtInName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(signInPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(lblErrorIn)
                        .addGap(24, 24, 24))
                    .addGroup(signInPanelLayout.createSequentialGroup()
                        .addComponent(pwdIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        tabAuth.addTab("Sign In", signInPanel);

        signUpPanel.setBackground(new java.awt.Color(204, 204, 255));
        signUpPanel.setToolTipText("Sign up");

        txtUpName.setColumns(22);
        txtUpName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUpNameActionPerformed(evt);
            }
        });

        txtUpEmail.setColumns(22);

        pwdUp.setColumns(22);

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel1.setText("Name :");

        jLabel2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel2.setText("Email : ");

        jLabel3.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel3.setText("Password : ");

        lblErrorUp.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblErrorUp.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout signUpPanelLayout = new javax.swing.GroupLayout(signUpPanel);
        signUpPanel.setLayout(signUpPanelLayout);
        signUpPanelLayout.setHorizontalGroup(
            signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signUpPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(signUpPanelLayout.createSequentialGroup()
                        .addComponent(lblErrorUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(signUpPanelLayout.createSequentialGroup()
                        .addGroup(signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtUpEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                .addComponent(txtUpName, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                            .addComponent(pwdUp, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(148, 148, 148))))
        );
        signUpPanelLayout.setVerticalGroup(
            signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signUpPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUpName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(txtUpEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pwdUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblErrorUp)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        tabAuth.addTab("Sign up", signUpPanel);

        authCard.add(tabAuth, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 600, 100, 32);
        rightWrap.add(authCard, gridBagConstraints);

        rootPanel.add(rightWrap, java.awt.BorderLayout.CENTER);

        getContentPane().add(rootPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnterActionPerformed
        lblErrorIn.setText("");
        lblErrorUp.setText("");
        if (tabAuth.getSelectedComponent() == signInPanel) {
            signIn();
        } else {
            signUp();
        }
    }//GEN-LAST:event_btnEnterActionPerformed

    private void txtUpNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUpNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpNameActionPerformed
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel authCard;
    private javax.swing.JButton btnEnter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblErrorIn;
    private javax.swing.JLabel lblErrorUp;
    private javax.swing.JPasswordField pwdIn;
    private javax.swing.JPasswordField pwdUp;
    private javax.swing.JPanel rightWrap;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JPanel signInPanel;
    private javax.swing.JPanel signUpPanel;
    private javax.swing.JPanel southBar;
    private javax.swing.JTabbedPane tabAuth;
    private javax.swing.JTextField txtInName;
    private javax.swing.JTextField txtUpEmail;
    private javax.swing.JTextField txtUpName;
    // End of variables declaration//GEN-END:variables
}
