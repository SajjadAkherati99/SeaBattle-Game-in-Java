package Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.google.gson.Gson;

import messages.Response;
import model.User;
import network.Connection;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField signUpName;

    @FXML
    private TextField signUpUsername;

    @FXML
    private TextField signUpPassword;

    @FXML
    private TextField signUpCountry;

    @FXML
    private TextField signInUsername;

    @FXML
    private TextField signInPassword;

    @FXML
    private Button signUp;

    @FXML
    private Button signIn;

    private Scene scene;
    private Stage mainStage;

    private Connection connectionToServer;

    boolean isSignIn;

    @FXML
    void signIn(ActionEvent event){

        scene = signUp.getScene();
        mainStage = (Stage) scene.getWindow();

        User newUser = new User();

        boolean check = (signInUsername.getText().equals("")) || (signInPassword.getText().equals(""));
        if (check){
            popupAlert(Alert.AlertType.ERROR, "Fill All Please!!!");
        }
        else {
            isSignIn = true;
            newUser.setUsername(signInUsername.getText());
            newUser.setPassword(signInPassword.getText());
            String userJson = userToJson(newUser);

            connectionToServer = new Connection(userJson, this);
            connectionToServer.start();
        }
    }

    @FXML
    void signUp(ActionEvent event){
        scene = signUp.getScene();
        mainStage = (Stage) scene.getWindow();

        User newUser = new User();

        boolean check = (signUpName.getText().equals("")) || (signUpUsername.getText().equals("")) ||
                (signUpPassword.getText().equals("")) || (signUpCountry.getText().equals(""));
        if (check){
            popupAlert(Alert.AlertType.ERROR, "Fill All Please!!!");
        }
        else {
            isSignIn = false;
            newUser.setName(signUpName.getText());
            newUser.setUsername(signUpUsername.getText());
            newUser.setPassword(signUpPassword.getText());
            newUser.setCountry(signUpCountry.getText());
            String userJson = userToJson(newUser);

            connectionToServer = new Connection(userJson, this);
            connectionToServer.start();
        }
    }

    private String userToJson(User user) {
        Gson gson = new Gson();

        return gson.toJson(user);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleServerMessage(HashMap<String, String> msg)
    {
        Platform.runLater(() ->
        {
            switch (msg.get(Response.ACTION_CODE)) {
                case Response.USER_EXIST: {
                    popupAlert(Alert.AlertType.ERROR, "this username already exist. try another one!!!");
                    break;
                }

                case Response.WRONG_PASSWORD: {
                    popupAlert(Alert.AlertType.ERROR, "you have entered the wrong password!!!");
                    break;
                }

                case Response.USER_DONT_EXIST: {
                    popupAlert(Alert.AlertType.ERROR, "this username doesn't exist. try another or SIGN UP!!!");
                    break;
                }

                case Response.SUCCESSFUL_ENTRANCE: {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/startPage.fxml"));
                        Parent root = loader.load();
                        StartGameController startGameController = (StartGameController)loader.getController();
                        connectionToServer.setStartGameController(startGameController);
                        startGameController.initValues(connectionToServer, (Stage) scene.getWindow(), scene);
                        Stage stage = (Stage) scene.getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        popupException(e);
                    }
                    break;
                }

                case Response.ERROR: {
                    popupAlert(Alert.AlertType.ERROR, "your connection was not stable. try another startTime");
                    break;
                }

                default: {
                    break;
                }
            }

        });
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    private void popupException(IOException e) {
    }

    public Scene getScene() {
        return scene;
    }
}