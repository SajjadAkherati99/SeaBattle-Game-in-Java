package model;

import Controllers.DatabaseController;


public class User{
    private String Name;
    private String Username;
    private String Password;
    private String country;
    private int wins = 0;
    private int loses = 0;
    private int level;
    private String lastSeen;

    public User() {
    }

    public User(String name, String username, String password, String country) {
        Name = name;
        Username = username;
        Password = password;
        this.country = country;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }

    public String getCountry() {
        return country;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void updateLevel(boolean win) {
        if(win){
            wins++;
            level++;
        }
        else {
            loses++;
            level--;
        }
        DatabaseController.updateUser(this);
    }
}
