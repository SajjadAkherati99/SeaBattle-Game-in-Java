import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class Pad implements Listener {
    private int width;
    private int height;
    private int x;
    private int y;
    boolean confused = false;

    public Pad() {
        this.width = Const.PAD_WIDTH;
        this.height = Const.PAD_HEIGHT;
        this.x = Const.WIDTH / 2 - this.width / 2 - Const.SCORE_WIDTH / 2;
        this.y = Const.HEIGHT - this.height * 2 - 20;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
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

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                if (!this.isConfused()) {
                    if (!(this.getX() + Const.PAD_STEP > Const.WIDTH - Const.SCORE_WIDTH
                            - this.getWidth())) {
                        this.setX(this.getX() + Const.PAD_STEP);
                    } else {
                        this.setX(Const.WIDTH - Const.SCORE_WIDTH - this.getWidth());
                    }
                } else {
                    if (!(this.getX() - Const.PAD_STEP < 0)) {
                        this.setX(this.getX() - Const.PAD_STEP);
                    } else {
                        this.setX(0);
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
                if (!this.isConfused()) {
                    if (!(this.getX() - Const.PAD_STEP < 0)) {
                        this.setX(this.getX() - Const.PAD_STEP);
                    } else {
                        this.setX(0);
                    }
                } else {
                    if (!(this.getX() + Const.PAD_STEP > Const.WIDTH - Const.SCORE_WIDTH
                            - this.getWidth())) {
                        this.setX(this.getX() + Const.PAD_STEP);
                    } else {
                        this.setX(Const.WIDTH - Const.SCORE_WIDTH - this.getWidth());
                    }
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public boolean isConfused() {
        return confused;
    }

    public void setConfused(boolean confused) {
        this.confused = confused;
    }
}
