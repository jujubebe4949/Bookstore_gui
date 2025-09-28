package Bookstore_gui.view.cart;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.repo.BookRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartView extends JPanel {
    private final CartController cart;
    private final OrderRepository orders;
    private final UserContext userCtx;
    private final Runnable afterCheckout; // 주문 후 OrdersView.refresh
    private final Runnable onBack;        // BOOKS 화면으로 복귀
    private final BookRepository bookRepo;  
    
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Title","Qty","Price","Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public CartView(CartController cart,
                    OrderRepository orders,
                    UserContext userCtx,
                    BookRepository bookRepo,  
                    Runnable afterCheckout,
                    Runnable onBack) {
        this.cart = cart;
        this.orders = orders;
        this.userCtx = userCtx;
        this.bookRepo = bookRepo;    
        this.afterCheckout = afterCheckout;
        this.onBack = onBack;

        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 하단 버튼 ---
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        JButton btnCheckout = new JButton("Checkout");
        south.add(btnBack);
        south.add(btnCheckout);
        add(south, BorderLayout.SOUTH);

        // --- 버튼 이벤트 ---
        btnBack.addActionListener(e -> { 
            if (onBack != null) onBack.run(); 
        });

        btnCheckout.addActionListener(e -> checkout());
    }

    /** 장바구니 최신 상태 반영 */
    public void refresh() {
        model.setRowCount(0);
        for (CartController.Line l : cart.lines()) {
            model.addRow(new Object[]{
                    l.title,
                    l.qty,
                    String.format("$%.2f", l.price),
                    String.format("$%.2f", l.subtotal())
            });
        }
    }

    /** 체크아웃 로직 */
    private void checkout() {
        String uid = userCtx.getUserId();
        if (uid == null || uid.isBlank()) {
            JOptionPane.showMessageDialog(this, 
                    "Please sign in first.",
                    "Not logged in", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cart.lines().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Your cart is empty. Please add items before checkout.",
                    "Empty cart", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<Order.Item> items = cart.lines().stream()
                    .map(l -> new Order.Item(l.id, l.title, l.qty, l.price))
                    .toList();

            String orderId = orders.create(uid, items);

            cart.clear();
            refresh();

            JOptionPane.showMessageDialog(this, 
                    "Order placed successfully!\nOrder ID: " + orderId,
                    "Order Confirmed", 
                    JOptionPane.INFORMATION_MESSAGE);

            if (afterCheckout != null) afterCheckout.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Checkout failed: " + ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}