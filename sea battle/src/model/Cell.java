package model;

public class Cell {
    private int Col;
    private int Row;
    private boolean isDestroyed;

    public Cell(int col, int row, boolean isDestroyed) {
        Col = col;
        Row = row;
        this.isDestroyed = isDestroyed;
    }

    public int getCol() {
        return Col;
    }

    public void setCol(int col) {
        Col = col;
    }

    public int getRow() {
        return Row;
    }

    public void setRow(int row) {
        Row = row;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void destroy() {
        isDestroyed = true;
    }
}
