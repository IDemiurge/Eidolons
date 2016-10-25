package main.swing.components.panels;

import main.content.PARAMS;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.misc.ValueBar;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.GuiManager;

public class DC_BarPanel extends Builder {
    ValueBar staminaBar;
    ValueBar enduranceBar;
    ValueBar essenceBar;
    ValueBar focusBar;
    ValueBar toughnessBar;
    ValueBar moraleBar;
    private Entity obj;

    public DC_BarPanel(Obj obj, boolean info) {
        comp = new G_Panel();
        this.obj = obj;
        toughnessBar = new ValueBar(PARAMS.TOUGHNESS, ColorManager.TOUGHNESS);
        enduranceBar = new ValueBar(PARAMS.ENDURANCE, ColorManager.ENDURANCE);
        staminaBar = new ValueBar(PARAMS.STAMINA, ColorManager.STAMINA);
        essenceBar = new ValueBar(PARAMS.ESSENCE, ColorManager.ESSENCE);
        focusBar = new ValueBar(PARAMS.FOCUS, ColorManager.FOCUS);
        moraleBar = new ValueBar(PARAMS.MORALE, ColorManager.MORALE);
        // AP_Bar (stripedBar);
        compArray = new G_Component[]{essenceBar, focusBar, moraleBar,
                toughnessBar, enduranceBar, staminaBar,};
        cInfoArray = new String[]{

                "id tb, pos 0 0, w " + GuiManager.getSquareCellSize() + "" + ",h "
                        + GuiManager.getSquareCellSize() + "/6",
                "id hb, pos 0 tb.y2, w " + GuiManager.getSquareCellSize() + ""
                        + ",h " + GuiManager.getSquareCellSize() + "/6",

                "id sb, pos 0 hb.y2, w " + GuiManager.getSquareCellSize() + ""
                        + ",h " + GuiManager.getSquareCellSize() + "/6",

                "id eb, pos 0 sb.y2, w " + GuiManager.getSquareCellSize() + ""
                        + ",h " + GuiManager.getSquareCellSize() + "/6",

                "id fb, pos 0 eb.y2, w " + GuiManager.getSquareCellSize() + ""
                        + ",h " + GuiManager.getSquareCellSize() + "/6",

                "id mb, pos 0 fb.y2, w " + GuiManager.getSquareCellSize() + ""
                        + ",h " + GuiManager.getSquareCellSize() + "/6",};
    }

    @Override
    public void init() {
        // refresh();
    }

    @Override
    public void refresh() {
        // if (info)
        // setObj(game.getManager().getInfoObj());
        // else {
        // setObj(game.getManager().getActiveObj());
        // }
        toughnessBar.setObj(obj);
        toughnessBar.refresh();
        enduranceBar.setObj(obj);
        enduranceBar.refresh();
        staminaBar.setObj(obj);
        staminaBar.refresh();
        essenceBar.setObj(obj);
        essenceBar.refresh();
        focusBar.setObj(obj);
        focusBar.refresh();
        moraleBar.setObj(obj);
        moraleBar.refresh();
    }

    public Entity getObj() {
        return obj;
    }

    public void setObj(Entity obj) {
        this.obj = obj;

    }

}
