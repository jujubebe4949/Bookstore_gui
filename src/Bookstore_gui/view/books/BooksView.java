// File: src/Bookstore_gui/view/books/BooksView.java
package Bookstore_gui.view.books;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.model.BookProduct;
import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.util.Money;
import Bookstore_gui.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class BooksView extends JPanel {
    private static final int CARD_W = 180;
    private static final int CARD_H = 300;
    private static final int GAP    = 16;

    private final BookRepository repo;
    private final CartController cart;
    private final JPanel grid = new JPanel(new GridLayout(0, 1, GAP, GAP));
    private List<BookProduct> shown;

    public BooksView(BookRepository repo, CartController cart) {
        this.repo = repo;
        this.cart = cart;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JScrollPane sp = new JScrollPane(grid);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { relayoutColumns(); }
        });

        // 첫 로드: DB에서 전체 목록
        applyQuery("");
    }

    /** 항상 DB에서 읽어서 shown을 갱신 */
    public void applyQuery(String q) {
        try {
            if (q == null || q.isBlank()) {
                shown = repo.findAll();
            } else if (repo instanceof Bookstore_gui.db.DbBookRepository dbRepo) {
                shown = dbRepo.findByTitleLike(q);
            } else {
                // 인터페이스에 검색이 없을 때 폴백(전체 읽은 뒤 필터)
                var all = repo.findAll();
                String key = q.toLowerCase();
                shown = all.stream()
                        .filter(b -> safe(b.getName()).contains(key) ||
                                     safe(b.getAuthor()).contains(key))
                        .toList();
            }
            rebuildGrid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            shown = java.util.Collections.emptyList();
            rebuildGrid();
        }
    }

    private void rebuildGrid() {
        grid.removeAll();
        if (shown != null) {
            for (BookProduct b : shown) {
                String id = b.getId();
                String imgPath = "/Bookstore_gui/view/common/images/books/" + id + ".jpg";
                ImageIcon raw = Resources.icon(imgPath);
                ImageIcon coverIcon = (raw != null)
                        ? Resources.scale(raw, 160, 240)
                        : Resources.placeholder(160, 240);

                final BookProduct book = b;
                final ImageIcon fcov = coverIcon;
                BookCard card = new BookCard(book, fcov, CARD_W, CARD_H,
                        () -> showDetails(book, fcov));
                grid.add(card);
            }
        }
        relayoutColumns();
        grid.revalidate();
        grid.repaint();
    }

    private void relayoutColumns() {
        int w = Math.max(1, getWidth() - 2*16);
        int col = Math.max(1, w / (CARD_W + GAP));
        grid.setLayout(new GridLayout(0, col, GAP, GAP));
        grid.revalidate();
    }

    /** BookDetailsDialog에 repo 전달하도록 수정 */
    private void showDetails(BookProduct book, ImageIcon icon) {
        new BookDetailsDialog(
                SwingUtilities.getWindowAncestor(this),
                book,
                icon,
                repo, // ✅ 재고 검증용 BookRepository 전달
                qty -> {
                    cart.add(book.getId(), book.getName(), book.getPrice(), qty);
                    JOptionPane.showMessageDialog(this,
                            "Added to cart: " + book.getName()
                                    + " x" + qty + "  (" + Money.fmt(book.getPrice()*qty) + ")");
                }
        ).setVisible(true);
    }

    private static String safe(String s){ return s==null ? "" : s.toLowerCase(); }
}