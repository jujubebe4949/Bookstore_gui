// =======================================
// File: src/Bookstore_gui/view/books/BookDetailsDialog.java
// 상세 다이얼로그(표지 + 제목/저자/가격/설명 + 수량/카트)
// =======================================
package Bookstore_gui.view.books;

import Bookstore_gui.model.BookProduct;
import Bookstore_gui.util.Money;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public class BookDetailsDialog extends JDialog {
    public BookDetailsDialog(Window owner, BookProduct b, ImageIcon cover, IntConsumer onAdd) {
        super(owner, "Book Details", ModalityType.APPLICATION_MODAL);
        setSize(700, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12,12));
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // 좌: 큰 표지
        JLabel img = new JLabel(cover);
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        add(img, BorderLayout.WEST);

        // 우: 텍스트
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(b.getName());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JLabel author = new JLabel("by " + b.getAuthor());
        JLabel price = new JLabel(Money.fmt(b.getPrice()));
        price.setForeground(new Color(0,120,0));
        JTextArea desc = new JTextArea(b.getDescription());
        desc.setLineWrap(true); desc.setWrapStyleWord(true); desc.setEditable(false);
        desc.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));

        JPanel buy = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JSpinner spQty = new JSpinner(new SpinnerNumberModel(1,1,99,1));
        JButton btnAdd = new JButton("Add to Cart");
        buy.add(new JLabel("Qty:")); buy.add(spQty); buy.add(btnAdd);

        right.add(title); right.add(Box.createVerticalStrut(4));
        right.add(author); right.add(Box.createVerticalStrut(12));
        right.add(price);  right.add(Box.createVerticalStrut(12));
        right.add(new JScrollPane(desc));
        right.add(Box.createVerticalStrut(12));
        right.add(buy);

        add(right, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            int q = (int) spQty.getValue();
            onAdd.accept(q); // why: 카트 연결은 콜백으로 외부에 위임
            dispose();
        });
    }
}