package libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.GearCluster;
import libgdx.gui.generic.NoHitGroup;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class CursorDecorator extends NoHitGroup {
    private final FadeImageContainer cursor = new FadeImageContainer();
    private static CursorDecorator instance;
    GearCluster gears;
    GearCluster smallGears;
    private VisualEnums.CURSOR cursorType;
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

    private void setCursorType(VisualEnums.CURSOR type) {
        if (this.cursorType == type) {
            return;
        }
        this.cursorType = type;
        cursor.setImage(
                GdxStringUtils.cropImagePath(type.getFilePath()));
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
                ActionMasterGdx.addRemoveAfter(gears);
            } else gears.remove();
        }
    }

    public void hovered(DC_Obj object) {
        if (waiting) {
            return;
        }
        //check special - interactive, ...
        Unit hero = Core.getMainHero();
        VisualEnums.CURSOR type = VisualEnums.CURSOR.ATTACK;
        boolean hostile = object.getOwner().isHostileTo(hero.getOwner());
        if (hostile) {
            type = VisualEnums.CURSOR.ATTACK_SNEAK;
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
