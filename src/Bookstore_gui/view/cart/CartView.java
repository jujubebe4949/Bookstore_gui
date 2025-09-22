// ====================================================================
// File: src/Bookstore_gui/view/cart/CartView.java  (수정 포인트만)
// KOR: 체크아웃 시 미로그인 -> 로그인 다이얼로그 호출
// ENG: At checkout, if not signed in -> open LoginDialog
// ====================================================================
package Bookstore_gui.view.cart;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.controller.UserContext;
import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;
import Bookstore_gui.util.Resources;
import Bookstore_gui.view.common.Renderers;
import Bookstore_gui.view.common.LoginDialog;
import Bookstore_gui.util.Money;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CartView extends JPanel {
    private final CartController cart;
    private final OrderRepository orders;
    private final UserContext userCtx;
    private final Runnable afterCheckout;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"", "Title", "Qty", "Price", "Subtotal", "ID"}, 0) {
        @Override public boolean isCellEditable(int r,int c){return false;}
        @Override public Class<?> getColumnClass(int c){ return c==0? Icon.class : Object.class; }
    };
    private final JTable table = new JTable(model);

    public CartView(CartController cart, OrderRepository orders, UserContext userCtx, Runnable afterCheckout) {
        this.cart = cart; this.orders = orders; this.userCtx = userCtx; this.afterCheckout = afterCheckout;
        setLayout(new BorderLayout(8,8)); setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        table.setRowHeight(64);
        table.getColumnModel().getColumn(0).setPreferredWidth(52);
        table.getColumnModel().getColumn(0).setCellRenderer(new Renderers.ThumbRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.removeColumn(table.getColumnModel().getColumn(5));

        JButton btnRemove=new JButton("Remove Selected");
        JButton btnCheckout=new JButton("Checkout");
        JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        south.add(btnRemove); south.add(btnCheckout); add(south, BorderLayout.SOUTH);

        btnRemove.addActionListener(e -> {
            int r=table.getSelectedRow(); if(r<0){JOptionPane.showMessageDialog(this,"Select an item");return;}
            String id=(String)model.getValueAt(r,5); cart.remove(id); refresh();
        });

        btnCheckout.addActionListener(e -> {
            if(cart.lines().isEmpty()){JOptionPane.showMessageDialog(this,"Cart empty");return;}

            // --- 핵심: 미로그인 → 로그인 다이얼로그 호출 ---
            if(!userCtx.isSignedIn()){
                boolean ok = LoginDialog.show(this, userCtx);
                if(!ok){ JOptionPane.showMessageDialog(this, "Sign in required."); return; }
            }

            List<Order.Item> items = new ArrayList<>();
            for(var l : cart.lines()) items.add(new Order.Item(l.id, l.title, l.qty, l.price));

            String orderId = orders.create(userCtx.getUserId(), items);
            cart.clear(); refresh();
            JOptionPane.showMessageDialog(this, "Order created: #" + (orderId.length()>8?orderId.substring(0,8):orderId));
            if(afterCheckout!=null) afterCheckout.run();
        });
    }

    public void refresh(){
        model.setRowCount(0);
        for(var l: cart.lines()){
            String imgPath = "/Bookstore_gui/view/common/images/books/" + l.id + ".jpg";
            ImageIcon cover = Resources.image(imgPath, 40, 60);
            if (cover == null) cover = Resources.placeholder(40, 60);
            model.addRow(new Object[]{cover, l.title, l.qty, Money.fmt(l.price), Money.fmt(l.price*l.qty), l.id});
        }
    }
}