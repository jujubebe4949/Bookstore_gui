package Bookstore_gui.view.orders;

import Bookstore_gui.controller.UserContext;
import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class OrdersView extends JPanel {
    private final OrderRepository repo;
    private final UserContext userCtx;
    private final Runnable onBack;

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
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.setRowHeight(22);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        // South bar: Back + Cancel
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        JButton btnCancel = new JButton("Cancel Order");
        south.add(btnBack);
        south.add(btnCancel);
        add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> { if (onBack != null) onBack.run(); });
        btnCancel.addActionListener(e -> cancelSelected());

        // double-click receipt
        table.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getClickCount()==2){ 
                    int r = table.getSelectedRow(); 
                    if(r>=0) showReceipt((String)model.getValueAt(r,0)); 
                }
            }
        });
    }

    public void refresh(){
        model.setRowCount(0);
        String uid = userCtx.getUserId(); 
        if(uid==null) return;

        for(Order o : repo.findByUser(uid)){
            model.addRow(new Object[]{
                shortId(o.getId()), 
                fmt.format(o.getCreatedAt()),
                o.getItems().size(), 
                String.format("%.2f", o.getTotal())
            });
        }
    }

    private void cancelSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String shortId = (String) model.getValueAt(r, 0);
        String uid = userCtx.getUserId();
        if (uid == null) return;

        // find full id by matching short id
        Order match = repo.findByUser(uid).stream()
                .filter(o -> shortId(o.getId()).equals(shortId))
                .findFirst().orElse(null);
        if (match == null) return;

        int ok = JOptionPane.showConfirmDialog(this,
                "Cancel this order?\n" + shortId,
                "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            boolean done = false;
            // repo가 DbOrderRepository 타입이면 cancelOrder 사용
            if (repo instanceof Bookstore_gui.db.DbOrderRepository db) {
                done = db.cancelOrder(match.getId());
            }
            if (done) {
                JOptionPane.showMessageDialog(this, "Order cancelled.");
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Cancel failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cancel failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceipt(String shortId){
        String uid = userCtx.getUserId(); 
        if(uid==null) return;

        Order match = repo.findByUser(uid).stream()
            .filter(o -> shortId(o.getId()).equals(shortId))
            .findFirst().orElse(null);

        if(match==null) return;

        StringBuilder sb = new StringBuilder(); 
        sb.append("Order #").append(shortId).append("\n\n");

        for(Order.Item it : match.getItems()){ 
            sb.append(String.format("%s  x%d  $%.2f\n", it.title, it.qty, it.subtotal())); 
        }
        sb.append("\nTotal: $").append(String.format("%.2f", match.getTotal()));

        JOptionPane.showMessageDialog(this, sb.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private String shortId(String id){ return id.length()>8? id.substring(0,8): id; }
}