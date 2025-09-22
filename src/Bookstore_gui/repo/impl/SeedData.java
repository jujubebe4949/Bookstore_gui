package Bookstore_gui.repo.impl;

import Bookstore_gui.model.BookProduct;
import java.util.*;

public final class SeedData {
    private SeedData() {}

    public static List<BookProduct> sampleBooks() {
        List<BookProduct> list = new ArrayList<>();

        list.add(new BookProduct("B001", "Crying In H Mart",
            "Memoir of family, food, and identity.", 25, 5,
            "Michelle Zauner"));

        list.add(new BookProduct("B002", "Project Hail Mary",
            "A lone astronaut on a desperate mission.", 24, 5,
            "Andy Weir"));

        list.add(new BookProduct("B003", "The Ministry For The Future",
            "Near‑future climate survival story.", 28, 5,
            "Kim Stanley Robinson"));

        list.add(new BookProduct("B004", "Two Places To Call Home",
            "Picture book about two loving homes.", 21, 5,
            "Phil Earle"));

        list.add(new BookProduct("B005", "He Puka Ngohe",
            "Māori activity workbook for learners.", 27, 5,
            "Katherine Q. Merewether & Pania Papa"));

        list.add(new BookProduct("B006", "Before George",
            "A girl rebuilds identity after tragedy.", 28, 5,
            "Deborah Robertson"));

        list.add(new BookProduct("B007", "How To Loiter In A Turf War",
            "Sharp, funny Auckland urban tale.", 28, 5,
            "Jessica (Coco Solid) Hansell"));

        list.add(new BookProduct("B008", "Verity",
            "Dark, addictive psychological thriller.", 28, 5,
            "Colleen Hoover"));

        list.add(new BookProduct("B009", "The Dispossessed",
            "Classic twin‑world utopia/anarchism.", 25, 5,
            "Ursula K. Le Guin"));

        list.add(new BookProduct("B010", "The Bullet That Missed",
            "Thursday Murder Club #3 mystery.", 26, 5,
            "Richard Osman"));

        list.add(new BookProduct("B011", "Don Binney: Flight Path",
            "Illustrated study of iconic NZ artist Don Binney.", 90, 3,
            "Gregory O'Brien"));

        list.add(new BookProduct("B012", "Art And Court Of James VI and I",
            "Art and objects of a dynamic royal court.", 80, 4,
            "Kate Anderson et al."));

        list.add(new BookProduct("B013", "Reasons Not To Worry",
            "Practical, modern Stoicism guide.", 33, 5,
            "Brigid Delaney"));

        list.add(new BookProduct("B014", "Exercised",
            "Science of activity, rest, and health.", 26, 5,
            "Daniel Lieberman"));

        list.add(new BookProduct("B015", "Recipetin Eats: Dinner",
            "150 fail‑proof dinner recipes.", 50, 5,
            "Nagi Maehashi"));

        list.add(new BookProduct("B016", "Vegful",
            "Vegetable‑forward cookbook for everyone.", 55, 5,
            "Nadia Lim"));

        list.add(new BookProduct("B017", "Easy Wins",
            "12 flavour hits, 125 recipes.", 60, 5,
            "Anna Jones"));

        list.add(new BookProduct("B018", "Pachinko",
            "Epic Korean‑Japanese family saga.", 25, 5,
            "Min Jin Lee"));

        list.add(new BookProduct("B019", "Stone Yard Devotional",
            "Moving novel of grief and forgiveness.", 38, 5,
            "Charlotte Wood"));

        list.add(new BookProduct("B020", "Beartown",
            "A hockey town tested by crisis.", 26, 5,
            "Fredrik Backman"));

        return list;
    }
}
