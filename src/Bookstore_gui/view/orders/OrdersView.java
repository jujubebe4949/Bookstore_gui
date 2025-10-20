// path: src/Bookstore_gui/view/orders/OrdersView.java
package Bookstore_gui.view.orders;

import Bookstore_gui.controller.UserContext;
import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.view.common.ErrorBanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Orders view with inline error banner, hidden full-id column, and safe cancel flow. */
public class OrdersView extends JPanel {
    private final OrderRepository repo;
    private final UserContext userCtx;
    private final Runnable onBack;

    private final ErrorBanner errorBanner = new ErrorBanner();

    // 0: hidden full id, 1: short id, 2: date, 3: items, 4: total
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"_ID","Order #","Date","Items","Total"}, 0) {
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable table = new JTable(model);

    private final JButton btnCancel = new JButton("Cancel Order");
    private final DateTimeFormatter fmt = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    public OrdersView(OrderRepository repo, UserContext userCtx, Runnable onBack){
        this.repo = repo;
        this.userCtx = userCtx;
        this.onBack = onBack;

        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // Inline error banner
        add(errorBanner, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Hide full id column
        TableColumn idCol = table.getColumnModel().getColumn(0);
        idCol.setMinWidth(0); idCol.setMaxWidth(0); idCol.setPreferredWidth(0);
        table.setRowHeight(22);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        btnCancel.setEnabled(false); // disabled until selection
        south.add(btnBack);
        south.add(btnCancel);
        add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> { if (onBack != null) onBack.run(); });
        btnCancel.addActionListener(e -> cancelSelected());

        // enable/disable cancel by selection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnCancel.setEnabled(table.getSelectedRow() >= 0);
            }
        });

        // double-click => receipt
        table.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getClickCount()==2){
                    showReceipt();
                }
            }
        });
    }

    /** Rebuild table for current user; shows banner when empty. */
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

        for (Order o : orders) {
            int itemCount = (o.getItems() == null) ? 0 : o.getItems().size();
            String totalStr = String.format("%.2f", o.getTotal());
            model.addRow(new Object[]{
                    o.getId(),                        // hidden full id
                    shortId(o.getId()),               // shown short id
                    fmt.format(o.getCreatedAt()),     // formatted date/time
                    itemCount,
                    totalStr
            });
        }
        btnCancel.setEnabled(false);
    }

    private void cancelSelected() {
        errorBanner.clear();

        String id = selectedOrderId();
        if (id == null) {
            errorBanner.showError("Select an order first.");
            return;
        }

        String uid = userCtx.getUserId();
        if (uid == null || uid.isBlank()) {
            errorBanner.showError("Please sign in.");
            return;
        }

        var match = repo.findByUser(uid).stream()
                .filter(o -> id.equals(o.getId()))
                .findFirst().orElse(null);
        if (match == null) {
            errorBanner.showError("Selected order was not found.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Cancel this order?\n" + shortId(id),
                "Confirm",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            boolean done = false;
            if (repo instanceof Bookstore_gui.db.DbOrderRepository db) {
                done = db.cancelOrder(id);
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

    private void showReceipt(){
        errorBanner.clear();

        String id = selectedOrderId();
        if (id == null) { errorBanner.showError("Receipt not available."); return; }

        String uid = userCtx.getUserId();
        if (uid == null || uid.isBlank()) { errorBanner.showError("Please sign in."); return; }

        var match = repo.findByUser(uid).stream()
                .filter(o -> id.equals(o.getId()))
                .findFirst().orElse(null);

        if (match == null) { errorBanner.showError("Receipt not available."); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(shortId(id)).append("\n\n");
        if (match.getItems() != null) {
            for (Order.Item it : match.getItems()) {
                sb.append(String.format("%s  x%d  $%.2f\n", it.title, it.qty, it.subtotal()));
            }
        }
        sb.append("\nTotal: $").append(String.format("%.2f", match.getTotal()));

        JOptionPane.showMessageDialog(this, sb.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private String selectedOrderId() {
        int r = table.getSelectedRow();
        return (r < 0) ? null : (String) model.getValueAt(r, 0); // hidden full id
    }

    private String shortId(String id){ return (id != null && id.length() > 8) ? id.substring(0,8) : id; }
}