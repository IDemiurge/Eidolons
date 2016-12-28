package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.data.filesys.PathFinder;
import main.system.auxiliary.GuiManager;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class Background extends Group {

    private final static String defaultBackground = "big\\dungeon.jpg";
    //private final static String defaultBackground = "big\\big bf grid test2.jpg";

    public Image backImage;

    private String imagePath;
    private boolean dirty;

    public Background(String path) {
        imagePath = path;
    }

    public Background( ) {
        this(        PathFinder.getImagePath()+defaultBackground);
    }
    public Background init() {
        update();
        return this;
    }
    public void setImagePath(String path) {
        if (!path.contains(PathFinder.getImagePath()))
            path=PathFinder.getImagePath()+path;
        imagePath = path;
        dirty=true;

    }
        public void update() {
            if (hasChildren())
        removeActor(backImage);
        backImage = new Image(new Texture(imagePath  ));
            backImage.setBounds(0,0,(float) GuiManager.getScreenWidth(), (float) GuiManager.getScreenHeight());
        addActor(backImage);
            dirty=false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
