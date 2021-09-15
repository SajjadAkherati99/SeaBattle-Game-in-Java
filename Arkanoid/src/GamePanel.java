import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    LinkedList<Ball> balls = new LinkedList<Ball>();
    int numOfBalls = 0;
    private Listener pad;
    private BufferedImage image;
    private LinkedList<Brick> bricks = new LinkedList<Brick>();
    private LinkedList<Prize> prizes = new LinkedList<Prize>();
    int difficulty;
    int previous_Difficulty;
    Timer timer = new Timer();
    int num_of_broken = 0;
    boolean fast_slow = false;
    boolean add = false;
    boolean gameOver = false;

    public GamePanel() {
    }

    @Override
    public void paint(Graphics g) {
        this.image = new BufferedImage(Const.WIDTH - Const.SCORE_WIDTH,
                Const.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics b = this.image.getGraphics();
        this.paint_Balls(b);
        this.pad.paint(b);
        this.update_Bricks(this.add, b);
        if(this.add)
            this.add = false;
        if(!this.prizes.isEmpty()) {
            for (Prize p : this.prizes) {
                p.paint(b);
            }
        }
        g.drawImage(this.image, 0, 0, this);
    }

    public void run() throws InterruptedException {
        this.move_Balls();
        if (gameOver){
            gameOver = false;
            Game.getInstance().gameOver();
        }
        if(!this.prizes.isEmpty()) {
            for (Prize p : this.prizes) {
                p.move();
            }
        }
        update_Prizes();
        repaint();
        Thread.sleep(this.difficulty);
    }

    public void move_Balls(){
        LinkedList<Ball> ball = new LinkedList<Ball>();
        if(!this.balls.isEmpty()) {
            for (Ball b : this.balls) {
                b.move();
                if(b.loss) {
                    this.numOfBalls -= 1;
                }
                else {
                    ball.add(b);
                }
            }
            this.setBalls(ball);
        }
        Game game = Game.getInstance();
        if(this.numOfBalls <= 0){
            game.minusLive();
        }
    }

    public void paint_Balls(Graphics g){
        LinkedList<Ball> balls = this.balls;
        if(!balls.isEmpty()) {
            for (Ball b : balls) {
                b.paint(g);
            }
        }
    }

    public LinkedList<Brick> getBricks() {
        return this.bricks;
    }

    public void setBricks(LinkedList<Brick> bricks) {
        this.bricks = bricks;
    }

    public void removeBrickLive(Brick b) {
        b.setStrenght(b.getStrenght() - 1);
        Game g = Game.getInstance();
        if (b.getStrenght() <= 0) {
            b.setCollision(false);
            g.setS(g.getS() + 1);
            g.getScore().setText("Score: " + g.getS());
            this.num_of_broken++;
            if(this.num_of_broken >= 30){
                this.difficulty--;
                this.previous_Difficulty = this.difficulty;
                if(this.difficulty < 2)
                    this.difficulty = 2;
                this.num_of_broken = 0;
            }
        }
        if(b.type == 1){
            Prize prize = new Prize(b.getX()+b.getWidth()/2, b.getY()+b.getHeight()/2);
            this.prizes.add(prize);
        }
    }

    public void init(boolean respawn) {
        this.pad = new Pad();
        int x = (Const.WIDTH - Const.SCORE_WIDTH)/2;
        int y = Const.HEIGHT - 4*Const.PAD_HEIGHT;
        Ball ball = new Ball(x, y);
        LinkedList<Ball> balls = new LinkedList<Ball>();
        balls.add(ball);
        this.prizes = new LinkedList<Prize>();
        this.setBalls(balls);
        this.numOfBalls = 1;
        this.num_of_broken = 0;
        this.difficulty = 6;
        Brick brick;
        x = 100;
        y = 50;
        if (respawn) {
            this.bricks = new LinkedList<Brick>();
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 5; j++) {
                    brick = new Brick(x, y);
                    this.bricks.add(brick);
                    x += 60;
                }
                x = 100;
                y += 40;
            }
        }
        this.timer.cancel();
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                add = true;
            }
        };
        this.add = false;
        this.timer.schedule(timerTask, Const.UPDATE_TIME, Const.UPDATE_TIME);
    }

    public void update_Bricks (boolean add, Graphics b){
        if (add) {
            for (Brick brick : this.bricks) {
                brick.setY(brick.getY() + 40);
                if (brick.isVisible()) {
                    brick.paint(b);
                }
            }
            int x = 100;
            int y = 50;
            for (int i = 0; i < 5; i++) {
                Brick brick = new Brick(x, y);
                this.bricks.add(brick);
                x += 60;
            }
        }
        else {
            for (Brick brick : this.bricks) {
                if (brick.isVisible()) {
                    brick.paint(b);
                }
            }
        }
    }

    public void update_Prizes(){
        LinkedList<Prize> newPrizes = new LinkedList<Prize>();
        for (Prize p: this.prizes){
            if(!p.isArrived()){
                newPrizes.add(p);
            }
        }
        this.prizes = newPrizes;
    }

    public void setBalls(LinkedList<Ball> balls) {
        this.balls = balls;
    }

    public Listener getPad() {
        return this.pad;
    }

    public void setPad(Listener pad) {
        this.pad = pad;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

}
