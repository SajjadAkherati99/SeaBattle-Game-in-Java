package Controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import messages.Request;
import messages.Response;
import model.User;
import model.UserPublicInformation;
import network.Connection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

import model.Cell;

public class StartGameController {

    static StartGameController instance = null;

    Connection connectionToServer;
    Stage mainStage;
    Scene scene;


    int refreshTime = 3;

    @FXML
    private AnchorPane gridsAnchorPane;

    @FXML
    private GridPane client_grid;

    @FXML
    private GridPane enemy_grid;

    @FXML
    private Label timerLabelClient;

    @FXML
    private Label timerLabelEnemy;

    @FXML
    private Label enemyLabel;

    @FXML
    private ProgressIndicator connectProgressIndicator;

    @FXML
    private TableView<UserPublicInformation> bestPlayer = new TableView<UserPublicInformation>();

    @FXML
    private TableColumn<UserPublicInformation, String> Username;

    @FXML
    private TableColumn<UserPublicInformation, Integer> Level;

    @FXML
    private TableColumn<UserPublicInformation, String> LastSeen;

    @FXML
    private VBox vBox;


    Integer startTime = 0;
    Timeline timeline = new Timeline();

    private Object wait = new Object();

    private HashMap<Button, Cell> enemyCells = new HashMap<Button, Cell>();
    private Button[] enemyButtons = new Button[256];
    private HashMap<Button, Cell> clientCells = new HashMap<Button, Cell>();
    private Button[] clientButtons = new Button[256];

    private String layoutJson;

    private String enemyUsername;

    private boolean turn;

    private boolean gameStarted = false;

    private boolean canSeeLiveGames = true;

    public Object getWait() {
        return wait;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }

    public StartGameController() {
    }

    public void initValues(Connection connectionToServer, Stage stage, Scene scene) {
        this.connectionToServer = connectionToServer;
        this.mainStage = stage;
        this.scene = scene;
    }

    @FXML
    void newGame(ActionEvent event) throws InterruptedException{
        canSeeLiveGames = false;
        if((refreshTime > 0) && (!gameStarted)) {
            timeline.stop();
            connectionToServer.getOut().println(Request.NEW_GAME);
            synchronized (wait) {
                wait.wait();
            }
            populateClientGrid(jsonToLayout(layoutJson));
            startTime += 10;
            refreshTime --;
            timerLabelClient.setText(startTime.toString());
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            startTime--;
                            timerLabelClient.setText(startTime.toString());
                            if(startTime <= 0) {
                                gameStarted = true;
                                connectionToServer.getOut().println(Request.START_GAME);
                                connectProgressIndicator.visibleProperty().bind(connectionToServer.runningProperty());
                                timeline.stop();
                                startTime = 25;
                                timerLabelClient.setText("25");
                            }
                        }
                    }));
            timeline.playFromStart();
        }
        else {
            popupAlert(Alert.AlertType.INFORMATION, "Sorry! You can't refresh your layout anymore.");
        }
    }

    @FXML
    void liveGames(){
        if (canSeeLiveGames) {
            connectionToServer.getOut().println(Request.LIVE_GAMES);
        }else {
            popupAlert(Alert.AlertType.ERROR, "Sorry! you are playing. come back later!");
        }
    }

    @FXML
    void bestPlayers(){
        connectionToServer.getOut().println(Request.PLAYER_TABLE);
    }

    @FXML
    void userInfo(){
        connectionToServer.getOut().println(Response.USER_INFO);
    }

    @FXML
    void quitGame(){

    }

    @FXML
    void helpGame(){
        popupAlert(Alert.AlertType.INFORMATION, "AP 2021, Dr.M.Ostovari");
    }

    private void populateClientGrid(LinkedList<Integer> layout)
    {
        String css = this.getClass().getResource("/resources/styles/layoutClient.css").toExternalForm();
        client_grid.getStylesheets().add(css);
        client_grid.getChildren().clear();

        //System.out.println(layout);

        LinkedList<Integer> cellShip = new LinkedList<>();
        cellShip.add(layout.get(0));
        cellShip.add(layout.get(0) + 1);
        cellShip.add(layout.get(0) + 2);
        cellShip.add(layout.get(0) + 3);

        for (int i = 1; i < 3; i++){
            cellShip.add(layout.get(i));
            cellShip.add(layout.get(i) + 1);
            cellShip.add(layout.get(i) + 2);
        }

        for (int i = 3; i < 6; i++){
            cellShip.add(layout.get(i));
            cellShip.add(layout.get(i) + 1);
        }

        for (int i = 6; i < 10; i++){
            cellShip.add(layout.get(i));
        }

        for(int i = 0; i < 16; i++)
        {
            for(int j = 0; j < 16; j++)
            {
                Button btn = new Button();
                btn.setPrefSize(50, 50);
                btn.getStyleClass().add("empty-cell");

                if (cellShip.contains(i + 16*j)) {
                    btn.getStyleClass().add("ship");
                    //System.out.println(i + " " + j);
                }

                clientButtons[i+16*j] = btn;
                clientCells.put(btn, new Cell(i, j, false));
                client_grid.add(btn, i, j);
            }
        }
    }

    private void populateEnemyGrid() {
        String css = this.getClass().getResource("/resources/styles/layoutEnemy.css").toExternalForm();
        enemy_grid.getStylesheets().add(css);
        enemy_grid.getChildren().clear();

        for(int i = 0; i < 16; i++)
        {
            for(int j = 0; j < 16; j++)
            {
                Button btn = new Button();
                btn.setPrefSize(50, 50);
                btn.getStyleClass().add("empty-cell");

                btn.setOnAction(attackClick);

                enemyCells.put(btn, new Cell(i, j, false));
                enemyButtons[i+16*j] = btn;
                enemy_grid.add(btn, i, j);
            }
        }
    }

    private LinkedList<Integer> jsonToLayout(String layoutJson) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<LinkedList<Integer>>(){}.getType();
        return gson.fromJson(layoutJson, collectionType);
    }

    private void popupAlert(Alert.AlertType alertType, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initOwner(mainStage);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }

    public void handleServerMessage(HashMap<String, String> msg) {
        switch (msg.get(Response.ACTION_CODE)) {
            case Response.GAME_BEGIN: {
                Platform.runLater(()-> {
                    connectProgressIndicator.visibleProperty().unbind();
                    connectProgressIndicator.visibleProperty().set(false);
                    populateEnemyGrid();
                    enemyLabel.setText(enemyUsername);
                });
                break;
            }

            case Response.TRUE : {
                turn = true;
                Platform.runLater(() -> {
                    updateTimers(timerLabelClient, timerLabelEnemy);
                });
                break;
            }

            case Response.FALSE : {
                turn = false;
                Platform.runLater(() -> {
                    updateTimers(timerLabelEnemy, timerLabelClient);
                });
                break;
            }

            case Response.WIN:{
                Platform.runLater(() -> {
                    resetStartGameController();
                    popupAlert(Alert.AlertType.INFORMATION, "Congratulations! You win the game.");
                });
                break;
            }

            case Response.LOSE:{
                Platform.runLater(() -> {
                    resetStartGameController();
                    popupAlert(Alert.AlertType.INFORMATION, "Sorry! You lose the game.");
                });
                break;
            }

            default: {
                break;
            }
        }
    }

    private void resetStartGameController() {
        try {
            timeline.stop();
            canSeeLiveGames = true;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/startPage.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            Platform.runLater(() -> {
                mainStage.setScene(scene);
                mainStage.show();
            });
            StartGameController startGameController = (StartGameController)loader.getController();
            connectionToServer.setStartGameController(startGameController);
            startGameController.initValues(connectionToServer, mainStage, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTimers(Label labelStart, Label labelStop) {
        timeline.stop();
        startTime = 25;
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        labelStop.setText("25");
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
            ae -> {
                startTime--;
                labelStart.setText(startTime.toString());
                if (startTime <= 0){
                    timeline.stop();
                    if (turn){
                        connectionToServer.getOut().println(Response.END_TURN);
                        turn = false;
                    }
                }
            }));
        timeline.play();
    }

    private final EventHandler<ActionEvent> attackClick = (ActionEvent event) -> {
        Button btn = (Button) event.getSource();
        if (!enemyCells.get(btn).isDestroyed())
            if (turn) {
                turn = false;
                Integer cellNum = (15-enemyCells.get(btn).getCol()) + 16*(enemyCells.get(btn).getRow());
                //Integer cellNum = enemyCells.get(btn).getCol() * 16 + (enemyCells.get(btn).getRow());
                //System.out.println(btn == enemyButtons.get(enemyCells.get(btn).getCol() + 16*(enemyCells.get(btn).getRow())));
                connectionToServer.getOut().println(Response.USE_TURN);
                connectionToServer.getOut().println(cellNum.toString());
            } else {
                popupAlert(Alert.AlertType.ERROR, "this is not your turn");
            }
    };

    public String getEnemyUsername() {
        return enemyUsername;
    }

    public void setEnemyUsername(String enemyUsername) {
        this.enemyUsername = enemyUsername;
    }

    public void updateEnemyCells(int cellNumber, boolean attackSuccessful) {
        cellNumber = (int)(cellNumber + 15 - 2*(cellNumber%16));
        Button btn = enemyButtons[cellNumber];
        Platform.runLater(() -> {
            enemyCells.get(btn).destroy();
            if (attackSuccessful)
                btn.setStyle("-fx-background-color: Red");
            else
                btn.setStyle("-fx-background-color: White");
        });
    }

    public void updateClientCells(int cellNum, boolean isShip) {
        Button btn = clientButtons[cellNum];
        Platform.runLater(() -> {
            clientCells.get(btn).destroy();
            if(isShip)
                btn.setStyle("-fx-background-color: Red");
            else
                btn.setStyle("-fx-background-color: White");
        });
    }

    public void clientShipDestroyed(int id) {
        int kind = (int)(id / 256);
        int startCell = (int)(id % 256);
        destroyClientCells(startCell, kind);
    }

    public void enemyShipDestroyed(int id) {
        int kind = (int)(id / 256);
        int startCell = (int)(id % 256);
        destroyEnemyCells(startCell, kind);
    }

    private void destroyClientCells(int startCell, int kind) {
        destroyCell(startCell, kind, clientButtons, clientCells);
    }

    private void destroyEnemyCells(int startCell, int kind) {
        startCell = (int)(startCell + 15 - 2*(startCell%16) - 4 + kind + 1 );
        destroyCell(startCell, kind, enemyButtons, enemyCells);
    }

    private void destroyCell(int startCell, int kind, Button[] buttons, HashMap<Button, Cell> cells) {
        int dc = 4 - kind;
        int cellNum = startCell-1;
        if ((startCell%16) > 0) {
            for (int numCell = (cellNum - 16); numCell <= (cellNum + 16); numCell += 16) {
                if ((numCell >= 0) && (numCell <= 255)) {
                    Button btn = buttons[numCell];
                    Cell btnCell = cells.get(btn);
                    Platform.runLater(() -> {
                        btnCell.destroy();
                        btn.setStyle("-fx-background-color: White");
                    });
                }
            }
        }

        cellNum = startCell + dc;
        if((startCell%16) <= (15-dc)) {
            for (int numCell = (cellNum - 16); numCell <= (cellNum + 16); numCell += 16) {
                if ((numCell >= 0) && (numCell <= 255)) {
                    Button btn = buttons[numCell];
                    Cell btnCell = cells.get(btn);
                    Platform.runLater(() -> {
                        btnCell.destroy();
                        btn.setStyle("-fx-background-color: White");
                    });
                }
            }
        }

        for (cellNum = startCell; cellNum < (startCell+dc); cellNum++){
            for(int numCell = (cellNum-16); numCell <= (cellNum+16); numCell+=32){
                if ((numCell >= 0) && (numCell <= 255)){
                    Button btn = buttons[numCell];
                    Cell btnCell = cells.get(btn);
                    Platform.runLater(() -> {
                        btnCell.destroy();
                        btn.setStyle("-fx-background-color: White");
                    });
                }
            }
        }
    }



    public static StartGameController getInstance(){
        if(StartGameController.instance == null){
            instance = new StartGameController();
        }
        return instance;
    }

    public void showUsersInformation(LinkedList<UserPublicInformation> usersInfo) {
        Platform.runLater(() -> {
            bestPlayer = new TableView();

            Username = new TableColumn<>("Username");
            Level = new TableColumn<>("Level");
            LastSeen = new TableColumn<>("LastSeen");

            Username.setCellValueFactory(new PropertyValueFactory<>("username"));
            Level.setCellValueFactory(new PropertyValueFactory<>("level"));
            LastSeen.setCellValueFactory(new PropertyValueFactory<>("lastSeen"));

            Username.setResizable(false);
            Level.setResizable(false);
            LastSeen.setResizable(false);

            Username.setPrefWidth(150);
            Level.setPrefWidth(100);
            LastSeen.setPrefWidth(150);

            bestPlayer.getColumns().addAll(Username);
            bestPlayer.getColumns().addAll(Level);
            bestPlayer.getColumns().addAll(LastSeen);

            bestPlayer.getItems().addAll(usersInfo);
            bestPlayer.setPrefSize(400, 400);

            vBox = new VBox(bestPlayer);
            vBox.setPrefSize(400, 400);

            Stage stage = new Stage();
            Image icon = new Image(String.valueOf(getClass().getResource("/resources/images/sea_battle.jpg")));
            stage.getIcons().add(icon);
            stage.setTitle("Sea Battle Game Best Players");
            stage.setResizable(false);
            Scene scene = new Scene(vBox);
            stage.setScene(scene);
            stage.show();
        });
    }

    public void showUserInformation(User userInfo) {
        Platform.runLater(() -> {
            ListView listViewMain = new ListView();
            listViewMain.getItems().add("Name");
            listViewMain.getItems().add("Username");
            listViewMain.getItems().add("Password");
            listViewMain.getItems().add("Country");
            listViewMain.getItems().add("Wins");
            listViewMain.getItems().add("Loses");
            listViewMain.getItems().add("Level");
            listViewMain.setEditable(false);
            listViewMain.setPrefSize(150, 150);

            ListView listViewInfo = new ListView();
            listViewInfo.getItems().add(userInfo.getName());
            listViewInfo.getItems().add(userInfo.getUsername());
            listViewInfo.getItems().add(userInfo.getPassword());
            listViewInfo.getItems().add(userInfo.getCountry());
            listViewInfo.getItems().add(((Integer)userInfo.getWins()).toString());
            listViewInfo.getItems().add(((Integer)userInfo.getLoses()).toString());
            listViewInfo.getItems().add(((Integer)userInfo.getLevel()).toString());
            listViewInfo.setEditable(true);
            listViewInfo.setPrefSize(150, 150);
            HBox hBox = new HBox(listViewMain, listViewInfo);

            Scene scene = new Scene(hBox, 300, 150);
            Stage stage = new Stage();
            Image icon = new Image(String.valueOf(getClass().getResource("/resources/images/sea_battle.jpg")));
            stage.getIcons().add(icon);
            stage.setResizable(false);
            stage.setTitle("User Information");
            stage.setScene(scene);
            stage.show();
        });
    }

    public void showLiveGames(Map<String, Long>liveGames) {
        Platform.runLater(() -> {
            ListView liveGamesView = new ListView();

            liveGamesView.getItems().addAll(liveGames.keySet());

            liveGamesView.setEditable(false);
            liveGamesView.setPrefSize(200, 400);

            HBox hBox = new HBox(liveGamesView);

            Scene scene = new Scene(hBox, 200, 400);
            Stage stage = new Stage();
            Image icon = new Image(String.valueOf(getClass().getResource("/resources/images/sea_battle.jpg")));
            stage.getIcons().add(icon);
            stage.setResizable(false);
            stage.setTitle("Live Games");
            stage.setScene(scene);
            stage.show();



            liveGamesView.setCellFactory(lv -> {
                ListCell<String> cell = new ListCell<String>() {
                    protected void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if(empty)
                            setText(null);
                        else
                            setText(item.toString());
                    }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY){
                        System.out.println(liveGames.get(cell.getItem()));
                    }
                });
                return cell;
            });

        });
    }
}
