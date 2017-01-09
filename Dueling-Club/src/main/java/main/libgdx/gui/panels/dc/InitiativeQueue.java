package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.libgdx.gui.panels.generic.EntityContainer;
import main.libgdx.gui.panels.generic.TextIconComp;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.GuiManager;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class InitiativeQueue extends EntityContainer {

    private static String clockImagePath = "\\UI\\custom\\Time.JPG";
    private Image clockImage;

    public InitiativeQueue() {
        super(null  , GuiManager.getSmallObjSize(), getMaxSlots(), 1, ()->
         DC_Game.game.getTurnManager().getDisplayedUnitQueue(),
        null, p->{
            DC_HeroObj unit = (DC_HeroObj) p.get();
            unit.invokeClicked();
         }  );

    }

    @Override
    public void initComps() {
        clockImage = new Image(TextureManager.getOrCreate(clockImagePath));
        addActor(clockImage);
        new TextIconComp(()->
         DC_Game.game.getRules().getTimeRule().getTimeRemaining()+"" ,
         ()->clockImagePath
          );
        super.initComps();
    }

    @Override
    public float getWidth() {
        return clockImage.getWidth()+ supplier.get().size() *GuiManager.getSmallObjSize();
    }

    private static int getMaxSlots() {
        return 8;
    }


}
