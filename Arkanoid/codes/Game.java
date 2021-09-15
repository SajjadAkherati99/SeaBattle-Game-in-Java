import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Timer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game {
    private final JFrame myFrame;
    private final GamePanel gamePanel;
    private final JPanel scorePanel;
    private final JLabel score;
    private final JLabel lives;
    private int s = 0;
    private int l;
    private boolean paused = true;
    private static Game instance = null;
    static Timer gameTimer = new Timer();

    private Game() {
        this.myFrame = new JFrame("Sajjad Arkanoid");
        this.myFrame.setSize(Const.WIDTH, Const.HEIGHT);
        this.myFrame.setLocationRelativeTo(null);
        this.myFrame.setLayout(new BorderLayout());
        this.myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.myFrame.setResizable(false);

        this.gamePanel = new GamePanel();
        this.scorePanel = new JPanel();

        this.scorePanel.setBackground(Color.DARK_GRAY);
        this.scorePanel
                .setPreferredSize(new Dimension(Const.SCORE_WIDTH, Const.HEIGHT));
        this.scorePanel.setLayout(new BoxLayout(this.scorePanel, BoxLayout.Y_AXIS));

        this.score = new JLabel("Score: " + this.s);
        this.score.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.score.setForeground(Color.GREEN);
        this.score.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0)); //pro odsazeni
        this.scorePanel.add(this.score);

        this.lives = new JLabel("Lives: " + this.l);
        this.lives.setForeground(Color.magenta);
        this.lives.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.lives.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        this.scorePanel.add(this.lives);

        JButton btn = new JButton("Play/Pause");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(100, 40));
        btn.setFocusable(false);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePause();
            }
        });
        this.scorePanel.add(btn);

        btn = new JButton("New Game");
        btn.setMaximumSize(new Dimension(100, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setFocusable(false);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused()) {
                    gamePause();
                }
                String ObjButtons[] = {"Yes", "No"};
                int PromptResult = JOptionPane.showOptionDialog(null,
                        "Start new game? Progress will not be saved.", "New game?",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    newGame();
                }
            }
        });
        this.scorePanel.add(btn);

        btn = new JButton("Save Game");
        btn.setMaximumSize(new Dimension(100, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setFocusable(false);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused()) {
                    gamePause();
                }
                boolean loop = true;
                String name = "";
                while (loop) {
                    String str =
                            "Insert a name: ";
                    name = JOptionPane.showInputDialog(str);
                    if (Files.exists(Paths.get(String.format("./Saved Games/%s.txt", name)))){
                        String[] ObjButtons = {"Yes", "No"};
                        int PromptResult =
                                JOptionPane.showOptionDialog(
                                        null, "a game with this name already exist.\n" +
                                                "Is it OK?",
                                        "warning", JOptionPane.DEFAULT_OPTION,
                                        JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                        if (PromptResult == JOptionPane.YES_OPTION) {
                            loop = false;
                        }
                    }
                    else {
                        loop = false;
                    }
                }
                if(!name.equals(""))
                    ReadWriteFiles.save_Game(Game.getInstance(), name);
            }
        });
        this.scorePanel.add(btn);

        btn = new JButton("Load Game");
        btn.setMaximumSize(new Dimension(100, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setFocusable(false);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused()) {
                    gamePause();
                }
                boolean loop = true;
                String name  = "default";
                while (loop) {
                    String str =
                            "Insert a name: ";
                    name = JOptionPane.showInputDialog(str);
                    File saved = new File(String.format("./Saved Games/%s.txt", name));
                    if (saved.exists()){
                        String[] ObjButtons = {"Yes", "No"};
                        int PromptResult =
                                JOptionPane.showOptionDialog(
                                        null, "Do you want to play a saved game?",
                                        "load games", JOptionPane.DEFAULT_OPTION,
                                        JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                        if (PromptResult == JOptionPane.YES_OPTION) {
                            ReadWriteFiles.load_Game(getInstance(), name);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(getInstance().myFrame, "there is no game with this name!!",
                                "warning",
                                JOptionPane.INFORMATION_MESSAGE);
                        saved.deleteOnExit();
                    }
                    loop = false;
                }
                ReadWriteFiles.save_Game(Game.getInstance(), name);
            }
        });
        this.scorePanel.add(btn);

        btn = new JButton("Prizes & Bricks");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(100, 40));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused()) {
                    gamePause();
                }
                JFrame Prizes_Bricks = new JFrame("Prizes & Bricks");
                Prizes_Bricks.getContentPane().setBackground(Color.BLACK);
                Prizes_Bricks.setSize(400, 280);
                Prizes_Bricks.setLocationRelativeTo(null);
                Prizes_Bricks.setLayout(null);
                Prizes_Bricks.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                Prizes_Bricks.setResizable(false);
                Prizes_Bricks.setVisible(true);
                edit_Prize_Brick(Prizes_Bricks);
            }
        });
        btn.setFocusable(false);
        this.scorePanel.add(btn);

        btn = new JButton("Last Scores");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(100, 40));
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused()) {
                    gamePause();
                }
                showBestScore();
            }
        });
        btn.setFocusable(false);
        this.scorePanel.add(btn);

        btn = new JButton("Exit");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(100, 40));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (isPaused()) {
                    gamePause();
                }
                String[] ObjButtons = {"Yes", "No"};
                int PromptResult =
                        JOptionPane.showOptionDialog(null, "Are you sure you want to exit?",
                                "Exit?", JOptionPane.DEFAULT_OPTION,
                                JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setFocusable(false);
        this.scorePanel.add(btn);

        this.myFrame.add(this.gamePanel);
        this.myFrame.add(this.scorePanel, BorderLayout.EAST);

        this.myFrame.setVisible(true);
        newGame();
    }

    public void edit_Prize_Brick(JFrame frame){
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("Normal Brick");
        panel.setBackground(Color.YELLOW);
        panel.setBounds(0, 0, 200, 30);
        panel.add(label);
        frame.add(panel);


        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel();
        label1.setText("Wooden Brick");
        panel1.setBackground(Color.RED);
        panel1.setBounds(0, 30, 200, 30);
        panel1.add(label1);
        frame.add(panel1);

        JPanel panel3 = new JPanel();
        JLabel label3 = new JLabel();
        label3.setText("Glass Brick");
        panel3.setBackground(Color.BLUE);
        panel3.setBounds(0, 60, 200, 30);
        panel3.add(label3);
        frame.add(panel3);

        JPanel panel4 = new JPanel();
        JLabel label4 = new JLabel();
        label4.setText("Invisible Brick");
        panel4.setBackground(Color.BLACK);
        panel4.setBounds(0, 90, 200, 30);
        panel4.add(label4);
        frame.add(panel4);

        JPanel panel5 = new JPanel();
        JLabel label5 = new JLabel();
        label5.setText("Prize Brick");
        panel5.setBackground(Color.MAGENTA);
        panel5.setBounds(0, 120, 200, 30);
        panel5.add(label5);
        frame.add(panel5);

        JPanel prize1 = new JPanel();
        JLabel l1 = new JLabel();
        l1.setText("Fire Ball Prize");
        prize1.setBackground(Color.ORANGE);
        prize1.setBounds(200, 0, 200, 30);
        prize1.add(l1);
        frame.add(prize1);

        JPanel prize2 = new JPanel();
        JLabel l2 = new JLabel();
        l2.setText("Multiple Balls Prize");
        prize2.setBackground(Color.PINK);
        prize2.setBounds(200, 30, 200, 30);
        prize2.add(l2);
        frame.add(prize2);

        JPanel prize3 = new JPanel();
        JLabel l3 = new JLabel();
        l3.setText("Large Pad Prize");
        prize3.setBackground(Color.CYAN);
        prize3.setBounds(200, 60, 200, 30);
        prize3.add(l3);
        frame.add(prize3);

        JPanel prize4 = new JPanel();
        JLabel l4 = new JLabel();
        l4.setText("Small Pad Prize");
        prize4.setBackground(Color.MAGENTA);
        prize4.setBounds(200, 90, 200, 30);
        prize4.add(l4);
        frame.add(prize4);

        JPanel prize5 = new JPanel();
        JLabel l5 = new JLabel();
        l5.setText("Fast Ball Prize");
        prize5.setBackground(Color.RED);
        prize5.setBounds(200, 120, 200, 30);
        prize5.add(l5);
        frame.add(prize5);

        JPanel prize6 = new JPanel();
        JLabel l6 = new JLabel();
        l6.setText("Slow Ball Prize");
        prize6.setBackground(Color.BLUE);
        prize6.setBounds(200, 150, 200, 30);
        prize6.add(l6);
        frame.add(prize6);

        JPanel prize7 = new JPanel();
        JLabel l7 = new JLabel();
        l7.setText("Confused Pad Prize");
        prize7.setBackground(Color.YELLOW);
        prize7.setBounds(200, 180, 200, 30);
        prize7.add(l7);
        frame.add(prize7);

        JPanel prize8 = new JPanel();
        JLabel l8 = new JLabel();
        l8.setText("Random Prize");
        prize8.setBackground(Color.WHITE);
        prize8.setBounds(200, 210, 200, 30);
        prize8.add(l8);
        frame.add(prize8);
    }

    public void newGame() {
        this.s = 0;
        this.l = 3;
        this.score.setText("Score: " + this.s);
        this.lives.setText("Lives: " + this.l);
        this.gamePanel.setDifficulty(4);
        this.gamePanel.init(true);
        if (isPaused()) {
            gamePause();
        }
    }

    public void gamePause() {
        if (this.paused) {
            this.myFrame.removeKeyListener(this.gamePanel.getPad());
            this.paused = false;
        } else {
            this.myFrame.addKeyListener(this.gamePanel.getPad());
            this.paused = true;
        }
    }

    public void gameOver() {
        ReadWriteFiles.write_Player_Score(this.s);
        String[] ObjButtons = {"Continue", "Exit"};
        int PromptResult = JOptionPane.showOptionDialog(null,
                "Do you want to play again?", "Play again?", JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
        if (PromptResult == JOptionPane.YES_OPTION) {
            newGame();
        } else {
            System.exit(0);
        }
    }

    public void showBestScore() {
        LinkedList<String> scoreList = ReadWriteFiles.read_Players_Scores();
        StringBuilder List = new StringBuilder("Scores \n");
        for(String s: scoreList){
            List.append(" \n").append(s);
        }
        JOptionPane.showMessageDialog(this.myFrame, List.toString(), "Last scores",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void minusLive() {
        this.l -= 1;
        if (!(this.l <= 0)) {
            this.lives.setText("Lives: " + this.l);
            this.gamePanel.init(false);
            this.myFrame.addKeyListener(this.gamePanel.getPad());
            gamePause();
        } else {
            gameOver();
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public int getS() {
        return this.s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getL() {
        return this.l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public JLabel getScore() {
        return this.score;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public JFrame getMyFrame() {
        return myFrame;
    }

    public void setTextScore(int s){
        this.score.setText("Scores: " + this.s);
    }

    public void setTextLive(int l){
        this.score.setText("Scores: " + this.l);
    }
}
