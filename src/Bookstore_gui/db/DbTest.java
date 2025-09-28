/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Bookstore_gui.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbTest {
    public static void main(String[] args) {
        try {
            String url = "jdbc:derby:BookStoreDB;create=true";
            Connection conn = DriverManager.getConnection(url, "app", "app");
            System.out.println("✅ Derby 연결 성공!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

