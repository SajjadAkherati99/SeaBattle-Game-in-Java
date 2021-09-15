import javax.swing.*;
import java.io.*;
import java.util.LinkedList;

public class ReadWriteFiles {

    public static LinkedList<String> read_Players_Scores(){
        BufferedReader inputStream = null;
        LinkedList<String> scoreList = new LinkedList<>();
        try {
            inputStream = new BufferedReader(new FileReader("lastScores.txt"));
            String l;
            while ((l = inputStream.readLine()) != null) {
                scoreList.add(l);
            }
        } catch (IOException e) {
            scoreList.add("No one is here.");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return scoreList;
    }

    public static void write_Player_Score(int score){
        PrintStream printStream;
        String str =
                "GAME OVER!!!\nYour Score: " + score + "\n Insert your name: ";
        String name = JOptionPane.showInputDialog(str);
        if (name != null) {
            String write = name + " : " + score;
            LinkedList<String> scoreLists = read_Players_Scores();
            if(!scoreLists.getFirst().equals("No one is here.")){
                for (String s: scoreLists){
                    String[] sArr = s.split(" : ", 3);
                    int bestScore  = Integer.parseInt(sArr[1]);
                    if (sArr[0].equals(name)) {
                        bestScore = Math.max(bestScore, score);
                        write = name + " : " + bestScore;
                        scoreLists.remove(s);
                        break;
                    }
                }
                scoreLists.addFirst(write);
            }else {
                scoreLists.set(0, write);
            }
            try {
                printStream = new PrintStream("lastScores.txt");
                for(String s:scoreLists){
                    printStream.println(s);
                    printStream.flush();
                }
            } catch (IOException e) {
                try {
                    FileOutputStream f = new FileOutputStream(new File("lastScores.txt"));
                    f.write(write.getBytes());
                    f.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void save_Game(Game game, String name){
        File file = new File("Saved Games");
        boolean fileCreator = file.mkdir();
        PrintStream printStream;
        try {
            printStream = new PrintStream(String.format("./Saved Games/%s.txt", name));
            printStream.println("{");

            printStream.println(game.getS());

            printStream.println(game.getL());

            printStream.println("{");
            if(!game.getGamePanel().balls.isEmpty()) {
                for (Ball b : game.getGamePanel().balls) {
                    printStream.println(b.getX() + " " +
                            b.getY() + " " +
                            b.getDirX() + " " +
                            b.getDirY());
                }
            }
            else{
                printStream.println("null");
            }
            printStream.println("}");

            printStream.println(game.getGamePanel().numOfBalls);

            printStream.println("{");
            Pad pad = (Pad) game.getGamePanel().getPad();
            printStream.println(
                    pad.getX() + " " +
                            pad.getY());
            printStream.println("}");
            printStream.println("{");
            if(!game.getGamePanel().getBricks().isEmpty()){
                for (Brick b:game.getGamePanel().getBricks()){
                    printStream.println(b.getStrenght() + " " +
                            b.type + " " +
                            b.getX() + " " +
                            b.getY());
                }
            }
            else {
                printStream.println("null");
            }
            printStream.println("}");

            printStream.println(game.getGamePanel().difficulty);

            printStream.println(game.getGamePanel().previous_Difficulty);

            printStream.println(game.getGamePanel().num_of_broken);

            printStream.println("}");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(game.getMyFrame(), "Folder is not found", "warning",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void load_Game(Game game, String name) {
        LinkedList<Ball> loadBall = new LinkedList<>();
        int load_NumOfBalls = 0;
        LinkedList<Brick> loadBricks = new LinkedList<>();
        int load_Difficulty = 0;
        int load_Previous_Difficulty = 0;
        int load_NumOfBroken = 0;
        Pad loadPad = new Pad();
        try {
            File file = new File(String.format("./Saved Games/%s.txt", name));
            BufferedReader br = new BufferedReader(new FileReader(file));
            int obj = 0;
            String st;
            while (true){
                try {
                    if ((st = br.readLine()) == null) {
                        break;
                    }
                    else {
                        switch (obj) {
                            case 0:
                            case 3:
                            case 6:
                            case 8:
                            case 9:
                                obj++;
                                break;
                            case 1:
                                game.setS(Integer.parseInt(st));
                                game.setTextScore(game.getS());
                                obj++;
                                break;
                            case 2:
                                game.setL(Integer.parseInt(st));
                                game.setTextLive(game.getL());
                                obj++;
                                break;
                            case 4:
                                if (st.equals("null") || st.equals("}")) {
                                    if(st.equals("}"))
                                        obj++;
                                }
                                else {
                                    String[] stArr = st.split(" ");
                                    Ball b = new Ball(Integer.parseInt(stArr[0]), Integer.parseInt(stArr[1]));
                                    b.setDirX(Integer.parseInt(stArr[2]));
                                    b.setDirY(Integer.parseInt(stArr[3]));
                                    loadBall.add(b);
                                }
                                break;
                            case 5:
                                load_NumOfBalls = Integer.parseInt(st);
                                obj++;
                                break;
                            case 7:
                                String[] stArr = st.split(" ");
                                loadPad.setX(Integer.parseInt(stArr[0]));
                                loadPad.setY(Integer.parseInt(stArr[1]));
                                obj++;
                                break;
                            case 10:
                                if (st.equals("null") || st.equals("}")) {
                                    if (st.equals("}")) {
                                        obj++;
                                    }
                                }
                                else {
                                    stArr = st.split(" ");
                                    Brick b = new Brick(Integer.parseInt(stArr[2]), Integer.parseInt(stArr[3]));
                                    b.setStrenght(Integer.parseInt(stArr[0]));
                                    b.setType(Integer.parseInt(stArr[1]));
                                    b.setVisible();
                                    loadBricks.add(b);
                                }
                                break;
                            case 11:
                                load_Difficulty = Integer.parseInt(st);
                                obj++;
                                break;
                            case 12:
                                load_Previous_Difficulty = Integer.parseInt(st);
                                obj++;
                                break;
                            case 13:
                                load_NumOfBroken = Integer.parseInt(st);
                                obj++;
                                break;
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(game.getMyFrame(), "error found",
                            "warning",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            game.getGamePanel().setBalls(loadBall);
            game.getGamePanel().numOfBalls = load_NumOfBalls;
            game.getGamePanel().setBricks(loadBricks);
            game.getGamePanel().setPad(loadPad);
            game.getGamePanel().difficulty = load_Difficulty;
            game.getGamePanel().previous_Difficulty = load_Previous_Difficulty;
            game.getGamePanel().num_of_broken = load_NumOfBroken;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(game.getMyFrame(), "there is no game with this name!!",
                    "warning",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
