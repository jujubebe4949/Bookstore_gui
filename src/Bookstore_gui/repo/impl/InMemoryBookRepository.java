/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bookstore_gui.repo.impl;

import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.model.BookProduct;

import java.util.*;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, BookProduct> map = new LinkedHashMap<>();

    public InMemoryBookRepository(List<BookProduct> initial) {
        if (initial != null) for (BookProduct b : initial) map.put(getId(b), b);
    }

    // (why) BookProduct의 id 접근 이름이 프로젝트마다 달 수 있어 헬퍼로 캡슐화
    private String getId(BookProduct b) {
        try { return (String) b.getClass().getMethod("getId").invoke(b); }
        catch (Exception ignore) {}
        try { return (String) b.getClass().getMethod("id").invoke(b); }
        catch (Exception ignore) {}
        // 최후: toString (학습/과제용 임시)
        return String.valueOf(Objects.hash(b));
    }

    @Override public List<BookProduct> findAll() { return new ArrayList<>(map.values()); }
    @Override public Optional<BookProduct> findById(String id) { return Optional.ofNullable(map.get(id)); }
}

