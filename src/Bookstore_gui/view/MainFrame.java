// File: src/Bookstore_gui/view/MainFrame.java
// TL;DR: Sidebar(검색+Books/Cart/Orders) + CardLayout 화면 전환. 
// Pseudocode
// 1) 필드: CardLayout, content JPanel, CartController, OrderRepository, UserContext, BookRepository
// 2) 좌측 사이드바 구성: 검색창 + 버튼 3개
// 3) 우측 content(CardLayout): BooksView, CartView, OrdersView 추가
// 4) 버튼 리스너: 카드 전환, 검색 → BooksView.applyQuery
// 5) 초기 카드: BOOKS

package Bookstore_gui.view;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.repo.impl.InMemoryBookRepository;
import Bookstore_gui.view.books.BooksView;
import Bookstore_gui.view.cart.CartView;
import Bookstore_gui.view.orders.OrdersView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout card = new CardLayout();
    private final JPanel content = new JPanel(card);

    private final CartController cart = new CartController();
    private final OrderRepository orders = new OrderRepository();
    private final UserContext userCtx = new UserContext();

    // NOTE: InMemoryBookRepository 가 무인자 생성자를 제공하도록 구현되어 있어야 합니다.
    private final BookRepository bookRepo = new InMemoryBookRepository();

    private BooksView booksView;
    private CartView  cartView;
    private OrdersView ordersView;

    public MainFrame() {
        super("Bookstore");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);

        // --- left sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel lbTitle = new JLabel("Bookstore");
        lbTitle.setFont(lbTitle.getFont().deriveFont(Font.BOLD, 18f));
        lbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lbTitle);
        sidebar.add(Box.createVerticalStrut(12));

        JTextField tfSearch = new JTextField();
        tfSearch.setMaximumSize(new Dimension(2000, 28)); // why: BoxLayout 폭 고정 방지
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

        // --- content (cards) ---
        booksView  = new BooksView(bookRepo, cart);
        ordersView = new OrdersView(orders, userCtx);
        cartView   = new CartView(cart, orders, userCtx, ordersView::refresh);
        content.add(booksView,  "BOOKS");
        content.add(cartView,   "CART");
        content.add(ordersView, "ORDERS");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
        split.setDividerLocation(240);
        split.setResizeWeight(0);
        setContentPane(split);

        // --- actions ---
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

        card.show(content, "BOOKS");
    }
}
