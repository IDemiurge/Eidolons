package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.NoHitGroup;

public class CursorDecorator extends NoHitGroup {
    private FadeImageContainer cursor=new FadeImageContainer();
    private static CursorDecorator instance;

    private CursorDecorator() {
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
        float x = Gdx.input.getX() - cursor.getWidth() / 3;//draggedOffsetX;
        float y = GdxMaster.getHeight() -
                (Gdx.input.getY() + cursor.getHeight());// draggedOffsetY;
        setPosition(x, y);
    }

    private void setCursorType(GdxMaster.CURSOR type) {
        cursor.setImage(type.getFilePath());
    }
    public void hovered(DC_Obj object){

        //check special - interactive, ...
        Unit hero = Eidolons.getMainHero();
        GdxMaster.CURSOR type= GdxMaster.CURSOR.ATTACK;
        boolean hostile = object.getOwner().isHostileTo(hero.getOwner());
        if (hostile) {
            type= GdxMaster.CURSOR.ATTACK_SNEAK;
        } else {

        }

        boolean melee;
        boolean sneak;

        //modifiers?
        setCursorType(type);

    }


    public void hoverOff(DC_Obj userObject) {
         setCursorType(GdxMaster.CURSOR.DEFAULT);
    }

    //TODO for actions and items too?
}
