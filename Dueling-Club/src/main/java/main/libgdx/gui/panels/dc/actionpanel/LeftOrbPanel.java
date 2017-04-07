package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;

import static main.libgdx.gui.panels.dc.actionpanel.RigthOrbPanel.addTooltip;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class LeftOrbPanel extends TablePanel {
    private Cell<OrbElement> toughness;
    private Cell<OrbElement> endurance;
    private Cell<OrbElement> stamina;

    public LeftOrbPanel() {
        toughness = add((OrbElement) null).width(100);
        endurance = add((OrbElement) null).width(100);
        stamina = add((OrbElement) null).width(100);
        left().bottom();
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final ResourceSource source = (ResourceSource) getUserObject();

        toughness.setActor(new OrbElement(
                getOrCreateR("/UI/components/new/orb toughness.png"), source.getToughness())
        );
        endurance.setActor(new OrbElement(
                getOrCreateR("/UI/components/new/orb endurance.png"), source.getEndurance())
        );
        stamina.setActor(new OrbElement(
                getOrCreateR("/UI/components/new/orb stamina.png"), source.getStamina())
        );

        toughness.getActor().setBackground("UI/components/2017/orbs/Symbol_Empty_L.png");
        endurance.getActor().setBackground("UI/components/2017/orbs/Symbol_Empty_L.png");
        stamina.getActor().setBackground("UI/components/2017/orbs/Symbol_Empty_L.png");

        toughness.getActor().setBackOffset(new Vector2(-24, -4));
        endurance.getActor().setBackOffset(new Vector2(-24, -4));
        stamina.getActor().setBackOffset(new Vector2(-24, -4));

        addTooltip(toughness.getActor(), "Toughness", source.getToughness());
        addTooltip(endurance.getActor(), "Endurance", source.getEndurance());
        addTooltip(stamina.getActor(), "Stamina", source.getStamina());
    }
}
