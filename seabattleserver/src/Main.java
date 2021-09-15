import model.Layout;
import network.SeaBattleServer;

public class Main {

    public static void main(String[] args){
        SeaBattleServer seaBattleServer = new SeaBattleServer();
        new Thread(seaBattleServer).start();

//        Layout layout = new Layout();
//        System.out.println(layout.getLayout());
    }
}
