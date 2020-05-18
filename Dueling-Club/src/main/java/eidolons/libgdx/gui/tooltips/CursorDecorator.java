package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.NoHitGroup;

public class CursorDecorator extends NoHitGroup {
    private FadeImageContainer cursor = new FadeImageContainer();
    private static CursorDecorator instance;
    GearCluster gears;

    private CursorDecorator() {
        addActor(cursor);
        cursor.setFadeDuration(0.5f);
    }

    public static CursorDecorator getInstance() {
        if (instance == null) {
            instance = new CursorDecorator();
        }
        return instance;
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        //TODO based on cursor tip !
        float x = Gdx.input.getX() + cursor.getWidth() / 2;//draggedOffsetX;
        float y = GdxMaster.getHeight() -
                (Gdx.input.getY() + cursor.getHeight()) + 32;// draggedOffsetY;
        setPosition(x, y);
    }

    private void setCursorType(GdxMaster.CURSOR type) {
        cursor.setImage(
                GdxImageMaster.cropImagePath(type.getFilePath()));
    }

    public void waiting() {
        if (gears != null) {
            gears.remove();
        }
        addActor(gears = new GearCluster(3, 0.5f, true));
        gears.fadeIn();
        
    }

    public void hovered(DC_Obj object) {

        //check special - interactive, ...
        Unit hero = Eidolons.getMainHero();
        GdxMaster.CURSOR type = GdxMaster.CURSOR.ATTACK;
        boolean hostile = object.getOwner().isHostileTo(hero.getOwner());
        if (hostile) {
            type = GdxMaster.CURSOR.ATTACK_SNEAK;
        } else {

        }

        boolean melee;
        boolean sneak;

        //modifiers?
        setCursorType(type);

    }


    public void hoverOff(DC_Obj userObject) {
        cursor.setEmpty();
    }

    //TODO for actions and items too?
}
