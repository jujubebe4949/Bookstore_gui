package Bookstore_gui.repo.impl;

import Bookstore_gui.model.BookProduct;
import Bookstore_gui.repo.BookRepository;
import java.util.*;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, BookProduct> map = new LinkedHashMap<>();

    public InMemoryBookRepository() {
        for (BookProduct b : SeedData.sampleBooks()) { // ✅ seed 20 items
            map.put(b.getId(), b);
        }
    }

    @Override public List<BookProduct> findAll() { return new ArrayList<>(map.values()); }
    @Override public Optional<BookProduct> findById(String id) { return Optional.ofNullable(map.get(id)); }
}
