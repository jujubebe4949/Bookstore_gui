// File: src/Bookstore_gui/view/books/BookDetailsDialog.java
package Bookstore_gui.view.books;

import Bookstore_gui.model.BookProduct;
import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.util.Money;
import Bookstore_gui.view.common.ErrorBanner;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

/**
 * Book details popup dialog with stock validation.
 * Shows cover, title, author, price, stock, and allows adding to cart.
 * Uses ErrorBanner for inline error display instead of popups.
 */
public class BookDetailsDialog extends JDialog {

    private final JTextField tfQty = new JTextField("1", 6);
    private final BookRepository repo;
    private final BookProduct book;
    private final ErrorBanner errorBanner = new ErrorBanner();

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

        // top banner for consistent inline error display
        add(errorBanner, BorderLayout.NORTH);

        // --- center area: book cover + info ---
        JPanel center = new JPanel(new BorderLayout(8, 8));
        if (cover != null) {
            center.add(new JLabel(cover), BorderLayout.WEST);
        }

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(new JLabel(book.getName()));
        info.add(new JLabel("By " + (book.getAuthor() == null ? "" : book.getAuthor())));
        info.add(new JLabel("Price: " + Money.fmt(book.getPrice())));
        info.add(Box.createVerticalStrut(8));

        // --- stock check ---
        int currentStock = 0;
        try {
            currentStock = repo.getStock(book.getId());
        } catch (Exception ex) {
            errorBanner.showError("Failed to load stock info: " + ex.getMessage());
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
        errorBanner.clear();

        try {
            int qty = Integer.parseInt(tfQty.getText().trim());
            if (qty <= 0) {
                errorBanner.showError("Quantity must be a positive integer.");
                tfQty.requestFocus();
                return;
            }

            int stock = repo.getStock(book.getId());
            if (qty > stock) {
                errorBanner.showError("Only " + stock + " item(s) in stock.");
                tfQty.requestFocus();
                return;
            }

            // Success → callback
            onOk.accept(qty);
            dispose();
        } catch (NumberFormatException ex) {
            errorBanner.showError("Please enter a valid integer quantity.");
            tfQty.requestFocus();
        } catch (Exception ex) {
            errorBanner.showError("Unexpected error: " + ex.getMessage());
        }
    }
}