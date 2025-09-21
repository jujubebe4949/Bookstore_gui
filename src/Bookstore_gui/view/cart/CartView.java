package Bookstore_gui.view.cart;

import Bookstore_gui.controller.CartController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CartView extends JPanel {
    private final CartController cart;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Title","Qty","Subtotal","ID"}, 0) {
        @Override public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public CartView(CartController cart){
        this.cart = cart;
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // ID 컬럼 숨김 | hide ID column
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.removeColumn(table.getColumnModel().getColumn(3));

        JButton btnRemove = new JButton("Remove Selected");
        JButton btnCheckout = new JButton("Checkout");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        south.add(btnRemove); south.add(btnCheckout);
        add(south, BorderLayout.SOUTH);

        btnRemove.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r < 0){ JOptionPane.showMessageDialog(this, "Select an item"); return; }
            String id = (String) model.getValueAt(r, 3);
            cart.remove(id); refresh();
        });
        btnCheckout.addActionListener(e -> {
            if(cart.lines().isEmpty()){ JOptionPane.showMessageDialog(this, "Cart empty"); return; }
            JOptionPane.showMessageDialog(this, String.format("Total: $%.2f", cart.total()));
            cart.clear(); refresh();
        });
    }

    // KOR: 컨트롤러 → 테이블 갱신 | ENG: sync table from controller
    public void refresh(){
        model.setRowCount(0);
        for(var l : cart.lines()){
            model.addRow(new Object[]{ l.title, l.qty, String.format("%.2f", l.subtotal()), l.id });
        }
    }
}
