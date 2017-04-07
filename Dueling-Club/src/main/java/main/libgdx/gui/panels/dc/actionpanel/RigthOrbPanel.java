package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;
import main.libgdx.gui.tooltips.ValueTooltip;

import java.util.Arrays;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class RigthOrbPanel extends TablePanel {
    private Cell<OrbElement> morale;
    private Cell<OrbElement> essence;
    private Cell<OrbElement> focus;

    public RigthOrbPanel() {
        morale = add((OrbElement) null).width(100);
        essence = add((OrbElement) null).width(100);
        focus = add((OrbElement) null).width(100);
        left().bottom();
    }

    public static void addTooltip(OrbElement el, String name, String val) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(name, val)));
        el.addListener(tooltip.getController());
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final ResourceSource source = (ResourceSource) getUserObject();

        morale.setActor(new OrbElement(
                getOrCreateR("/UI/components/new/orb morale.png"), source.getMorale())
        );
        essence.setActor(new OrbElement(
                getOrCreateR("/UI/components/new/orb essence.png"), source.getEssence())
        );
        focus.setActor(new OrbElement(
                getOrCreateR("/UI/components/new/orb focus.png"), source.getFocus())
        );

        morale.getActor().setBackground("UI/components/2017/orbs/Symbol_Empty_R.png");
        essence.getActor().setBackground("UI/components/2017/orbs/Symbol_Empty_R.png");
        focus.getActor().setBackground("UI/components/2017/orbs/Symbol_Empty_R.png");

        morale.getActor().setBackOffset(new Vector2(-32, -5));
        essence.getActor().setBackOffset(new Vector2(-32, -5));
        focus.getActor().setBackOffset(new Vector2(-32, -5));

        addTooltip(morale.getActor(), "Morale", source.getMorale());
        addTooltip(essence.getActor(), "Essence", source.getEssence());
        addTooltip(focus.getActor(), "Focus", source.getFocus());
    }
}
