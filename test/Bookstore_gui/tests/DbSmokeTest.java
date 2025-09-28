/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bookstore_gui.tests;

import Bookstore_gui.db.DbManager;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class DbSmokeTest {

    @Test
    public void canConnectAndQuery() throws Exception {
        DbManager.initSchema(); // no-op이어도 안전
        try (Connection c = DbManager.connect();
             Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }
}