import java.awt.Graphics;

public interface IPaintable {
    public void paint(Graphics g);

    public int getX();

    public int getY();

    public void setX(int x);


    public void setY(int y);


    public int getWidth();


    public int getHeight();
}
