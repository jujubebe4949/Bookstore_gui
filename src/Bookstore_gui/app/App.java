package Bookstore_gui.app;

import Bookstore_gui.db.Schema;
import Bookstore_gui.view.StartFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Look & Feel 설정 (시스템 기본)
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // DB 스키마 초기화 (지금은 비어있고, Step 3에서 채움)
        Schema.ensure();

        // 시작 화면 띄우기
        SwingUtilities.invokeLater(() -> new StartFrame().setVisible(true));
    }
}