package model;

import messages.Response;
import network.Connection;

import java.util.ArrayList;
import java.util.LinkedList;

public class Player {
    private Connection connection;
    private User userInfo;
    private Layout layout;
    private boolean turn = false;

    public Player(Connection connection, User userInfo, Layout layout) {
        this.connection = connection;
        this.userInfo = userInfo;
        this.layout = layout;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
        connection.sendToClient(Response.CLIENT_TURN);
        if(turn)
        {
            connection.sendToClient(Response.TRUE);
        } else
        {
            connection.sendToClient(Response.FALSE);
        }
    }
}
