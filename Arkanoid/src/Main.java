import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Game game = Game.getInstance();

        while (true){
            game.getGamePanel().run();
        }
    }
}
