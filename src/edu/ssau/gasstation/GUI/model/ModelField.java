package edu.ssau.gasstation.GUI.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.awt.image.BufferedImage;

/**
 * Created by Sasha on 18.12.2016.
 */
public class ModelField {
    public static final int CELL_SIZE = 32; //cell size in pixels
    private Cell[][] cells;
    private Canvas canvas;
    private GraphicsContext gc;
    //sizes in cells!
    private int fieldSizeX;
    private int fieldSizeY;

    public ModelField(){
        this.fieldSizeX = 2;
        this.fieldSizeY = 2;
        this.cells = new Cell[fieldSizeX][fieldSizeY];
        this.canvas = new Canvas(fieldSizeX*CELL_SIZE,fieldSizeY*CELL_SIZE);
        this.gc = canvas.getGraphicsContext2D();
    }

    public ModelField(int fieldSizeX, int fieldSizeY){
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.cells = new Cell[fieldSizeX][fieldSizeY];
        this.canvas = new Canvas(fieldSizeX*CELL_SIZE,fieldSizeY*CELL_SIZE);
        this.gc = canvas.getGraphicsContext2D();
    }

    public void setFieldSizeX(int fieldSizeX) {
        this.fieldSizeX = fieldSizeX;
    }

    public void setFieldSizeY(int fieldSizeY) {
        this.fieldSizeY = fieldSizeY;
    }

    public int getFieldSizeX() {
        return fieldSizeX;
    }

    public int getFieldSizeY() {
        return fieldSizeY;
    }

    public int getState(int x, int y){
        return cells[x][y].getState();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setState(int x, int y, int state){
        cells[x][y].setState(state);
    }

    public void init(){
        for (int i = 0; i < fieldSizeX; i++){
            for (int j=0; j<fieldSizeY; j++){
                cells[i][j] = new Cell(i*CELL_SIZE, j*CELL_SIZE, 0);
            }
        }
    }

    public void draw(){
        for (Cell[] line : cells){
            for (Cell cell : line){
                drawElment(cell, this.gc);
            }
        }
    }

    private static void drawElment(Cell elem, GraphicsContext gc){
        if (elem.getSprite() == null) return;
        else {
            gc.drawImage(toImage(elem), elem.getX(), elem.getY()); //отрисовка
        }
    }

    private static Image toImage(Cell cell){
        BufferedImage bufferedImage = cell.getSprite().getSprite();
        WritableImage wr = null;
        if (bufferedImage != null) {
            wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    pw.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
        }
        ImageView imageView = new ImageView(wr);
        return imageView.getImage();
    }
}
