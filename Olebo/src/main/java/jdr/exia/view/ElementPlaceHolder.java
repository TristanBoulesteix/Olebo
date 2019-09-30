package jdr.exia.view;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ElementPlaceHolder {


    private static Image sprite;

    static {
        try {
            sprite = ImageIO.read(ElementPlaceHolder.class.getResource(".png").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int x;
    private int y;
    private Rectangle hitbox;


    ElementPlaceHolder() {
        this.x = 50;
        this.y = 50;
        this.hitbox = new Rectangle(x, y, 32, 32);
    }


    public Image getSprite() {
        return sprite;
    }

    public void setSprite(Image sprite) {
        ElementPlaceHolder.sprite = sprite;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }


}
