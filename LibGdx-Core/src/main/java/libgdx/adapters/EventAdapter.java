package libgdx.adapters;

import com.badlogic.gdx.math.Vector2;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.system.libgdx.GdxEventAdapter;
import eidolons.system.libgdx.wrapper.VectorGdx;
import libgdx.GDX;
import libgdx.anims.text.FloatingText;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.handlers.GridCommentHandler;
import libgdx.gui.dungeon.tooltips.ValueTooltip;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

public class EventAdapter implements GdxEventAdapter {

    @Override
    public void veil(Coordinates c, boolean black, boolean enter) {
// GuiEventManager.trigger(GuiEventType.add)
//     new veil
    }

    @Override
    public void cannotActivate(DC_ActiveObj e, String reason) {
        VisualEnums.TEXT_CASES CASE = VisualEnums.TEXT_CASES.DEFAULT;
        if (!StringMaster.isEmpty(e.getCosts().getReasonsString())) {
            reason = e.getCosts().getReasonsString();
            CASE = VisualEnums.TEXT_CASES.REQUIREMENT;
        }
        LogMaster.log(1, "Cannot Activate " + e.getName() + ": " + reason);
        if (!e.getOwnerUnit().isMine())
            if (e.getOwnerUnit().isAiControlled())
                return;
        EUtils.showInfoText(e.getCosts().getReasonsString());

        FloatingText f = FloatingTextMaster.getInstance().getFloatingText(e,
                CASE,               reason);
        f.setDisplacementY(100);
        f.setDuration(3);
        Vector2 c = GridMaster.getCenteredPos(e
                .getOwnerUnit().getCoordinates());
        f.setX(c.x);
        f.setY(c.y);
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, f);
    }

    @Override
    public void tooltip(String description) {
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new ValueTooltip(description));
    }

    @Override
    public String comment(String img, String text, Coordinates c) {
        GridCommentHandler.instance.comment_(img, text, c);
        return
                GridCommentHandler.removeSequentialKey(  text );
    }

    @Override
    public void comment_(Unit unit, String key, VectorGdx at) {
        GridCommentHandler.instance.comment_( unit, key, GDX.vector(at));
    }
}
