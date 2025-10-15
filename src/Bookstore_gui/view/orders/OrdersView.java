// path: src/Bookstore_gui/view/orders/OrdersView.java
package Bookstore_gui.view.orders;

import Bookstore_gui.controller.UserContext;
import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.view.common.ErrorBanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/** Orders view with inline error banner and safe cancel flow. */
public class OrdersView extends JPanel {
    private final OrderRepository repo;
    private final UserContext userCtx;
    private final Runnable onBack;

    private final ErrorBanner errorBanner = new ErrorBanner();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Order #","Date","Items","Total"}, 0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table = new JTable(model);
    private final DateTimeFormatter fmt = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm")
        .withZone(ZoneId.systemDefault());

    public OrdersView(OrderRepository repo, UserContext userCtx, Runnable onBack){
        this.repo = repo;
        this.userCtx = userCtx;
        this.onBack = onBack;

        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // Inline error banner (why: unify UX, avoid disruptive popups)
        add(errorBanner, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        table.setRowHeight(22);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        JButton btnCancel = new JButton("Cancel Order");
        south.add(btnBack);
        south.add(btnCancel);
        add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> { if (onBack != null) onBack.run(); });
        btnCancel.addActionListener(e -> cancelSelected());

        // Double-click receipt (read-only, low risk → keep dialog)
        table.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getClickCount()==2){
                    int r = table.getSelectedRow();
                    if(r>=0) showReceipt((String)model.getValueAt(r,0));
                }
            }
        });
    }

    /** Rebuilds table for current user; shows banner when empty. */
    public void refresh(){
        errorBanner.clear();
        model.setRowCount(0);

        String uid = userCtx.getUserId();
        if (uid == null || uid.isBlank()) {
            errorBanner.showError("Please sign in to view your orders.");
            return;
        }

        var orders = repo.findByUser(uid);
        if (orders == null || orders.isEmpty()) {
            errorBanner.showError("No orders found.");
            return;
        }

        for(Order o : orders){
            model.addRow(new Object[]{
                shortId(o.getId()),
                fmt.format(o.getCreatedAt()),
                o.getItems().size(),
                String.format("%.2f", o.getTotal())
            });
        }
    }

    /** Cancels selected order with confirmation and banner-based errors. */
    private void cancelSelected() {
        errorBanner.clear();

        int r = table.getSelectedRow();
        if (r < 0) {
            errorBanner.showError("Select an order first.");
            return;
        }

        String shortId = (String) model.getValueAt(r, 0);
        String uid = userCtx.getUserId();
        if (uid == null || uid.isBlank()) {
            errorBanner.showError("Please sign in.");
            return;
        }

        // Resolve full id by short id
        Order match = repo.findByUser(uid).stream()
                .filter(o -> shortId(o.getId()).equals(shortId))
                .findFirst().orElse(null);

        if (match == null) {
            errorBanner.showError("Selected order was not found.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Cancel this order?\n" + shortId,
                "Confirm",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            boolean done = false;
            // Use concrete method if available (why: only db impl exposes cancel)
            if (repo instanceof Bookstore_gui.db.DbOrderRepository db) {
                done = db.cancelOrder(match.getId());
            }

            if (done) {
                JOptionPane.showMessageDialog(this, "Order cancelled.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } else {
                errorBanner.showError("Cancel failed.");
            }
        } catch (Exception ex) {
            errorBanner.showError("Cancel failed: " + ex.getMessage());
        }
    }

    /** Shows a simple read-only receipt for the selected order. */
    private void showReceipt(String shortId){
        errorBanner.clear();

        String uid = userCtx.getUserId();
        if(uid == null || uid.isBlank()){
            errorBanner.showError("Please sign in.");
            return;
        }

        Order match = repo.findByUser(uid).stream()
            .filter(o -> shortId(o.getId()).equals(shortId))
            .findFirst().orElse(null);

        if(match == null){
            errorBanner.showError("Receipt not available.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(shortId).append("\n\n");
        for(Order.Item it : match.getItems()){
            sb.append(String.format("%s  x%d  $%.2f\n", it.title, it.qty, it.subtotal()));
        }
        sb.append("\nTotal: $").append(String.format("%.2f", match.getTotal()));

        JOptionPane.showMessageDialog(this, sb.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private String shortId(String id){ return id.length()>8 ? id.substring(0,8) : id; }
}