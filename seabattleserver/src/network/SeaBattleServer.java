package network;

import com.sun.management.GarbageCollectionNotificationInfo;
import model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SeaBattleServer implements Runnable{

    private ServerSocket serverSocket;


    private ArrayList<Game> games = new ArrayList<>();
    private ArrayList<Game> queuedGames = new ArrayList<>();

    private ArrayList<String> onlineUsers = new ArrayList<>();

    public SeaBattleServer() {
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(5050);
            while (true) {

                Socket socket = serverSocket.accept();
                new Connection(this, socket).start();

            }

        } catch (IOException e) {
            System.out.println("Something went wrong while creating the serversocket");

        } finally {
            try {
                if (serverSocket != null & !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {

                System.out.println("Something went wrong while closing the serversocket");

            }
        }
    }

    public Game searchQueuedGame() {
        synchronized (this.queuedGames){
            if(this.queuedGames.isEmpty())
                return null;
            else
                return this.queuedGames.remove(0);
        }
    }

    public void addQueuedGame(Game newGame) {
        addGame(newGame);
        synchronized(this.queuedGames) {
            this.queuedGames.add(newGame);
        }
    }

    private void addGame(Game newGame) {
        synchronized (this.games){
            games.add(newGame);
        }
    }

    public void removeGame(Game game){
        synchronized (this.games){
            games.remove(game);
        }
    }

    public void removeQueuedGame(Game game) {
        synchronized(this.queuedGames) {
            Iterator<Game> itr = this.queuedGames.iterator();
            while(itr.hasNext()) {
                Game g = itr.next();
                if(g.equals(game)) {
                    itr.remove();
                }
            }
        }
    }

    void addOnlineUser(String onlineUser) {
        synchronized (this.onlineUsers){
            onlineUsers.add(onlineUser);
        }
    }

    void removeOnlineUser(String onlineUser) {
        synchronized (this.onlineUsers){
            onlineUsers.remove(onlineUser);
        }
    }

    boolean isOnlineUser(String user){
        synchronized (this.onlineUsers){
            return(onlineUsers.contains(user));
        }
    }

    public Map<String, Long> getLiveGames() {
        Map<String, Long> liveGames = new HashMap<>();
        synchronized (games){
            for (Game game:games){
                String player1VSPlayer2 = String.format("%s         VS        %s",
                        game.getPlayer1().getUserInfo().getUsername(),
                        game.getPlayer2().getUserInfo().getUsername());
                liveGames.put(player1VSPlayer2, game.getGameId());
            }
        }
        return liveGames;
    }
}
