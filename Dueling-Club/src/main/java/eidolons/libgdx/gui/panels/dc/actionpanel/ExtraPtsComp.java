package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import main.system.graphics.FontMaster;

public class ExtraPtsComp extends TablePanelX {
    SymbolButton button;
    LabelX label;
    boolean attack;

    public ExtraPtsComp(boolean attack) {
        this.attack = attack;
        addActor(button = new SymbolButton(attack ? STD_BUTTON.EXTRA_ATK : STD_BUTTON.EXTRA_MOVES,
                this::toggle));
        addActor(label = new LabelX(StyleHolder.getSizedLabelStyle(FontMaster.FONT.METAMORPH, 20)));
        // read from obj on first update?
        {
            button.setChecked(true);
        }
        addListener(new DynamicTooltip(() ->
                "Turn use " + getPointsString() + " " +
                        (button.isChecked() ? "OFF" : "ON")).getController());
    }

    private String getPointsString() {
        return (attack ? "Extra Attack" : "Free Move") +
                " points";
    }

    private void toggle() {
        if ((getUserObject() instanceof PanelActionsDataSource)) {
            Unit obj = ((PanelActionsDataSource) getUserObject()).getUnit();
            boolean on = button.isChecked();
            if (attack) {
                obj.setExtraAtkPointsOn(on);
            } else {
                obj.setMovePointsOn(on);
            }
            EUtils.showInfoText("Will " + (button.isChecked() ? "" : "NOT") +
                    " use " + getPointsString());

            label.setStyle(StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.METAMORPH,
                    20, on ? GdxColorMaster.PALE_GOLD : GdxColorMaster.GOLDEN_GRAY));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float x = 35 - label.getWidth();
        label.setPosition(x, 39);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        if ((getUserObject() instanceof PanelActionsDataSource)) {
            Unit obj = ((PanelActionsDataSource) getUserObject()).getUnit();
            label.setText(obj.getParam(attack ? PARAMS.C_EXTRA_ATTACKS : PARAMS.C_EXTRA_MOVES));
            label.pack();

            if (!attack) {
                boolean on = obj.checkCanDoFreeMove(null);
                button.setDisabled(!on);
                label.setStyle(StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.METAMORPH,
                        20, on ? GdxColorMaster.PALE_GOLD : GdxColorMaster.GOLDEN_GRAY));
            }
        }
    }
}
