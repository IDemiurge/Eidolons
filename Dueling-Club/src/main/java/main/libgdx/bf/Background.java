package main.libgdx.bf;

import main.game.DC_Game;
import main.libgdx.gui.panels.generic.Comp;
import main.system.auxiliary.GuiManager;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class Background extends Comp {

    private final static String defaultBackground = "big\\dungeon.jpg";
    //private final static String defaultBackground = "big\\big bf grid test2.jpg";
 
    private boolean dirty = true;

    public Background(String path) {
        
        super(path);
        
    }

    public Background() {
        this(defaultBackground);
    }

    public Background init() {
        if (DC_Game.game != null)
            if (DC_Game.game.getDungeonMaster() != null)
                if (DC_Game.game.getDungeonMaster().getDungeonNeverInit() != null) {
                    setImagePath(
                            DC_Game.game.getDungeonMaster().getDungeonNeverInit().getMapBackground());
                }
        update();
        return this;
    }



    public void update() {
        super.update();
        image.setBounds(image.getImageX(), image.getImageY(), (float) GuiManager.getScreenWidth(), (float) GuiManager.getScreenHeight());


    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
