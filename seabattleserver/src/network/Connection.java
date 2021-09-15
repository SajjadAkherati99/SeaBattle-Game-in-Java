package network;

import Controllers.DatabaseController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import messages.Request;
import messages.Response;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Connection extends Thread{

    private Socket socket;
    private SeaBattleServer mainServer;

    private String userJson;
    private Layout layout;

    User user;

    private Game game;

    Gson gson = new Gson();

    private BufferedReader in;
    private PrintWriter out;

    Connection(SeaBattleServer mainServer, Socket socket) {

        this.socket = socket;
        this.mainServer = mainServer;

    }

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String input;

            while ((input = in.readLine()) != null) {
                switch (input) {
                    case Request.SIGN_IN: {
                        userJson = in.readLine();
                        user = userJsonToUser(userJson);
                        if(!DatabaseController.checkUserExistence(user)){
                            sendToClient(Response.USER_DONT_EXIST);
                        }else if(DatabaseController.checkIncorrectPassword(user)){
                            sendToClient(Response.WRONG_PASSWORD);
                        }else {
                            sendToClient(Response.SUCCESSFUL_ENTRANCE);
                            user = DatabaseController.readUserInformation(user);
                            mainServer.addOnlineUser(user.getUsername());
                        }
                        break;
                    }

                    case Request.SIGN_UP : {
                        userJson = in.readLine();
                        user = userJsonToUser(userJson);
                        if (DatabaseController.checkUserExistence(user)) {
                            sendToClient(Response.USER_EXIST);
                        }
                        else {
                            DatabaseController.addToUsers(user);
                            sendToClient(Response.SUCCESSFUL_ENTRANCE);
                            mainServer.addOnlineUser(user.getUsername());
                        }
                        break;
                    }

                    case Request.NEW_GAME: {
                        sendToClient(Response.NEW_GAME_ACCEPT);
                        layout = new Layout();
                        sendToClient(layoutToJson(layout));
                        break;
                    }

                    case Request.START_GAME: {
                        this.game = joinOrCreatGame();
                        break;
                    }

                    case Response.END_TURN:{
                        game.update(this, false);
                        break;
                    }

                    case Response.USE_TURN:{
                        String cellNum = in.readLine();
                        boolean keepOn = game.update(this, cellNum);
                        game.update(this, keepOn);
                        if (this.game.isGameEnd()) {
                            this.setGame(null);
                            this.user.updateLevel(true);
                        }
                        break;
                    }

                    case Request.PLAYER_TABLE:{
                        LinkedList<UserPublicInformation> usersInformation = DatabaseController.getUsersInformation();
                        sendToClient(Response.PLAYER_TABLE);
                        sendToClient(convertToString(usersInformation));
                        break;
                    }

                    case Request.USER_INFO: {
                        sendToClient(Response.USER_INFO);
                        sendToClient(userToUserJson(user));
                        break;
                    }

                    case Request.LIVE_GAMES: {
                        sendToClient(Response.LIVE_GAMES);
                        Map<String, Long> liveGames = mainServer.getLiveGames();
                        sendToClient(liveGamesToGson(liveGames));
                        break;
                    }

                    default:{
                        sendToClient(Response.ERROR);
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            closeSocket();
            return;
        }
        closeSocket();
    }

    private String liveGamesToGson(Map<String, Long> liveGames) {
        Gson gson = new Gson();
        return gson.toJson(liveGames);
    }

    private String userToUserJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    private String convertToString(LinkedList<UserPublicInformation> usersInformation) {
        for (UserPublicInformation u : usersInformation){
            if(mainServer.isOnlineUser(u.getUsername())){
                u.setLastSeen("online");
            }
            else {
                u.setLastSeen("offline");
            }
        }
        Gson gson = new Gson();
        return gson.toJson(usersInformation);
    }

    private Game joinOrCreatGame() {
        Game g = mainServer.searchQueuedGame();
        if(g == null){
            Game newGame = new Game(this.getId());
            newGame.setPlayer1(new Player(this, user, layout));
            mainServer.addQueuedGame(newGame);
            return newGame;
        }
        else {
            g.setPlayer2(new Player(this, user, layout));
            g.gameBeginResponse();
            mainServer.removeQueuedGame(g);
            return g;
        }
    }

    public synchronized void sendToClient(String msg) {
        out.println(msg);
    }

    private void closeSocket() {
        try {
            if(this.socket != null && !this.socket.isClosed())
            {
                mainServer.removeOnlineUser(user.getUsername());
                if (game != null) {
                    game.aPlayerDisconnected(this);
                }
                this.socket.close();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while closing the socket");
        }
    }

    User userJsonToUser(String userJson)
    {
        Type collectionType = new TypeToken<User>(){}.getType();
        User user = gson.fromJson(userJson, collectionType);
        //System.out.println(user.getUsername(), user.getPassword());
        return new User(user.getName(), user.getUsername(), user.getPassword(), user.getCountry());
    }

    private String layoutToJson(Layout layout) {
        Gson gson = new Gson();

        return gson.toJson(layout.getLayout());
    }

    public SeaBattleServer getMainServer() {
        return mainServer;
    }

    public void setMainServer(SeaBattleServer mainServer) {
        this.mainServer = mainServer;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
