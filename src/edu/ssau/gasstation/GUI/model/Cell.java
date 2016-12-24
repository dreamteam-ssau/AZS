package edu.ssau.gasstation.GUI.model;

/**
 * Created by Sasha on 18.12.2016.
 */
public class Cell {
    private int x;
    private int y;
    private int state; //0-empty, 1-car, 2- trk, 3 - cash, 4 - tank, 5- enter, -1 -exit

    public Cell(int x, int y, int state){
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getState() {
        return state;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getHeight(){
        return ModelField.CELL_SIZE;
    }

    public int getWidth(){
        return ModelField.CELL_SIZE;
    }


    public Sprite getSprite(){
        switch (this.state){
            case -1:
                return Sprite.EXIT;
            case 0:
                return Sprite.EMPTY;
            case 1:
                return Sprite.CAR;
            case 2:
                return Sprite.TRK;
            case 3:
                return Sprite.CASH;
            case 4:
                return Sprite.TANK;
            case 5:
                return Sprite.ENTER;
            default:
                return null;
        }
    }
}
