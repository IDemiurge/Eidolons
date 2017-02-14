package main.swing.components.panels;

import main.content.PARAMS;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ColorManager;
import main.system.text.ToolTipMaster.TOOLTIP_TYPE;
import main.system.text.TooltipMouseListener;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ValueOrbPanel extends G_Panel {
    private ValueOrb toughnessOrb;
    private ValueOrb enduranceOrb;
    private ValueOrb staminaOrb;
    private ValueOrb essenceOrb;
    private ValueOrb focusOrb;
    private ValueOrb moraleOrb;
    private Obj obj;
    private List<ValueOrb> orbs = new LinkedList<>();

    public ValueOrbPanel(boolean info) {
        super("flowy, ins 2 2 0 0");
        toughnessOrb = createOrb(PARAMS.TOUGHNESS, ColorManager.TOUGHNESS);
        enduranceOrb = createOrb(PARAMS.ENDURANCE, ColorManager.ENDURANCE);
        moraleOrb = createOrb(PARAMS.MORALE, ColorManager.MORALE);
        staminaOrb = createOrb(PARAMS.STAMINA, ColorManager.STAMINA);
        focusOrb = createOrb(PARAMS.FOCUS, ColorManager.FOCUS);
        essenceOrb = createOrb(PARAMS.ESSENCE, ColorManager.ESSENCE);

        if (info) {
            add(focusOrb, "");
            add(essenceOrb, "wrap");
            add(moraleOrb, "");
            add(staminaOrb, "wrap");
            add(toughnessOrb, "");
            add(enduranceOrb, "wrap");
        } else {
            add(toughnessOrb, "");
            add(enduranceOrb, "wrap");
            add(moraleOrb, "");
            add(staminaOrb, "wrap");
            add(focusOrb, "");
            add(essenceOrb, "wrap");
        }

    }

    private ValueOrb createOrb(PARAMS p, Color c) {
        ValueOrb valueOrb = new ValueOrb(p, c);
        valueOrb.addMouseListener(new TooltipMouseListener(TOOLTIP_TYPE.DC_DYNAMIC_PARAM,
                DC_Game.game.getToolTipMaster(), p));

        return valueOrb;
    }

    public void add(Component comp, Object constraints) {
        if (comp instanceof ValueOrb) {
            orbs.add((ValueOrb) comp);
        }
        super.add(comp, constraints);
    }

    @Override
    public void refresh() {
        for (ValueOrb orb : orbs) {
            if (DC_Game.game.getBattleField().getBuilder().getTopPanel().isDebugOn()) {
                orb.setObj(null);
            } else {
                orb.setObj(getObj());
            }
            orb.refresh();
        }
        super.refresh();
    }

    public Obj getObj() {
        return obj;
    }

    public void setObj(Obj obj) {
        this.obj = obj;
    }
}
