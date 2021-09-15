package model;

public class UserPublicInformation {
    String username;
    int level = 0;
    String lastSeen;

    public UserPublicInformation(String username, int level, String lastSeen) {
        this.username = username;
        this.level = level;
        this.lastSeen = lastSeen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
