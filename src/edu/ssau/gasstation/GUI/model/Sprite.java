package edu.ssau.gasstation.GUI.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Sasha on 18.12.2016.
 */
public enum Sprite {
    EMPTY("empty", 0),
    CAR("car", 1),
    ENTER("enter",5),
    EXIT("exit", -1),
    CASH("cash",3 ),
    TANK("tank", 4),
    TRK("trk", 2);

    private BufferedImage sprite;
    private int state;
    Sprite(String element, int state){
        try {
            this.state = state;
            this.sprite = ImageIO.read(new File("D:\\Synchronization\\Git\\zelenkoslaves\\java\\zelenkoslaves\\src\\edu\\ssau\\gasstation\\GUI\\view\\icons\\"+element+".png"));
        } catch (Exception e){
            this.sprite = null;
            this.state = -2;
            e.printStackTrace();
        }
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public int getState() {
        return state;
    }
}
