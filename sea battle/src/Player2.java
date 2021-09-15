import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.stage.*;

public class Player2 extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/begin.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, Color.LIGHTYELLOW);
        Image icon = new Image(String.valueOf(getClass().getResource("/resources/images/sea_battle.jpg")));
        stage.setTitle("Sea Battle Game");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
