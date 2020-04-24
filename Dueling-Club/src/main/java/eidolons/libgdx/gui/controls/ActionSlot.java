package eidolons.libgdx.gui.controls;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionPanel;
import eidolons.libgdx.gui.panels.headquarters.HqSlotActor;

import java.util.function.Supplier;

/**
 * Created by JustMe on 11/13/2018.
 * TODO to replace the old item comps
 */
public class ActionSlot extends HqSlotActor<DC_ActiveObj> {

    protected Supplier<String> infoTextSupplier;
    protected  ShaderProgram shader;
    protected Runnable clickAction;
    protected boolean valid = true;
    protected boolean hover;
    protected  static boolean darkened;
    protected  final float scaleByOnHover = (new Float(64) / ActionPanel.IMAGE_SIZE) - 1;
    protected  float size = UiMaster.getIconSize();
    protected  RadialMenu customRadialMenu;
    
    
    public ActionSlot(DC_ActiveObj model) {
        super(model);
    }

    @Override
    protected String getOverlay(DC_ActiveObj model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return null;
    }
}
