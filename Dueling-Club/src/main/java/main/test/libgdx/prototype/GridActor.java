package main.test.libgdx.prototype;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.data.filesys.PathFinder;
import main.libgdx.DC_GDX_GridPanel;

/**
 * Created by PC on 07.11.2016.
 */
public class GridActor extends Actor {
    private DC_GDX_GridPanel gridPanel;
    Sprite sprite;
    int raws = 10;
    int lines = 10;
    private float X;
    private float Y;

    public GridActor() {
        PathFinder.init();

//        sprite = new Sprite(new Texture(PathFinder.getImagePath() + "mini\\unit\\Nature\\griff.jpg"));
//        sprite.setBounds(0,0,5,5);

        gridPanel = new DC_GDX_GridPanel(PathFinder.getImagePath(), raws, lines).init();
//        Lightmap lightmap = new Lightmap(gridPanel.getUnits());
        setBounds(gridPanel.getX(),gridPanel.getY(),gridPanel.getWidth(),gridPanel.getHeight());
        addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("GRID touch detected");
                System.out.println("Mouse touch is on: " + x + " " + y);
                System.out.println(" Cell with coords : " + (int)x/132 + " _ " + (int)y/113);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

//    @Override
//    public Actor hit(float x, float y, boolean touchable) {
//        if (gridPanel.hit(x, y, touchable) != null){
//            return gridPanel.hit(x, y, touchable);
//        }
//        else {
//            return null;
//        }
//    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        System.out.println("Grid Draw " + System.currentTimeMillis());
        gridPanel.draw(batch, 1);
        super.draw(batch, parentAlpha);
    }
}
