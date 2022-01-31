package libgdx.gui.dungeon.controls;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.entity.active.ActiveObj;
import libgdx.gui.UiMaster;
import libgdx.gui.dungeon.controls.radial.RadialMenu;
import libgdx.gui.dungeon.panels.dc.actionpanel.ActionPanel;
import libgdx.gui.dungeon.panels.headquarters.HqSlotActor;

import java.util.function.Supplier;

/**
 * Created by JustMe on 11/13/2018.
 * TODO to replace the old item comps
 */
public class ActionSlot extends HqSlotActor<ActiveObj> {

    protected Supplier<String> infoTextSupplier;
    protected  ShaderProgram shader;
    protected Runnable clickAction;
    protected boolean valid = true;
    protected boolean hover;
    protected  static boolean darkened;
    protected  final float scaleByOnHover = (64f / ActionPanel.IMAGE_SIZE) - 1;
    protected  float size = UiMaster.getIconSize();
    protected RadialMenu customRadialMenu;
    
    
    public ActionSlot(ActiveObj model) {
        super(model);
    }

    @Override
    protected String getOverlay(ActiveObj model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return null;
    }
}
