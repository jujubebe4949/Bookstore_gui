/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bookstore_gui.controller;

/**
 *
 * @author julia
 */
public class UserContext {
     private String userId;   // why: 주문 소유자 식별 / owner id
    private String userName; // why: 환영/표시용   / display name

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }

    public void signIn(String id, String name){ this.userId = id; this.userName = name; }
    public void signOut(){ this.userId = null; this.userName = null; }
    public boolean isSignedIn(){ return userId != null && !userId.isEmpty(); }
}