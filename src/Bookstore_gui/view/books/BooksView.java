// File: src/Bookstore_gui/view/books/BooksView.java
package Bookstore_gui.view.books;

import Bookstore_gui.controller.CartController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class BooksView extends JPanel {
    private final CartController cart; // why: 담기 연결 | add-to-cart
    private final JTextField tfSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnAdd    = new JButton("Add to Cart");
    private final JTable table;
    private final DefaultTableModel model;

    public BooksView(CartController cart){
        this.cart = cart;
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Keyword:"));
        top.add(tfSearch); top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Title","Author","Price","Stock","ID"}, 0){
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        table = new JTable(model); table.setRowHeight(22);

        // 숫자 우정렬 | right align numbers
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(3).setCellRenderer(right);

        // ID 숨김 | hide ID
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.removeColumn(table.getColumnModel().getColumn(4));

        JTableHeader h = table.getTableHeader();
        h.setFont(h.getFont().deriveFont(Font.BOLD));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        bottom.add(btnAdd); add(bottom, BorderLayout.SOUTH);

        seedDemoRows(); // why: 화면 확인용 더미 | demo rows for UI check

        btnSearch.addActionListener(e -> doSearch());
        btnAdd.addActionListener(e -> doAdd());
    }

    private void doSearch(){
        String q = tfSearch.getText().trim().toLowerCase();
        seedDemoRows(); // reset demo rows first
        if(q.isEmpty()) return;
        for(int i = model.getRowCount()-1; i >= 0; i--){
            String title = String.valueOf(model.getValueAt(i,0)).toLowerCase();
            if(!title.contains(q)) model.removeRow(i);
        }
    }

    private void doAdd(){
        int r = table.getSelectedRow();
        if(r < 0){ JOptionPane.showMessageDialog(this, "Select a book"); return; }
        String title = String.valueOf(model.getValueAt(r, 0));
        double price = Double.parseDouble(String.valueOf(model.getValueAt(r, 2)));
        String id    = String.valueOf(model.getValueAt(r, 4)); // hidden column
        cart.add(id, title, price, 1);
        JOptionPane.showMessageDialog(this, "Added: " + title);
    }

    private void seedDemoRows(){
        model.setRowCount(0);
        model.addRow(new Object[]{"Clean Code","Robert C. Martin", 38.50, 5, "B001"});
        model.addRow(new Object[]{"Effective Java","Joshua Bloch", 45.00, 3, "B002"});
        model.addRow(new Object[]{"Refactoring","Martin Fowler", 49.90, 0, "B003"});
        model.addRow(new Object[]{"Design Patterns","GoF", 59.00, 7, "B004"});
    }
}
