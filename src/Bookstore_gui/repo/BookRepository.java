/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bookstore_gui.repo;

import java.util.*;
import Bookstore_gui.model.BookProduct; // <-- your previous class (move under this package)

public interface BookRepository {
    List<BookProduct> findAll();
    Optional<BookProduct> findById(String id);
}
