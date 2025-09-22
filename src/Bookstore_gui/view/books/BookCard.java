// File: src/Bookstore_gui/view/books/BookCard.java
package Bookstore_gui.view.books;

import Bookstore_gui.model.BookProduct;
import Bookstore_gui.util.Money;

import javax.swing.*;
import java.awt.*;

public class BookCard extends JPanel {
    public BookCard(BookProduct b, ImageIcon cover, int w, int h, Runnable onClick) {
        setPreferredSize(new Dimension(w, h));
        setLayout(new BorderLayout(6,6));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(8,8,8,8)
        ));
        setBackground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel img = new JLabel(cover);
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setPreferredSize(new Dimension(160, 240));
        img.setOpaque(true);
        img.setBackground(new Color(245,245,245));
        img.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        add(img, BorderLayout.CENTER);

        JPanel meta = new JPanel();
        meta.setOpaque(false);
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));

        JLabel title  = new JLabel(b.getName());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        JLabel author = new JLabel(b.getAuthor());
        JLabel price  = new JLabel(Money.fmt(b.getPrice()));
        price.setForeground(new Color(0,120,0));

        title.setToolTipText(b.getName());
        author.setToolTipText(b.getAuthor());
        if (title.getText().length()  > 28) title.setText(title.getText().substring(0, 28) + "…");
        if (author.getText().length() > 28) author.setText(author.getText().substring(0, 28) + "…");

        meta.add(title); meta.add(author); meta.add(price);
        add(meta, BorderLayout.SOUTH);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
        });

        System.out.println("[CARD] " + b.getId()
                + " icon=" + (cover==null? "null" : cover.getIconWidth()+"x"+cover.getIconHeight()));
    }
}