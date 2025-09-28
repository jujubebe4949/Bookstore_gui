package Bookstore_gui.view.books;

import Bookstore_gui.model.BookProduct;
import Bookstore_gui.repo.BookRepository;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

/**
 * Book details popup dialog with stock validation.
 * - Shows cover, title, author, price, stock.
 * - User enters quantity, checked against stock.
 * - On success → callback to add to cart.
 */
public class BookDetailsDialog extends JDialog {
    private final JTextField tfQty = new JTextField("1", 6);
    private final BookRepository repo;
    private final BookProduct book;

    public BookDetailsDialog(Window owner,
                             BookProduct book,
                             ImageIcon cover,
                             BookRepository repo,
                             IntConsumer onOk) {
        super(owner, "Add to Cart", ModalityType.APPLICATION_MODAL);
        this.repo = repo;
        this.book = book;

        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // --- center area: book cover + info ---
        JPanel center = new JPanel(new BorderLayout(8, 8));
        if (cover != null) {
            center.add(new JLabel(cover), BorderLayout.WEST);
        }

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(new JLabel(book.getName()));
        info.add(new JLabel("By " + (book.getAuthor() == null ? "" : book.getAuthor())));
        info.add(new JLabel(String.format("Price: $%.2f", book.getPrice())));
        info.add(Box.createVerticalStrut(8));

        // --- stock check ---
        int currentStock = 0;
        try {
            currentStock = repo.getStock(book.getId());
        } catch (Exception ex) {
            // fallback in case of DB error
            JOptionPane.showMessageDialog(this,
                    "Failed to load stock info: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        info.add(new JLabel("In stock: " + currentStock));

        // --- qty input row ---
        JPanel qtyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        qtyRow.add(new JLabel("Qty:"));
        qtyRow.add(tfQty);
        info.add(qtyRow);

        center.add(info, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // --- south buttons ---
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnAdd = new JButton("Add");
        south.add(btnCancel);
        south.add(btnAdd);
        add(south, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnAdd.addActionListener(e -> submit(onOk));

        pack();
        setLocationRelativeTo(owner);
    }

    /** Validate quantity and call onOk if valid */
    private void submit(IntConsumer onOk) {
        try {
            int qty = Integer.parseInt(tfQty.getText().trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity must be a positive integer.",
                        "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                tfQty.requestFocus();
                return;
            }

            int stock = repo.getStock(book.getId()); // 최신 재고 조회
            if (qty > stock) {
                JOptionPane.showMessageDialog(this,
                        "Only " + stock + " in stock.",
                        "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                tfQty.requestFocus();
                return;
            }

            // Success → callback
            onOk.accept(qty);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid integer quantity.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            tfQty.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}