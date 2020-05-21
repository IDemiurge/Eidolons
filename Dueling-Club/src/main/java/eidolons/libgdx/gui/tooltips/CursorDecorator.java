package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.NoHitGroup;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class CursorDecorator extends NoHitGroup {
    private FadeImageContainer cursor = new FadeImageContainer();
    private static CursorDecorator instance;
    GearCluster gears;
    GearCluster smallGears;
    private GdxMaster.CURSOR cursorType;
    private boolean waiting;

    private CursorDecorator() {
        //TODO IDEA: hide main cursor sometimes?
        addActor(cursor);
        cursor.setFadeDuration(0.35f);
        GuiEventManager.bind(GuiEventType.WAITING_ON , p-> waiting());
        GuiEventManager.bind(GuiEventType.WAITING_OFF , p-> waitingDone());
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
        if (this.cursorType == type) {
            return;
        }
        this.cursorType = type;
        cursor.setImage(
                GdxImageMaster.cropImagePath(type.getFilePath()));
    }

    public void waitingDone() {
        if (!waiting) {
            return;
        }
        waiting = false;
        fadeGears();
    }

    public void waiting() {
        if (waiting) {
            return;
        }
        hoverOff();
        waiting = true;
        fadeGears();

        addActor(gears = new GearCluster(3, 0.5f, true));
        gears.fadeIn();

    }

    private void fadeGears() {
        if (gears != null) {
            if (gears.getColor().a > 0) {
                gears.fadeOut();
                ActionMaster.addRemoveAfter(gears);
            } else gears.remove();
        }
    }

    public void hovered(DC_Obj object) {
        if (waiting) {
            return;
        }
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


    public void hoverOff(   ) {
        cursor.setEmpty();
    }

    //TODO for actions and items too?
}
