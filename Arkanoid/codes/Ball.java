import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Ball implements IMovable {
    private int x, y;
    private final int size = 14;
    private final int speed;
    private int dirX, dirY;
    boolean loss = false;
    boolean fire = false;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = 1;
        this.dirX = 0;
        this.dirY = -1;
    }

    @Override
    public void paint(Graphics g) {
        if(this.fire){
            g.setColor(Color.ORANGE);
        }
        else {
            g.setColor(Color.WHITE);
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

        if (this.x + this.size / 2 >= Const.WIDTH - Const.SCORE_WIDTH - this.size) {
            this.dirX *= -1;
        }
        if (this.x - this.size / 2 <= 0) {
            this.dirX *= -1;
        }
        if (this.y - this.size / 2 <= 0) {
            this.dirY *= -1;
        }
        if (this.y + this.size / 2 >= Const.HEIGHT - 20) {
            this.loss = true;
        }

        if (this.x + this.size >= g.getGamePanel().getPad().getX()
                + g.getGamePanel().getPad().getWidth() / 2
                && this.x + this.size <= g.getGamePanel().getPad().getX()
                + g.getGamePanel().getPad().getWidth() / 2 + 7) {
            if (this.y + this.size >= g.getGamePanel().getPad().getY()) {
                this.dirX = 0;
                this.dirY *= -1;
            }
        }

        if (this.x + this.size >= g.getGamePanel().getPad().getX()
                && this.x + this.size < g.getGamePanel().getPad().getX()
                + g.getGamePanel().getPad().getWidth() / 2) {
            if (this.y + this.size >= g.getGamePanel().getPad().getY()) {
                this.dirX = -1;
                this.dirY *= -1;
            }
        }
        if (this.x + this.size > g.getGamePanel().getPad().getX()
                + g.getGamePanel().getPad().getWidth() / 2 + 7
                && this.x <= g.getGamePanel().getPad().getX()
                + g.getGamePanel().getPad().getWidth()) {
            if (this.y + this.size >= g.getGamePanel().getPad().getY()) {
                this.dirX = 1;
                this.dirY *= -1;
            }
        }
        g.getGamePanel().gameOver = brick_Ball(g);
    }

    public boolean brick_Ball(Game g){
        LinkedList<Brick> newBricks = new LinkedList<Brick>();
        for (Brick b : g.getGamePanel().getBricks()) {
            if (b.isVisible()) {
                boolean removeBrick = false;

                if ((this.x + this.size > b.getX() && this.x < b.getX() + b.getWidth())) {
                    if (this.y + this.size >= b.getY()
                            && this.y <= b.getY() + b.getHeight()) {
                        removeBrick = true;
                        if (!this.fire)
                            this.dirY *= -1;
                    }
                }

                else if ((this.y + this.size > b.getY() && this.y < b.getY() + b.getHeight())) {
                    if (this.x + this.size >= b.getX()
                            && this.x <= b.getX() + b.getWidth()) {
                        removeBrick = true;
                        if (!this.fire) {
                            this.dirX *= -1;
                            if(this.dirX == 0)
                                this.dirY *= -1;
                        }
                    }
                }
                if (removeBrick) {
                    g.getGamePanel().removeBrickLive(b);
                }

                if (b.getY() >= (Const.HEIGHT - 3 * b.getHeight())) {
                    return true;
                }
            }
            if (b.isCollision()) {
                newBricks.add(b);
            }
        }
        g.getGamePanel().setBricks(newBricks);
        return false;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public int getDirX() {
        return this.dirX;
    }

    public void setDirX(int dirX) {
        this.dirX = dirX;
    }

    public int getDirY() {
        return this.dirY;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
    }

    @Override
    public int getWidth() {
        return this.size;
    }

    @Override
    public int getHeight() {
        return this.size;
    }
}
