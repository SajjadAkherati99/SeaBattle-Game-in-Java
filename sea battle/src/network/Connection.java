package network;

import Controllers.GameController;
import Controllers.LoginController;
import Controllers.StartGameController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import messages.Request;
import messages.Response;
import model.User;
import model.UserPublicInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Connection extends Service<HashMap<String, String>> {

    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    private String userJson;

    private LoginController loginController;
    private StartGameController startGameController;
    private GameController gameController;


    public Connection(String userJson, LoginController loginController) {
        this.userJson = userJson;
        this.loginController = loginController;
    }



    @Override
    protected Task<HashMap<String, String>> createTask() {
        return new Task<HashMap<String, String>>() {
            @Override
            protected HashMap<String, String> call() throws Exception {
                HashMap<String, String> msg = new HashMap<>();

                try {
                    socket = new Socket(InetAddress.getLocalHost(), 5050);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);

                    if(loginController.isSignIn()){
                        out.println(Request.SIGN_IN);
                    }else {
                        out.println(Request.SIGN_UP);
                    }
                    out.println(userJson);

                    String responseCode = "";
                    String body = "";

                    msg.put(Request.ACTION_CODE, responseCode);
                    msg.put(Request.BODY, body);
                    while ((responseCode = in.readLine()) != null) {
                        switch (responseCode) {
                            case Response.USER_EXIST: {
                                msg.put(Response.ACTION_CODE, Response.USER_EXIST);
                                loginController.handleServerMessage(msg);
                                break;
                            }

                            case Response.WRONG_PASSWORD: {
                                msg.put(Response.ACTION_CODE, Response.WRONG_PASSWORD);
                                loginController.handleServerMessage(msg);
                                break;
                            }

                            case Response.USER_DONT_EXIST: {
                                msg.put(Response.ACTION_CODE, Response.USER_DONT_EXIST);
                                loginController.handleServerMessage(msg);
                                break;
                            }

                            case Response.SUCCESSFUL_ENTRANCE: {
                                msg.put(Response.ACTION_CODE, Response.SUCCESSFUL_ENTRANCE);
                                loginController.handleServerMessage(msg);
                                break;
                            }

                            case Response.NEW_GAME_ACCEPT: {
                                synchronized (startGameController.getWait()) {
                                    String layoutJson = in.readLine();
                                    startGameController.setLayoutJson(layoutJson);
                                    startGameController.getWait().notify();
                                }
                                break;
                            }

                            case Response.GAME_BEGIN: {
                                startGameController.setEnemyUsername(in.readLine());
                                msg.put(Response.ACTION_CODE, Response.GAME_BEGIN);
                                startGameController.handleServerMessage(msg);
                                break;
                            }

                            case Response.CLIENT_TURN:
                            case Response.GAME_END: {
                                String turn = in.readLine();
                                msg.put(Response.ACTION_CODE, turn);
                                startGameController.handleServerMessage(msg);
                                break;
                            }

                            case Response.ERROR: {
                                msg.put(Response.ACTION_CODE, Response.ERROR);
                                loginController.handleServerMessage(msg);
                                break;
                            }

                            case Response.ATTACK_SUCCESSFUL:{
                                String cellNum = in.readLine();
                                startGameController.updateEnemyCells(Integer.parseInt(cellNum), true);
                                break;
                            }

                            case Response.ATTACK_LOOS:{
                                String cellNum = in.readLine();
                                startGameController.updateEnemyCells(Integer.parseInt(cellNum), false);
                                break;
                            }

                            case Response.DESTROY_CELL_SHIP:{
                                String cellNum = in.readLine();
                                startGameController.updateClientCells(Integer.parseInt(cellNum), true);
                                break;
                            }

                            case Response.DESTROY_CELL:{
                                String cellNum = in.readLine();
                                startGameController.updateClientCells(Integer.parseInt(cellNum), false);
                                break;
                            }

                            case Response.COMPLETELY_DESTROYED_CLIENT:{
                                int ID = Integer.parseInt(in.readLine());
                                startGameController.clientShipDestroyed(ID);
                                break;
                            }

                            case Response.COMPLETELY_DESTROYED_ENEMY:{
                                int ID = Integer.parseInt(in.readLine());
                                startGameController.enemyShipDestroyed(ID);
                                break;
                            }

                            case Response.PLAYER_TABLE: {
                                String table = in.readLine();
                                LinkedList<UserPublicInformation> usersInfo = new LinkedList<>();
                                usersInfo = userInfoJsonToUserInfo(table);
                                startGameController.showUsersInformation(usersInfo);
                                break;
                            }

                            case Response.USER_INFO: {
                                String userInfo = in.readLine();
                                startGameController.showUserInformation(userJsonToUserInfo(userInfo));
                                break;
                            }

                            case Response.LIVE_GAMES:{
                                String liveGamesJson = in.readLine();
                                Map<String, Long> liveGames = liveGamesFromGson(liveGamesJson);
                                startGameController.showLiveGames(liveGames);
                                break;
                            }

                            default:{
                                break;
                            }
                        }
                        if (isCancelled()) {
                            System.out.println("123456");
                            break;
                        }
                    }

                } catch (Exception e) {

                    msg.put(Response.ACTION_CODE, Response.GAME_END);
                    msg.put(Response.BODY, Response.CLIENT_DISCONNECTED);
                    gameController.handleServerMessage(msg);
                    closeSocket();
                    return null;

                }

                msg.put(Response.ACTION_CODE, Response.GAME_END);
                msg.put(Response.BODY, Response.CLIENT_DISCONNECTED);
                gameController.handleServerMessage(msg);
                closeSocket();
                return null;
            }
        };
    }

    private Map<String, Long> liveGamesFromGson(String liveGamesJson) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Map<String, Long>>(){}.getType();
        return gson.fromJson(liveGamesJson, collectionType);
    }

    private User userJsonToUserInfo(String userInfo) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<User>(){}.getType();
        return gson.fromJson(userInfo, collectionType);
    }

    private LinkedList<UserPublicInformation> userInfoJsonToUserInfo(String table) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<LinkedList<UserPublicInformation>>(){}.getType();
            return gson.fromJson(table, collectionType);
    }

    public void closeSocket() {

        try {
            if(this.socket != null && !this.socket.isClosed())
            {
                this.socket.close();
                this.cancel();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while closing the socket");
        }
    }

    public void setStartGameController(StartGameController startGameController) {
        this.startGameController = startGameController;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public StartGameController getStartGameController() {
        return startGameController;
    }
}
