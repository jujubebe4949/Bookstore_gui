// path: src/Bookstore_gui/view/cart/CartView.java
package Bookstore_gui.view.cart;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.view.common.ErrorBanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

/** Cart view with inline error banner, remove action, and better layout. */
public class CartView extends JPanel {

    private final CartController cart;
    private final OrderRepository orders;
    private final UserContext userCtx;
    private final Runnable afterCheckout;
    private final Runnable onBack;
    private final BookRepository bookRepo;

    private final ErrorBanner errorBanner = new ErrorBanner();

    // 0:id(hidden) 1:title 2:qty 3:price 4:subtotal
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Title","Qty","Price","Subtotal"}, 0) {
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

        add(errorBanner, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // hide ID column
        TableColumn idCol = table.getColumnModel().getColumn(0);
        idCol.setMinWidth(0); idCol.setMaxWidth(0); idCol.setPreferredWidth(0);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnBack = new JButton("Back");
        JButton btnRemove = new JButton("Remove");
        JButton btnCheckout = new JButton("Checkout");
        south.add(btnBack);
        south.add(btnRemove);
        south.add(btnCheckout);
        add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> { if (onBack != null) onBack.run(); });
        btnRemove.addActionListener(e -> removeSelected());
        btnCheckout.addActionListener(e -> checkout());

        // Delete key removes selected row
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DELETE"), "del");
        table.getActionMap().put("del", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { removeSelected(); }
        });
    }

    /** Refresh cart table with latest items (fills hidden ID column). */
    public void refresh() {
        errorBanner.clear();
        model.setRowCount(0);
        for (CartController.Line l : cart.lines()) {
            model.addRow(new Object[]{
                    l.id,
                    l.title,
                    l.qty,
                    String.format("$%.2f", l.price),
                    String.format("$%.2f", l.subtotal())
            });
        }
    }

    private void removeSelected() {
        errorBanner.clear();
        int r = table.getSelectedRow();
        if (r < 0) { errorBanner.showError("Select an item to remove."); return; }
        String id = (String) model.getValueAt(r, 0);
        cart.remove(id);
        refresh();
    }

    /** Handles checkout logic with validation and error handling. */
    private void checkout() {
        errorBanner.clear();

        String uid = userCtx.getUserId();
        if (uid == null || uid.isBlank()) { errorBanner.showError("Please sign in before checking out."); return; }
        if (cart.lines().isEmpty()) { errorBanner.showError("Your cart is empty."); return; }

        try {
            List<Order.Item> items = cart.lines().stream()
                    .map(l -> new Order.Item(l.id, l.title, l.qty, l.price))
                    .toList();

            String orderId = orders.create(uid, items);

            cart.clear();
            refresh();

            JOptionPane.showMessageDialog(this,
                    "Order completed.\nOrder ID: " + orderId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            if (afterCheckout != null) afterCheckout.run();
        } catch (Exception ex) {
            errorBanner.showError(ex.getMessage() == null ? "Checkout failed." : ex.getMessage());
        }
    }
}