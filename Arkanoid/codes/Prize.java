import java.awt.*;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Prize implements IMovable{
    private int x, y;
    private final int size = 30;
    private final int speed;
    private int dirX, dirY;
    private int type;
    private boolean arrived = false;

    public Prize(int x, int y) {
        this.x = x;
        this.y = y;
        Random random = new Random();
        this.type = random.nextInt(8);
        this.speed = 1;
        this.dirX = 0;
        this.dirY = 1;
    }

    @Override
    public void paint(Graphics g) {
        switch (this.type){
            case 0:
                g.setColor(Color.ORANGE);
                break;
            case 1:
                g.setColor(Color.PINK);
                break;
            case 2:
                g.setColor(Color.CYAN);
                break;
            case 3:
                g.setColor(Color.MAGENTA);
                break;
            case 4:
                g.setColor(Color.RED);
                break;
            case 5:
                g.setColor(Color.BLUE);
                break;
            case 6:
                g.setColor(Color.YELLOW);
                break;
            case 7:
                g.setColor(Color.WHITE);
                break;
        }
        g.fillOval(this.x, this.y, this.size, this.size);
    }

    @Override
    public void move() {
        Game g = Game.getInstance();
        if (!g.isPaused()) {
            return;
        }
        this.x = this.x + this.speed * this.dirX;
        this.y = this.y + this.speed * this.dirY;

        if (this.y + this.size >= g.getGamePanel().getPad().getY()){
            this.arrived = true;
            if (this.x >= g.getGamePanel().getPad().getX() &&
                    this.x <= (g.getGamePanel().getPad().getX()+
                            g.getGamePanel().getPad().getWidth())){
                apply_Effect(g);
            }
        }
    }

    public void apply_Effect(Game game) {
        switch (this.type){
            case 0:
                if(!game.getGamePanel().balls.isEmpty()) {
                    Ball fire_Ball = game.getGamePanel().balls.getFirst();
                    fire_Ball.fire = true;
                    Game.gameTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            fire_Ball.fire = false;
                        }
                    }, Const.PRIZE_TIME);
                }
                break;
            case 1:
                int x = (Const.WIDTH - Const.SCORE_WIDTH)/2 - 40;
                int y = Const.HEIGHT/2;
                game.getGamePanel().balls.add(new Ball(x, y));
                game.getGamePanel().balls.add(new Ball(x + 80, y));
                game.getGamePanel().numOfBalls = game.getGamePanel().numOfBalls + 2;
                break;
            case 2:
                make_Pad_bigger(game);
                break;
            case 3:
                make_Pad_smaller(game);
                break;
            case 4:
                make_Game_Slow(game, false);
                break;
            case 5:
                make_Game_Slow(game, true);
                break;
            case 6:
                make_Pad_confused(game);
                break;
            case 7:
                Random random = new Random();
                this.type = random.nextInt(7);
                apply_Effect(game);
                break;
        }
    }

    public void make_Pad_bigger(Game game){
        Pad pad = (Pad) game.getGamePanel().getPad();
        if(pad.getWidth() <= 200){
            pad.setWidth(pad.getWidth() + 40);
            pad.setX(pad.getX() - 20);
        }
    }

    public void make_Pad_smaller(Game game){
        Pad pad = (Pad) game.getGamePanel().getPad();
        if(pad.getWidth() >= 20){
            pad.setWidth(pad.getWidth() - 40);
            pad.setX(pad.getX() + 20);
        }
    }

    public void make_Pad_confused(Game game){
        Pad pad = (Pad) game.getGamePanel().getPad();
        pad.setConfused(true);
        Game.gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                pad.setConfused(false);
            }
        }, Const.PRIZE_TIME);
    }

    public void make_Game_Slow(Game game, boolean slow){
        int difficulty = game.getGamePanel().difficulty;
        game.getGamePanel().previous_Difficulty = difficulty;
        if(slow){
            difficulty++;
        }
        else {
            if(difficulty > 2)
                difficulty--;
        }
        game.getGamePanel().setDifficulty(difficulty);
        game.getGamePanel().fast_slow = true;
        Game.gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                game.getGamePanel().difficulty = game.getGamePanel().previous_Difficulty;
            }
        }, Const.PRIZE_TIME);
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    public boolean isArrived() {
        return arrived;
    }
}
