package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.system.TempEventManager;

public class CellBorderManager extends Group {
    private int cellW;
    private int cellH;

    protected Image greenBorder;
    protected Image redBorder;

    public CellBorderManager(int cellW, int cellH) {
        this.cellW = cellW;
        this.cellH = cellH;

        greenBorder = new Image(getColoredBorderTexture(Color.GREEN));
        greenBorder.setVisible(false);
        greenBorder.setBounds(2,2,4,4);
        redBorder = new Image(getColoredBorderTexture(Color.RED));
        redBorder.setVisible(false);

        initCallbacks();
    }

    private Texture getColoredBorderTexture(Color c) {
        Pixmap p = new Pixmap(cellW + 10, cellH + 10, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p.drawRectangle(6, 6, p.getWidth() - 12, p.getHeight() - 12);
        p = BlurUtils.blur(p, 3, 1, true);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p.drawRectangle(6, 6, p.getWidth() - 12, p.getHeight() - 12);
        p = BlurUtils.blur(p, 2, 2, true);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p = BlurUtils.blur(p, 1, 1, true);
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private Borderable greenOwner = null;

    private void initCallbacks() {

        TempEventManager.bind("show-green-border", obj -> {
            if (greenOwner != obj && obj == null) {
                greenOwner.setBorder(null);
                greenBorder.setVisible(false);
            } else {
                Borderable b = (Borderable) obj.get();
                greenBorder.setVisible(true);
                greenBorder.setWidth(b.getW());
                greenBorder.setHeight(b.getH());
                greenBorder.setX(0);
                greenBorder.setY(0);
                b.setBorder(greenBorder);
                greenOwner = b;
            }
        });
    }

    private void showGreenBorder(float x, float y) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
/*        if (greenBorder.isVisible()) {
            greenBorder.draw(batch, parentAlpha);
        }*/
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;//this is abstract object
    }
}
