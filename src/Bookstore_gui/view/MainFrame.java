// File: src/Bookstore_gui/view/MainFrame.java
package Bookstore_gui.view;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.view.books.BooksView;
import Bookstore_gui.view.cart.CartView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    // fields
    private final CardLayout card = new CardLayout();
    private final JPanel content = new JPanel(card);
    private final CartController cart = new CartController(); // shared
    private CartView cartView;                                 // to refresh

    public MainFrame() {
        super("Bookstore");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);

        // left sidebar
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 8, 8));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 8));
        JButton btnBooks  = new JButton("Books");
        JButton btnCart   = new JButton("Cart");
        JButton btnOrders = new JButton("Orders");
        sidebar.add(btnBooks); sidebar.add(btnCart); sidebar.add(btnOrders);

        // right content cards (mounted HERE, inside ctor)
        content.add(new BooksView(cart), "BOOKS");
        cartView = new CartView(cart);
        content.add(cartView, "CART");
        content.add(placeholder("Orders here"), "ORDERS");

        // layout
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
        split.setDividerLocation(200);
        split.setResizeWeight(0);
        setContentPane(split);

        // navigation
        btnBooks.addActionListener(e -> card.show(content, "BOOKS"));
        btnCart.addActionListener(e -> { cartView.refresh(); card.show(content, "CART"); });
        btnOrders.addActionListener(e -> card.show(content, "ORDERS"));

        // default
        card.show(content, "BOOKS");
    }

    private JComponent placeholder(String text) {
        JPanel p = Ui.roundedCard();
        p.setLayout(new GridBagLayout());
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 18f));
        p.add(l, new GridBagConstraints());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        wrap.add(p, BorderLayout.CENTER);
        return wrap;
    }
}