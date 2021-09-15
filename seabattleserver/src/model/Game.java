package model;

import messages.Response;
import network.Connection;

import java.util.ArrayList;

public class Game {
    private Player player1;

    private Player player2;

    private boolean gameEnd = false;

    private long gameId = 0;

    private ArrayList<Connection> watchList;

    public boolean isGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(boolean gameEnd) {
        this.gameEnd = gameEnd;
    }

    public Game(long gameId) {
        this.gameId = gameId;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }


    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Player getPlayer1() {
        return player1;
    }


    public Player getPlayer2() {
        return player2;
    }

    public long getGameId() {
        return gameId;
    }

    public void gameBeginResponse() {
        player1.getConnection().sendToClient(Response.GAME_BEGIN);
        player1.getConnection().sendToClient(player2.getUserInfo().getUsername());

        player2.getConnection().sendToClient(Response.GAME_BEGIN);
        player2.getConnection().sendToClient(player1.getUserInfo().getUsername());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        player1.setTurn(true);
        player2.setTurn(false);
    }

    public void update(Connection connection, boolean keepOn) {
        if (keepOn){
            if (player1.getConnection() == connection) {
                player1.setTurn(true);
                player2.setTurn(false);
            } else {
                player1.setTurn(false);
                player2.setTurn(true);
            }
        }
        else {
            if (player1.getConnection() == connection) {
                player1.setTurn(false);
                player2.setTurn(true);
            } else {
                player1.setTurn(true);
                player2.setTurn(false);
            }
        }
    }

    public boolean update(Connection connection, String cellNum) {
        int cellNumber = Integer.parseInt(cellNum);
        boolean keepOn;
        if(connection == player1.getConnection()){
            keepOn = sendMessage(cellNum, cellNumber, player2, player1);
        }
        else{
            keepOn = sendMessage(cellNum, cellNumber, player1, player2);
        }
        return keepOn;
    }

    private boolean sendMessage(String cellNum, int cellNumber, Player player1, Player player2) {
        boolean attackSuccessful;
        attackSuccessful = player1.getLayout().checkExist(cellNumber);
        if(attackSuccessful){
            player2.getConnection().sendToClient(Response.ATTACK_SUCCESSFUL);
            player2.getConnection().sendToClient(cellNum);

            player1.getConnection().sendToClient(Response.DESTROY_CELL_SHIP);
            player1.getConnection().sendToClient(cellNum);

            Integer ID = player1.getLayout().checkShip_i_isDead();
            if (ID >= 0) {
                player2.getConnection().sendToClient(Response.COMPLETELY_DESTROYED_ENEMY);
                player2.getConnection().sendToClient(ID.toString());

                player1.getConnection().sendToClient(Response.COMPLETELY_DESTROYED_CLIENT);
                player1.getConnection().sendToClient(ID.toString());

                gameEnd = player1.getLayout().shipsEnd();
                if (gameEnd){
                    player1.getConnection().sendToClient(Response.GAME_END);
                    player1.getConnection().sendToClient(Response.LOSE);

                    player2.getConnection().sendToClient(Response.GAME_END);
                    player2.getConnection().sendToClient(Response.WIN);

                    player1.getUserInfo().updateLevel(false);
                    player1.getConnection().getMainServer().removeGame(this);
                    player1.getConnection().setGame(null);
                }
            }
            return true;
        }
        else {
            player2.getConnection().sendToClient(Response.ATTACK_LOOS);
            player2.getConnection().sendToClient(cellNum);

            player1.getConnection().sendToClient(Response.DESTROY_CELL);
            player1.getConnection().sendToClient(cellNum);
            return false;
        }
    }

    public void aPlayerDisconnected(Connection connection) {
        Player disconnectedPlayer;
        Player stillConnectedPlayer;
        if (player1.getConnection() == connection){
            disconnectedPlayer = player1;
            stillConnectedPlayer = player2;
        } else {
            disconnectedPlayer = player2;
            stillConnectedPlayer = player1;
        }

        if(stillConnectedPlayer != null) {
            stillConnectedPlayer.getConnection().sendToClient(Response.GAME_END);
            stillConnectedPlayer.getConnection().sendToClient(Response.WIN);
            stillConnectedPlayer.getConnection().setGame(null);
        }
        else {
            connection.getMainServer().removeGame(this);
            connection.getMainServer().removeQueuedGame(this);
        }
        disconnectedPlayer.getConnection().getMainServer().removeGame(this);
        disconnectedPlayer.getConnection().setGame(null);
    }
}
