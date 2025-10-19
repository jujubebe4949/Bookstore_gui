/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Bookstore_gui.view;

import Bookstore_gui.view.common.BackgroundPanel;
import Bookstore_gui.controller.CartController;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.db.*;
import Bookstore_gui.repo.*;
import Bookstore_gui.view.books.BooksView;
import Bookstore_gui.view.cart.CartView;
import Bookstore_gui.view.orders.OrdersView;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
/**
 *
 * @author julia
 */
public class MainFrameForm extends javax.swing.JFrame {
    // ---- state / services ----
    private final UserContext userCtx;
    
    private static final String HERO_IMAGE = "/Bookstore_gui/view/common/mainimage.jpg";
    private BufferedImage heroImg;     
    private JLabel bannerLabel;        
    private int bannerHeight = 100;    
    private final CardLayout card = new CardLayout();
    private final JPanel contentPanel = new JPanel(card);

    private final BookRepository  bookRepo = new DbBookRepository();
    private final CartController  cart     = new CartController(bookRepo);
    private final OrderRepository orders   = new DbOrderRepository();

    private BooksView  booksView;
    private CartView   cartView;
    private OrdersView ordersView;

    // ---- UI (side bar) ---
    private JLabel navBooks;
    private JLabel navCart;
    private JLabel navOrders;
    
    public MainFrameForm(UserContext userCtx) {
       this.userCtx = userCtx;
        initComponents();

        this.navBooks  = jLabel1;   // "Books"
        this.navCart   = btnCart;   // "Cart"
        this.navOrders = btnOrders; // "Orders"

        mainSplit.setRightComponent(contentPanel);
        setupTopBanner();
        setTitle("Bookstore");
        setSize(1100, 620);
        setLocationRelativeTo(null);   
        setResizable(false); 
        setupLogic();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
               Bookstore_gui.db.DbManager.closeQuietly();
           }
         });
     }    
    
    public MainFrameForm() {
         this(new UserContext()); 
    }
    private void addTopBanner() {
    
        BackgroundPanel topBanner = new BackgroundPanel("/Bookstore_gui/view/common/mainimage.jpg");
        topBanner.setPreferredSize(new Dimension(0, 100)); 
        topBanner.setLayout(new BorderLayout());
    
        JLabel bannerTitle = new JLabel("BOOKSTORE 603", SwingConstants.CENTER);
        bannerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        bannerTitle.setForeground(Color.WHITE);
        topBanner.add(bannerTitle, BorderLayout.CENTER);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topBanner, BorderLayout.NORTH);
        getContentPane().add(mainSplit, BorderLayout.CENTER);

        validate();
    }
    private void setupTopBanner() {
        try {
            heroImg = ImageIO.read(getClass().getResource(HERO_IMAGE));
        } catch (Exception e) {
            System.err.println("Hero image load failed: " + e.getMessage());
            return;
        }

        bannerLabel = new JLabel();
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setVerticalAlignment(SwingConstants.CENTER);

    
        bannerLabel2.setLayout(new BorderLayout());
        bannerLabel2.add(bannerLabel, BorderLayout.CENTER);
        bannerLabel2.setPreferredSize(new Dimension(10, bannerHeight));

        updateBanner(); 
        bannerLabel2.addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override public void componentResized(java.awt.event.ComponentEvent e) {
            updateBanner();
            }
        });
    }

    private void updateBanner() {
        if (heroImg == null) return;

           int targetW = bannerLabel2.getWidth() > 0 ? bannerLabel2.getWidth() : 800;
           int targetH = bannerHeight;

        double srcW = heroImg.getWidth();
        double srcH = heroImg.getHeight();
        double scale = Math.max(targetW / srcW, targetH / srcH); // cover

        int scaledW = (int)Math.round(srcW * scale);
        int scaledH = (int)Math.round(srcH * scale);

        int x = Math.max(0, (scaledW - targetW) / 2);
        int y = Math.max(0, (scaledH - targetH) / 2);

        BufferedImage scaled = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(heroImg, 0, 0, scaledW, scaledH, null);
        g.dispose();

        BufferedImage cropped = scaled.getSubimage(x, y, Math.min(targetW, scaledW - x), Math.min(targetH, scaledH - y));
        bannerLabel.setIcon(new ImageIcon(cropped));

        bannerLabel2.revalidate();
        bannerLabel2.repaint();
    }
        private void setupLogic() {
            try { DbManager.initSchema(); } catch (Exception ignored) {}

            booksView  = new BooksView(bookRepo, cart);
            ordersView = new OrdersView(orders, userCtx, () -> card.show(contentPanel, "BOOKS"));
            Runnable onBack = () -> card.show(contentPanel, "BOOKS");
            Runnable afterCheckout = ordersView::refresh;
            cartView   = new CartView(cart, orders, userCtx, bookRepo, afterCheckout, onBack);

            contentPanel.add(booksView,  "BOOKS");
            contentPanel.add(cartView,   "CART");
            contentPanel.add(ordersView, "ORDERS");

            // nav
            addNavClick(navBooks,  () -> card.show(contentPanel, "BOOKS"));
            addNavClick(navCart,   () -> { cartView.refresh();   card.show(contentPanel, "CART"); });
            addNavClick(navOrders, () -> { ordersView.refresh(); card.show(contentPanel, "ORDERS"); });
            card.show(contentPanel, "BOOKS");
        
            // profile/logout 
            addNavClick(btnLogout, () -> {
                userCtx.signOut();
                dispose();
               new StartFrame().setVisible(true);
            });
            addNavClick(btnProfile, () -> {
             if (!userCtx.isSignedIn()) {
                JOptionPane.showMessageDialog(this, "Please login first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String newName = JOptionPane.showInputDialog(this, "New display name:", userCtx.getName());
            if (newName == null) return;
            newName = newName.trim();
            if (newName.isEmpty()) return;

            try {
                boolean ok = new DbUserRepository().updateUserName(userCtx.getUserId(), newName);
                if (ok) {
                    userCtx.setUser(userCtx.getUserId(), newName, userCtx.getEmail());
                    refreshUserLabel();
                    JOptionPane.showMessageDialog(this, "Updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshUserLabel();
        card.show(contentPanel, "BOOKS");
    }

    private void addNavClick(JComponent c, Runnable action) {
        if (c == null) return;
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
        });
    }

    private void styleAsLink(JLabel l) {
        l.setForeground(Color.BLACK);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
        l.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
    }

    private void styleAsNav(JLabel l) {
        l.setForeground(Color.BLACK);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 20f));
        l.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
    }

    private void refreshUserLabel() {
        lbUser.setText(userCtx.isSignedIn() ? userCtx.getName() : "Guest");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainSplit = new javax.swing.JSplitPane();
        sidebarPanel = new javax.swing.JPanel();
        btnProfile = new javax.swing.JLabel();
        btnLogout = new javax.swing.JLabel();
        lbUser = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 20000));
        jLabel1 = new javax.swing.JLabel();
        btnCart = new javax.swing.JLabel();
        btnOrders = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        bannerLabel2 = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainSplit.setDividerLocation(150);
        mainSplit.setDividerSize(6);

        sidebarPanel.setBackground(new java.awt.Color(0, 153, 153));
        sidebarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow"));
        sidebarPanel.setLayout(new javax.swing.BoxLayout(sidebarPanel, javax.swing.BoxLayout.Y_AXIS));

        btnProfile.setFont(new java.awt.Font("Apple Chancery", 1, 22)); // NOI18N
        btnProfile.setText("Profile");
        btnProfile.setPreferredSize(new java.awt.Dimension(80, 35));
        sidebarPanel.add(btnProfile);

        btnLogout.setFont(new java.awt.Font("Apple Chancery", 1, 22)); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.setPreferredSize(new java.awt.Dimension(72, 35));
        sidebarPanel.add(btnLogout);

        lbUser.setFont(new java.awt.Font("Apple Chancery", 1, 20)); // NOI18N
        lbUser.setText("Guest");
        lbUser.setPreferredSize(new java.awt.Dimension(54, 35));
        sidebarPanel.add(lbUser);
        sidebarPanel.add(filler2);

        jLabel1.setFont(new java.awt.Font("American Typewriter", 1, 24)); // NOI18N
        jLabel1.setText("Books");
        sidebarPanel.add(jLabel1);

        btnCart.setFont(new java.awt.Font("American Typewriter", 1, 24)); // NOI18N
        btnCart.setText("Cart");
        sidebarPanel.add(btnCart);

        btnOrders.setFont(new java.awt.Font("American Typewriter", 1, 24)); // NOI18N
        btnOrders.setText("Orders");
        sidebarPanel.add(btnOrders);
        sidebarPanel.add(filler1);

        mainSplit.setLeftComponent(sidebarPanel);

        getContentPane().add(mainSplit, java.awt.BorderLayout.CENTER);

        bannerLabel2.setName("TopBanner"); // NOI18N
        bannerLabel2.setOpaque(false);

        lbTitle.setBackground(new java.awt.Color(0, 153, 153));
        lbTitle.setFont(new java.awt.Font("American Typewriter", 1, 36)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(244, 242, 242));
        lbTitle.setText("BOOKSTORE603");
        lbTitle.setAutoscrolls(true);
        lbTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        lbTitle.setSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout bannerLabel2Layout = new javax.swing.GroupLayout(bannerLabel2);
        bannerLabel2.setLayout(bannerLabel2Layout);
        bannerLabel2Layout.setHorizontalGroup(
            bannerLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bannerLabel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbTitle)
                .addContainerGap(1691, Short.MAX_VALUE))
        );
        bannerLabel2Layout.setVerticalGroup(
            bannerLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bannerLabel2Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(bannerLabel2, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
  
        java.awt.EventQueue.invokeLater(() -> new MainFrameForm().setVisible(true));
    }
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bannerLabel2;
    private javax.swing.JLabel btnCart;
    private javax.swing.JLabel btnLogout;
    private javax.swing.JLabel btnOrders;
    private javax.swing.JLabel btnProfile;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbUser;
    private javax.swing.JSplitPane mainSplit;
    private javax.swing.JPanel sidebarPanel;
    // End of variables declaration//GEN-END:variables
}
