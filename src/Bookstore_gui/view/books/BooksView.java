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
import java.util.stream.Collectors;

public class BooksView extends JPanel {
    private static final int CARD_W = 180;
    private static final int CARD_H = 300;
    private static final int GAP    = 16;

    private final BookRepository repo;
    private final CartController cart;
    private final JPanel grid = new JPanel(new GridLayout(0, 1, GAP, GAP));
    private List<BookProduct> all;
    private List<BookProduct> shown;

    public BooksView(BookRepository repo, CartController cart) {
        this.repo = repo;
        this.cart = cart;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JScrollPane sp = new JScrollPane(grid);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        all = repo.findAll();
        if(all == null) all = java.util.Collections.emptyList();
        shown = all;
        rebuildGrid();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { relayoutColumns(); }
        });
    }

    public void applyQuery(String q) {
        if (q == null || q.isBlank()) shown = all;
        else {
            String key = q.toLowerCase();
            shown = all.stream().filter(b ->
                safe(b.getName()).contains(key) || safe(b.getAuthor()).contains(key)
            ).collect(Collectors.toList());
        }
        rebuildGrid();
    }

  private void rebuildGrid() {
    grid.removeAll();

    for (BookProduct b : shown) {
        // 1) 이미지 경로 만들기
        String id = b.getId();
        String imgPath = "/Bookstore_gui/view/common/images/books/" + id + ".jpg";

        // 2) 클래스패스에서 이미지 로드 + 로그
        ImageIcon raw = Bookstore_gui.util.Resources.icon(imgPath);
        System.out.println("[IMG-LOAD] try=" + imgPath + " found=" + (raw != null));

        // 3) 스케일 또는 플레이스홀더
        ImageIcon coverIcon = (raw != null)
                ? Bookstore_gui.util.Resources.scale(raw, 160, 240)
                : Bookstore_gui.util.Resources.placeholder(160, 240);

        // 4) 람다에서 캡처할 final 변수로 고정
        final BookProduct book = b;
        final ImageIcon fcov = coverIcon;

        // 5) 카드 생성 + 클릭 시 상세
        BookCard card = new BookCard(book, fcov, CARD_W, CARD_H, () -> showDetails(book, fcov));
        grid.add(card);
    }

    // 6) 레이아웃/리페인트
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

    private void showDetails(BookProduct book, ImageIcon icon) {
        new BookDetailsDialog(SwingUtilities.getWindowAncestor(this), book, icon, qty -> {
            // FIX: match CartController signature (id, title, price, qty)
            cart.add(book.getId(), book.getName(), book.getPrice(), qty);
            JOptionPane.showMessageDialog(this, "Added to cart: " + book.getName()
                    + " x" + qty + "  (" + Money.fmt(book.getPrice()*qty) + ")");
        }).setVisible(true);
    }

    private static String safe(String s){ return s==null ? "" : s.toLowerCase(); }
}
