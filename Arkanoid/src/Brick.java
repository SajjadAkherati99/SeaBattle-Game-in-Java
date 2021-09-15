import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Brick implements IPaintable {
    private int strenght;
    int type;
    private int x;
    private int y;
    private final int width;
    private final int height;
    private boolean collision = true;
    boolean visible = true;

    public void setVisible() {
        if (this.type == 3){
            Game.gameTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    visible = !visible;
                }
            }, Const.BLINKING_TIME, Const.BLINKING_TIME);
        }
        else{
            this.visible = true;
        }
    }

    public Brick(int x, int y) {
        this.x = x;
        this.y = y;
        Random random = new Random();
        this.type = random.nextInt(10);
        this.strenght = 1;
        if(this.type == 4 || this.type==8 || this.type == 9)
            this.strenght = 2;
        this.setVisible();
        this.width = 50;
        this.height = 30;
    }

    @Override
    public void paint(Graphics g) {
        switch (this.type) {
            case 5:
            case 6:
            case 7:
            case 0:
                g.setColor(Color.YELLOW);
                break;
            case 1:
                g.setColor(Color.MAGENTA);
                break;
            case 2:
                g.setColor(Color.BLACK);
                break;
            case 3:
                g.setColor(Color.BLUE);
                break;
            case 8:
            case 9:
            case 4:
                if (this.strenght == 1)
                    g.setColor(Color.YELLOW);
                else
                    g.setColor(Color.RED);
                break;
        }
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(Color.WHITE);
        g.drawRect(this.x, this.y, this.width, this.height);
    }

    public void setStrenght(int strenght) {
        this.strenght = strenght;
    }

    public int getStrenght() {
        return this.strenght;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public boolean isCollision() {
        return this.collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setType(int type) {
        this.type = type;
    }
}
