// File: src/Bookstore_gui/view/MainFrame.java
package Bookstore_gui.view;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.db.DbManager;
import Bookstore_gui.db.DbBookRepository;
import Bookstore_gui.db.DbOrderRepository;
import Bookstore_gui.db.DbUserRepository;
import Bookstore_gui.view.books.BooksView;
import Bookstore_gui.view.cart.CartView;
import Bookstore_gui.view.orders.OrdersView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout card = new CardLayout();
    private final JPanel content  = new JPanel(card);

    private final BookRepository  bookRepo = new DbBookRepository();
    private final CartController  cart     = new CartController(bookRepo);
    private final OrderRepository orders   = new DbOrderRepository();
    private final UserContext     userCtx;

    private BooksView booksView;
    private CartView  cartView;
    private OrdersView ordersView;

    private final JLabel lbUser = new JLabel("Guest");

    public MainFrame(UserContext userCtx) {
        super("Bookstore");
        this.userCtx = userCtx;

        try { DbManager.initSchema(); } catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel lbTitle = new JLabel("Bookstore");
        lbTitle.setFont(lbTitle.getFont().deriveFont(Font.BOLD, 18f));
        lbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lbTitle);
        sidebar.add(Box.createVerticalStrut(8));

        JPanel who = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        JButton btnProfile = new JButton("Profile");
        JButton btnLogout  = new JButton("Logout");
        who.add(new JLabel("User: "));
        who.add(lbUser);
        who.add(Box.createHorizontalStrut(6));
        who.add(btnProfile);
        who.add(Box.createHorizontalStrut(4));
        who.add(btnLogout);
        who.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(who);
        sidebar.add(Box.createVerticalStrut(12));

        JTextField tfSearch = new JTextField();
        tfSearch.setMaximumSize(new Dimension(2000, 28));
        JButton btnSearch = new JButton("Search");
        btnSearch.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(tfSearch);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnSearch);
        sidebar.add(Box.createVerticalStrut(18));

        JButton btnBooks = new JButton("Books");
        JButton btnCart  = new JButton("Cart");
        JButton btnOrdersBtn = new JButton("Orders");
        btnBooks.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCart.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnOrdersBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(btnBooks);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnCart);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnOrdersBtn);
        sidebar.add(Box.createVerticalGlue());

        booksView  = new BooksView(bookRepo, cart);
        ordersView = new OrdersView(orders, userCtx, () -> card.show(content, "BOOKS"));

        Runnable onBack = () -> card.show(content, "BOOKS");
        Runnable afterCheckout = ordersView::refresh;

        cartView   = new CartView(cart, orders, userCtx, bookRepo, afterCheckout, onBack);

        content.add(booksView,  "BOOKS");
        content.add(cartView,   "CART");
        content.add(ordersView, "ORDERS");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
        split.setDividerLocation(240);
        split.setResizeWeight(0);
        setContentPane(split);

        Runnable doSearch = () -> {
            String q = tfSearch.getText() == null ? "" : tfSearch.getText().trim();
            card.show(content, "BOOKS");
            booksView.applyQuery(q);
        };
        btnSearch.addActionListener(e -> doSearch.run());
        tfSearch.addActionListener(e -> doSearch.run());
        btnBooks.addActionListener(e -> card.show(content, "BOOKS"));
        btnCart.addActionListener(e -> { cartView.refresh(); card.show(content, "CART"); });
        btnOrdersBtn.addActionListener(e -> { ordersView.refresh(); card.show(content, "ORDERS"); });

        btnLogout.addActionListener(e -> {
            userCtx.signOut();
            dispose();
            new StartFrame().setVisible(true); // 왜: 완전 재시작(돌아가기 충족)
        });

        btnProfile.addActionListener(e -> {
            if (!userCtx.isSignedIn()) {
                JOptionPane.showMessageDialog(this, "Please login first.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String newName = JOptionPane.showInputDialog(this,
                    "Enter new display name:", userCtx.getName());
            if (newName == null) return;
            newName = newName.trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.",
                        "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                DbUserRepository urepo = new DbUserRepository();
                boolean ok = urepo.updateUserName(userCtx.getUserId(), newName);
                if (ok) {
                    userCtx.setUser(userCtx.getUserId(), newName, userCtx.getEmail());
                    refreshUserLabel();
                    JOptionPane.showMessageDialog(this, "Updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshUserLabel();
        card.show(content, "BOOKS");
    }

    private void refreshUserLabel() {
        lbUser.setText(userCtx.isSignedIn() ? userCtx.getName() : "Guest");
    }
}